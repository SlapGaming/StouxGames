package nl.stoux.stouxgames.games.parkour;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.GameState;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerState;
import nl.stoux.stouxgames.storage.YamlStorage;
import nl.stoux.stouxgames.util._;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Parkour extends AbstractGame {
	
	private ParkourSQL sql;
	
	//Lobby
	private Location lobby;
	private HashMap<Location, MapJoiner> locToJoiner;
	private HashMap<Integer, MapJoiner> intToJoiner;
	
	//Maps
	private HashMap<Integer, String[]> leaderboardMaps;
	
	
	public Parkour() {
		super(GameMode.Parkour);
		yaml = new YamlStorage("Parkour");
		config = yaml.getConfig();
		setupGame();
		initializeEventHandler(new ParkourHandler(this));
	}

	@Override
	protected void setupGame() {
		enabled = false;
		state = GameState.disabled;
		
		//Connect with SQL
		if (sql != null) {
			sql.disconnect();
			_.getSQLControl().removeSQLClass(sql);
		}
		sql = new ParkourSQL(this);
		if (!sql.connect()) return;
		
		if ((lobby = getLocation(config, "lobby", true)) == null) { //Get Lobby
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to get Lobby location.");
			return;
		}
				
		//Get pads
		ConfigurationSection pads = config.getConfigurationSection("pads");
		if (!config.contains("pads") || pads == null) {
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to get Pressure pads.");
			return;
		}
		
		intToJoiner = new HashMap<>();
		locToJoiner = new HashMap<>();
		
		
		for (String padKey : pads.getKeys(false)) { //Loop thru pads
			try {
				int padNr = Integer.parseInt(padKey); //Parse key
				if (padNr < 1 ) throw new NumberFormatException("Invalid number (under 1)");
				
				Location padLocation = getLocation(config, "pads." + padKey + ".pad", false); //Get location
				String signDirection = pads.getString(padKey + ".sign-location"); //Get Sign Direction
				if (padLocation == null || signDirection == null) { //Check if not null
					throw new NullPointerException("Pad location or Sign Direction == null");
				}
				
				Block pressurePad = padLocation.getBlock();
				
				Material padMaterial = pressurePad.getType(); //Check if Pressure Plate
				if (padMaterial != Material.STONE_PLATE && padMaterial != Material.WOOD_PLATE) {
					throw new NullPointerException("Found block was not a Pressure plate.");
				}
				
				BlockFace bf = BlockFace.DOWN;
				
				switch (signDirection.toUpperCase()) {
				case "W": bf = BlockFace.WEST; 	break;
				case "E": bf = BlockFace.EAST; 	break;
				case "N": bf = BlockFace.NORTH; break;
				case "S": bf = BlockFace.SOUTH; break;
				default:
					throw new NullPointerException("Found direction is invalid.");
				}
				
				Block b = pressurePad.getRelative(bf); //Get relative
				if (b.getType() != Material.SIGN && b.getType() != Material.SIGN_POST && b.getType() != Material.WALL_SIGN) { //Check if sign
					throw new NullPointerException("Found block was not a sign!");
				}
				
				MapJoiner mj = new MapJoiner(b);
				intToJoiner.put(padNr, mj);
				locToJoiner.put(pressurePad.getLocation(), mj);				
			} catch (Exception e) {
				_.log(Level.SEVERE, GameMode.Parkour, "Failed to get pressure pad, with key: " + padKey + "(Ex: "+ e.getMessage() + ")");
			}
		}
		
		if (intToJoiner.size() < 1) { //Check if any MapJoiners have been added
			_.log(Level.SEVERE, GameMode.Parkour, "No pressure pads succesfully added! No way to join maps.");
			return;
		}
		
		//Get Maps
		String sep = System.getProperty("file.separator");
		ArrayList<ParkourMap> foundMaps = new ArrayList<>();
		File f = new File (_.getPlugin().getDataFolder().getAbsolutePath() + sep + "parkour");
		if (!f.exists()) {
			f.mkdirs();
		}
		for (File foundFile : f.listFiles()) { //Loop thru found files
			String filename = foundFile.getName();
			if (!filename.contains(".yml")) continue;
			ParkourMap pm = new ParkourMap(this, filename.substring(0, filename.length() - 4)); //Create the map
			if (pm.setup()) { //Set it up, if correct add to list
				foundMaps.add(pm);
			} else {
				System.out.println("Failed to add "+ filename.substring(0, filename.length() - 4));
			}
		}
		
		if (foundMaps.size() < 1) { //No maps found
			_.log(Level.SEVERE, GameMode.Parkour, "No maps found.");
			return;
		}
		
		if (intToJoiner.size() < foundMaps.size()) {
			_.log(Level.WARNING, GameMode.Parkour, "There are more maps than pressure pads!");
		}
		
		ArrayList<Integer> padNumberList = new ArrayList<>(intToJoiner.keySet()); //Create new list
		Collections.sort(padNumberList); //Sort from Low -> High
		int x = 0;
		int listSize = foundMaps.size();
		for (int padNr : padNumberList) { //Loop thru pads
			intToJoiner.get(padNr).setLinkedMap(foundMaps.get(x));
			x++;
			if (x >= listSize) break; //Break if all maps been looped thru
		}
		
		if (!generateMapsCommand()) return; //Generate the leaderboard Maps
		
		_.getSQLControl().addSQLClass(sql); //Add SQL Class
		
		_.log(Level.INFO, GameMode.Parkour, "Up & Running.");
		
		enabled = true;
		state = GameState.playing;
	}
	
	protected static Location getLocation(FileConfiguration cConfig, String path, boolean hasYaw) {
		if (!cConfig.contains(path)) {
			_.log(Level.SEVERE, GameMode.Parkour, "Following path is missing: " + path);
			return null;
		}
		Vector v = cConfig.getVector(path);
		if (v == null) {
			_.log(Level.SEVERE, GameMode.Parkour, "Following Location has not been found: " + path);
			return null;
		}
		Location loc = v.toLocation(_.getWorld());
		if (hasYaw) {
			String yawPath = path + "-yaw";
			if (cConfig.contains(yawPath)) {
				int yaw = cConfig.getInt(yawPath);
				loc.setYaw(yaw);
			} else {
				_.log(Level.WARNING, GameMode.Parkour, "Failed to get Yaw for Vector: " + yawPath);
			}
		}
		return loc;
	}

	@Override
	public void disableGame() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Get the SQL class
	 * @return the SQL class
	 */
	public ParkourSQL getSQL() {
		return sql;
	}
	
	/**
	 * Message all players on a certain map 
	 * @param message The message
	 * @param map The map
	 */
	public void broadcastToMapPlayers(String message, ParkourMap map) {
		for (GamePlayer gp : getPlayers()) { //Loop thru players
			ParkourPlayer pp = (ParkourPlayer) gp; //Cast to ParkourPlayer
			if (pp.isOnParkourMap()) { //Check if on map
				if (pp.getCurrentMap() == map) { //Check if on correct map
					_.msg(pp.getPlayer(), GameMode.Parkour, message); //Message
				}
			}
		}
	}
	
	/**
	 * Generate the maps command.
	 * 
	 * Get all the maps that should be able to be called for leaderboards.
	 * @return succes
	 */
	private boolean generateMapsCommand() {
		HashSet<ParkourMap> maps = new HashSet<>();
		for (MapJoiner value : locToJoiner.values()) {
			maps.add(value.getLinkedMap());
		}
		
		leaderboardMaps = sql.generateMapsCommand(maps);
		return (leaderboardMaps != null);
	}
	
	/**
	 * Get the maps that have a leaderboard available.
	 * @return Map with ID -> {Map name, Author}
	 */
	public HashMap<Integer, String[]> getLeaderboardMaps() {
		return leaderboardMaps;
	}
	
	/**
	 * Send the map info string. This contains Name, Author & ID.
	 * @param sender The commandsender who to send the info to
	 * @param mapID the ID of the map
	 */
	public void sendMapInfo(CommandSender sender, int mapID) {
		if (!leaderboardMaps.containsKey(mapID)) return; //Check if Map contains ID
		String[] mapInfo = leaderboardMaps.get(mapID);
		_.msg(sender, GameMode.Parkour, "Map: " + ChatColor.YELLOW + mapInfo[0] + ChatColor.GRAY + " (By " + mapInfo[1] + ") " + ChatColor.WHITE + " - ID: " + ChatColor.GREEN + "#" + mapID);
	}
	
	
	
	
	
	
	/*
	 * Methods & shit
	 */
	
	/**
	 * On player move event
	 * @param pp The Parkourplayer
	 * @param event The event
	 */
	public void onPlayerMove(ParkourPlayer pp, PlayerMoveEvent event) {
		if (pp.getState() != PlayerState.playing || state != GameState.playing) {
			return;
		}

		//Check if the player has actually moved
		if (!_.hasMoved(event.getFrom(), event.getTo())) return;
		
		if (pp.isOnParkourMap()) { //Should never be false
			pp.getCurrentMap().onPlayerMove(pp, event); //Let the map handle the rest
		}
	}
	
	/**
	 * Player interact event
	 * @param pp
	 * @param event
	 */
	public void onPlayerInteract(ParkourPlayer pp, PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL) {
			onPlayerPressurePad(pp, event.getClickedBlock());
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			onPlayerClickButton(pp, event.getClickedBlock());
		}
	}
	
	/**
	 * Player stands on a pressure plate
	 * @param pp The player
	 * @param clickedBlock The clicked block for the event
	 */
	private void onPlayerPressurePad(ParkourPlayer pp, Block clickedBlock) {
		if (pp.getState() != PlayerState.lobby) return; //Check if in lobby
		MapJoiner mj = locToJoiner.get(clickedBlock.getLocation());
		if (mj == null) return; //Check if a MapJoiner is linked to this pad
		if (mj.getLinkedMap() == null) return; //Check if this MapJoiner has a map linked to it
		pp.joinMap(mj.getLinkedMap()); //Join the map		
	}
	
	/**
	 * Player clicks a button
	 * @param player The player
	 * @param b
	 */
	private void onPlayerClickButton(ParkourPlayer pp, Block b) {
		if (pp.getState() != PlayerState.playing) return; //Check if playing
		if (!pp.isOnParkourMap()) return; //Check if on map
		pp.getCurrentMap().onPlayerClickButton(pp, b); //Let map handle it
	}
	
	@Override
	public void playerQuit(GamePlayer gP) {
		players.remove(gP.getName());
		gP.playerQuitGame();
	}

	@Override
	public GamePlayer playerJoins(Player p) {
		if (state != GameState.playing) {
			_.badMsg(p, "Sorry, but you currently cannot join this game!");
			return null;
		}
		
		ParkourPlayer pp = new ParkourPlayer(p, this); //Create new player
		pp.resetPlayer();
		_.getPlayerController().addPlayer(pp); //Add the player to the main controller
		players.put(pp.getName(), pp); //Add to players list
		pp.setState(PlayerState.lobby); //Set state
		p.teleport(getLobby()); //Teleport player
		return pp;
	}

	@Override
	public GamePlayer playerJoinsSpectate(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Location getLobby() {
		return lobby;
	}

	@Override
	protected void handleTab() {
		// TODO Auto-generated method stub

	}
	
	
	
	private class MapJoiner {
		
		private Block signBlock;
		private ParkourMap linkedMap;
		
		public MapJoiner(Block sign) {
			this.signBlock = sign;
			clearSign();
		}
		
		public ParkourMap getLinkedMap() {
			return linkedMap;
		}
		
		public void setLinkedMap(ParkourMap linkedMap) {
			this.linkedMap = linkedMap;
			Sign sign = (Sign) signBlock.getState();
			sign.setLine(0, ChatColor.AQUA + "--Map--");
			sign.setLine(1, ChatColor.AQUA + linkedMap.getName());
			sign.setLine(3, ChatColor.DARK_RED + "[Join]");	
			sign.update();
		}
		
		private void clearSign() {
			Sign sign = (Sign) signBlock.getState();
			sign.setLine(0, "");
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			sign.update();
		}
		
	}
	
	
	
	
}
