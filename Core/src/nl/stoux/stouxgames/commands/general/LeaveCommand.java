package nl.stoux.stouxgames.commands.general;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.stoux.stouxgames.commands.AbstractCommand;
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
	public boolean handle() {
		voidHandle();
		return true;
	}
	
	private void voidHandle() {
		if (!_.isAPlayer(sender)) { //Check if actual player
			_.badMsg(sender, "You need to be ingame to do that!");
			return;
		}
		
		Player p = (Player) sender; //Cast to player
		
		if (p.getLocation().getWorld() != _.getWorld()) { //Check if correct world
			_.badMsg(sender, "This can only be done in the games world!");
			return;
		}
		
		GamePlayer gP = getGamePlayer();
		if (gP == null) {
			_.badMsg(sender, "You can only do this command while in a game.");
			return;
		}
		
		//Leave the game the player is in
		gP.getGame().playerQuit(gP);
		
		//Remove from main list
		_.getPlayerController().removePlayer(gP.getName());
	}

}
