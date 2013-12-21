package nl.stoux.stouxgames.commands.exception;

import org.bukkit.ChatColor;

public enum Message {

	alreadyRunningCommand("You cannot do this at this moment, you're still running a command."),
	gameNotRunning("This game is currently not running!"),
	incorrectWorld("You need to be in the mini-games world to do that!"),
	notInAGame("You need to be in a game to do that!"),
	notInMinecraft("You need to be in-game to do that!"),
	noPermission("You don't have permission to do this!"),
	
	//Parkour
	invalidMapID("This is not a valid map ID. Check out " + ChatColor.AQUA + "/parkour maps"),
	notOnParkourMap("You aren't on a parkour map!");
	
	private String msg;
	private Message(String msg) {
		this.msg = ChatColor.RED + msg;
	}
	
	@Override
	public String toString() {
		return msg;
	}
	
	
}
