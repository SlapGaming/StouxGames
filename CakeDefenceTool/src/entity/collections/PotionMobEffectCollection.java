/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.collections;

import entity.enums.PotionEffectType;
import java.io.Serializable;

/**
 *
 * @author Leon
 */
public class PotionMobEffectCollection implements Serializable {
    
    private static final long serialVersionUID = 7320966325887460384L;
    
    public static PotionEffectType[] MOBONLY = new PotionEffectType[] {
        PotionEffectType.SPEED, PotionEffectType.SLOW, PotionEffectType.INCREASE_DAMAGE, PotionEffectType.JUMP,
        PotionEffectType.REGENERATION, PotionEffectType.DAMAGE_RESISTANCE,
        PotionEffectType.FIRE_RESISTANCE, PotionEffectType.WEAKNESS, PotionEffectType.ABSORPTION
    };
    public static PotionEffectType[] ALL = PotionEffectType.values();
    
}
