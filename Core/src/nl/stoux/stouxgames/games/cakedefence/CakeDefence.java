package nl.stoux.stouxgames.games.cakedefence;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import entity.main.Routine;

import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.GameState;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerState;
import nl.stoux.stouxgames.storage.SavedRegion;
import nl.stoux.stouxgames.storage.YamlStorage;
import nl.stoux.stouxgames.util._;
import nl.stoux.stouxgames.util._T;

public class CakeDefence extends AbstractGame {
	
	//Lobby Countdown
	private BukkitTask lobbyCountdownTask;
	
	//The game
	private BukkitTask gameTask;
	private CakeDefenceGame game;
	private CakeDefence instance;
	
	//Main Stuff
	private Location spawnLocation;
	private Location spectateLocation;
	private ProtectedRegion lavaRegion; //Dies region
	private ProtectedRegion mainRegion;
		
	//Music Block
	private Block musicBlock;
	
	//Spawner
	private ArrayList<SavedRegion> closedSpawners;
	private int closedSpawnersSize;
	private ArrayList<SavedRegion> openSpawners;
	private int openSpawnersSize;
	
	//Routines
	private ArrayList<File> routines;

	public CakeDefence() {
		super(GameMode.CD);
		this.instance = this;
		yaml = new YamlStorage("CakeDefence");
		config = yaml.getConfig();
		players = new HashMap<>();
		setupGame();
		initializeEventHandler(new CakeDefenceHandler(this));
	}

	@Override
	protected void setupGame() {
		enabled = false;
		state = GameState.disabled;
		
		//Get stuff out of the config
		everythingFound = true;
		
		//Get regionmanager
		RegionManager rm = _.getWorldGuard().getRegionManager(_.getWorld());
		
		//Dies region
		lavaRegion = getRegion(rm, "lava");
		mainRegion = getRegion(rm, "main");
		
		//Get locations
		spawnLocation = getLocation("spawn");
		spectateLocation = getLocation("spectate"); 
		Location musicBlockLocation = getLocation("music");
		
		if (!everythingFound) { //Check if everything went correctly
			_.log(Level.SEVERE, gm, "Disabled.");
			return;
		}
		
		//Set Spawn Yaw
		spawnLocation.setYaw(0F);
		
		//Get the block -> Set to air
		musicBlock = musicBlockLocation.getBlock();
		musicBlock.setType(Material.AIR);
		
		//Spawners
		closedSpawners = new ArrayList<>();
		openSpawners = new ArrayList<>();
		
		//Get spawners
		int count = 1;
		while (count < 13) {
			ProtectedRegion region = getRegion(rm, "spawner" + count); //Get all spawners
			if (region != null) {
				SavedRegion sr = new SavedRegion(_.getBlocksInRegion(region.getId()), region.getId()); //Make the saved region
				if (sr != null) {
					closedSpawners.add(sr); //Add it to the closed spawners
				}
			}
			count++;
		}
		if (closedSpawners.size() != 12) {
			_.log(Level.SEVERE, gm, "Invalid number of spawners: " + closedSpawners.size() + " (Must be 12)");
			return;
		}
		
		closedSpawnersSize = 12;
		openSpawnersSize = 0;
				
		//Get routines
		getRoutines();
		if (routines.size() < 1) {
			_.log(Level.SEVERE, "No CakeDefence routines found!");
			return;
		}
	
		//Set spawners to snow
		for (SavedRegion sr : closedSpawners) {
			sr.setRegionToMaterial(Material.SNOW_BLOCK);
		}
		
		//Set enabled
		enabled = true;
		state = GameState.lobby;
		_.log(Level.INFO, gm, "Up & running.");
	}

	@Override
	public void disableGame() {
		musicBlock.setType(Material.AIR);
		if (gameTask instanceof BukkitTask) { //Cancel the task
			gameTask.cancel();
		}
		
		if (lobbyCountdownTask instanceof BukkitTask) { //Cancel lobby (if running)
			lobbyCountdownTask.cancel();
		}
		
		if (game != null) { //Shutdown any running games
			game.shutdown();
		}
		
		//Restore spawners
		for (SavedRegion sr : openSpawners) sr.restore();
		for (SavedRegion sr : closedSpawners) sr.restore();
		
		//Remove players
		removeAllPlayers();
	}
	
