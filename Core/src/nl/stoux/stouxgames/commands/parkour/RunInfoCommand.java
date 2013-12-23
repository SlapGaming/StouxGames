package nl.stoux.stouxgames.commands.parkour;

import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.parkour.ParkourMap;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer.ParkourRun;
import nl.stoux.stouxgames.util._;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RunInfoCommand extends ParkourCommand {

	public RunInfoCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}
	
	@Override
	public boolean handle() throws CommandException {
		ParkourPlayer pp = parkourCheck("runinfo", true); //Standard checks
		
		ParkourRun run = pp.getRun();
		ParkourMap map = pp.getCurrentMap();
		msg(pp, ChatColor.YELLOW + "--- " + ChatColor.GREEN + " Run Info " + ChatColor.YELLOW + " --- ");
		msg(pp, ChatColor.YELLOW + "Map: " + ChatColor.WHITE + map.getName() + ChatColor.GRAY + " (Use '/mapinfo' for more info)");
		if (run.isFinished()) {
			msg(pp, ChatColor.YELLOW + "Run: " + ChatColor.WHITE + "You finished this run! " + ChatColor.GRAY + "(Time: " + _.getGameController().getTimeString(run.getPassedTime()) + ")");
		} else if (run.hasStarted()) {
			msg(pp, ChatColor.YELLOW + "Run: " + ChatColor.WHITE + "You're currently on a run! " + ChatColor.GRAY + "(Time: " + _.getGameController().getTimeString(run.getPassedTime()) + ")");
			
		} else {
			//TODO
		}
		return true;
	}
	
	private void msg(ParkourPlayer pp, String message) {
		_.msg(pp.getPlayer(), GameMode.Parkour, message);
	}

}
