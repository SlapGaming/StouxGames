package nl.stoux.stouxgames.games.spleef;

import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import nl.stoux.stouxgames.games.DefaultEventHandler;
import nl.stoux.stouxgames.player.GamePlayer;

public class SpleefHandler extends DefaultEventHandler<Spleef, GamePlayer> {

	/**
	 * A Spleef Event Handler
	 * @param spleef The game of spleef
	 */
	public SpleefHandler(Spleef spleef) {
		super(spleef);
	}
	
	@Override
	public void onBlockDamage(GamePlayer gP, BlockDamageEvent event) {
		game.onPlayerBlockInteract(gP, event.getBlock());
	}
	
	@Override
	public void onPlayerMove(GamePlayer gP, PlayerMoveEvent event) {
        game.onPlayerMove(gP, event);
	}
	

}
