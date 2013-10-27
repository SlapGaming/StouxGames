package nl.stoux.stouxgames.commands;


import nl.stoux.stouxgames.commands.general.LeaveCommand;
import nl.stoux.stouxgames.commands.sonic.SonicCommand;
import nl.stoux.stouxgames.commands.sonic.WarpsonicCommand;
import nl.stoux.stouxgames.util._;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandHandler {

	public static boolean handle(CommandSender sender, Command cmd, String[] args) {
		String command = cmd.getName().toLowerCase(); //The command
		AbstractCommand commandObj = null; //The Abstract command
		
		switch (command) {
		//General
		case "leave":
		case "gleave": commandObj = new LeaveCommand(sender, args); 	break;
		
		//Sonic
		case "warpsonic": commandObj = new WarpsonicCommand(sender, args); break;
		case "sonic": commandObj = new SonicCommand(sender, args); break;
		
		//Spleef
		
		
		
		
		
		
		
		}
		if (commandObj != null) {
			boolean handled = commandObj.handle();
			if (!handled) {
				_.badMsg(sender, cmd.getUsage());
			}
		}
		return true;
	}
	
}
