package nl.stoux.stouxgames.listener;

import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.GameState;
import nl.stoux.stouxgames.games.cakedefence.CakeDefence;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerController;
import nl.stoux.stouxgames.util._;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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
	
	@EventHandler
	public void onPlayerDies(PlayerDeathEvent event) {
		GamePlayer gP = getGamePlayer(event.getEntity());
		if (gP == null) return;
		gP.getGame().Events.onPlayerDied(gP, event);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			GamePlayer gP = getGamePlayer((Player) event.getEntity());
			if (gP == null) return;
			gP.getGame().Events.onPlayerDamage(gP, event);
		}
	}
	
	@EventHandler
	public void onEntityDies(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) return; //Return if player
		CakeDefence cd = (CakeDefence) _.getGameController().getGame(GameMode.CD); //Get CD
		if (cd != null) { //Check if exists
			if (cd.getGameState() == GameState.playing) { //Check if playing
				cd.mobDies(event.getEntity().getUniqueId(), event);
			}
		}
	}
	
	@EventHandler
	public void onEntityCombust(EntityExplodeEvent event) {
		if (event.getEntity() instanceof Player) return;
		CakeDefence cd = (CakeDefence) _.getGameController().getGame(GameMode.CD); //Get CD
		if (cd != null) { //Check if exists
			if (cd.getGameState() == GameState.playing) { //Check if playing
				cd.mobDies(event.getEntity().getUniqueId());
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		GamePlayer gP = getGamePlayer(event.getPlayer());
		if (gP == null) return;
		gP.getGame().Events.onPlayerRespawn(gP, event);
	}
	
	@EventHandler
	public void onSlimeSplit(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.SLIME_SPLIT) { //Check if slime split
			CakeDefence cd = (CakeDefence) _.getGameController().getGame(GameMode.CD); //Get CD
			if (cd != null) { //Check if CD is running
				if (cd.getGameState() == GameState.playing) { //Check if Playing
					cd.slimeSpawns(event.getEntity()); //Call function
				}
			}
		}
		
	}
	
	
}
