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
public enum PotionItemType implements Serializable {
    
    //Regeneration
    REGEN("Regeneration", 8193, 16385),
    REGEN_EXTENDED("Regeneration (Extended)", 8257, 16449),
    REGEN_2("Regeneration II", 8225, 16417),    
    REGEN_2_EXTENDED("Regeneration II (Extended)", 8289, false),    
    
    //Swiftness
    SWIFT("Swiftness", 8194, 16386),
    SWIFT_EXTENDED("Swiftness (Extended)", 8258, 16418),
    SWIFT_2("Swiftness II", 8226, 16450),
    SWIFT_2_EXTENDED("Swiftness II (Extended)", 8290, false),
        
    //Fire Resistance
    FIRE_RES("Fire Resistance", 8195, 16387),
    FIRE_RES_EXTENDED("Fire Resistance (Extended)", 8259, 16451),
        
    //Healing
    HEALING("Healing", 8197, 16389),
    HEALING_2("Healing II", 8229, 16421),
    
    //Night Vision
    NIGHT("Night Vision", 8198, 16390),
    NIGHT_EXTENDED("Night Vision (Extended)", 8262, 16454),
    
    //Strength 
    STRENGTH("Strength ", 8201, 16393),
    STRENGTH_EXTENDED("Strength (Extended)", 8265, 16457),
    STRENGTH_2("Strength II", 8233, 16425),
    STRENGTH_2_EXTENDED("Strength II (Extended)", 8297, false);
    
    
    private String displayname;
    private int dataValue;
    private int splashValue;
    
    private PotionItemType(String displayname, int dataValue, boolean splash) {
        this.displayname = displayname;
        this.dataValue = dataValue;
        splashValue = -1;
    }
    
    private PotionItemType(String displayname, int dataValue, int splashValue) {
        this.displayname = displayname;
        this.dataValue = dataValue;
        this.splashValue = splashValue;
    }
    
    
    

    public int getDataValue() {
        return dataValue;
    }

    public String getDisplayname() {
        return displayname;
    }

    public int getSplashValue() {
        return splashValue;
    }
    
    /**
     * Check if normal potion
     * @return yes/no
     */
    public boolean isNormal() {
        if (dataValue == -1) return false;
        else return true;
    }
    
    /**
     * Check if splash potion
     * @return yes/no
     */
    public boolean isSplash() {
        if (splashValue == -1) return false;
        else return true;
    }
    
    
}
