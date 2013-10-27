package nl.stoux.stouxgames.pressurepads;

import nl.stoux.stouxgames.util._;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PressurePadListener implements Listener {

	private PressurePadHandler handler;
	
	public PressurePadListener(PressurePadHandler handler) {
		this.handler = handler;
	}
	
	@EventHandler
	public void stepOnPad(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL) { //Check if stepped on pad
			return;
		}
		
		Location blockLocation = event.getClickedBlock().getLocation(); //Get the block's location
		if (blockLocation.getWorld() != _.getWorld()) { //Check if the correct world
			return;
		}
		
		handler.handlePadPressed(event.getPlayer(), event.getClickedBlock().getLocation()); //Handle the event
	}

}
