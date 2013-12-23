package nl.stoux.stouxgames.games.parkour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer.ParkourRun;
import nl.stoux.stouxgames.storage.YamlStorage;
import nl.stoux.stouxgames.util._;

public class ParkourMap {

	//Main class
	private Parkour parkour;
	
	//Saved data
	private YamlStorage yaml;
	private FileConfiguration config;
	
	//General info
	private int parkourID;
	private String filename;
	private String author;
	private String name;
	private boolean allowRestartOnCheckpoint;
	
	//Lobby
	private Location lobby;
	private Location startPoint; //Optional
	
	//Button Locations
	private Location resetButton;
	private Location continueButton;
	
	//Start & End
	private ProtectedRegion start;
	private ProtectedRegion end;

	//Death Height
	private int deathHeight;
	
	//Checkpoints
	private ArrayList<Checkpoint> checkpoints;
	private HashMap<String, Checkpoint> checkpointMap;
	private HashMap<Integer, Checkpoint> checkpointNrMap;
	private int nrOfCheckpoints;
	
	//The regionmanager
	private RegionManager rm;
	
	
	
	
	public ParkourMap(Parkour parkour, String filename) {
		this.parkour = parkour;
		this.filename = filename;
		yaml = new YamlStorage("parkour" + System.getProperty("file.separator") + filename);
		config = yaml.getConfig();
		rm = _.getWorldGuard().getRegionManager(_.getWorld());
	}
	
	/**
	 * Set the map up.
	 * @return Succes
	 */
	public boolean setup() {
		if ((author = config.getString("author")) == null) { //Get author
			_.log(Level.SEVERE, GameMode.Parkour, "Map " + filename + " missing author name!");
			return false;
		}
		if ((name = config.getString("name")) == null) { //Get name
			_.log(Level.SEVERE, GameMode.Parkour, "Map " + filename + " missing author name!");
			return false;
		}
				
		if (allowRestartOnCheckpoint = config.getBoolean("allowrestartoncheckpoint")) {
			if ((resetButton = getButtonLocation("reset-button")) == null || (continueButton = getButtonLocation("continue-button")) == null) {
				return false; //Mising buttons
			}
		}
		
		if ((lobby = Parkour.getLocation(config, "lobby", true)) == null) return false; //Get Lobby Location
		if ((start = getProtectedRegion("start")) == null) return false; //Get start region
		if ((end = getProtectedRegion("end")) == null) return false; //Get end region
		
		//Get the optional StartPoint location
		if (config.contains("startpoint")) {
			Vector v = config.getVector("startpoint");
			if (v != null) {
				startPoint = Parkour.getLocation(config, "startpoint", true);
			}
		}
		
		if ((deathHeight = getIntFromConfig("deathheight", true)) == -1) return false; //Get the death height
		
		checkpoints = new ArrayList<>();
		ConfigurationSection checkpointConfig = config.getConfigurationSection("checkpoints"); //Get all checkpoints
		if (checkpointConfig != null) {
			int foundCp = 0;
			for (String key : checkpointConfig.getKeys(false)) {
				try {
					int cp = Integer.parseInt(key);
					if (cp != (foundCp + 1)) {
						_.log(Level.SEVERE, GameMode.Parkour, "The checkpoint order is wrong. The found number should be: " + (foundCp + 1) + " but " + cp + " was found.");
						return false;
					}
					foundCp = cp;
					Checkpoint newCP = new Checkpoint(cp);
					if (!newCP.correct()) {
						_.log(Level.SEVERE, GameMode.Parkour, "Incorrect checkpoint, key: " + key);
						return false;
					}
					checkpoints.add(newCP);
				} catch (NumberFormatException e) {
					_.log(Level.WARNING, GameMode.Parkour, "Invalid checkpoint number.");
				}
			}
		}	
		
		nrOfCheckpoints = checkpoints.size();
		checkpointMap = new HashMap<>();
		checkpointNrMap = new HashMap<>();
		for (Checkpoint cp : checkpoints) { //Fill maps
			checkpointMap.put(cp.getRegionName(), cp);
			checkpointNrMap.put(cp.getCheckpointNumber(), cp);
		}
		
		//Fully setup -> SQL Part
		ParkourSQL sql = parkour.getSQL();
		if ((parkourID = getIntFromConfig("id", false)) == -1) { //No ID Given -> Generate one
			int key = sql.addNewMap(name, author, filename);
			if (key == -1) { //Failed to add map
				return false;
			}
			parkourID = key;
			config.set("id", key);
			yaml.saveConfig();
		} else { //ID Found -> Check if correct
			try {
				String[] mapInfo = sql.getMapInfo(parkourID);
				if (mapInfo == null) { //Check if in DB
					_.log(Level.SEVERE, GameMode.Parkour, "No ParkourMap found in Database with ID " + parkourID);
					return false;
				}
				
				if (!mapInfo[2].equals(filename)) { //Check if correct Filename
					_.log(Level.SEVERE, GameMode.Parkour, "Filenames don't match! Something is fishy. SQL: " + mapInfo[2] + " | Found: " + filename);
					return false;
				}
				
				if (!mapInfo[0].equals(name) || !mapInfo[1].equals(author)) { //Check if names are correct
					boolean updated = sql.updateMapInfo(parkourID, name, author); //If not update
					_.log(Level.WARNING, GameMode.Parkour, "Updating Author & Map Name in Database for ID " + parkourID + " (File: " + filename + "). " + 
							(updated ? "Updated." : "WARNING: Failed to update!")); //Log that updating + Succes
					if (!updated) return false; //If not succes -> Return false
				}
			} catch (NullPointerException e) { //SQL Problems
				return false;
			}
		}
		
		
		return true;
	}
	
