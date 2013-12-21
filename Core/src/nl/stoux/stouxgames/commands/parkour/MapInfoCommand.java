package nl.stoux.stouxgames.commands.parkour;

import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.parkour.ParkourMap;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer;
import nl.stoux.stouxgames.util._;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MapInfoCommand extends ParkourCommand {

	public MapInfoCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}
	
	@Override
	public boolean handle() throws CommandException {
		ParkourPlayer pp = parkourCheck("mapinfo", true); //Standard checks
		
		ParkourMap map = pp.getCurrentMap();
		msg(pp, ChatColor.YELLOW + "--- " + ChatColor.GREEN + " Map Info " + ChatColor.YELLOW + " --- ");
		msg(pp, ChatColor.YELLOW + "Map name: " + ChatColor.WHITE + map.getName() + ChatColor.GRAY + " (ID: " + map.getID() + ")");
		msg(pp, ChatColor.YELLOW + "Author: " + ChatColor.WHITE + map.getAuthor());
		int cps = map.getNrOfCheckpoints();
		msg(pp, ChatColor.YELLOW + "Number of checkpoints: " + ChatColor.WHITE + (cps == 0 ? "No checkpoints." : cps));
		msg(pp, ChatColor.YELLOW + "Save progress: " + ChatColor.WHITE + "This map " +
				(map.hasAllowRestartOnCheckpoint() ? ChatColor.AQUA + "will" : ChatColor.RED + "will not") + ChatColor.WHITE + " save your current progress if you leave the map.");		
		return true;
	}
	
	private void msg(ParkourPlayer pp, String message) {
		_.msg(pp.getPlayer(), GameMode.Parkour, message);
	}

}
