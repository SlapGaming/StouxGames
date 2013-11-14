package nl.stoux.stouxgames.games.tntrun;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TNTRunBlockTask extends BukkitRunnable {

	private ConcurrentHashMap<Block, Integer> removeBlocks;
	private HashSet<Block> remove;
	
	private ConcurrentHashMap<Player, Integer> playerTicks;
	
	private TNTRun tntRun;
	private boolean hasTNT;
	
	public TNTRunBlockTask(TNTRun tntRun, boolean hasTNT) {
		this.hasTNT = hasTNT;
		this.tntRun = tntRun;
		remove = new HashSet<>();
		removeBlocks = new ConcurrentHashMap<>();
		playerTicks = new ConcurrentHashMap<>();
	}

	@Override
	public void run() {
		for (Entry<Block, Integer> entry : removeBlocks.entrySet()) {
			int x = entry.getValue();
			x--; //Decrease ticks left till destroyed
			entry.setValue(x);
			if (x < 1) {
				//Remove the block
				remove.add(entry.getKey()); //Add block to remove list
			} 
		}
		for (Block b : remove) { //Loop thru blocks
			removeBlocks.remove(b); //Remove the block from the map
			b.setType(Material.AIR); //Set the block to air
			if (hasTNT) { //If TNT under block
				b.getRelative(BlockFace.DOWN).setType(Material.AIR); //Set underlying block to Air
			}
		}
		for (Entry<Player, Integer> entry : playerTicks.entrySet()) { //Loop thru players
			int x = entry.getValue(); x++; entry.setValue(x); //Increase their value with 1
			if (x > 6) { //If last movement was more than 6 ticks ago
				findClosestBlock(entry.getKey()); //Start removing blocks
			}
		}
	}
	
	/**
	 * Find the closest game block near a player
	 * @param p The player
	 */
	public void findClosestBlock(Player p) {
		Block b = p.getLocation().subtract(0, 1, 0).getBlock(); //Get the block under the player
		if (b.getType() != Material.AIR) { 
			/* Check if the block he/she is standing on is air yet (otherwise that one just needs to be removed)
			 * This scenario is only safeguard & should not be able to be recreated in game.
			 */
			if (isGameBlock(b)) {
				b.setType(Material.AIR);
			}
			return;
		}
		int x = 0;
		BlockFace bF = BlockFace.EAST;
		while (true) {
			switch (x) {
			case 0: bF = BlockFace.EAST; break;
			case 1: bF = BlockFace.NORTH_EAST; break;
			case 2: bF = BlockFace.NORTH; break;
			case 3: bF = BlockFace.NORTH_WEST; break;
			case 4: bF = BlockFace.WEST; break;
			case 5: bF = BlockFace.SOUTH_WEST; break;
			case 6: bF = BlockFace.SOUTH; break;
			case 7: bF = BlockFace.SOUTH_EAST; break;
			default: return;
			}
			Block relativeBlock = b.getRelative(bF);
			if (relativeBlock.getType() != Material.AIR) {
				if (isGameBlock(relativeBlock)) {
					relativeBlock.setType(Material.AIR);
					if (hasTNT) {
						relativeBlock.getRelative(BlockFace.DOWN).setType(Material.AIR);
					}
					return;
				}
			}
			x++;
		}
	}
	
	/**
	 * Check if the block is a floor block
	 * @param b The block
	 * @return is a game block
	 */
	private boolean isGameBlock(Block b) {
		return tntRun.containsBlock(b.getX(), b.getY(), b.getZ());
	}
	
	/**
	 * Add a block to the be-removed list
	 * @param b The block
	 */
	public void addBlock(Block b) {
		if (!removeBlocks.contains(b)) { //If block not registered yet
			removeBlocks.put(b, 4); //Add to blocks to be removed
		}
	}
	
	/**
	 * A player moved, set ticks -> 0
	 * @param p The player
	 */
	public void playerMoved(Player p) {
		playerTicks.put(p, 0);
	}
	
	
	/**
	 * Remove a player from the hashmap
	 * @param p The player
	 */
	public void playerDied(Player p) {
		playerTicks.remove(p);
	}

}
