package nl.stoux.stouxgames.commands.parkour;

import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer;

import org.bukkit.command.CommandSender;

public class LeaveMapCommand extends ParkourCommand {

	public LeaveMapCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean handle() throws CommandException {
		ParkourPlayer pp = parkourCheck("leavemap", true); //Do all checks
		pp.leaveMap(true); //Leave the map
		return true;
	}

}
