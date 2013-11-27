package nl.stoux.stouxgames.commands;


import nl.stoux.stouxgames.commands.cd.CakeDefenceCommand;
import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.commands.general.LeaveCommand;
import nl.stoux.stouxgames.commands.main.StouxGamesCommand;
import nl.stoux.stouxgames.commands.sonic.SonicCommand;
import nl.stoux.stouxgames.commands.sonic.WarpsonicCommand;
import nl.stoux.stouxgames.commands.spleef.SpleefCommand;
import nl.stoux.stouxgames.commands.tntrun.TntRunCommand;
import nl.stoux.stouxgames.util._;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandHandler {

	public static boolean handle(CommandSender sender, Command cmd, String[] args) {
		String command = cmd.getName().toLowerCase(); //The command
		AbstractCommand commandObj = null; //The Abstract command
		
		switch (command) {
		//General
		case "stouxgames":	commandObj = new StouxGamesCommand(sender, args);		break;
		case "gleave": 		commandObj = new LeaveCommand(sender, args); 			break;
		
		//Sonic
		case "warpsonic": 	commandObj = new WarpsonicCommand(sender, args); 		break;
		case "sonic": 		commandObj = new SonicCommand(sender, args); 			break;
		
		//Spleef
		case "spleef":		commandObj = new SpleefCommand(sender, args);			break;
		
		//TNT Run
		case "tntrun": 		commandObj = new TntRunCommand(sender, args);			break;
		
		//Cake Defence
		case "cakedefence":	commandObj = new CakeDefenceCommand(sender, args);		break;
		
		
		}
		if (commandObj != null) {
			try {
				boolean handled = commandObj.handle();
				if (!handled) {
					_.badMsg(sender, cmd.getUsage());
				}
			} catch (CommandException e) {
				sender.sendMessage(e.getMessage());
			}
		}
		return true;
	}
	
}
