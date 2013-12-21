package nl.stoux.stouxgames.commands.parkour;

import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer.ParkourRun;
import nl.stoux.stouxgames.util._;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CheckpointCommand extends ParkourCommand {

	public CheckpointCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean handle() throws CommandException {
		ParkourPlayer pp = parkourCheck("checkpoint",true); //Standard checks
		
		ParkourRun run = pp.getRun();
		if (run.isFinished()) { //If player finished
			_.msg(pp.getPlayer(), GameMode.Parkour, "You already finished your run! " + ChatColor.GRAY + " (Time: " + _.getGameController().getTimeString(run.getPassedTime()) + ")");
		} else if (run.hasStarted()) { //If player started
			int nrOfCheckpoints = run.getCurrentMap().getNrOfCheckpoints();
			if (nrOfCheckpoints == 0) {
				_.msg(pp.getPlayer(), GameMode.Parkour, "This map doesn't have any checkpoints!");
			} else {
				_.msg(pp.getPlayer(), GameMode.Parkour, "You have passed " + ChatColor.GREEN + String.valueOf(run.getCurrentCheckpoint()) + "/" + nrOfCheckpoints + ChatColor.WHITE + " checkpoints.");
			}
		} else { //Player hasn't started
			_.msg(pp.getPlayer(), GameMode.Parkour, "You haven't started running yet!");
		}
		return true;
	}

}
