package nl.stoux.stouxgames.commands.exception;

import org.bukkit.ChatColor;

public class CommandException extends Exception {

	private static final long serialVersionUID = 4983059478261142647L;

	public CommandException(String message) {
		super(ChatColor.RED + message);
	}
	
	public CommandException(Message msg) {
		super(ChatColor.RED + msg.toString());
	}

}
