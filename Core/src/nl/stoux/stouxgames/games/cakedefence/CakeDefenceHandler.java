package nl.stoux.stouxgames.games.cakedefence;

import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import nl.stoux.stouxgames.games.DefaultEventHandler;
import nl.stoux.stouxgames.games.GameState;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerState;
import nl.stoux.stouxgames.util._T;

public class CakeDefenceHandler extends DefaultEventHandler<CakeDefence, GamePlayer> {

	public CakeDefenceHandler(CakeDefence game) {
		super(game);
	}
	
	@Override
	public void onPlayerDied(GamePlayer gP, PlayerDeathEvent event) {
		game.onPlayerDies(gP, event);
	}
	
	@Override
	public void onPlayerRespawn(final GamePlayer gP, PlayerRespawnEvent event) {
		event.setRespawnLocation(game.getLobby());
		if (game.getGameState() == GameState.playing) {
			_T.runLater_Sync(new BukkitRunnable() {
				
				@Override
				public void run() {
					if (game.getGameState() == GameState.playing && gP.getState() != PlayerState.playing) {
                        game.giveSpectateKit(gP);
					}
				}
			},1);
		}
	}
	
	
	@Override
	public void onFoodLevelChange(GamePlayer gP, FoodLevelChangeEvent event) {
		return;
	}
	
	@Override
	public void onItemDrop(GamePlayer gP, PlayerDropItemEvent event) {
		return;
	}
	
	@Override
	public void onItemPickup(GamePlayer gP, PlayerPickupItemEvent event) {
		return;
	}
	
	@Override
	public void onInventoryEvent(GamePlayer gP, InventoryOpenEvent event) {
		return;
	}
	
	@Override
	public void onPlayerMove(GamePlayer gP, PlayerMoveEvent event) {
        game.onPlayerMove(gP, event);
	}

}
