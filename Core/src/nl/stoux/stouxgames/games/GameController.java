package nl.stoux.stouxgames.games;

import java.util.Collection;
import java.util.HashMap;

public class GameController {

	private HashMap<GameMode, AbstractGame> games;
	
	public GameController() {
		games = new HashMap<>();
	}
	
	/**
	 * Put a game in the collection
	 * @param game The game
	 */
	public void addGame(AbstractGame game) {
		games.put(game.getGamemode(), game);
	}
	
	/**
	 * Get a game based on its gamemode
	 * @param gamemode The gamemode
	 */
	public AbstractGame getGame(GameMode gamemode) {
		return games.get(gamemode);
	}
	
	/**
	 * Get all the registered games
	 * @return the games
	 */
	public Collection<AbstractGame> getGames() {
		return games.values();
	}
	
	
}
