package nl.stoux.stouxgames.commands.sonic;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.sonic.Sonic;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpsonicCommand extends AbstractCommand {

	
	public WarpsonicCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean handle() throws CommandException {
		//Player checks
		checkPermission("warpsonic");
		Player p = getPlayer();
		checkWorld(p.getWorld());
		GamePlayer gP = getGamePlayer();
		
		//Game checks
		Sonic sonic = (Sonic) _.getGameController().getGame(GameMode.Sonic);
		checkCorrectGame(gP, GameMode.Sonic);
		
		sonic.onWarpSonicCommand(gP); //Warp player		
		return true;
	}

}
