package nl.stoux.stouxgames.games;

import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;

public class DefaultEventHandler<T extends AbstractGame, T2 extends GamePlayer> {
	
	//The abstract game
	protected T game;
	
	public DefaultEventHandler(T game) {
		this.game = game;
	}

	/**
	 * A player has quit minecraft
	 * @param gP The player
	 * @param event The event
	 */
	public void onQuit(T2 gP, PlayerQuitEvent event) {
		game.playerQuit(gP);
	}
	
	/**
	 * A player has interacted with something
	 * @param gP The player
	 * @param event The event
	 */
	public void onPlayerInteract(T2 gP, PlayerInteractEvent event) {
		return;
	}
	
	/**
	 * A player has moved
	 * @param gP The player
	 * @param event The event
	 */
	public void onPlayerMove(T2 gP, PlayerMoveEvent event) {
		return;
	}
	
	/**
	 * A player has damaged a block
	 * @param gP The player
	 * @param event The event
	 */
	public void onBlockDamage(T2 gP, BlockDamageEvent event) {
		return;
	}
	
	/**
	 * A player's food level changed.
	 * @param gP The player
	 * @param event The event
	 */
	public void onFoodLevelChange(T2 gP, FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	/**
	 * A player has opened an Inventory
	 * @param gP The player
	 * @param event The event
	 */
	public void onInventoryEvent(T2 gP, InventoryOpenEvent event) {
		//event.setCancelled(true);
	}
	
	/**
	 * A player has dropped an item
	 * @param gP The player
	 * @param event The event
	 */
	public void onItemDrop(T2 gP, PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	/**
	 * A player has picked up an item
	 * @param gP The player
	 * @param event The event
	 */
	public void onItemPickup(T2 gP, PlayerPickupItemEvent event) {
		event.setCancelled(true);
	}
	
	/**
	 * A player has died
	 * @param gP The player
	 * @param event The event
	 */
	public void onPlayerDied(T2 gP, PlayerDeathEvent event) {
		gP.getGame().playerQuit(gP);
		_.badMsg(gP.getPlayer(), "You died o.O?");
	}
	
	/**
	 * A player has taken damage
	 * @param gP The player
	 * @param event The event
	 */
	public void onPlayerDamage(T2 gP, EntityDamageEvent event) {
		return;
	}
	
	/**
	 * A player respawns
	 * @param gP The player
	 * @param event The event
	 */
	public void onPlayerRespawn(T2 gP, PlayerRespawnEvent event) {
		event.setRespawnLocation(game.getLobby());
	}
	
	/**
	 * A player toggles sneaking
	 * @param gP The player
	 * @param event The event
	 */
	public void onPlayerSneak(T2 gP, PlayerToggleSneakEvent event) {
		return;
	}

}
