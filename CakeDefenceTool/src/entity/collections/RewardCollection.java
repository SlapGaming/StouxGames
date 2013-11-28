/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.collections;

import entity.instance.Item;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Leon
 */
public class RewardCollection implements Serializable {
    
    private static final long serialVersionUID = 2562797475018348412L;
    
    private ArrayList<Item> items;
    
    public RewardCollection() {
        items = new ArrayList<>();
    }

    public RewardCollection(ArrayList<Item> items) {
        this.items = items;
    }
    
    public void addReward(Item item) {
        items.add(item);
    }
    
    public ArrayList<Item> getItems() {
        return items;
    }
    
    public void removeItem(Item item) {
        items.remove(item);
    }
        
}
