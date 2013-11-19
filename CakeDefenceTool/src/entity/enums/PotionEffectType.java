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
public enum PotionEffectType implements Serializable {
    
    //Mobs & Players
    SPEED               ("Speed Boost", true),
    SLOW                ("Slowness", true),
    INCREASE_DAMAGE     ("Strength Boost", true),
    JUMP                ("Jump Boost", true),
    CONFUSION           ("Nausea/Confusion", true),
    REGENERATION        ("Regeneration", true),
    DAMAGE_RESISTANCE   ("Damage Resistance", true),
    FIRE_RESISTANCE     ("Fire Resistance", true),
    WEAKNESS            ("Weakness", true),
    ABSORPTION          ("Absorption", true),
    
    //Player only
    BLINDNESS           ("Blindness", false),
    NIGHT_VISION        ("Night Vision", false),
    HUNGER              ("Hunger", false);
    
    private String displayname;
    private boolean all;
    private PotionEffectType(String displayname, boolean all) {
        this.displayname = displayname;
    }

    public boolean onPlayerOnly() {
        return !all;
    }
    
    public String getDisplayname() {
        return displayname;
    }
    
    
}
