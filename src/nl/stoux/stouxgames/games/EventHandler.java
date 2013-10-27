package nl.stoux.stouxgames.games;

import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.stoux.stouxgames.player.GamePlayer;

public class EventHandler {
	
	//The abstract game
	private AbstractGame game;
	
	public EventHandler(AbstractGame game) {
		this.game = game;
	}

	/**
	 * A player has quit minecraft
	 * @param gP The player
	 * @param event The event
	 */
	public void onQuit(GamePlayer gP, PlayerQuitEvent event) {
		game.playerQuit(gP);
	}
	
	/**
	 * A player has interacted with something
	 * @param gP The player
	 * @param event The event
	 */
	public void onPlayerInteract(GamePlayer gP, PlayerInteractEvent event) {
		return;
	}
	
	/**
	 * A player has moved
	 * @param gP The player
	 * @param event The event
	 */
	public void onPlayerMove(GamePlayer gP, PlayerMoveEvent event) {
		return;
	}
	
	/**
	 * A player has damaged a block
	 * @param gP The player
	 * @param event The event
	 */
	public void onBlockDamage(GamePlayer gP, BlockDamageEvent event) {
		return;
	}
	
	/**
	 * A player's food level changed.
	 * @param gP The player
	 * @param event The event
	 */
	public void onFoodLevelChange(GamePlayer gP, FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	/**
	 * A player has opened an Inventory
	 * @param gP The player
	 * @param event The event
	 */
	public void onInventoryEvent(GamePlayer gP, InventoryOpenEvent event) {
		event.setCancelled(true);
	}
	
	/**
	 * A player has dropped an item
	 * @param gP The player
	 * @param event The event
	 */
	public void onItemDrop(GamePlayer gP, PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	/**
	 * A player has picked up an item
	 * @param gP The player
	 * @param event The event
	 */
	public void onItemPickup(GamePlayer gP, PlayerPickupItemEvent event) {
		event.setCancelled(true);
	}

}
