/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package customelements.groupcombobox;

import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Leon
 */
public class GroupModel extends DefaultComboBoxModel<Object> {
    
        @Override
        public void setSelectedItem(Object anObject) {
            if (anObject instanceof NonClickableEntry) {
                int index = getIndexOf(anObject);
                if (index < getSize()) {
                    setSelectedItem(getElementAt(index+1));
                }
            } else {
                super.setSelectedItem(anObject);
            }
        }
    
}
