package nl.stoux.stouxgames.commands.sonic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.sonic.Sonic;
import nl.stoux.stouxgames.player.GamePlayer;

public class WarpsonicCommand extends AbstractCommand {

	
	public WarpsonicCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean handle() throws CommandException {
		checkPermission("warpsonic");
		Player p = getPlayer();
		checkWorld(p.getWorld());
		GamePlayer gP = getGamePlayer();
		
		AbstractGame game = gP.getGame();
		if (game.getGamemode() != GameMode.Sonic) throw new CommandException("You are not playing sonic. Join sonic @ the mini-games hub!");
		Sonic sonic = (Sonic) game;
		sonic.onWarpSonicCommand(gP);		
		return true;
	}

}
