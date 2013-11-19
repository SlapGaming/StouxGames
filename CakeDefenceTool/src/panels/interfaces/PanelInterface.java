/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.interfaces;

/**
 *
 * @author Leon
 */
public interface PanelInterface {

    /**
     * Get the title of the panel
     *
     * @return the title
     */
    public String getTitle();

    public String getHelp();

    public void next();

    public void back();
}
