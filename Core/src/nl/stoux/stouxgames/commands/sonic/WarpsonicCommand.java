package nl.stoux.stouxgames.commands.sonic;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.sonic.Sonic;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;

public class WarpsonicCommand extends AbstractCommand {

	
	public WarpsonicCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean handle() {
		if (!_.testPermission(sender, "warpsonic")) { //Check permission
			_.badMsg(sender, "You don't have permission to do this.");
			return true;
		}
		if (!(sender instanceof Player)) { //Check if ingame
			_.badMsg(sender, "You need to be ingame to do this.");
			return true;
		}
		Player p = (Player) sender;
		if (p.getWorld() != _.getWorld()) { //Check if in the correct world
			_.msg(sender, GameMode.Sonic, "You are in the wrong world! Use " + ChatColor.AQUA + "/spawn games" + ChatColor.WHITE + " to teleport to the games world!");
			return true;
		}
		GamePlayer gP = _.getPlayerController().getGamePlayer(p.getName());
		if (gP == null) { //Check if the player is in a game
			_.badMsg(p, "You are not playing sonic. Join sonic @ The minigames hub!");
			return true;
		}
		AbstractGame game = gP.getGame();
		if (game.getGamemode() != GameMode.Sonic) { //Check if in the correct game
			_.badMsg(p, "You are not playing sonic. Join sonic @ The minigames hub!");
			return true;
		}
		Sonic sonic = (Sonic) game;
		sonic.onWarpSonicCommand(gP);		
		return true;
	}

}
