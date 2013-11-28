/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.main;

import entity.collections.Round;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Leon
 */
public class Routine implements Serializable {
    
    private static final long serialVersionUID = 6422160254393652059L;
    
    private GeneralSettings settings;
    private StartReward startReward;
    private ArrayList<Round> rounds;
    
    public Routine() {
        rounds = new ArrayList<>();
    }

    public GeneralSettings getGeneralSettings() {
        return settings;
    }
    
    public ArrayList<Round> getRounds() {
        return rounds;
    }
    
    public void addRound(Round round) {
        rounds.add(round);
    }
    
    public void removeRound(int roundIndex) {
        rounds.remove(roundIndex);
    }
    
    public StartReward getStartReward() {
        return startReward;
    }

    public void setGeneralSettings(GeneralSettings settings) {
        this.settings = settings;
    }

    public void setStartReward(StartReward startReward) {
        this.startReward = startReward;
    }
    
    
    
    
    
    public boolean isComplete() {
        if (settings != null && startReward != null && !rounds.isEmpty()) {
            return true;
        }
        return false;
    }
    
    
    
    
}
