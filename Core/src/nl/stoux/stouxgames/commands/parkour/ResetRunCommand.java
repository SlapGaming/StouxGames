package nl.stoux.stouxgames.commands.parkour;

import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer;
import nl.stoux.stouxgames.util._;

import org.bukkit.command.CommandSender;

public class ResetRunCommand extends ParkourCommand {

	public ResetRunCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}
	
	@Override
	public boolean handle() throws CommandException {
		ParkourPlayer pp = parkourCheck("resetrun", true); //Default checks
		pp.getRun().reset(); //Reset their run
		pp.getPlayer().teleport(pp.getCurrentMap().getLobby()); //Teleport to that map's lobby
		_.msg(pp.getPlayer(), GameMode.Parkour, "You've been teleported to the lobby of this map!");
		return true;
	}

}
