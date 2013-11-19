/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package customelements.multilinetable;

import java.util.Collection;

/**
 *
 * @author Leon
 */
public class StringArray {

    private String line;
    
    public StringArray(String line) {
        this.line = line;
    }
    
    public StringArray(Collection<?> lines) {
        if (lines == null) {
            line = "";
            return;
        }
        for (Object s : lines) {
            if (line == null) {
                line = s.toString();
            } else {
                line = line + "\n" + s.toString();
            }
        }
    }
    
    @Override
    public String toString() {
        return line;
    }
    
    
    
    
    
}
