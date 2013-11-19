/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.interfaces;

import entity.instance.Item;
import entity.instance.PotionItem;

/**
 *
 * @author Leon
 */
public interface RewardCallable {
    
    public void addReward(Item item);
        
    public void update();
    
}
