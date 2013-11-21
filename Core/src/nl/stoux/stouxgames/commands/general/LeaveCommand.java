package nl.stoux.stouxgames.commands.general;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;

/**
 * Leave command
 * Command: /gleave
 * Will be called indirectly by SlapHomebrew (/leave -> /gleave)
 * @author Stoux
 */
public class LeaveCommand extends AbstractCommand {

	public LeaveCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean handle() throws CommandException {
		checkPermission("leave");
		Player p = getPlayer();
		checkWorld(p.getLocation());
		GamePlayer gP = getGamePlayer();
		
		//Leave the game the player is in
		gP.getGame().playerQuit(gP);
		
		//Remove from main list
		_.getPlayerController().removePlayer(gP.getName());
		return true;
	}
	
	
}