	/**
	 * Get the location of a button block
	 * @param path The path to the vector
	 * @return The location or null
	 */
	private Location getButtonLocation(String path) {
		Location foundLoc = Parkour.getLocation(config, path, false);
		if (foundLoc == null) return null; //Did not find location
		
		Block b = foundLoc.getBlock();
		if (b.getType() != Material.STONE_BUTTON && b.getType() != Material.WOOD_BUTTON) { //Check if Button
			_.log(Level.SEVERE, GameMode.Parkour, "The found block was not a button! Path: " + path);
			return null;
		}
		
		return b.getLocation();
	}
	
	/**
	 * Get a int from the config
	 * @param path The path to the int
	 * @return the int or -1 if invalid
	 */
	private int getIntFromConfig(String path, boolean msg) {
		if (!config.contains(path)) {
			if (msg) _.log(Level.SEVERE, GameMode.Parkour, "The following path hasn't been found: " + path);
			return -1;
		}
		int foundInt = config.getInt(path);
		if (foundInt == 0 || foundInt == -1) {
			if (msg) _.log(Level.SEVERE, GameMode.Parkour, "The found int for path '" + path + "' is invalid.");
			return -1;
		}
		return foundInt;
	}
	
	/**
	 * Find the a region by its name in the config
	 * @param path The path to the region
	 * @return The region or null
	 */
	private ProtectedRegion getProtectedRegion(String path) {
		String name = config.getString(path);
		if (name == null) {
			_.log(Level.SEVERE, GameMode.Parkour, "No regionname found at: " + path);
			return null;
		}
		ProtectedRegion region = rm.getRegion(name);
		if (region == null) {
			_.log(Level.SEVERE, GameMode.Parkour, "No region found with name: " + name + " (path: " + path + ")");
			return null;
		}
		return region;
	}
	
	/**
	 * A player has moved on this map
	 * @param p The player
	 * @param event The event
	 */
	protected void onPlayerMove(ParkourPlayer p, PlayerMoveEvent event) {
		ParkourRun run = p.getRun();
		
		if (p.getPlayer().getLocation().getY() < deathHeight) { //Check if player is dead
			if (run.hasStarted() && !run.isFinished()) { //Not actually doing a run, ignore
				teleportPlayerBackToCheckpoint(p, true, true);
				run.incrementFails();
				return;
			}
		}
		
		ApplicableRegionSet foundRegions = rm.getApplicableRegions(p.getPlayer().getLocation());
		for (ProtectedRegion region : foundRegions) { //Loop thru regions
			if (region == start) { //Passed start
				if (run.isFinished() || !run.hasStarted()) { //If finished or not started -> Start
					run.reset();
					run.start();
					parkour.broadcastToPlayers(p.getName() + " started a new run on " + name + "!");
				}
			} else if (region == end) { //Passed finish
				if (!run.isFinished()) { //Check if finished
					if (run.getCurrentCheckpoint() != nrOfCheckpoints) { //Finished but missed a checkpoint
						teleportPlayerBackToCheckpoint(p, false, true);
					} else {
						long finishTime = run.finish();
						parkour.broadcastToPlayers(p.getName() + " finished the map " + name + " with a time of " + _.getGameController().getTimeString(finishTime) +"!");
					}
				}
			} else if (checkpointMap.containsKey(region.getId())) {
				Checkpoint cp = checkpointMap.get(region.getId());
				int foundCP = cp.getCheckpointNumber();
				int currentCP = run.getCurrentCheckpoint();
				if (foundCP == currentCP) { //Still on the same checkpoint
					continue;
				} else if (foundCP == (currentCP + 1)) { //Moved to next checkpoint
					run.setCheckpoint(foundCP);
					parkour.broadcastToMapPlayers(p.getName() + " passed checkpoint " + foundCP + " with a time of " + _.getGameController().getTimeString(run.getPassedTime()), this);
				} else if (foundCP > (currentCP + 1)) { //Missed a checkpoint
					teleportPlayerBackToCheckpoint(p, false, true);
				}
				
			}
		}
	}
	
