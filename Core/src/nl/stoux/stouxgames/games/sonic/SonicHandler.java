package nl.stoux.stouxgames.games.sonic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import nl.stoux.stouxgames.games.DefaultEventHandler;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._T;

public class SonicHandler extends DefaultEventHandler<Sonic, SonicPlayer> {

	public SonicHandler(Sonic sonic) {
		super(sonic);
	}
	
	@Override
	public void onPlayerMove(SonicPlayer gP, PlayerMoveEvent event) {
		game.onPlayerMove(gP, event);
	}
	
	@Override
	public void onPlayerInteract(SonicPlayer gP, PlayerInteractEvent event) {
        game.onPlayerInteract(gP, event);
	}

	@Override
	public void onPlayerDamage(SonicPlayer gP, EntityDamageEvent event) {
		final Player p = gP.getPlayer();
		_T.runLater_Sync(new Runnable() {
			
			@Override
			public void run() {
				if (!p.isOnline()) return; //Check if still online
				if (p.isDead()) return; //Check if actually alive..
				ItemStack boots = p.getInventory().getBoots(); //Get boots
				if (boots != null) {
					if (boots.getType() == Material.LEATHER_BOOTS) { //Check if leather boots
						boots.setDurability((short)0);
					}
				}
			}
		}, 1);
	}
	
}
