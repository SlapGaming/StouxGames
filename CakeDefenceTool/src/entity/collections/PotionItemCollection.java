/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.collections;

import entity.enums.PotionItemType;
import java.io.Serializable;

/**
 *
 * @author Leon
 */
public class PotionItemCollection implements Serializable {
    
    public static PotionItemType[] REGEN = new PotionItemType[] {
        PotionItemType.REGEN, PotionItemType.REGEN_2, PotionItemType.REGEN_2_EXTENDED, PotionItemType.REGEN_EXTENDED
    };
    public static PotionItemType[] SWIFT = new PotionItemType[] {
        PotionItemType.SWIFT, PotionItemType.SWIFT_2, PotionItemType.SWIFT_2_EXTENDED, PotionItemType.SWIFT_EXTENDED
    };
    public static PotionItemType[] FIRE_RES = new PotionItemType[] {
        PotionItemType.FIRE_RES, PotionItemType.FIRE_RES_EXTENDED
    };
    public static PotionItemType[] HEALING = new PotionItemType[] {
        PotionItemType.HEALING, PotionItemType.HEALING_2
    };
    public static PotionItemType[] NIGHT = new PotionItemType[] {
        PotionItemType.NIGHT, PotionItemType.NIGHT_EXTENDED
    };
    public static PotionItemType[] STRENGTH = new PotionItemType[] {
        PotionItemType.STRENGTH, PotionItemType.STRENGTH_2, PotionItemType.STRENGTH_2_EXTENDED, PotionItemType.STRENGTH_EXTENDED
    };
    
}