package nl.stoux.stouxgames.games.tntrun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.GameState;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerState;
import nl.stoux.stouxgames.storage.SavedRegion;
import nl.stoux.stouxgames.storage.YamlStorage;
import nl.stoux.stouxgames.util._;
import nl.stoux.stouxgames.util._T;

public class TNTRun extends AbstractGame {

	//The private TNTRun this
	final private TNTRun tntRun = this;
	
	//Countdown tasks
	private BukkitTask startCountdown;
	
	//Floors have TNT
	private boolean hasTNT;
	
	//Floor 1
	private SavedRegion floor1;
	private ProtectedRegion floor1Region;
	private ArrayList<Block> floor1Blocks;
	
	//Floor 2
	private SavedRegion floor2;
	private ProtectedRegion floor2Region;
	private ArrayList<Block> floor2Blocks;
	
	//Floor 3
	private SavedRegion floor3;
	private ProtectedRegion floor3Region;
	private ArrayList<Block> floor3Blocks;
	
	//Gameover region
	private ProtectedRegion gameoverRegion;
	
	//Lobby location
	private Location lobby;
	
	//BlockRemover
	private BukkitTask blockRemover;
	private TNTRunBlockTask blockRemoverTask;
	
	public TNTRun() {
		super(GameMode.TNTRun);
		yaml = new YamlStorage("TNTRun");
		config = yaml.getConfig();
		setupGame();
		initializeEventHandler(new TNTRunHandler(this));
	}
	
	/**
	 * Initialize the game
	 */
	private void setupGame() {
		enabled = false;
		state = GameState.disabled;
		
		//Get the region manager
		RegionManager rm = _.getWorldGuard().getRegionManager(_.getWorld());
		
		//Get stuff out the config
		if (!getRegion(1, "floor1", rm) || !getRegion(2, "floor2", rm) || !getRegion(3, "floor3", rm) || !getRegion(4, "gameover", rm)) {
			//If a region has not been found
			_.log(Level.SEVERE, gm, "Disabled.");
			return;
			
		}
		
		//Check for TNT blocks
		hasTNT = config.getBoolean("tnt");
		
		//Get Lobby teleport
		Vector lobbyVector = config.getVector("lobby");
		if (lobbyVector == null) {
			_.log(Level.SEVERE, gm, "Config faulty. Lobby vector is missing. Has been set to games world spawn.");
			config.set("lobby", _.getWorld().getSpawnLocation().toVector());
			return;
		}
		lobby = lobbyVector.toLocation(_.getWorld());
		lobby.setYaw(180F);
		
		enabled = true;
		state = GameState.lobby;
		_.log(Level.INFO, gm, "Up & running.");
	}
	
	/**
	 * Initialize a Floor Region
	 * @param floor The floor (1 -3 | 4 = game over region)
	 * @param configPath The path in the config
	 * @param rm The world's regionmanager
	 * @return succesfully intialized
	 */
	private boolean getRegion(int floor, String configPath, RegionManager rm) {
		if (!config.contains(configPath)) { //Doesn't contain this entry
			_.log(Level.SEVERE, gm, "Config faulty. Missing path: " + configPath);
			return false;
		}
		String name = config.getString(configPath);
		if (name.equals("empty") || name == null) {
			_.log(Level.SEVERE, gm, "Config faulty. Incorrect path: " + configPath);
			return false;
		}	
		ProtectedRegion region = rm.getRegion(name);
		if (region == null) {
			_.log(Level.SEVERE, gm, "Config faulty. Missing region for: " + configPath);
			return false;
		}
		if (floor == 4) {
			gameoverRegion = region;
		} else {
			ArrayList<Block> blocks = _.getBlocksInRegion(name);
			if (blocks == null) {
				_.log(Level.SEVERE, gm, "Config faulty. Missing region for: " + configPath);
				return false;
			}
			switch (floor) {
			case 1:
				floor1 = new SavedRegion(blocks, name);
				floor1Region = region;
				floor1Blocks = blocks;
			case 2:
				floor2 = new SavedRegion(blocks, name);
				floor2Region = region;
				floor2Blocks = blocks;
			case 3:
				floor3 = new SavedRegion(blocks, name);
				floor3Region = region;
				floor3Blocks = blocks;
				break;
			}
		}
		return true;
	}

	
	/*
	 * Game events
	 */
	private void lobbyCountdown(int minutes) {
		state = GameState.lobbyJoining;
		String minutesString;
		if (minutes == 1) minutesString = " minute"; 
		else minutesString = " minutes";
		_.broadcast(gm, "A new game of TNTRun will start in " + minutes + minutesString + "! Do " + ChatColor.AQUA + "/spawn games" + ChatColor.WHITE + " and join TNTRun!"); //Send the message
		_T.runLater_Sync(new Runnable() {
			
			@Override
			public void run() {
				int nrPlayers = 0;
				for (GamePlayer gP : getPlayers()) {
					if (gP.getState() == PlayerState.lobbyPlayer) {
						nrPlayers++;
					}
				}
				if (nrPlayers > 2) {
					startGame();
				} else {
					state = GameState.lobby;
					broadcastToPlayers("Not enough players to start the game.");
				}
			}
		}, minutes * 60 * 20);
		
	}
	
