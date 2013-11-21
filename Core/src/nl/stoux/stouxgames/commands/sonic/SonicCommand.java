package nl.stoux.stouxgames.commands.sonic;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.commands.exception.Message;
import nl.stoux.stouxgames.commands.exception.UsageException;
import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.sonic.Sonic;
import nl.stoux.stouxgames.util._;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
	public boolean handle() throws CommandException {
		if (sonic == null) throw new CommandException(Message.gameNotRunning);
		
		if (args.length == 0 || (args.length == 1 && args[0].toLowerCase().equals("help"))) { //Send help
			checkPermission("sonic.help"); //Check for permission
			String[] helpMessages = new String[]{ //Create messages
					ChatColor.YELLOW + "---" + ChatColor.GREEN + " Sonic Help " + ChatColor.YELLOW + "---",
					ChatColor.YELLOW + "Credits: " + ChatColor.WHITE + "Map made by FVDisco" + ChatColor.YELLOW + " | " + ChatColor.WHITE + "Multiplayer by naithantu (& Stoux).",
					ChatColor.YELLOW + "/sonic time <Player> " + ChatColor.WHITE + ": Get the player's highscore.",
					ChatColor.YELLOW + "/sonic leaderboard <monthly> " + ChatColor.WHITE + ": Get the (Monthly) leaderboard.",
					ChatColor.YELLOW + "/warpsonic " + ChatColor.WHITE + ": Warp to the start. " + ChatColor.GRAY + "(Only available in-game!)"
			};
			for (String msg : helpMessages) {
				_.msg(sender, GameMode.Sonic, msg); //Send messages
			}
			return true;
		}
		
		switch (args[0].toLowerCase()) {
		case "time": //Get time
			checkPermission("sonic.time");
			String lookingForPlayer = "";
			if (args.length == 1) {
				if (!(sender instanceof Player)) throw new UsageException("/sonic time [Player]");
				else lookingForPlayer = sender.getName();
			} else {
				lookingForPlayer = args[1];
			}
			if (!sonic.getLeaderboard().sendSonicTime(sender, lookingForPlayer)) throw new CommandException(Message.alreadyRunningCommand);
			break;
		case "leaderboard": case "leaderboards":
			checkPermission("sonic.leaderboard");
			boolean monthly = true;
			if (args.length == 1) { //All time leaderboard
				monthly = false;
			}
			if (!sonic.getLeaderboard().sendLeaderboard(sender, monthly)) throw new CommandException(Message.alreadyRunningCommand);
			break;
		default:
			throw new UsageException("/sonic <time [player]|leaderboard [monthly]>");
		}
		return true;
	}

}
