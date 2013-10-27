package nl.stoux.stouxgames.games.sonic;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import nl.stoux.stouxgames.games.EventHandler;
import nl.stoux.stouxgames.player.GamePlayer;

public class SonicHandler extends EventHandler {

	private Sonic sonic;
	
	public SonicHandler(Sonic sonic) {
		super(sonic);
		this.sonic = sonic;
	}
	
	@Override
	public void onPlayerMove(GamePlayer gP, PlayerMoveEvent event) {
		sonic.onPlayerMove(gP, event);
	}
	
	@Override
	public void onPlayerInteract(GamePlayer gP, PlayerInteractEvent event) {
		sonic.onPlayerInteract(gP, event);
	}

}
