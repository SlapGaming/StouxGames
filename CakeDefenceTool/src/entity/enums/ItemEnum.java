/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.enums;

import entity.collections.EnchantmentCollection;
import java.io.Serializable;

/**
 *
 * @author Leon
 */
public enum ItemEnum implements Serializable {
    
    //Armor -- Leather
    LEATHER_HELMET      ("Leather Helmet",      ItemType.Helmet,        298, 1, EC.GENERAL_ARMOR),
    LEATHER_CHESTPLATE  ("Leather Chestplate",  ItemType.Chestplate,    299, 1, EC.GENERAL_ARMOR),
    LEATHER_LEGGINGS    ("Leather Leggings",    ItemType.Legs,          300, 1, EC.GENERAL_ARMOR),
    LEATHER_BOOTS       ("Leather Boots",       ItemType.Boots,         301, 1, EC.BOOTS),
    
    //Armor -- Chainmail
    CHAINMAIL_HELMET    ("Chainmail Helmet",        ItemType.Helmet,    302, 1, EC.GENERAL_ARMOR),
    CHAINMAIL_CHESTPLATE("Chainmail Chestplate",    ItemType.Chestplate,    303, 1, EC.GENERAL_ARMOR),
    CHAINMAIL_LEGGINGS  ("Chainmail Leggings",      ItemType.Legs,      304, 1, EC.GENERAL_ARMOR),
    CHAINMAIL_BOOTS     ("Chainmail Boots",         ItemType.Boots,     305, 1, EC.BOOTS),
    
    //Armor -- Iron
    IRON_HELMET     ("Iron Helmet",     ItemType.Helmet,    306, 1, EC.GENERAL_ARMOR),
    IRON_CHESTPLATE ("Iron Chestplate", ItemType.Chestplate,    307, 1, EC.GENERAL_ARMOR),
    IRON_LEGGINGS   ("Iron Leggings",   ItemType.Legs,      308, 1, EC.GENERAL_ARMOR),
    IRON_BOOTS      ("Iron Boots",      ItemType.Boots,     309, 1, EC.BOOTS),
    
    //Armor -- Diamond
    DIAMOND_HELMET      ("Diamond Helmet",      ItemType.Helmet,    310, 1, EC.GENERAL_ARMOR),
    DIAMOND_CHESTPLATE  ("Diamond Chestplate",  ItemType.Chestplate,    311, 1, EC.GENERAL_ARMOR),
    DIAMOND_LEGGINGS    ("Diamond Leggings",    ItemType.Legs,      312, 1, EC.GENERAL_ARMOR),
    DIAMOND_BOOTS       ("Diamond Boots",       ItemType.Boots,     313, 1, EC.BOOTS),
    
    //Armor -- Gold
    GOLD_HELMET     ("Gold Helmet",     ItemType.Helmet,    314, 1, EC.GENERAL_ARMOR),
    GOLD_CHESTPLATE ("Gold Chestplate", ItemType.Chestplate,    315, 1, EC.GENERAL_ARMOR),
    GOLD_LEGGINGS   ("Gold Leggings",   ItemType.Legs,      316, 1, EC.GENERAL_ARMOR),
    GOLD_BOOTS      ("Gold Boots",      ItemType.Boots,     317, 1, EC.BOOTS),
    
    //Weapons
    IRON_SWORD      ("Iron Sword",      ItemType.Weapon, 267, 1, EC.MELEE),
    WOODEN_SWORD    ("Wooden Sword",    ItemType.Weapon, 268, 1, EC.MELEE),
    STONE_SWORD     ("Stone Sword",     ItemType.Weapon, 272, 1, EC.MELEE),
    DIAMOND_SWORD   ("Diamond Sword",   ItemType.Weapon, 276, 1, EC.MELEE),
    GOLD_SWORD      ("Gold Sword",      ItemType.Weapon, 283, 1, EC.MELEE),
    
    //Bow
    BOW     ("Bow",     ItemType.Weapon,    261, 1, EC.BOW),
    ARROW   ("Arrow",   ItemType.Arrow,     262, 64),
    
    //Food
    APPLE               ("Apple",               ItemType.Food, 260, 64),
    MUSHROOM_SOUP       ("Mushroom Soup",       ItemType.Food, 282, 1),
    BREAD               ("Bread",               ItemType.Food, 297, 64),
    COOKED_PORKCHOP     ("Cooked Porkchop",     ItemType.Food, 320, 64),
    GOLDEN_APPLE        ("Golden Apple",        ItemType.Food, 322, 64),
    SUPER_GOLDEN_APPLE  ("Super Golden Apple",  ItemType.Food, 322, 64),
    COOKED_FISH         ("Cooked Fish",         ItemType.Food, 350, 64),
    COOKIE              ("Cookie",              ItemType.Food, 357, 64),
    MELON_SLICE         ("Melon Slice",         ItemType.Food, 360, 64),
    STEAK               ("Steak",               ItemType.Food, 364, 64),
    COOKED_CHICKEN      ("Cooked Chicken",      ItemType.Food, 366, 64),
    ROTTEN_FLESH        ("Rotten Flesh",        ItemType.Food, 367, 64),
    CARROT              ("Carrot",              ItemType.Food, 391, 64),
    BAKED_POTATO        ("Baked Potato",        ItemType.Food, 393, 64),
    POISONOUS_POTATO    ("Poisonous Potato",    ItemType.Food, 394, 64),
    GOLDEN_CARROT       ("Golden Carrot",       ItemType.Food, 396, 64),
    PUMPKIN_PIE         ("Pumpkin Pie",         ItemType.Food, 400, 64),
    
    //Other
    SNOWBALL    ("Snowball",    ItemType.Other,     332, 16),
    MILK        ("Milk",        ItemType.Other,     335, 1),
    FISHING_ROD ("Fishing Rod", ItemType.Other,     346, 1),
    
    //Potion
    POTION      ("Potion",      ItemType.Potion,    373, 1);
    
    //Attributes
    private String displayname;
    private ItemType type;
    private int itemID;
    private int maxAmount;
    private int allowedEnchants;
    
    
    private ItemEnum(String displayname, ItemType type, int itemID, int maxAmount) {
        this.displayname = displayname;
        this.type = type;
        this.itemID = itemID;
        this.maxAmount = maxAmount;
        this.allowedEnchants = 0;
    }
    
    private ItemEnum(String displayname, ItemType type, int itemID, int maxAmount, EC allowedEnchants) {
        this.displayname = displayname;
        this.type = type;
        this.itemID = itemID;
        this.maxAmount = maxAmount;
        this.allowedEnchants = allowedEnchants.group;
    }

    public String getDisplayname() {
        return displayname;
    }

    public int getItemID() {
        return itemID;
    }

    public int getMaxAmount() {
        return maxAmount;
    }
    
    public boolean hasEnchants() {
        if (allowedEnchants == 0) return false;
        else return true;
    }
    
    public EnchantmentType[] getAllowedEnchants() {
        switch(allowedEnchants) {
            case 1: return EnchantmentCollection.ALL;
            case 2: return EnchantmentCollection.MELEE;
            case 3: return EnchantmentCollection.BOW;
            case 4: return EnchantmentCollection.GENERAL_ARMOR;
            case 5: return EnchantmentCollection.BOOTS;
        }
        return null;
    }

    public ItemType getType() {
        return type;
    }
    
    private enum EC {
        
        NONE(0),
        ALL(1),
        MELEE(2),
        BOW(3),
        GENERAL_ARMOR(4),
        BOOTS(5);
        
        private int group;
        
        private EC(int group) {
            this.group = group;
        }
        
    }
    
}
