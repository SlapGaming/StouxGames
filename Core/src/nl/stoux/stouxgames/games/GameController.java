package nl.stoux.stouxgames.games;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class GameController {

	private HashMap<GameMode, AbstractGame> games;
	private SimpleDateFormat timeFormat;
	
	public GameController() {
		games = new HashMap<>();
		timeFormat = new SimpleDateFormat("mm:ss:SSS");
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
	
	/**
	 * Get a String of the passed time. Format: [Minutes]:[Seconds]:[Milliseconds]
	 * @param startTime The starting time
	 * @param endTime The end time
	 * @return The string
	 */
	public String getTimeString(long startTime, long endTime) {
		return getTimeString(endTime - startTime);
	}
	
	/**
	 * Get a String of the passed time. Format: [Minutes]:[Seconds]:[Milliseconds]
	 * @param time The total time
	 * @return The string
	 */
	public String getTimeString(long time) {
		return timeFormat.format(new Date(time));
	}
	
	
}
