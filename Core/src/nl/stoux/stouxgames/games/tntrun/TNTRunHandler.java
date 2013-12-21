package nl.stoux.stouxgames.games.tntrun;

import org.bukkit.event.player.PlayerMoveEvent;

import nl.stoux.stouxgames.games.DefaultEventHandler;
import nl.stoux.stouxgames.player.GamePlayer;

public class TNTRunHandler extends DefaultEventHandler {

	private TNTRun tntRun;
	
	public TNTRunHandler(TNTRun tntRun) {
		super(tntRun);
		this.tntRun = tntRun;
	}
	
	@Override
	public void onPlayerMove(GamePlayer gP, PlayerMoveEvent event) {
		tntRun.onMove(gP, event);
	}

}
