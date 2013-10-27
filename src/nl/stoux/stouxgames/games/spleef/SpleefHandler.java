package nl.stoux.stouxgames.games.spleef;

import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import nl.stoux.stouxgames.games.EventHandler;
import nl.stoux.stouxgames.player.GamePlayer;

public class SpleefHandler extends EventHandler {

	private Spleef spleef;
	
	/**
	 * A Spleef Event Handler
	 * @param spleef The game of spleef
	 */
	public SpleefHandler(Spleef spleef) {
		super(spleef);
		this.spleef = spleef;
	}
	
	@Override
	public void onBlockDamage(GamePlayer gP, BlockDamageEvent event) {
		spleef.onPlayerBlockInteract(gP, event.getBlock());
	}
	
	@Override
	public void onPlayerMove(GamePlayer gP, PlayerMoveEvent event) {
		spleef.onPlayerMove(gP, event);
	}
	

}
