package nl.stoux.stouxgames.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class SavedRegion {

	//The saved blocks
	private HashMap<Block, Material> blocksMap;
	
	//The name of the region
	private String regionname;
	
	/**
	 * Save the region
	 * @param blocks All the blocks in the region
	 * @param regionname the region's name
	 */
	public SavedRegion(ArrayList<Block> blocks, String regionname) {
		this.regionname = regionname;
		blocksMap = new HashMap<>();
		for (Block block : blocks) {
			blocksMap.put(block, block.getType());
		}
	}
	
	/**
	 * Get the region's name
	 * @return the region
	 */
	public String getRegionname() {
		return regionname;
	}
	
	/**
	 * Restore the blocks
	 */
	public void restore() {
		for (Entry<Block, Material> entry : blocksMap.entrySet()) { //For all entry's
			entry.getKey().setType(entry.getValue()); //Set block to saved material
		}
	}
	
	/**
	 * Set the whole region to a particular block
	 * @param m The material
	 */
	public void setRegionToMaterial(Material m) {
		for (Block block : blocksMap.keySet()) {
			block.setType(m);
		}
	}
	
}
