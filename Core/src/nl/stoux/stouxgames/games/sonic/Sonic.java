package nl.stoux.stouxgames.games.sonic;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.GameState;
import nl.stoux.stouxgames.games.sonic.SonicPlayer.SonicRun;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerState;
import nl.stoux.stouxgames.storage.YamlStorage;
import nl.stoux.stouxgames.util._;
import nl.stoux.stouxgames.util._T;

public class Sonic extends AbstractGame {
		
	//RegionManager
	private RegionManager rm;
	
	//Locations
	private Location spawn;
	private Location race;
	private Location tutorial;
	
	//Buttons
	private Block spawnToRaceButton;
	private Block spawnToTutorialButton;
	private Block tutorialToRaceButton;
	
	//Checkpoints
	private HashMap<String, Integer> checkpoints;
	
	//Jumps
	private HashMap<String, Integer> jumps;
	
	//Leaderboard
	private SonicLeaderboard leaderboard;
	
	public Sonic() {
		super(GameMode.Sonic);
		yaml = new YamlStorage("Sonic");
		config = yaml.getConfig();
		players = new HashMap<>();
		setupGame();
		initializeEventHandler(new SonicHandler(this)); //TODO
	}
	
	/**
	 * Initialize the game
	 */
	protected void setupGame() {
		enabled = false;
		state = GameState.disabled;
		
		checkpoints = new HashMap<>();
		jumps = new HashMap<>();
		
		//Get stuff out of the config
		boolean everythingFound = true;
		
		//Locations
		spawn = getLocationOutConfig("spawn");
		race = getLocationOutConfig("racetrack");
		tutorial = getLocationOutConfig("tutorial");
		if (spawn == null || race == null || tutorial == null) {
			everythingFound = false;
		} else { //Set Yaws
			spawn.setYaw(180F);
			race.setYaw(180F);
			tutorial.setYaw(180F);
		}
		
		//Buttons
		spawnToRaceButton = findButtonBlock("spawntorace");
		spawnToTutorialButton = findButtonBlock("spawntotut");
		tutorialToRaceButton = findButtonBlock("tuttorace");
		if (spawnToRaceButton == null || spawnToTutorialButton == null || tutorialToRaceButton == null) {
			everythingFound = false;
		}
		
		//Regions
		rm = _.getWorldGuard().getRegionManager(_.getWorld()); //Get the region manager
		ProtectedRegion startEndRegion = getRegion(rm, "start_end"); //Get the start/end checkpoint
		if (startEndRegion != null) { //If not null add to map under cp 0
			checkpoints.put(startEndRegion.getId(), 0);
		} else {
			everythingFound = false; 
		}
		
		int x = 1;
		while (x < 6) { //Get all jumps & checkpoints
			ProtectedRegion cp = getRegion(rm, "checkpoint" + x); //Get the checkpoint
			if (cp != null) { //If not null
				checkpoints.put(cp.getId(), x); //Add to map
			}
			ProtectedRegion jump = getRegion(rm, "jump" + x);
			if (jump != null) {
				jumps.put(jump.getId(), x);
			}
			
			if (cp == null || jump == null) {
				everythingFound = false;
			}
			x++;
		}
		
		//Create the leaderboard
		leaderboard = new SonicLeaderboard(this);
		boolean leaderboardConnected = leaderboard.connectWithSQL();
		
		if (!everythingFound || !leaderboardConnected) { //If something went wrong
			_.log(Level.SEVERE, gm, "Disabled.");
			return;
		}
		
		enabled = true;
		state = GameState.playing;
		_.log(Level.INFO, gm, "Up & Running.");
	}
	
	@Override
	public void disableGame() {
		removeAllPlayers();
	}
	
	/**
	 * Find the button by getting a Vector out of the config
	 * @param path Path to the vector in the config
	 * @return The block (a Stone_button) or null
	 */
	private Block findButtonBlock(String path) {
		Location loc = getLocationOutConfig(path); //Get the location
		if (loc == null) return null; //If null -> Return null
		Block b = loc.getBlock(); //Get the block based on the Location
		if (b.getType() == Material.STONE_BUTTON) { //Check if it indeed is a Stone_Button
			return b;
		} else { //Else return null
			return null;
		}
	}
	
	/**
	 * Get a Location out of the config
	 * @param path Path to the vector
	 * @return The vector or null
	 */
	private Location getLocationOutConfig(String path) {
		Vector v;
		if (!config.contains(path)) { //Check if the config contains this path
			_.log(Level.SEVERE, gm, "No entry found for the following path: " + path);
			v = null;
		} else {
			v = config.getVector(path); //Get the vector out of the path
			if (v == null) { //Check if it was an actual vector
				_.log(Level.SEVERE, gm, "No vector found for path: " + path);
			}
		}
		if (v == null) { //If nothing found, set -> null
			config.set(path, _.getWorld().getSpawnLocation().toVector());
			return null;
		} else { 
			return v.toLocation(_.getWorld()); //Morth vector -> loc & return
		}
	}
	
	/**
	 * Find a region
	 * @param rm The regionmanager of the world
	 * @param path The path in the config
	 * @return The protectedregion or null
	 */
	private ProtectedRegion getRegion(RegionManager rm, String path) {
		if (!config.contains(path) || config.getString(path).equals("empty")) {
			_.log(Level.SEVERE, gm, "The following region was not defined: " + path);
			return null;
		}
		String regionname = config.getString(path);
		ProtectedRegion region = rm.getRegion(regionname);
		if (region == null) {
			_.log(Level.SEVERE, gm, "No region found with the name: " + regionname);
		}
		return region;
	}
	
	/*
	 * Other methods
	 */
	/**
	 * Get the leaderboard 
	 * @return The leaderboard
	 */
	public SonicLeaderboard getLeaderboard() {
		return leaderboard;
	}
	
