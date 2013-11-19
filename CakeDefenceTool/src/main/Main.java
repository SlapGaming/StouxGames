/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Frame.MainFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import logic.Controller;
import panels.SelectPanel;

/**
 *
 * @author Leon
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            setLookAndFeel();
            MainFrame mf = new MainFrame();
            Controller fc = new Controller(mf);
            mf.setPanel(new SelectPanel());
            mf.setVisible(true);
        } catch (UnsupportedOperationException e) {
            MainFrame.throwError("Sorry, not supported yet!");
        }
    }
    
    /**
     * Tries setting the Look & Feel to Nimbus, otherwise system.
     */
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
    }
    
    
}
