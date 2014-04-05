package nl.stoux.stouxgames.joinmethod;

import nl.stoux.stouxgames.games.GameController;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class PortalHandler implements Listener {
	
	private GameController gameControl;
	
	public PortalHandler(GameController gameControl) {
		this.gameControl = gameControl;
	}
	
	@EventHandler
	public void onPortalEnter(EntityPortalEnterEvent event) {
		//Check if human & in correct world
		if (!(event.getEntity() instanceof Player) || event.getEntity().getLocation().getWorld() != _.getWorld()) {
			return; //Ignore event
		}
		
		//Get player
		Player target = (Player) event.getEntity();
		
		//Check regions
		ApplicableRegionSet regions = _.getPlugin().getWorldguard().getRegionManager(_.getWorld()).getApplicableRegions(event.getLocation());
		for (ProtectedRegion region : regions) {
			if (region.getId().toLowerCase().contains("portal")) {
				if (entersGamePortal(target, region.getId())) {
					return;
				}
			}
		}
	}
	
	/**
	 * Player touches a possible game portal
	 * @param player The player
	 * @param RegionID The region of the portal
	 * @return did join game
	 */
	private boolean entersGamePortal(Player player, String RegionID) {
		switch(RegionID.toLowerCase().replace("portal", "")) {
		case "cakedefence":
			joinGame(GameMode.CD, player);
			break;
			
		case "parkour":
			joinGame(GameMode.Parkour, player);
			break;
			
		case "sonic":
			joinGame(GameMode.Sonic, player);
			break;
			
		case "spleef":
			joinGame(GameMode.Spleef, player);
			break;
			
		case "tntrun":
			joinGame(GameMode.TNTRun, player);
			break;
		
		default:
			return false;
		}
		return true;
	}
	
	/**
	 * Make a player join a game
	 * @param gm The gamemode
	 * @param player The player
	 */
	private void joinGame(GameMode gm, Player player) {
		GamePlayer gp = _.getPlayerController().getGamePlayer(player.getName());
		if (gp != null) {
			if (System.currentTimeMillis() - gp.getJoinTime() > 500) {
				_.badMsg(player, "You are already in a game, huh? Return to your game.");
			}
			return;
		}
		gameControl.getGame(gm).playerJoins(player);
	}
	
	
	

}
