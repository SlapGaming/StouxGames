package nl.stoux.stouxgames.commands.cd;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.commands.exception.Message;
import nl.stoux.stouxgames.commands.exception.UsageException;
import nl.stoux.stouxgames.commands.main.StouxGamesCommand;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.GameState;
import nl.stoux.stouxgames.games.cakedefence.CakeDefence;
import nl.stoux.stouxgames.util._;

public class CakeDefenceCommand extends AbstractCommand {

	public CakeDefenceCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean handle() throws CommandException {
		if (args.length == 0) { //Redirect to standard StouxGames command
			return new StouxGamesCommand(sender, new String[]{}).handle();
		}
		
		CakeDefence cd = (CakeDefence) _.getGameController().getGame(GameMode.CD);
		if (cd == null) throw new CommandException(Message.gameNotRunning);
		
		switch(args[0].toLowerCase()) {
		case "reloadroutines": //Reload the routines
			checkPermission("cd.reloadroutines");
			cd.findRoutines();
			_.msg(sender, GameMode.CD, "Routines reloaded!");
			break;
			
		case "routines": //Get all the routines
			checkPermission("cd.routines");
			String buildString = "";
			ArrayList<File> list = cd.getRoutines();
			for (File f : list) {
				if (buildString.equals("")) {
					buildString = f.getName();
				} else {
					buildString = buildString + ", " + f.getName();
				}
			}
			_.msg(sender, cd.getGamemode(), "Found routines: " + buildString);
			break;
			
		case "forceroutine": case "forcer": //Force a routine
			checkPermission("cd.forceroutine");
			if (args.length != 2) throw new UsageException("/cakedefence forceroutine <routinefilename>");
			String filename = args[1].toLowerCase();
			for (File f : cd.getRoutines()) {
				if (f.getName().toLowerCase().equals(filename)) {
					cd.setForcedRoutine(filename);
					_.msg(sender, GameMode.CD, "Next round will be forced to: " + filename);
					return true;
				}
			}
			throw new CommandException("File not found. Use '/stouxgames routines' to get all the routines.");
			
		case "forcestart": case "forces": //Force start the round
			checkPermission("cd.forcestart");
			if (cd.getGameState() != GameState.lobbyJoining && cd.getGameState() != GameState.lobby) throw new CommandException("The game cannot be forced start from this GameState.");
			if (!cd.countDownEnded(false)) throw new CommandException("Failed to force start. No players in the game?");
			break;
			
			
			
			
		
		
		
		}
		
		return true;
	}
	
	

	

}
