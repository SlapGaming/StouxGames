/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.instance;

import entity.enums.MobType;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Leon
 */
public class Mob implements Serializable {
    
    private MobType type; //Type
    private int amount; //Number of mobs
    private Collection<PotionEffect> potionEffects;

    public Mob(MobType type, int amount, Collection<PotionEffect> potionEffects) {
        this.type = type;
        this.amount = amount;
        this.potionEffects = potionEffects;
    }
    
    public Mob(MobType type, int amount) {
        this.type = type;
        this.amount = amount;
        potionEffects = null;
    }
    
    public boolean hasPotionEffects() {
        if (potionEffects == null) return false;
        else return true;
    }

    public MobType getMobType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }         

    public CompareResult compare(Object obj) {
        CompareResult result = CompareResult.DifferentClass;
        if (obj instanceof Mob) {
            result = CompareResult.SameClass;
            Mob mob = (Mob) obj;
            if (mob.getMobType() == type) {
                result = CompareResult.SameType;
                if (amount == mob.getAmount()) {
                    result = CompareResult.SameTypeAmount;
                }
                boolean potionsMatch;
                if (potionEffects == null && mob.getPotionEffects() == null) { //Both null -> Same
                    potionsMatch = true;
                } else if (potionEffects == null || mob.getPotionEffects() == null) { //Not both null -> False
                    potionsMatch = false;
                } else { //Both filled in -> Compare
                    Collection<PotionEffect> foundPotionEffects = mob.getPotionEffects();
                    if (foundPotionEffects.size() == potionEffects.size()) { //same size
                        boolean allFound = true;
                        for (PotionEffect eff : foundPotionEffects) {
                            boolean found = false;
                            for (PotionEffect ownEff : potionEffects) {
                                if (eff.equals(ownEff)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) allFound = false;
                        }
                        if (allFound) potionsMatch = true;
                        else potionsMatch = false;
                    } else {
                        potionsMatch = false;
                    }
                }
                if (potionsMatch) {
                    if (result == CompareResult.SameTypeAmount) result = CompareResult.Same;
                    else if (result == CompareResult.SameType) result = CompareResult.SameTypePotions;
                }
            }
        }
        return result;
    }
    
    public enum CompareResult {
        DifferentClass,
        SameClass,
        SameType,
        SameTypeAmount,
        SameTypePotions,
        Same
    }

    
    
 
}
