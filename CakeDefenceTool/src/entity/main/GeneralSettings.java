/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.main;

import java.io.Serializable;

/**
 *
 * @author Leon
 */
public class GeneralSettings implements Serializable {
    
    private String routinename;
    private String author;
    
    private boolean mobDropOnDeath;
    private boolean playerDropOnDeath;

    public GeneralSettings(String routinename, String author, boolean mobDropOnDeath, boolean playerDropOnDeath) {
        this.routinename = routinename;
        this.author = author;
        this.mobDropOnDeath = mobDropOnDeath;
        this.playerDropOnDeath = playerDropOnDeath;
    }

    public String getAuthor() {
        return author;
    }

    public String getRoutinename() {
        return routinename;
    }

    public boolean isMobDropOnDeath() {
        return mobDropOnDeath;
    }

    public boolean isPlayerDropOnDeath() {
        return playerDropOnDeath;
    }
    
}
