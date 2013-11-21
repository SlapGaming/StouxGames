package nl.stoux.stouxgames.commands.exception;

import org.bukkit.ChatColor;

public class UsageException extends CommandException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5931497931759768909L;

	public UsageException(String message) {
		super(ChatColor.RED + "Usage: " + message);
	}

}
