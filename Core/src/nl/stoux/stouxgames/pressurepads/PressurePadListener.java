package nl.stoux.stouxgames.pressurepads;

import nl.stoux.stouxgames.util._;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PressurePadListener implements Listener {

	private PressurePadHandler handler;
	private World spawn;
	
	public PressurePadListener(PressurePadHandler handler) {
		this.handler = handler;
		spawn = _.getPlugin().getServer().getWorld("world_start"); //Add Spawn world for Christmas event
	}
	
	@EventHandler
	public void stepOnPad(PlayerInteractEvent event) {
		if (event.getAction() != Action.PHYSICAL) { //Check if stepped on pad
			return;
		}
		
		Location blockLocation = event.getClickedBlock().getLocation(); //Get the block's location
		World clickedWorld = blockLocation.getWorld();
		if (clickedWorld != _.getWorld() && clickedWorld != spawn) { //Check if the correct world
			return;
		}
		
		handler.handlePadPressed(event.getPlayer(), blockLocation); //Handle the event
	}

}