	private void startGame() {
		_.broadcast(gm, "A new game of TNTRun is starting!");
		state = GameState.starting;
		
		//Restore floors, just in case
		restoreFloors();
		
		startCountdown = _T.runTimer_Sync(new Runnable() {
			
			private int seconds = 3;
			private boolean hardcore = false;
			private HashSet<Block> startBlocks;
			
			@Override
			public void run() {
				switch (seconds) {
				case 3:
					broadcastToPlayers("TNTRun is starting in 3 seconds!");
					int nrBlocks = floor1Blocks.size();  //Get nr of blocks in floor
					startBlocks = new HashSet<>(); //Make new hashset
					for (GamePlayer gP : getPlayers()) {
						Block chosenBlock = floor1Blocks.get(_.getRandomInt(nrBlocks));
						startBlocks.add(chosenBlock);
						gP.getPlayer().teleport(chosenBlock.getLocation().add(0, 1.5, 0)); //Teleport to random block
						gP.setState(PlayerState.playing); //Set player state
						gP.resetPlayer(); //Reset their stats
					}
					handleTab();
					break;
				case 2:
					broadcastToPlayers("TNTRun is starting in 2 seconds!");
					break;
				case 1:
					if (_.getRandomInt(20) == 8) { //5% chance for hardcore mode
						hardcore = true;
						broadcastToPlayers("TNTRun is starting in 1 seconds! Hardcore mode activated...");
					} else {
						broadcastToPlayers("TNTRun is starting in 1 seconds!");
					}
					break;
				case 0:
					blockRemoverTask = new TNTRunBlockTask(tntRun, hasTNT);
					blockRemover = _T.runTimer_Sync(blockRemoverTask, 0, 1); //start the task
					broadcastToPlayers("TNTRun has started! Start running!!");
					for (Block b : startBlocks) {
						blockRemoverTask.addBlock(b);
					}
					if (hardcore) { //If hardcore mode give everyone blindness
						for (GamePlayer gP : getPlayers()) {
							if (gP.getState() == PlayerState.playing) {
								_.giveInfinitePotion(gP.getPlayer(), PotionEffectType.BLINDNESS);
							}
						}
					}
					_T.runLater_Sync(new Runnable() {
						
						@Override
						public void run() {
							state = GameState.playing;
							checkRemainingPlayers();
						}
					}, 1);
					handleTab();
					startCountdown.cancel();
					break;
				default:
					startCountdown.cancel();
				}
				seconds--;
			}
		}, 5, 20);		
	}
	
	private void gameEnded() {
		state = GameState.finished;
		restoreFloors();
		int willingToPlay = 0; //Check howmany players are willing to play & set their state to correct one
		for (GamePlayer player : players.values()) {
			switch (player.getState()) {
			case gameover: case playing: case lobbyPlayer: case lobby: case joining:
				willingToPlay++;
				player.setState(PlayerState.lobbyPlayer);
				break;
			case lobbySpectator: case spectating:
				player.setState(PlayerState.lobbySpectator);
				break;
			}
			player.resetPlayer();
		}
		if (startCountdown instanceof BukkitTask) {
			startCountdown.cancel();
		}
		if (blockRemover instanceof BukkitTask) {
			blockRemover.cancel();
		}
		if (willingToPlay > 2) {
			state = GameState.lobbyJoining;
			lobbyCountdown(1);
		} else {
			state = GameState.lobby;
		}
		handleTab();
	}
	
	/**
	 * Called when a player has won
	 * @param gP The winning player
	 */
	private void playerWon(GamePlayer gP) {
		_.broadcast(gm, gP.getName() + " has won TNTRun!");
		gP.getPlayer().teleport(lobby);
		gameEnded();
	}
	
	/**
	 * Player died
	 * Player -> Game over
	 * @param gP The player that died
	 */
	private void playerDied(GamePlayer gP) {
		gP.setState(PlayerState.gameover);
		gP.resetPlayer();
		gP.getPlayer().teleport(lobby);
		blockRemoverTask.playerDied(gP.getPlayer());
		checkRemainingPlayers();
	}
	
