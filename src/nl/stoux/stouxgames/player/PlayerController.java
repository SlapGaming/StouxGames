package nl.stoux.stouxgames.player;

import java.util.HashMap;

public class PlayerController {

	private HashMap<String, GamePlayer> gamePlayers;
	
	/**
	 * Constructor
	 */
	public PlayerController() {
		gamePlayers = new HashMap<>();
	}
	
	/**
	 * Check of a player in a game is
	 * @param playername the player
	 * @return in game
	 */
	public boolean isGamePlayer(String playername) {
		return (gamePlayers.get(playername) != null);
	}
	
	/**
	 * Get a GamePlayer
	 * @param playername the player's name
	 * @return The gameplayer or null
	 */
	public GamePlayer getGamePlayer(String playername) {
		return gamePlayers.get(playername);
	}
	
	/**
	 * Add a player
	 * @param player
	 */
	public void addPlayer(GamePlayer player) {
		gamePlayers.put(player.getPlayer().getName(), player);
	}
	
	/**
	 * Remove a player
	 * @param player the player's name
	 */
	public void removePlayer(String player) {
		gamePlayers.remove(player);
	}
	
	
}
