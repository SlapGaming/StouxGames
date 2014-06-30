package nl.stoux.stouxgames.games.parkour;

import nl.stoux.stouxgames.games.DefaultEventHandler;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer.ParkourRun;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._T;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ParkourHandler extends DefaultEventHandler<Parkour, ParkourPlayer> {

	public ParkourHandler(Parkour parkour) {
		super(parkour);
	}
	
	@Override
	public void onPlayerMove(ParkourPlayer gP, PlayerMoveEvent event) {
		game.onPlayerMove(gP, event);
	}
	
	@Override
	public void onPlayerInteract(ParkourPlayer gP, PlayerInteractEvent event) {
        game.onPlayerInteract(gP, event);
	}
	
	@Override
	public void onPlayerDied(ParkourPlayer gP, PlayerDeathEvent event) {
		if (gP.isOnParkourMap()) gP.getRun().incrementFails();
	}
	
	@Override
	public void onPlayerRespawn(final ParkourPlayer gP, PlayerRespawnEvent event) {
		if (gP.isOnParkourMap()) { //Check if on a map
			event.setRespawnLocation(gP.getCurrentMap().getLobby()); //Set the respawn to lobby
			final ParkourRun run = gP.getRun();
			if (run.hasStarted() && !run.isFinished()) { //Check if currently running
				_T.runLater_Sync(new Runnable() {
					@Override
					public void run() {
						if (run != null) {
							if (run.hasStarted() && !run.isFinished()) { //Check if currently running
                                gP.getCurrentMap().teleportPlayerBackToCheckpoint(gP, true, true);
							}
						} 
					}
				}, 1);
			}
		} else {
			event.setRespawnLocation(gP.getGame().getLobby());
		}
	}
	
	@Override
	public void onPlayerDamage(ParkourPlayer gP, EntityDamageEvent event) {
		if (event.getCause() == DamageCause.FALL) {
			event.setCancelled(true);
		}
	}

}
