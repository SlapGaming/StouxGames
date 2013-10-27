package nl.stoux.stouxgames.games.spleef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

import nl.stoux.stouxgames.StouxGames;
import nl.stoux.stouxgames.external.TabControl;
import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.GameState;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerState;
import nl.stoux.stouxgames.storage.SavedRegion;
import nl.stoux.stouxgames.storage.YamlStorage;
import nl.stoux.stouxgames.util._;
import nl.stoux.stouxgames.util._T;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.mcsg.double0negative.tabapi.TabAPI;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;


public class Spleef extends AbstractGame {
			
	//Playground
	private SavedRegion playground;
	private HashMap<Location, Block> destroyBlocks;
	private ArrayList<Block> blocks;
	private Material[] floorTypes;
	private PotionEffectType[] potionTypes;
	
	//Floor
	private ProtectedRegion groundRegion;
	
	//Lobby
	private Location lobby;
		
	//Tasks
	private BukkitTask startingTask;
	
	public Spleef() {
		super(GameMode.Spleef);
		yaml = new YamlStorage(/*"/nl/stoux/stouxgames/games/spleef/", */"Spleef");
		config = yaml.getConfig();
		setupGame();
		initializeEventHandler(new SpleefHandler(this));
	}
	
	/**
	 * Setup the game/area
	 */
	public void setupGame() {
		enabled = false;
		state = GameState.disabled;
		destroyBlocks = new HashMap<>();
		blocks = null;
		
		//Get Stuff from Config
		String regionname = config.getString("region");
		String groundname = config.getString("ground");
		Vector vectorLoc = config.getVector("lobby");
		
		//Check if correct YML
		if (regionname == null || regionname.equals("empty") || groundname == null || groundname.equals("empty") || vectorLoc == null) { 
			//Reset values
			config.set("lobby", _.getWorld().getSpawnLocation().toVector());
			yaml.saveConfig();
			_.log(Level.SEVERE, gm, "Spleef YML not setup. Spleef is disabled.");
			return;
		}
		
		//Get ground region
		groundRegion = _.getWorldGuard().getRegionManager(_.getWorld()).getRegion(groundname);
		if (groundRegion == null) {
			_.log(Level.SEVERE, gm, "Spleef ground regionname has not been found in world: " + _.getWorld().getName());
			return;
		}
		
		
		//Get playground region
		blocks = _.getBlocksInRegion(regionname);
		if (blocks == null) {
			_.log(Level.SEVERE, gm, "Spleef regionname has not been found in world: " + _.getWorld().getName());
			return;
		}
		
		//Fill Playground info
		playground = new SavedRegion(blocks, regionname); //Save the region
		lobby = vectorLoc.toLocation(_.getWorld()); //Make lobby spawn location
		lobby.setYaw(90F);
		for (Block b : blocks) {
			destroyBlocks.put(b.getLocation(), b);
		}
		
		//Floor types
		floorTypes = new Material[]{
				Material.ICE,
				Material.SNOW_BLOCK,
				Material.QUARTZ_BLOCK,
				Material.GLASS,
				Material.FENCE,
				Material.LEAVES,
				Material.ENCHANTMENT_TABLE,
				Material.SOUL_SAND
		};
		
		//Potion effects
		potionTypes = new PotionEffectType[]{
				PotionEffectType.BLINDNESS,
				PotionEffectType.CONFUSION,
				PotionEffectType.SPEED,
				PotionEffectType.SLOW
		};
		
		enabled = true;
		
		state = GameState.lobby;
		_.log(Level.INFO, gm, "Up & running.");
	}
	
	/**
	 * Reload the game
	 */
	public void reloadGame() {
		yaml.reloadConfig();
		setupGame();
	}
	
	/**
	 * See if the spleef arena is enabled
	 * @return is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	
	/*
	 * 
	 * Overides
	 * 
	 */
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
	
	@Override
	public GamePlayer playerJoinsSpectate(Player p) {
		if (!enabled || state == GameState.enabled || state == GameState.disabled) {
			_.badMsg(p, "This game is currently not activated.");
			return null;
		}
		GamePlayer gP = new GamePlayer(p, this);
		gP.resetPlayer();
		_.getPlayerController().addPlayer(gP);
		playerJoinedSpectate(gP);
		return gP;
	}
	
	@Override
	public void playerQuit(GamePlayer gP) {
		players.remove(gP.getName());
		gP.playerQuitGame();
		switch(state) {
		case starting:
			startingTask.cancel();
			//DO NOT BREAK 
		case playing: //Player gets removed from the game
			players.remove(gP.getName());
			broadcastToPlayers(gP.getName() + " has left the game!");
			checkRemainingPlayers();
			break;
		default: //Probably a spectator
			handleTab();
		}
	}
	
	
	/*
	 * 
	 * Game stuff
	 * 
	 */
	
