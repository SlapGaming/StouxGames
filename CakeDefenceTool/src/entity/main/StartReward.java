/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.main;

import entity.collections.RewardCollection;
import entity.instance.Item;
import java.io.Serializable;

/**
 *
 * @author Leon
 */
public class StartReward implements Serializable {
    
    private static final long serialVersionUID = 876382336533960142L;
    
    private Item boots;
    private Item legs;
    private Item chest;
    private Item head;
    
    private RewardCollection startInventory;

    public StartReward() {
        
    }

    public StartReward(Item boots, Item legs, Item chest, Item head, RewardCollection startInventory) {
        this.boots = boots;
        this.legs = legs;
        this.chest = chest;
        this.head = head;
        this.startInventory = startInventory;
    }

    public Item getBoots() {
        return boots;
    }

    public Item getChest() {
        return chest;
    }

    public Item getHead() {
        return head;
    }

    public Item getLegs() {
        return legs;
    }

    public RewardCollection getStartInventory() {
        return startInventory;
    }

    public void setBoots(Item boots) {
        this.boots = boots;
    }

    public void setChest(Item chest) {
        this.chest = chest;
    }

    public void setHead(Item head) {
        this.head = head;
    }

    public void setLegs(Item legs) {
        this.legs = legs;
    }

    public void setStartInventory(RewardCollection startInventory) {
        this.startInventory = startInventory;
    }
    
}
