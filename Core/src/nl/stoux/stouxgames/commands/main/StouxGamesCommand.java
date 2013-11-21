package nl.stoux.stouxgames.commands.main;

import nl.stoux.stouxgames.StouxGames;
import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.commands.exception.Message;
import nl.stoux.stouxgames.commands.exception.UsageException;
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
	public boolean handle() throws CommandException {
		
		if (args.length == 0 || args[0].toLowerCase().equals("info")) { //Info command
			checkPermission("info");
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
			checkPermission("reload");
			StouxGames p = _.getPlugin();
			p.onDisable();
			p.onEnable();
			sender.sendMessage(header + "Plugin reloaded!");
			break;
			
		case "reloadgame": case "restartgame":
			checkPermission("reloadgame");
			if (args.length != 2) throw new UsageException("/stouxgames reloadgame [GameMode]");
			GameMode gm;
			try {
				gm = GameMode.valueOf(args[1]);
			} catch (Exception e) {
				throw new CommandException(args[1] + " is not a valid gamemode.");
			}
			AbstractGame game = _.getGameController().getGame(gm);
			if (game == null) throw new CommandException(Message.gameNotRunning);
			game.reloadGame();
			sender.sendMessage(header + "Game reloaded!");
			break;
			
		case "reloadpads": case "reloadpad":
			checkPermission("reloadpads");
			_.getPlugin().getPressurePadHandler().reset();
			sender.sendMessage(header + "Pads reloaded!");
			break;
			
		default:
			throw new UsageException("/stouxgames <info>");
			
		}
		return true;
	}

}
