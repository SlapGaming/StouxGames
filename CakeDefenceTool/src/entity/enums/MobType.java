/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.enums;

import java.io.Serializable;

/**
 *
 * @author Leon
 */
public enum MobType implements Serializable {
    
	//Zombies
	BABY_ZOMBIE("Baby Zombie"),
	ZOMBIE("Zombie"),
	ZOMBIE_VILLAGER("Zombie Villager"),
	BABY_ZOMBIE_PIGMAN("Baby Pigman"),
	ZOMBIE_PIGMAN("Zombie Pigman"),
	
	//Skeletons
	SKELETON("Skeleton"),
	WITHER_SKELETON("Wither Skeleton"),
		
	//Slime
	SLIME("Slime"),
	MAGMA_CUBE("Magma Cube"),
	
	//Spider
	SPIDER("Spider"),
	SPIDER_JOCKEY("Spider Jockey"),
	SPIDER_WITHER_JOCKEY("Wither Jockey"),
	CAVE_SPIDER("Cave Spider"),
	
        //Creepers
	CREEPER("Creeper"),
	POWERED_CREEPER("Powered Creeper"),
        
        //Misc
	SILVERFISH("Silverfish"),
	WITCH("Witch"),
	IRON_GOLEM("Iron Golem");
	
	
	private String displayname;
	
	private MobType(String displayname) {
		this.displayname = displayname;
	}
        
        public String getDisplayname() {
            return displayname;
        }
        
}
