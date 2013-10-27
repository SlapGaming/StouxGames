package nl.stoux.stouxgames.listener;

import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerController;
import nl.stoux.stouxgames.util._;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

	private PlayerController controller;
	
	/**
	 * New Event listener
	 */
	public EventListener(PlayerController controller) {
		this.controller = controller;
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * Get a GamePlayer
	 * @param p The player
	 * @return The GamePlayer or null if not found
	 */
	private GamePlayer getGamePlayer(Player p) {
		return controller.getGamePlayer(p.getName());
	}
	
	
	/*
	 * Events
	 */
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		GamePlayer gP = getGamePlayer(event.getPlayer()); //Get the player
		if (gP == null) return; //If not a player
		gP.getGame().playerQuit(gP);
		_.getPlayerController().removePlayer(gP.getName());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		GamePlayer gP = getGamePlayer(event.getPlayer()); //Get the player
		if (gP == null) return; //If not a player
		gP.getGame().Events.onPlayerInteract(gP, event);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		GamePlayer gP = getGamePlayer(event.getPlayer()); //Get the player
		if (gP == null) return; //If not a player
		gP.getGame().Events.onPlayerMove(gP, event);
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		GamePlayer gP = getGamePlayer(event.getPlayer()); //Get the player
		if (gP == null) return; //If not a player
		gP.getGame().Events.onBlockDamage(gP, event);
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		HumanEntity entity = event.getEntity();
		if (entity instanceof Player) {
			GamePlayer gP = getGamePlayer((Player) entity); //Get the player
			if (gP == null) return; //If not a player
			gP.getGame().Events.onFoodLevelChange(gP, event);
		}
	}
	
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		GamePlayer gP = getGamePlayer(event.getPlayer()); //Get the player
		if (gP == null) return; //If not a player
		gP.getGame().Events.onItemPickup(gP, event);
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		GamePlayer gP = getGamePlayer(event.getPlayer()); //Get the player
		if (gP == null) return; //If not a player
		gP.getGame().Events.onItemDrop(gP, event);
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		HumanEntity entity = event.getPlayer();
		if (entity instanceof Player) {
			GamePlayer gP = getGamePlayer((Player) entity); //Get the player
			if (gP == null) return; //If not a player
			gP.getGame().Events.onInventoryEvent(gP, event);
		}
	}
	
	
	
	
}
