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

public class ParkourHandler extends DefaultEventHandler {

	private Parkour parkour;
	
	public ParkourHandler(Parkour parkour) {
		super(parkour);
		this.parkour = parkour;
	}
	
	@Override
	public void onPlayerMove(GamePlayer gP, PlayerMoveEvent event) {
		parkour.onPlayerMove((ParkourPlayer) gP, event);
	}
	
	@Override
	public void onPlayerInteract(GamePlayer gP, PlayerInteractEvent event) {
		parkour.onPlayerInteract((ParkourPlayer) gP, event);
	}
	
	@Override
	public void onPlayerDied(GamePlayer gP, PlayerDeathEvent event) {
		ParkourPlayer pp = (ParkourPlayer) gP;
		if (pp.isOnParkourMap()) pp.getRun().incrementFails();
	}
	
	@Override
	public void onPlayerRespawn(GamePlayer gP, PlayerRespawnEvent event) {
		final ParkourPlayer pp = (ParkourPlayer) gP;
		if (pp.isOnParkourMap()) { //Check if on a map
			event.setRespawnLocation(pp.getCurrentMap().getLobby()); //Set the respawn to lobby
			final ParkourRun run = pp.getRun();
			if (run.hasStarted() && !run.isFinished()) { //Check if currently running
				_T.runLater_Sync(new Runnable() {
					@Override
					public void run() {
						if (run != null) {
							if (run.hasStarted() && !run.isFinished()) { //Check if currently running
								pp.getCurrentMap().teleportPlayerBackToCheckpoint(pp, true, true);
							}
						} 
					}
				}, 1);
			}
		} else {
			event.setRespawnLocation(pp.getGame().getLobby());
		}
	}
	
	@Override
	public void onPlayerDamage(GamePlayer gP, EntityDamageEvent event) {
		if (event.getCause() == DamageCause.FALL) {
			event.setCancelled(true);
		}
	}

}