	/**
	 * Teleport player back to the last checkpoint
	 * @param p The player
	 * @param dead The player has died
	 * @param msg Should message the player about death/missed a checkpoint
	 */
	public void teleportPlayerBackToCheckpoint(ParkourPlayer p, boolean dead, boolean msg) {
		ParkourRun run = p.getRun();
		Player player = p.getPlayer();
		if (run.getCurrentCheckpoint() != 0) { //Last checkpoint was NOT start
			_.msg(player, GameMode.Parkour, 
					(msg ? (dead ? "You have died! " : "You missed a checkpoint! ") : "") + 
					"You've been reset to checkpoint " + run.getCurrentCheckpoint() + "!");
			checkpointNrMap.get(run.getCurrentCheckpoint()).teleportPlayer(player);
		} else {
			_.msg(player, GameMode.Parkour, (dead ? "You have died!" : "You missed a checkpoint!") + " You've been reset to the start!");
			run.reset();
			player.teleport(startPoint != null ? startPoint : lobby); //L33t ? : format
		}
	}
	
	/**
	 * A player has clicked a button
	 * @param pp The player
	 * @param clickedButton The button
	 */
	public void onPlayerClickButton(ParkourPlayer pp, Block clickedButton) {
		if (!allowRestartOnCheckpoint) return; 
				
		Location blockLocation = clickedButton.getLocation();
				
		if (resetButton.equals(blockLocation)) { //Check if reset button
			pp.getRun().reset();
			_.msg(pp.getPlayer(), GameMode.Parkour, "Your run has been reset!");
			return;
		}
		
		if (continueButton.equals(blockLocation)) { //Check if continue button
			if (!pp.getRun().hasSavedCheckpoint()) { //Check if there is something to continue
				_.msg(pp.getPlayer(), GameMode.Parkour, "There is nothing to continue!");
				return;
			}
			
			teleportPlayerBackToCheckpoint(pp, false, false);
			pp.getRun().start();
			_.msg(pp.getPlayer(), GameMode.Parkour, "Current time: " + _.getGameController().getTimeString(pp.getRun().getPassedTime()));
			return;
		}
		
	}
	
	/**
	 * Teleport a player to the start of the map
	 * @param p The player
	 */
	public void teleportToMapStart(Player p) {
		p.teleport(startPoint != null ? startPoint : lobby); //L33t ? : format
	}
	
	/*
	 ****************
	 * Info Methods *
	 ****************
	 */
	
	/**
	 * Get this parkour's ID
	 * @return the ID
	 */
	public int getID() {
		return parkourID;
	}
	
	/**
	 * Get the author of this map
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * Get the name of this map
	 * @return the map
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the lobby location
	 * @return the location
	 */
	public Location getLobby() {
		return lobby;
	}
	
	/**
	 * Get the number of checkpoints this map has
	 * @return number
	 */
	public int getNrOfCheckpoints() {
		return nrOfCheckpoints;
	}
	
	
	/**
	 * Check if players are allowed to save checkpoints
	 * @return allow restart
	 */
	public boolean hasAllowRestartOnCheckpoint() {
		return allowRestartOnCheckpoint;
	}
	
	/**
	 * Get a checkpoint
	 * @param checkpointNr The checkpoint number
	 * @return The checkpoint or null
	 */
	public Checkpoint getCheckpoint(int checkpointNr) {
		return checkpointNrMap.get(checkpointNr);
	}
	
	
	
	public class Checkpoint {
		
		//The checkpoint number
		private int checkpointNumber;
		
		private Location restartLocation;
		
		//Checkpoint info
		private ProtectedRegion region;

		public Checkpoint(int cp) {
			checkpointNumber = cp;
			restartLocation = Parkour.getLocation(config, "checkpoints." + cp + ".restartlocation", true);
			region = getProtectedRegion("checkpoints." + cp + ".region");
		}
		
		/**
		 * Check if the checkpoint has been correctly setup
		 * @return correct
		 */
		public boolean correct() {
			if (restartLocation == null || region == null) {
				return false;
			} else {
				return true;
			}
		}
		
		/**
		 * Check if a location is in the checkpoint region
		 * @param loc The location
		 * @return in checkpoint
		 */
		public boolean isOnCheckpoint(Location loc) {
			return region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}
		
		/**
		 * Teleport the player to this checkpoint
		 * @param p The player
		 */
		public void teleportPlayer(Player p) {
			p.teleport(restartLocation);
		}
			
		/**
		 * Get the checkpoint number of this checkpoint
		 * @return the cp numbert
		 */
		public int getCheckpointNumber() {
			return checkpointNumber;
		}
		
		/**
		 * Get the name of the checkpoint region
		 * @return the name
		 */
		public String getRegionName(){
			return region.getId();
		}
		
	}
	
	

}
