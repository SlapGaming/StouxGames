package nl.stoux.stouxgames.commands;

import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;

import org.bukkit.command.CommandSender;

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
	 */
	public GamePlayer getGamePlayer() {
		return _.getPlayerController().getGamePlayer(sender.getName());
	}
	
	/**
	 * Handle the command
	 * @return succesfully handled
	 */
	abstract public boolean handle();
}
