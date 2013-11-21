package nl.stoux.stouxgames.commands;

import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.commands.exception.Message;
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
