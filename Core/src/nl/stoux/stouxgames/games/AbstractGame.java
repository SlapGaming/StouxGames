package nl.stoux.stouxgames.games;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.mcsg.double0negative.tabapi.TabAPI;

import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerController;
import nl.stoux.stouxgames.player.PlayerState;
import nl.stoux.stouxgames.storage.YamlStorage;
import nl.stoux.stouxgames.util._;

public abstract class AbstractGame {

	//Event Handler
	public DefaultEventHandler Events;
	
	//Setup info
	protected YamlStorage yaml;
	protected FileConfiguration config;
	protected boolean enabled;
	
	//Game Info
	protected GameMode gm;
	protected GameState state;
	protected boolean automaticRun;
	
	//Players
	protected HashMap<String, GamePlayer> players;
	
	/**
	 * Make a new Game
	 * @param gm the modus
	 */
	public AbstractGame(GameMode gm) {
		this.gm = gm;
		state = GameState.disabled;
		automaticRun = false;
		players = new HashMap<>();
		enabled = false;
	}
	
	/**
	 * Reload this games config
	 */
	public void reloadConfig() {
		yaml.reloadConfig();
	}
	
	/**
	 * Get the gamemode
	 * @return gamemode
	 */
	public GameMode getGamemode() {
		return gm;
	}
	
	/**
	 * Get the game's state
	 * @return the state
	 */
	public GameState getGameState() {
		return state;
	}
	
	/**
	 * Get all the players in this game
	 * @return the players
	 */
	public Collection<GamePlayer> getPlayers() {
		return players.values();
	}
	
	/**
	 * Handle the Tab
	 */
	protected abstract void handleTab();
	
	/**
	 * Update the tab for all players
	 */
	protected void updateTabPlayers() {
		for (GamePlayer gP : players.values()) {
			TabAPI.updatePlayer(gP.getPlayer());
		}
	}
	
	/**
	 * Send a message to all the players in this game
	 * @param message the message
	 */
	public void broadcastToPlayers(String message) {
		String msg = ChatColor.GREEN + "[" + gm + "] " + ChatColor.WHITE + message;
		for (GamePlayer gP : players.values()) {
			gP.getPlayer().sendMessage(msg);
		}
	}
	
	/**
	 * Initialize the Event handler
	 * @param handler the handler
	 */
	protected void initializeEventHandler(DefaultEventHandler handler) {
		this.Events = handler;
	}
	
	/*
	 * 
	 * Game related
	 * 
	 */
	
	/**
	 * Get the number of players with a certain state(s)
	 * @param states The Playerstate(s)
	 * @return Number of players
	 */
	protected int countPlayers(PlayerState[] states) {
		int nrPlayers = 0;
		for (GamePlayer gP : players.values()) {
			for (PlayerState state : states) {
				if (gP.getState() == state) {
					nrPlayers++;
				}
			}
		}
		return nrPlayers;
	}
	
	/**
	 * Send a message to the player that he is currently spectate
	 * @param gP The player
	 */
	protected void spectateMessage(GamePlayer gP) {
		_.msg(gP.getPlayer(), gm, "You have joined this game in spectating mode!");
		_.msg(gP.getPlayer(), gm, "Leave the game (" + ChatColor.AQUA + "/leave" + ChatColor.WHITE +") and step on the 'play' pressure plate to join the game as player.");
	}
	
	/**
	 * Remove all the players
	 */
	protected void removeAllPlayers() {
		PlayerController playerController = _.getPlayerController(); //Get the controller
		for (GamePlayer gP : players.values()) { //Loop thru players
			playerController.removePlayer(gP.getName());
			gP.playerQuitGame();
		}
		players.clear();
	}
	
	/**
	 * Setup the game
	 */
	protected abstract void setupGame();
	
	/**
	 * Disable the game
	 */
	public abstract void disableGame();
	
	/**
	 * Reload the game
	 */
	public void reloadGame() {
		disableGame();
		yaml.reloadConfig();
		setupGame();
	}
	
	/**
	 * Called when a player quits the game
	 * @param gP The quitting player
	 */
	public abstract void playerQuit(GamePlayer gP);	
	
	/**
	 * Called when a player joins the game (as a Player)
	 * @param p The player
	 * @return The GamePlayer or null if failed
	 */
	public abstract GamePlayer playerJoins(Player p);
	
	/**
	 * Called when a player joins the game (as specatator)
	 * @param p
	 * @return
	 */
	public abstract GamePlayer playerJoinsSpectate(Player p);
	
	/**
	 * Get the lobby and/or spectate location
	 * @return the location
	 */
	public abstract Location getLobby();
}
