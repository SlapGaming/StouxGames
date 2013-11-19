/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.collections;

import entity.enums.ItemEnum;
import entity.enums.ItemType;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Leon
 */
public class ItemCollection implements Serializable {
    
    //Per armor piece
    final public static ItemEnum[] BOOTS = getTypeCollection(ItemType.Boots);
    final public static ItemEnum[] LEGGINGS = getTypeCollection(ItemType.Legs);
    final public static ItemEnum[] CHESTPLATE = getTypeCollection(ItemType.Chestplate);
    final public static ItemEnum[] HELMET = getTypeCollection(ItemType.Helmet);
    
    final public static ItemEnum[] CHAINMAIL = new ItemEnum[] {
        ItemEnum.CHAINMAIL_BOOTS, ItemEnum.CHAINMAIL_CHESTPLATE, ItemEnum.CHAINMAIL_HELMET, ItemEnum.CHAINMAIL_LEGGINGS
    };
    final public static ItemEnum[] DIAMOND = new ItemEnum[] {
        ItemEnum.DIAMOND_BOOTS, ItemEnum.DIAMOND_CHESTPLATE, ItemEnum.DIAMOND_HELMET, ItemEnum.DIAMOND_LEGGINGS
    };
    final public static ItemEnum[] GOLD = new ItemEnum[] {
        ItemEnum.GOLD_BOOTS, ItemEnum.GOLD_CHESTPLATE, ItemEnum.GOLD_HELMET, ItemEnum.GOLD_LEGGINGS
    };
    final public static ItemEnum[] IRON = new ItemEnum[] {
        ItemEnum.IRON_BOOTS, ItemEnum.IRON_CHESTPLATE, ItemEnum.IRON_HELMET, ItemEnum.IRON_LEGGINGS
    };
    final public static ItemEnum[] LEATHER = new ItemEnum[] {
        ItemEnum.LEATHER_BOOTS, ItemEnum.LEATHER_CHESTPLATE, ItemEnum.LEATHER_HELMET, ItemEnum.LEATHER_LEGGINGS
    };
    
    final public static ItemEnum[] SWORDS = new ItemEnum[] {
        ItemEnum.DIAMOND_SWORD, ItemEnum.GOLD_SWORD, ItemEnum.IRON_SWORD, ItemEnum.STONE_SWORD, ItemEnum.WOODEN_SWORD
    };
    final public static ItemEnum[] WEAPON = getTypeCollection(ItemType.Weapon);
    final public static ItemEnum[] BOW_ARROW = new ItemEnum[] {
        ItemEnum.BOW, ItemEnum.ARROW
    };
    final public static ItemEnum[] BOW = new ItemEnum[] {
        ItemEnum.BOW
    };
    final public static ItemEnum[] ARROW = new ItemEnum[] {
        ItemEnum.ARROW
    };
    
    final public static ItemEnum[] FOOD = getTypeCollection(ItemType.Food);
    final public static ItemEnum[] OTHER = getTypeCollection(ItemType.Other);
    
    final public static ItemEnum[] POTION = new ItemEnum[] {
        ItemEnum.POTION
    };
    
    
    private static ItemEnum[] getTypeCollection(ItemType type) {
        ArrayList<ItemEnum> items = new ArrayList<>();
        for (ItemEnum item : ItemEnum.values()) {
            if (type == item.getType()) {
                items.add(item);
            }
        }
        return items.toArray(new ItemEnum[items.size()]);
    }
    
}
