/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.collections;

import entity.enums.MobType;
import java.io.Serializable;

/**
 *
 * @author Leon
 */
public class MobTypeCollection implements Serializable {
    
    public static MobType[] ZOMBIES = new MobType[]{
        MobType.BABY_ZOMBIE, MobType.ZOMBIE, MobType.ZOMBIE_VILLAGER, MobType.BABY_ZOMBIE_PIGMAN, MobType.ZOMBIE_PIGMAN
    };
    public static MobType[] SKELETONS = new MobType[] {
        MobType.SKELETON, MobType.WITHER_SKELETON
    };
    public static MobType[] SLIMES = new MobType[] {
        MobType.SLIME, MobType.MAGMA_CUBE
    };
    public static MobType[] SPIDERS = new MobType[] {
        MobType.CAVE_SPIDER, MobType.SPIDER, MobType.SPIDER_JOCKEY, MobType.SPIDER_WITHER_JOCKEY
    };
    public static MobType[] CREEPERS = new MobType[] {
        MobType.CREEPER, MobType.POWERED_CREEPER
    };
    public static MobType[] MISC = new MobType[] {
        MobType.SILVERFISH, MobType.WITCH
    };
    
    
}
