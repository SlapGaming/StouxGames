/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.instance;

import entity.enums.EnchantmentType;
import entity.enums.ItemEnum;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Leon
 */
public class Item implements Serializable {

    private static final long serialVersionUID = 4080595211072787051L;
    
    private ItemEnum itemType; //The type of Item
    private int amount; //Amount of this item
    private int durability;
    private HashMap<EnchantmentType, Integer> enchants; //Key: Type | Value: Level

    public Item(ItemEnum itemType, int amount, HashMap<EnchantmentType, Integer> enchants, int durability) {
        this.itemType = itemType;
        this.amount = amount;
        this.enchants = enchants;
        this.durability = durability;
    }

    public Item(ItemEnum itemType, int amount) {
        this.itemType = itemType;
        this.amount = amount;
        this.enchants = null;
        durability = -1;
    }

    public int getAmount() {
        return amount;
    }

    public boolean hasEnchants() {
        return (enchants != null);
    }

    public HashMap<EnchantmentType, Integer> getEnchants() {
        return enchants;
    }

    public String getEnchantsString() {
        String extra = "";
        if (hasEnchants()) {
            for (Map.Entry<EnchantmentType, Integer> entry : getEnchants().entrySet()) {
                if (!extra.equals("")) {
                    extra = extra + "\n";
                }
                extra = extra + entry.getKey().getDisplayname() + " | Level: " + entry.getValue();
            }
        }
        if (hasDurability()) {
            if (!extra.equals("")) {
                extra = extra + "\n";
            }
            extra = extra + "Durability: " + getDurability() + "%";
        }
        return extra;
    }

    public ItemEnum getItemType() {
        return itemType;
    }

    public boolean hasDurability() {
        return (durability != -1);
    }

    public int getDurability() {
        return durability;
    }

    public boolean isPotion() {
        return (this instanceof PotionItem);
    }

    public PotionItem getPotion() {
        if (this instanceof PotionItem) {
            return (PotionItem) this;
        }
        return null;
    }
}
