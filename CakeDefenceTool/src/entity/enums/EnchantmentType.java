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
public enum EnchantmentType implements Serializable {
    
    //Melee
    DAMAGE_ALL("Sharpness", 5),
    DAMAGE_UNDEAD("Smite", 5),
    DAMAGE_ARTHROPODS("Bane of Arthropods", 5),
    KNOCKBACK("Knockback", 2),
    FIRE_ASPECT("Fire Aspect", 2),
    LOOT_BONUS_MOBS("Looting", 3),
    
    //Bows
    ARROW_DAMAGE("Power", 5),
    ARROW_INFINITE("Infinity", 1),
    ARROW_FIRE("Flame", 1),
    ARROW_KNOCKBACK("Punch", 2),
    
    //General Armor
    PROTECTION_ENVIRONMENTAL("Protection", 4),
    PROTECTION_EXPLOSIONS("Blast Protection", 4),
    PROTECTION_PROJECTILE("Projectile Protection", 4),
    PROTECTION_FIRE("Fire Protection", 4),
    THORNS("Thorns", 3),
    
    //Specific Armor
    PROTECTION_FALL("Feather Falling", 4),
    
    //Other
    DURABILITY("Unbreaking", 3);
    
    private String displayname;
    private int maxLevel;
    
    private EnchantmentType(String displayname, int maxLevel) {
        this.displayname = displayname;
        this.maxLevel = maxLevel;
    }

    public String getDisplayname() {
        return displayname;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
    
}