	//Setup: Everything found
	private boolean everythingFound;	
	
	/*
	 * Create/Getters for regions/locations
	 * Mainly for setup
	 */
		
	/**
	 * Find a region by the name specified in the config
	 * @param rm The regionmanager of the world
	 * @param path The path to the name
	 * @return The region of null
	 */
	private ProtectedRegion getRegion(RegionManager rm, String path) {
		if (!config.contains(path)) { //Check if in config
			_.log(Level.SEVERE, gm, "The specified path has not been found in the config: " + path);
			everythingFound = false;
			return null;
		}
		String name = config.getString(path); //Get string out of config
		if (name == null || name.equals("empty")) { //Check if something is specified
			_.log(Level.SEVERE, gm, "No region name found in the config found for: " + path);
			everythingFound = false;
			return null;
		}
		ProtectedRegion region = rm.getRegion(name); //Get region
		if (region == null) { //No region found
			_.log(Level.SEVERE, gm, "No region found with the name: " + name);
			everythingFound = false;
			return null;
		}
		return region;
	}
	
	/**
	 * Get the location in the config
	 * @param path The path to the location
	 * @return The location or null
	 */
	private Location getLocation(String path) {
		if (!config.contains(path)) { //Check if config contains
			_.log(Level.SEVERE, gm, "The specified path has not been found in the config: " + path);
			everythingFound = false;
			return null;
		}
		Vector v = config.getVector(path);
		if (v == null) { //Check if vector
			_.log(Level.SEVERE, gm, "The specified path is not a Vector: " + path);
			everythingFound = false;
			return null;
		}
		return v.toLocation(_.getWorld());
	}
	
