package nl.stoux.stouxgames.commands.main;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameController;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.util._;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StouxGamesCommand extends AbstractCommand {

	private String header;
	
	public StouxGamesCommand(CommandSender sender, String[] args) {
		super(sender, args);
		header = ChatColor.GREEN + "[StouxGames] " + ChatColor.WHITE;
	}

	@Override
	public boolean handle() {
		if (args.length == 0) { //Info command
			String[] msgs = new String[] {
				header + "Mini-games plugin made by " + ChatColor.GREEN + "Stoux",
				header + "Current version: " + _.getPlugin().getDescription().getVersion() + ChatColor.GRAY + " (Beta)",
				header + "Available games: "
			};
			GameController gameController = _.getGameController();
			boolean first = true;
			for (AbstractGame game : gameController.getGames()) { //Loop thru games
				if (!first) msgs[2] = msgs[2] + ", ";
				else first = false;
				msgs[2] = msgs[2] + game.getGamemode().getName() + ChatColor.GRAY + " (" + game.getPlayers().size() + " players)" + ChatColor.WHITE; //Add all available games to the list 
			}
			for (String msg : msgs) { //Send messages
				sender.sendMessage(msg);
			}
			return true;
		}
		
		switch (args[0].toLowerCase()) {
		case "reload":
			if (!_.testPermission(sender, "reload")) {
				_.noPermission(sender);
				return true;
			}
			
			break;
		case "reloadgame": case "restartgame":
			if (!_.testPermission(sender, "reloadgame")) {
				_.noPermission(sender);
				return true;
			}
			if (args.length < 2) {
				_.badMsg(sender, "Usage: /stouxgames reloadgame [GameMode]");
				return true;
			}
			GameMode gm;
			try {
				gm = GameMode.valueOf(args[1]);
			} catch (Exception e) {
				_.badMsg(sender, "Not a valid gamemode.");
				return true;
			}
			AbstractGame game = _.getGameController().getGame(gm);
			if (game == null) {
				_.badMsg(sender, "This game is not loaded. So can't reload lol.");
				return true;
			}
			game.reloadGame();
			break;
			
		}
		return true;
	}

}
