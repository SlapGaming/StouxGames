/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.instance;

import entity.enums.PotionEffectType;
import java.io.Serializable;

/**
 *
 * @author Leon
 */
public class PotionEffect implements Serializable {
    
    private PotionEffectType type;
    private int level;
    private int length;

    public PotionEffect(PotionEffectType type, int level, int length) {
        this.type = type;
        this.level = level;
        this.length = length;
    }

    public PotionEffectType getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
    
    public boolean isInfinite() {
        return (level == -1);
    }

    @Override
    public String toString() {
        return type.getDisplayname() + " | Level: " + level + " | Length: " + getLengthString(length);
    }
    
    public static String getLengthString(int length) {
        if (length == -1) {
            return "infinite";
        } else {
           return + length + "s";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PotionEffect) {
            PotionEffect potionEffect = (PotionEffect) obj;
            if (potionEffect.getType() == type && potionEffect.getLength() == length && potionEffect.getLevel() == level) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 41 * hash + this.level;
        hash = 41 * hash + this.length;
        return hash;
    }
    
    
    
    
    
    
}