	/**
	 * Load all the routines
	 */
	private void getRoutines() {
		String sep = System.getProperty("file.separator");
		routines = new ArrayList<>();
		File f = new File (_.getPlugin().getDataFolder().getAbsolutePath() + sep + "cakedefence");
		if (!f.exists()) {
			f.mkdirs();
		}
		for (File foundFile : f.listFiles()) {
			if (!foundFile.getName().contains(".stx")) continue;
			try {
				Object foundObject;
				try (FileInputStream fis = new FileInputStream(foundFile); ObjectInputStream ois = new ObjectInputStream(fis);) {
					foundObject = ois.readObject();
				}
				if (foundObject instanceof Routine) {
					if (((Routine) foundObject).isComplete()) {
						routines.add(foundFile);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Game Logic
	 */
	
	/**
	 * Start a countdown for the game to start
	 * @param minutes Time till the game starts
	 */
	private void lobbyCountdown(int minutes) {
		state = GameState.lobbyJoining;
		String minutesString;
		if (minutes == 1) minutesString = " minute"; 
		else minutesString = " minutes";
		_.broadcast(gm, "A new game of Cake Defence will start in " + minutes + minutesString + "! Do " + ChatColor.AQUA + "/spawn games" + ChatColor.WHITE + " and join Cake Defence!"); //Send the message
		for (GamePlayer gP : players.values()) { //Send Spectate message to all spectators
			if (gP.getState() == PlayerState.spectating || gP.getState() == PlayerState.lobbySpectator) {
				spectateMessage(gP);
			}
		}
		lobbyCountdownTask = _T.runLater_Sync(new Runnable() {
			
			@Override
			public void run() {
				int nrOfPlayers = countPlayers(new PlayerState[]{ //Count willing players
						PlayerState.lobby, PlayerState.lobbyPlayer,
						PlayerState.gameover, PlayerState.playing,
						PlayerState.joining
				});
				if (nrOfPlayers < 2) { //If less then 5 players -> Cancel
					broadcastToPlayers("Not enough players to start the game.. There need to be atleast 5!");
					state = GameState.lobby;
				} else { //Start the game
					startGame();
				}
			}
		}, minutes * 60 * 20);
	}
	
	/**
	 * Start a new game
	 */
	private void startGame() {
		_.broadcast(gm, "A new game of Cake Defence is starting! Come spectate & support the players!");
		state = GameState.starting;
		
		_T.run_ASync(new Runnable() {
			
			@Override
			public void run() {
				broadcastToPlayers("Selecting Routine...");
				CakeDefenceRoutine selectedRoutine = null;
				
				File f = routines.get(_.getRandomInt(routines.size()));
				
				try (FileInputStream fis = new FileInputStream(f); ObjectInputStream ois = new ObjectInputStream(fis);) {
					selectedRoutine = new CakeDefenceRoutine((Routine) ois.readObject());
				} catch (Exception e) {
					//Something went horribly wrong
					broadcastToPlayers("Something went horribly wrong. Cake Defence is being shut down! Warn Stoux!");
					_.log(Level.SEVERE, gm, "Loading routine failed. Exception: " + e.getMessage());
					_T.run_Sync(new Runnable() {
						
						@Override
						public void run() {
							disableGame();
						}
					});
					return;
				}
				
				final CakeDefenceRoutine pickedRoutine = selectedRoutine;
				_T.run_Sync(new Runnable() {
					
					@Override
					public void run() {
						//Broadcast routine info
						broadcastToPlayers("Picked Routine: " + ChatColor.AQUA + pickedRoutine.getRoutinename() + 
								ChatColor.WHITE + " - " + pickedRoutine.getNrOfRounds() + " rounds " + 
								ChatColor.GRAY + "(Author: " + pickedRoutine.getAuthor() + ")");
						
						for (GamePlayer gP : getPlayers()) { //Loop thru players
							gP.resetPlayer();
							switch (gP.getState()) {
							case lobby: case lobbyPlayer: case playing: case gameover: case joining:
								gP.getPlayer().teleport(spawnLocation);
								gP.setState(PlayerState.playing);
								break;
							case lobbySpectator: case spectating:
								gP.setState(PlayerState.spectating);
								break;
							}
						}
						
						//Start the game
						game = new CakeDefenceGame(instance, pickedRoutine);
						state = GameState.playing;
						gameTask = _T.runLater_Sync(game, 10);		
					}
				});
			}
		});
	}
	
	/**
	 * The game has ended
	 */
	public void endGame() {
		if (gameTask instanceof BukkitTask) { //Cancel if needed
			gameTask.cancel();
		}
		String winners = null;
		int willingPlayers = 0;
		for (GamePlayer gP : getPlayers()) {
			switch (gP.getState()) {
			case playing: //Add to winning players
				if (winners == null) winners = gP.getName();
				else winners = winners + ", " + gP.getName();
				gP.getPlayer().teleport(spectateLocation);
			case gameover: case joining: case lobby: case lobbyPlayer:
				willingPlayers++; //Increase willing players
				gP.setState(PlayerState.lobbyPlayer);
				break;
			case lobbySpectator: case spectating:
				gP.setState(PlayerState.lobbySpectator);
				break;
			}
			gP.resetPlayer();
		}
		
		for (Entity e : _.getWorld().getEntities()) {
			if (!(e instanceof Player)) {
				Location loc = e.getLocation();
				if (mainRegion.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
					e.remove();
				}
			}
		}
		
		if (winners != null) {
			_.broadcast(gm, "Victory! The following players were able to defend the cake: " + winners);
		} else {
			_.broadcast(gm, "Oh no! The players have lost the cake, the mobs win!");
		}
		
		if (willingPlayers > 1) { //Check if enough players for another round 
			lobbyCountdown(1);
		} else {
			broadcastToPlayers("There are not enough players.. You need " + (5 - willingPlayers) + " more player(s).");
			state = GameState.lobby;
		}
		
		//Set game to null
		game = null;
	}
	
	/**
	 * Reset all players
	 */
	public void resetAllPlayers() {
		for (GamePlayer gP : getPlayers()) {
			gP.resetPlayer();
			if (gP.getState() == PlayerState.playing) {
				gP.getPlayer().teleport(spectateLocation);
			}
		}
	}
	
	/**
	 * A player has died
	 * @param gP The player
	 */
	public void onPlayerDies(GamePlayer gP, PlayerDeathEvent event) {
		//Check if correct state
		if (state != GameState.playing) return;
		if (gP.getState() != PlayerState.playing) return;
		if (game == null) return;
		
		if (!game.getRoutine().hasPlayerDrops()) {
			event.getDrops().clear();
		}
		
		//Game over
		gP.setState(PlayerState.gameover);
		broadcastToPlayers(gP.getName() + " has died!");
		
		//Shut down if no players left
		int players = countPlayers(new PlayerState[]{PlayerState.playing});
		if (players < 1) game.shutdown();
		else {
			//TODO There belongs something here but what
		}
		
	}
	
	/*
	 * Spawner methods
	 */
	/**
	 * Open a random number of spawners
	 * @param mobTypes The number of different mobs (minimum of spawners that will open)
	 */
	public void openSpawners(int mobTypes) {
		int mobs;
		if (mobTypes >= 12) {
			//Open all spawners
			mobs = 12;
		} else {
			//Calculate how many spawners should be opened
			int max = 12 - mobTypes;
			mobs = _.getRandomInt(max) + mobTypes;
		}
		int count = 0;
		while (count < mobs && closedSpawnersSize > 0) {
			//Get a random closed spawner
			int random = _.getRandomInt(closedSpawnersSize);
			SavedRegion sr = closedSpawners.get(random);
			
			//Move to other list
			closedSpawners.remove(random);
			openSpawners.add(sr);
			
			//Open the spawners
			sr.setRegionToMaterial(Material.AIR);
			
			//Process count changes
			closedSpawnersSize--;
			openSpawnersSize++;
			count++;
		}
	}
	
	/**
	 * Close all open spawners
	 */
	public void closeSpawners() {
		for (SavedRegion sr : openSpawners) {
			closedSpawners.add(sr);
			sr.setRegionToMaterial(Material.SNOW_BLOCK);
		}
		openSpawners.clear();
		openSpawnersSize = 0;
		closedSpawnersSize = 12;
	}
	
	/**
	 * Get a random location for a mob to spawn
	 * @return The location or null if there are no open spawners
	 */
	public Location getSpawnLocation() {
		if (openSpawnersSize < 1) return null; //Check if there are any open spawners
		
		//Get a random spawners
		int random = _.getRandomInt(openSpawnersSize);
		ArrayList<Block> blocks = openSpawners.get(random).getBlocks();
		
		Location loc = null;
		switch(random) { //Get one of the 4 blocks in the ArrayList
		case 0: case 4: case 8:
			loc = blocks.get(0).getLocation();
			break;
		case 1: case 5: case 9:
			loc = blocks.get(1).getLocation();
			break;
		case 2: case 6: case 10:
			loc = blocks.get(2).getLocation();
			break;
		case 3: case 7: case 11:
			loc = blocks.get(3).getLocation();
			break;
		default: 
			return null;
		}
		//Return the location - Add 0.5 to be more in the middle
		return loc.add(0.5, 0.5, 0.5);
	}
	
	/*
	 * Other
	 */
	
	/**
	 * Event called when a mob has dies that does not drop items
	 * @param id
	 */
	public void mobDies(UUID id) {
		if (game == null) return;
		game.mobDies(id);
	}

	/**
	 * Event called when a mob dies
	 * @param id
	 * @param event
	 */
	public void mobDies(UUID id, EntityDeathEvent event) {
		if (game == null) return;
		game.mobDies(id, event);
	}
	
	/**
	 * A slime spawns
	 * @param e The slime
	 */
	public void slimeSpawns(Entity e) {
		if (game == null) return;
		Location loc = e.getLocation();
		if (mainRegion.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
			game.spawnedMobs.put(e.getUniqueId().toString(), (LivingEntity) e);
		}
	}
	
	/**
	 * Get the lava region / dead region
	 * @return the region
	 */
	public ProtectedRegion getLavaRegion() {
		return lavaRegion;
	}
	
	/*
	 * Overrides 
	 */
	
	@Override
	public void playerQuit(GamePlayer gP) {
		players.remove(gP.getName()); //Remove from list
		gP.playerQuitGame(); //Reset player
		if (game == null) return; //If no game is running, do nothing
		
		switch (gP.getState()) {
		case playing: //If the player is playing the game
			broadcastToPlayers(gP.getName() + " has left the game!"); //Broadcast
			int stillPlaying = countPlayers(new PlayerState[] {PlayerState.playing});
			if (stillPlaying < 1) { //Count players left
				game.shutdown(); //If 0 shutdown
			}
			break;
		default:
			//Nothing really
		}
	}

	@Override
	public GamePlayer playerJoins(Player p) {
		if (!enabled || state == GameState.enabled || state == GameState.disabled) {
			_.badMsg(p, "This game is currently not activated.");
			return null;
		}
		GamePlayer gP = new GamePlayer(p, this);
		gP.resetPlayer();
		_.getPlayerController().addPlayer(gP);
		gP.setState(PlayerState.joining);
		
		switch (state) {			
		case lobbyJoining: case finished: case starting: case lobby: case playing: 
			gP.getPlayer().teleport(spectateLocation);
			gP.setState(PlayerState.lobbyPlayer);
			break;
		default:
			_.badMsg(gP.getPlayer(), "You cannot join this game at this moment.");
			return null;
		}
		
		broadcastToPlayers(gP.getName() + " has joined Cake Defence!");
		players.put(gP.getName(), gP);
		
		if (state == GameState.lobby) {
			int willingPlayers = countPlayers(new PlayerState[] {PlayerState.lobby, PlayerState.lobbyPlayer, PlayerState.joining, PlayerState.gameover, PlayerState.playing});
			if (willingPlayers > 1) {
				lobbyCountdown(1);
			} else {
				broadcastToPlayers("You need " + (5 - willingPlayers) + " more player(s) for the game to start!");
			}
		} else if (state == GameState.playing) {
			giveSpectateKit(gP);
		}
		return gP;
	}

	@Override
	public GamePlayer playerJoinsSpectate(Player p) {
		if (!enabled || state == GameState.enabled || state == GameState.disabled) {
			_.badMsg(p, "This game is currently not activated.");
			return null;
		}
		GamePlayer gP = new GamePlayer(p, this);
		gP.resetPlayer();
		_.getPlayerController().addPlayer(gP);
		gP.setState(PlayerState.spectating);
		gP.getPlayer().teleport(spectateLocation);
		spectateMessage(gP);
		giveSpectateKit(gP);
		return null;
	}

	private ItemStack spectateBow;
	private ItemStack spectateArrows;
	
	/**
	 * Give the player the spectate kit (Bow & Arrow)
	 */
	public void giveSpectateKit(GamePlayer gP) {
		if (spectateBow == null || spectateArrows == null) {
			//Create bow
			spectateBow = new ItemStack(Material.BOW);
			spectateBow.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
			spectateBow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
			ItemMeta meta = spectateBow.getItemMeta();
			meta.setDisplayName("Cake Defence Bow");
			spectateBow.setItemMeta(meta);
			
			//Arrows
			spectateArrows = new ItemStack(Material.ARROW, 64);
		}
		
		//Add stuff to inventory
		gP.getPlayer().getInventory().addItem(spectateBow, spectateArrows);
	}
	
	@Override
	public Location getLobby() {
		return spectateLocation;
	}
	
	@Override
	protected void handleTab() {
		return;
	}
	

	
	public void getAliveMobs(Player p) {
		for (LivingEntity e : game.spawnedMobs.values()) {
			System.out.println(e.getType().toString());
		}
	}
	
}
