package nl.stoux.stouxgames.games.sonic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import nl.stoux.stouxgames.games.EventHandler;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._T;

public class SonicHandler extends EventHandler {

	private Sonic sonic;
	
	public SonicHandler(Sonic sonic) {
		super(sonic);
		this.sonic = sonic;
	}
	
	@Override
	public void onPlayerMove(GamePlayer gP, PlayerMoveEvent event) {
		sonic.onPlayerMove(gP, event);
	}
	
	@Override
	public void onPlayerInteract(GamePlayer gP, PlayerInteractEvent event) {
		sonic.onPlayerInteract(gP, event);
	}

	@Override
	public void onPlayerDamage(GamePlayer gP, EntityDamageEvent event) {
		final Player p = gP.getPlayer();
		_T.runLater_Sync(new Runnable() {
			
			@Override
			public void run() {
				if (!p.isOnline()) return; //Check if still online
				ItemStack boots = p.getInventory().getBoots(); //Get boots
				if (boots.getType() == Material.LEATHER_BOOTS) { //Check if leather boots
					boots.setDurability((short)0);
				}
			}
		}, 1);
	}
	
}
