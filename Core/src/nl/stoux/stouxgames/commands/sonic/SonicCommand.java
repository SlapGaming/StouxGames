package nl.stoux.stouxgames.commands.sonic;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.sonic.Sonic;
import nl.stoux.stouxgames.util._;

public class SonicCommand extends AbstractCommand {

	//The sonic game
	private static Sonic sonic;
	
	public SonicCommand(CommandSender sender, String[] args) {
		super(sender, args);
		if (sonic == null) {
			AbstractGame game = _.getGameController().getGame(GameMode.Sonic);
			if (game != null) {
				sonic = (Sonic) game;
			}
		}
	}

	@Override
	public boolean handle() {
		boolean help = false;
		if (args.length == 0) { //Help
			help = true;
		} else {
			switch (args[0].toLowerCase()) {
			case "help": //Help
				help = true;
				break;
			case "time":
				if (!_.testPermission(sender, "sonic.time")) {
					_.noPermission(sender);
					return true;
				}
				if (!checkForSonic()) {
					return true;
				}
				String lookingForPlayer = "";
				if (args.length == 1) {
					if (!(sender instanceof Player)) {
						_.badMsg(sender, "Usage: /sonic time [Player]");
						return true;
					} else {
						lookingForPlayer = sender.getName();
					}
				} else {
					lookingForPlayer = args[1];
				}
				Essentials ess = _.getPlugin().getEssentials();
				if (ess != null) {
					User u = ess.getUserMap().getUser(lookingForPlayer);
					if (u == null) {
						_.badMsg(sender, "This player has never been on the server.");
						return true;
					}
					lookingForPlayer = u.getName();
				}
				if (!sonic.getLeaderboard().sendSonicTime(sender, lookingForPlayer)) {
					_.badMsg(sender, "You cannot do this at this moment. You're still executing a previous command?");
				}
				break;
			case "leaderboard": case "leaderboards":
				if (!_.testPermission(sender, "sonic.leaderboard")) {
					_.noPermission(sender);
					return true;
				}
				if (!checkForSonic()) {
					return true;
				}
				boolean monthly = true;
				if (args.length == 1) { //All time leaderboard
					monthly = false;
				}
				if (!sonic.getLeaderboard().sendLeaderboard(sender, monthly)) {
					_.badMsg(sender, "You cannot do this at this moment. You're still executing a previous command?");
				}
				break;
			default: //Doing it wrong, send /sonic help
				help = true;
			}
		}
		
		if (help) { //Send help string
			if (!_.testPermission(sender, "sonic.help")) {
				_.noPermission(sender);
				return true;
			}
			String[] helpMessages = new String[]{
					ChatColor.YELLOW + "---" + ChatColor.GREEN + " Sonic Help " + ChatColor.YELLOW + "---",
					ChatColor.YELLOW + "Credits: " + ChatColor.WHITE + "Map made by FVDisco" + ChatColor.YELLOW + " | " + ChatColor.WHITE + "Multiplayer by naithantu (& Stoux).",
					ChatColor.YELLOW + "/sonic time <Player> " + ChatColor.WHITE + ": Get the player's highscore.",
					ChatColor.YELLOW + "/sonic leaderboard <monthly> " + ChatColor.WHITE + ": Get the (Monthly) leaderboard.",
					ChatColor.YELLOW + "/warpsonic " + ChatColor.WHITE + ": Warp to the start. " + ChatColor.GRAY + "(Only available in-game!)"
			};
			for (String msg : helpMessages) {
				_.msg(sender, GameMode.Sonic, msg);
			}
		}
		return true;
	}
	
	/**
	 * Check if sonic has been found.
	 * @return is found
	 */
	private boolean checkForSonic() {
		if (sonic == null) {
			_.badMsg(sender, "Sonic has not been setup.");
			return false;
		}
		return true;
	}

}
