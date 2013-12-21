package nl.stoux.stouxgames.commands;

import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.commands.exception.Message;
import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractCommand {

	protected CommandSender sender;
	protected String[] args;
	
	public AbstractCommand(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	/**
	 * Get a game player
	 * @return the gameplayer
	 * @throws CommandException if not player found
	 */
	public GamePlayer getGamePlayer() throws CommandException {
		GamePlayer gp = _.getPlayerController().getGamePlayer(sender.getName());
		if (gp == null) throw new CommandException(Message.notInAGame);
		return gp;
	}
	
	/**
	 * This does all the standard player checks, that includes:
	 * - checkPermission(permission)
	 * - getPlayer()
	 * - checkWorld()
	 * - getGamePlayer()
	 * 
	 * @param permission The permission that needs to be checked
	 * @return The game player
	 * @throws CommandException if any of the checks fails
	 */
	public GamePlayer standardChecks(String permission) throws CommandException {
		//Player checks
		checkPermission(permission);
		Player p = getPlayer();
		checkWorld(p.getWorld());
		return getGamePlayer();
	}
	
	/**
	 * Check if the player is in the correct game
	 * Also checks if the game is not null
	 * @param gp The gameplayer
	 * @param correctGameMode The correct gamemode this player should be in
	 * @throws CommandException if in the wrong the game or game not running
	 */
	public void checkCorrectGame(GamePlayer gp, GameMode correctGameMode) throws CommandException {
		if (gp.getGame().getGamemode() != correctGameMode) {
			throw new CommandException("You can only do this command in " + correctGameMode.getName());
		}
	}
	
	/**
	 * Check if a game is running
	 * @param game The game
	 * @throws CommandException if not running
	 */
	public void isGameRunning(AbstractGame game) throws CommandException {
		if (game == null) throw new CommandException(Message.gameNotRunning);
	}
	
	/**
	 * Handle the command
	 * @return succesfully handled
	 */
	abstract public boolean handle() throws CommandException;
	
	/**
	 * Get the player based on the CommandSender
	 * @return The player
	 * @throws CommandException if the sender is not a player
	 */
	protected Player getPlayer() throws CommandException {
		if (sender instanceof Player) {
			return (Player) sender; 
		} else {
			throw new CommandException(Message.notInMinecraft);
		}
	}
	
	/**
	 * Check if in the correct world
	 * @param loc the location
	 * @throws CommandException if incorrect world
	 */
	protected void checkWorld(Location loc) throws CommandException {
		checkWorld(loc.getWorld());
	}
	
	/**
	 * Check if in the correct world
	 * @param w the world
	 * @throws CommandException if incorrect world
	 */
	protected void checkWorld(World w) throws CommandException {
		if (w != _.getWorld()) {
			throw new CommandException(Message.incorrectWorld);
		}
	}
	
	protected void checkPermission(String perm) throws CommandException {
		if (!_.testPermission(sender, perm)) throw new CommandException(Message.noPermission);
	}
}