	/**
	 * Called when a player 'dies' (hits the floor)
	 * @param p The player
	 */
	public void playerDied(GamePlayer p) {
		//Set new player state
		p.setState(PlayerState.gameover);
				
		//Reset
		p.resetPlayer();
		
		//Teleport back to Lobby
		p.getPlayer().teleport(lobby);
		
		//Check for remaining players
		checkRemainingPlayers();
	}
	
	/**
	 * Check for remaining players
	 */
	public void checkRemainingPlayers() {
		int playersLeft = 0; String playerWon = "";
		for (GamePlayer gP : players.values()) {
			if (gP.getState() == PlayerState.playing) {
				playersLeft++;
				playerWon = gP.getName();
			}
		}
		if (playersLeft == 1) { //Only one player left -> Wins
			gameWon(players.get(playerWon));
		} else if (playersLeft < 1) {
			//Something went wrong here..
			gameEnded();
		} else {
			handleTab();
		}
	}
	
	public void lobbyCountdown(int minutes) {
		state = GameState.lobbyJoining;
		String minutesString;
		if (minutes == 1) minutesString = " minute"; 
		else minutesString = " minutes";
		_.broadcast(gm, "A new game of spleef will start in " + minutes + minutesString + "! Do " + ChatColor.AQUA + "/spawn games" + ChatColor.WHITE + " and join spleef!"); //Send the message
		for (GamePlayer gP : players.values()) {
			if (gP.getState() == PlayerState.lobbySpectator || gP.getState() == PlayerState.spectating) {
				
			}
		}
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
					broadcastToPlayers("Not enough players to start the game.");
				}
			}
		}, minutes * 60 * 20);
		
	}
	
	/**
	 * Start the game
	 */
	public void startGame() {
		_.broadcast(gm, "A new game of Spleef is starting!");
		state = GameState.starting;		
		
		playground.setRegionToMaterial(floorTypes[_.getRandomInt(floorTypes.length)]); //Set the floor
		
		startingTask = _T.runTimer_Sync(new Runnable() {
			
			private int countdown = 3;
			private boolean potionBoost = false;
			
			@Override
			public void run() {
				switch(countdown) { //Start counting down
				case 3:
					int nrOfBlocks = blocks.size();
					for (GamePlayer gP : players.values()) {
						gP.getPlayer().teleport(blocks.get(_.getRandomInt(nrOfBlocks)).getLocation().add(0.5, 1.5, 0.5)); //Teleport -> Field
						gP.setState(PlayerState.playing); //Set playing state
					}
					broadcastToPlayers("Spleef is starting in 3 seconds!");
					for (GamePlayer player : players.values()) {
						player.resetPlayer();
					}
					handleTab(); //Update the Tab
					break;
				case 2:
					broadcastToPlayers("Spleef is starting in 2 seconds!");
					break;
				case 1:
					if (_.getRandomInt(5) == 4) { //20% Chance
						potionBoost = true;
						broadcastToPlayers("Spleef is starting in 1 second! Bonus potion activated!");
					} else {
						broadcastToPlayers("Spleef is starting in 1 second!");
					}
					break;
				case 0:
					if (potionBoost) { //20% Chance
						PotionEffectType potionType = potionTypes[_.getRandomInt(potionTypes.length)];
						for (GamePlayer gP : players.values()) {
							if (gP.getState() == PlayerState.playing) {
								_.giveInfinitePotion(gP.getPlayer(), potionType);
							}
						}
					}
					broadcastToPlayers("Spleef has started! Goodluck!");
					state = GameState.playing;
					checkRemainingPlayers();
					startingTask.cancel();
					break;
				default:
					startingTask.cancel();
				}
				countdown--;
			}
		}, 0, 20);
	}
	
	/**
	 * Run when the game has ended
	 */
	public void gameEnded() {
		playground.restore();
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
		startingTask.cancel();
		if (willingToPlay > 2) {
			state = GameState.lobbyJoining;
			lobbyCountdown(1);
		} else {
			state = GameState.lobby;
		}
		handleTab();
	}
	
	/**
	 * A player has won the game
	 * @param gP The winning player
	 */
	public void gameWon(GamePlayer gP) {
		state = GameState.finished; //Set game state
		_.broadcast(gm, gP.getName() + " has won spleef!");
		gP.getPlayer().teleport(lobby);
		gameEnded();
	}
	
	
	/**
	 * Called when a player has joined the game
	 * @param gP The new player
	 */
	public void playerJoined(GamePlayer gP) {
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
		broadcastToPlayers(gP.getName() + " has joined spleef!");
		
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
	
	/**
	 * Teleport the player to the lobby
	 * @param gP The player
	 */
	public void playerJoinedSpectate(GamePlayer gP) {
		gP.setState(PlayerState.spectating);
		gP.getPlayer().teleport(lobby);
		spectateMessage(gP);
		handleTab();
	}	
	
	/*
	 * 
	 * Events
	 * 
	 */
	/**
	 * On interact
	 * @param gP The player who did it
	 * @param f The affected block
	 */
	public void onPlayerBlockInteract(GamePlayer gP, Block f) {
		if (gP == null || f == null) {
			return;
		}
		if (gP.getState() != PlayerState.playing) return; //Not playing
		if (state != GameState.playing) return;
		Location loc = f.getLocation(); //Get block's location
		Block b = destroyBlocks.get(loc); //Get the block
		if (b != null) {
			b.setType(Material.AIR); //Set -> Air
		}
	}
	
	/**
	 * On player move
	 * @param gP The player who moved
	 * @param event The event
	 */
	public void onPlayerMove(GamePlayer gP, PlayerMoveEvent event) {
		if (gP.getState() != PlayerState.playing) return; //Not playing
		if (state != GameState.playing && state != GameState.starting) return; //Stop if not playing
		
		//Check if actual move (Not just mouse moving)
		Location from = event.getFrom();
		Location to = event.getTo();
		
		if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) { //Has moved
			if (state == GameState.playing) {
				Block movedTo = to.getBlock();
				if (groundRegion.contains(movedTo.getX(), movedTo.getY(), movedTo.getZ())) {
					playerDied(gP); //if in ground region -> Dies
				}
			} else {
				if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	
	
	/*
	 * TabHandler
	 * 
	 * Javadoc:
	 * @see nl.stoux.stouxgames.games.AbstractGame#handleTab()
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	protected void handleTab() {
		if (!TabControl.isEnabled()) return; //Cancel if no TabAPI
				
		Collection<GamePlayer> gamePlayers = players.values(); //Players
		ArrayList<GamePlayer> playing = new ArrayList<>();
		ArrayList<GamePlayer> dead = new ArrayList<>();
		int spectators = 0;
		for (GamePlayer gP : gamePlayers) { //Make the 2 lists
			switch (gP.getState()) {
			case playing:
				playing.add(gP);
				break;
			case spectating:
				spectators++;
				break;
			case gameover:
				dead.add(gP);
				break;
			}
		}
		int nrPlayers = playing.size();
		int nrDead = dead.size();
		
		String playersHeader = ChatColor.GOLD + "   - Players -";
		String deadHeader = ChatColor.GOLD + " - Game Over -";
		StouxGames plugin = _.getPlugin();
		
		String playersString = (nrPlayers + nrDead) + " (" + nrDead + " dead)";
		
		for (GamePlayer gP : getPlayers()) {
			boolean nextPlayer = false;
			Player p = gP.getPlayer();
			TabControl.setTopTab(gP, this, playersString, spectators);
			int row = 6;
			int colom = 0;
			TabAPI.setTabString(plugin, p, 5, 1, playersHeader); //Players header
			
			for (GamePlayer playingPlayer : getPlayers()) {
				TabAPI.setTabString(plugin, p, row, colom, playingPlayer.getName()); //Set the string
				colom++;
				if (colom > 2) { //Check if colom end is reached
					colom = 0; //Reset colom
					row++; //Increase a row
					if (row > 19) { //If row greater then 19 (Outside tab)
						nextPlayer = true; //Go to next player
						break; //Break the for loop
					}
				}
			}
			row++; row++;
			if (nextPlayer == true || row > 17) { //Go to next player if needed
				continue;
			}
			if (nrDead > 0) {
				TabAPI.setTabString(plugin, p, row, 1, deadHeader); //Dead header
				for (GamePlayer deadPlayer : dead) { //Loop thru dead players
					TabAPI.setTabString(plugin, p, row, colom, ChatColor.STRIKETHROUGH + deadPlayer.getName()); //Set the string
					colom++;
					if (colom > 2) { //Check if colom end is reached
						colom = 0; //Reset colom
						row++; //Increase a row
						if (row > 19) { //If row greater then 19 (Outside tab)
							nextPlayer = true; //Go to next player
							break; //Break the for loop
						}
					}
				}
			}
		}
		
		updateTabPlayers();
	}
	
	
	
	
}
