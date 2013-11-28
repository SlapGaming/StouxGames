/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.collections;

import entity.instance.Mob;
import java.io.Serializable;
import java.util.HashSet;

/**
 *
 * @author Leon
 */
public class Round implements Serializable {
    
    private static final long serialVersionUID = -9194509654974120243L;
    
    //Mobs
    private HashSet<Mob> mobs;
    
    //Rewards
    private RewardCollection everyone;
    private RewardCollection bad;
    private RewardCollection mediocre;
    private RewardCollection good;

    public Round(HashSet<Mob> mobs, RewardCollection everyone, RewardCollection bad, RewardCollection mediocre, RewardCollection good) {
        this.mobs = mobs;
        this.everyone = everyone;
        this.bad = bad;
        this.mediocre = mediocre;
        this.good = good;
    }

    public RewardCollection getBad() {
        return bad;
    }

    public RewardCollection getEveryone() {
        return everyone;
    }

    public RewardCollection getGood() {
        return good;
    }

    public RewardCollection getMediocre() {
        return mediocre;
    }

    public HashSet<Mob> getMobs() {
        return mobs;
    }

    public void setMobs(HashSet<Mob> mobs) {
        this.mobs = mobs;
    }

    public void setBad(RewardCollection bad) {
        this.bad = bad;
    }

    public void setMediocre(RewardCollection mediocre) {
        this.mediocre = mediocre;
    }

    public void setGood(RewardCollection good) {
        this.good = good;
    }

    public void setEveryone(RewardCollection everyone) {
        this.everyone = everyone;
    }
    
    
    
}