	/**
	 * Get the location of the race start
	 * @return The location
	 */
	public Location getRaceStartLocation() {
		return race;
	}
	
	@Override
	public Location getLobby() {
		return spawn;
	}
	
	/*
	 * Events
	 */
	public void onPlayerMove(GamePlayer gP, PlayerMoveEvent event) {
		if (gP.getState() != PlayerState.playing || state != GameState.playing) {
			return;
		}
		
		Location from = event.getFrom();
		Location to = event.getTo();
		
		//Check if the player has actually moved
		if (!_.hasMoved(from, to)) {
			return;
		}
		
		for (ProtectedRegion region : rm.getApplicableRegions(event.getTo())) {
			String regionID = region.getId();
			if (checkpoints.containsKey(regionID)) { //Player crossed a checkpoint
				SonicPlayer sP = (SonicPlayer) gP; //The player
				SonicRun sR = sP.getSonicRun(); //The players run
				int passedCheckpoint = checkpoints.get(regionID);
				if (passedCheckpoint == 0) { //Passed start/end
					if (sR == null) { //Hasn't started yet -> Start new run
						sP.newRun();
						return;
					} else {
						int currentCheckpoint = sR.getCurrentCheckpoint();
						if (currentCheckpoint == 0) { //Still starting
							return;
						} else { //Has passed checkpoints -> Thus is finishing
							sR.end();
							return;
						}
					}
				} else { //Passed a checkpoint
					if (passedCheckpoint == sR.getCurrentCheckpoint()) { //Still passing the same checkpoint
						return;
					} else {
						sR.passedCheckpoint(passedCheckpoint);
						return;
					}
				}				
			} else if (jumps.containsKey(regionID)) { //Player crossed a Jump
				int passedJump = jumps.get(regionID);
				SonicPlayer sP = (SonicPlayer) gP;
				SonicRun sR = sP.getSonicRun();
				if (sR == null) { //Hasn't started racing yet
					_.badMsg(sP.getPlayer(), "You haven't started racing yet! You've been warped to the start!");
					teleportToRaceStart(sP);
					return;
				}
				if (sR.getCurrentJump() == passedJump) { //Still passing the same Jump
					return;
				} else {
					sR.passedJump(passedJump);
					return;
				}
			}
		}
	}
	
	/**
	 * Called when a player interacts with a block
	 * @param gP The player
	 * @param event The event
	 */
	public void onPlayerInteract(GamePlayer gP, PlayerInteractEvent event) {
		if (state != GameState.playing || event.getAction() != Action.RIGHT_CLICK_BLOCK) { //Check if the game is running & the action = right click block
			return;
		}
		Block b = event.getClickedBlock(); //Get the clicked block
		if (b.equals(spawnToRaceButton) || b.equals(tutorialToRaceButton)) { //If button to teleport from Sonic Spawn/Tutorial -> Race
			teleportToRaceStart((SonicPlayer) gP);
		} else if (b.equals(spawnToTutorialButton)) { //From spawn -> Tutorial
			teleportToSonicTutorial((SonicPlayer) gP);
		}	
	}
	
	/**
	 * Teleport the player to the race start & reset his run
	 * @param sP The player
	 */
	public void teleportToRaceStart(SonicPlayer sP) { 
		sP.getPlayer().teleport(race);
		sP.resetSonicRun();
		sP.setState(PlayerState.playing);
	}
	
	/**
	 * Teleport the player to the sonic lobby
	 * @param sP The player
	 */
	public void teleportToSonicLobby(SonicPlayer sP) {
		sP.setState(PlayerState.lobby);
		sP.getPlayer().teleport(spawn);
	}
	
	/**
	 * Teleport the player to the sonic tutorial
	 * @param sP
	 */
	public void teleportToSonicTutorial(SonicPlayer sP) {
		sP.setState(PlayerState.lobby);
		sP.getPlayer().teleport(tutorial);
	}
	
	
	
	/*
	 * Player actions
	 */
	
	/**
	 * A player issues the /warpsonic command
	 * @param gP The player
	 */
	public boolean onWarpSonicCommand(GamePlayer gP) {
		if (!(gP instanceof SonicPlayer) || state != GameState.playing) {
			return false;
		}
		SonicPlayer sP = (SonicPlayer) gP; //Cast
		sP.resetSonicRun(); //Reset the person's run (if there is any)
		teleportToSonicLobby(sP);
		return true;
	}

	@Override
	public void playerQuit(GamePlayer gP) {
		players.remove(gP.getName());
		gP.playerQuitGame();
	}

	@Override
	public GamePlayer playerJoins(Player p) {
		if (!enabled || state == GameState.enabled || state == GameState.disabled) { //Check if playing
			_.badMsg(p, "This game is currently not activated.");
			return null;
		}
		final SonicPlayer sP = new SonicPlayer(p, this); //Make a new SonicPlayer
		sP.resetPlayer(); //Reset the player's stats
		players.put(sP.getName(), sP); //Put the player in the map
		_.getPlayerController().addPlayer(sP); //Add the player to the main controller
		sP.becomeSonic(); //Let the player become sonic
		teleportToSonicLobby(sP);
		_T.runLater_Sync(new Runnable() {
			
			@Override
			public void run() {
				if (sP.getPlayer().isOnline()) 
					teleportToSonicLobby(sP);
			}
		}, 1);
		leaderboard.checkConnection(); //Revive connection if needed
		return sP;
	}

	@Override
	public GamePlayer playerJoinsSpectate(Player p) {
		_.badMsg(p, "This game doesn't have a spectate mode. How did you even..");
		return null;
	}
	
	@Override
	protected void handleTab() {
		return;
	}

}
