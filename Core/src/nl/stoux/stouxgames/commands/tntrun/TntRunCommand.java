package nl.stoux.stouxgames.commands.tntrun;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.commands.exception.Message;
import nl.stoux.stouxgames.commands.exception.UsageException;
import nl.stoux.stouxgames.commands.main.StouxGamesCommand;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.GameState;
import nl.stoux.stouxgames.games.tntrun.TNTRun;
import nl.stoux.stouxgames.util._;

import org.bukkit.command.CommandSender;

public class TntRunCommand extends AbstractCommand {

	public TntRunCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean handle() throws CommandException {
		if (args.length == 0) { //Redirect to standard StouxGames command
			return new StouxGamesCommand(sender, new String[]{}).handle();
		}
		
		TNTRun tnt = (TNTRun) _.getGameController().getGame(GameMode.TNTRun);
		if (tnt == null) throw new CommandException(Message.gameNotRunning);
		
		switch (args[0].toLowerCase()) {
		case "forcepotion": case "forcep": case "forcemode": case "forcem": 
			
			break;
		case "forcestart": case "forces":
			checkPermission("tntrun.forcestart");
			if (tnt.getGameState() == GameState.lobby || tnt.getGameState() == GameState.lobbyJoining) {
				tnt.forceStartGame();
			} else {
				throw new UsageException("TNT Run cannot be forced start from this game state.");
			}
			break;
		}
		
		return true;
	}

	

}