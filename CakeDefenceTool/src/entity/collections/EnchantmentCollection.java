/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.collections;

import entity.enums.EnchantmentType;
import java.io.Serializable;

/**
 *
 * @author Leon
 */
public class EnchantmentCollection implements Serializable {
    
    public static EnchantmentType[] ALL = EnchantmentType.values();
    public static EnchantmentType[] MELEE = new EnchantmentType[] {
        EnchantmentType.DAMAGE_ALL,
        EnchantmentType.DAMAGE_UNDEAD,
        EnchantmentType.DAMAGE_ARTHROPODS,
        EnchantmentType.KNOCKBACK,
        EnchantmentType.FIRE_ASPECT,
        EnchantmentType.LOOT_BONUS_MOBS,
        EnchantmentType.DURABILITY
    };
    public static EnchantmentType[] BOW = new EnchantmentType[] {
        EnchantmentType.ARROW_DAMAGE,
        EnchantmentType.ARROW_FIRE,
        EnchantmentType.ARROW_INFINITE,
        EnchantmentType.ARROW_KNOCKBACK,
        EnchantmentType.DURABILITY
    };
    public static EnchantmentType[] GENERAL_ARMOR = new EnchantmentType[] {
        EnchantmentType.PROTECTION_ENVIRONMENTAL,
        EnchantmentType.PROTECTION_EXPLOSIONS,
        EnchantmentType.PROTECTION_FIRE,
        EnchantmentType.PROTECTION_PROJECTILE,
        EnchantmentType.DURABILITY
    };
    public static EnchantmentType[] BOOTS = new EnchantmentType[] {
        EnchantmentType.PROTECTION_ENVIRONMENTAL,
        EnchantmentType.PROTECTION_EXPLOSIONS,
        EnchantmentType.PROTECTION_FIRE,
        EnchantmentType.PROTECTION_PROJECTILE,
        EnchantmentType.DURABILITY,
        EnchantmentType.PROTECTION_FALL
    };
    
}