	/*
	 * Game methods
	 */
	/**
	 * Check the remaining players -> Take actions
	 */
	private void checkRemainingPlayers() {
		int playersLeft = 0; GamePlayer winningPlayer = null;
		for (GamePlayer gP : getPlayers()) {
			if (gP.getState() == PlayerState.playing) { //Check for playing players
				playersLeft++; //Increase players left
				winningPlayer = gP; //Set that player as winner/Prevents double looping
			}
		}
		if (playersLeft == 1) { //Somone won
			playerWon(winningPlayer);
		} else if (playersLeft < 1) { //Something went wrong here..
			gameEnded();
		} else { //Else player died, just handle the tab
			handleTab();
		}
		
	}
	
	/**
	 * Restore the 3 floors to their former glory
	 */
	private void restoreFloors() {
		floor1.restore();
		floor2.restore();
		floor3.restore();
		if (hasTNT) {
			tntUnderBlocks(floor1Blocks);
			tntUnderBlocks(floor2Blocks);
			tntUnderBlocks(floor3Blocks);
		}
	}
	
	/**
	 * Add TNT under all the blocks
	 * @param blocks
	 */
	private void tntUnderBlocks(ArrayList<Block> blocks) {
		for (Block b : blocks) {
			b.getRelative(BlockFace.DOWN).setType(Material.TNT);
		}
	}
	
	/*
	 * Listener events
	 */
	public void onMove(GamePlayer gP, PlayerMoveEvent event) {
		if (state != GameState.playing || gP.getState() != PlayerState.playing) { //Check if the game is running & the player in question is playing
			return;
		}
		
		blockRemoverTask.playerMoved(gP.getPlayer());
		
		Location from = event.getFrom();
		Location to = event.getTo();
		
		//Check if the player has actually moved
		if (!_.hasMoved(from, to)) {
			return;
		}
		
		//Get the block
		Block underlyingBlock = from.add(0, -1, 0).getBlock();
		if (underlyingBlock.getType() == Material.AIR) { //Check if not air
			return;
		}
		Location blockLocation = underlyingBlock.getLocation();
		int blockX = blockLocation.getBlockX();
		int blockY = blockLocation.getBlockY();
		int blockZ = blockLocation.getBlockZ();
		
		//Check if the block is one of the layers
		if (containsBlock(blockX, blockY, blockZ)) {
			blockRemoverTask.addBlock(underlyingBlock);
		} else { //Check if player died
			Location toLoc = to;
			if (gameoverRegion.contains(toLoc.getBlockX(), toLoc.getBlockY(), toLoc.getBlockZ())) { //Check if in gameover region
				//Player has died
				playerDied(gP);
			}
		}
	}
	
	/**
	 * Check if a block (by it's x,y,z) is in a region
	 * @param x The block's X
	 * @param y The block's Y
	 * @param z The block's Z
	 * @return Is in the region
	 */
	protected boolean containsBlock(int x, int y, int z) {
		if (floor1Region.contains(x, y, z)) {
			return true;
		} else if (floor2Region.contains(x, y, z)) {
			return true;
		} else if (floor3Region.contains(x, y, z)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	/*
	 * Player events
	 */
	@Override
	public void playerQuit(GamePlayer gP) {
		players.remove(gP.getName());
		gP.playerQuitGame();
		switch(state) {
		case starting:
			startCountdown.cancel();
			//DO NOT BREAK 
		case playing: //Player gets removed from the game
			players.remove(gP.getName());
			broadcastToPlayers(gP.getName() + " has left the game!");
			blockRemoverTask.playerDied(gP.getPlayer());
			checkRemainingPlayers();
			break;
		default: //Probably a spectator
			handleTab();
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
		playerJoined(gP);
		return gP;
	}
	
	/**
	 * Player has joined the game
	 * @param gP The player
	 */
	private void playerJoined(GamePlayer gP) {
		gP.setState(PlayerState.joining);
		switch (state) {
		case finished: case lobbyJoining: case lobby: case playing:  case starting:
			gP.getPlayer().teleport(lobby);
			gP.setState(PlayerState.lobbyPlayer);
			break;
		default:
			_.badMsg(gP.getPlayer(), "You cannot join this game at this moment.");
			return;
		}
		
		players.put(gP.getName(), gP);
		broadcastToPlayers(gP.getName() + " has joined TNTRun!");
		
		gP.setState(PlayerState.lobbyPlayer);
		
		if (state == GameState.lobby) {
			//Check number of players
			int nrPlayers = countPlayers(new PlayerState[]{PlayerState.lobbyPlayer});
			if (nrPlayers > 2) { //Check if there are plenty of players to start a new game
				lobbyCountdown(1);
			} else {
				broadcastToPlayers("You need " + (3 - nrPlayers) + " more player(s) for the game to start!");
			}
		}
		handleTab();
	}

	@Override
	public GamePlayer playerJoinsSpectate(Player p) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	/*
	 * Tab
	 */
	@Override
	protected void handleTab() {
		// TODO Auto-generated method stub

	}

}
