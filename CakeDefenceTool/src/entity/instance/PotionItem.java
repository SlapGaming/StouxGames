/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.instance;

import entity.enums.ItemEnum;
import entity.enums.PotionItemType;
import java.io.Serializable;

/**
 *
 * @author Leon
 */
public class PotionItem extends Item implements Serializable {
    
    private static final long serialVersionUID = 6464081121938169628L;
    
    private PotionItemType type;
    private boolean splash;

    public PotionItem(PotionItemType type, boolean splash) {
        super(ItemEnum.POTION, 1);
        this.type = type;
        this.splash = splash;
    }

    public PotionItemType getType() {
        return type;
    }

    public boolean isSplash() {
        return splash;
    }

    @Override
    public String toString() {
        String returnString = type.getDisplayname();
        if (splash) {
            returnString = returnString = " (Splash)";
        }
        return returnString;
    }
    
    
    
}
