/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package customelements.groupcombobox;

import entity.enums.EnchantmentType;
import entity.enums.ItemEnum;
import entity.enums.MobType;
import entity.enums.PotionEffectType;
import entity.enums.PotionItemType;
import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author Leon
 */
public class GroupRenderer  extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String valueString = null;
        if (value instanceof NonClickableEntry) {
            JLabel label = new JLabel(value.toString());
            Font f = label.getFont();
            label.setFont(f.deriveFont(f.getStyle() | Font.BOLD | Font.ITALIC));
            return label;
        } else if (value instanceof ItemEnum) {
            valueString = ((ItemEnum) value).getDisplayname();
        } else if (value instanceof PotionItemType) {
            valueString = ((PotionItemType) value).getDisplayname();
        } else if (value instanceof EnchantmentType) {
            valueString = ((EnchantmentType) value).getDisplayname();
        } else if (value instanceof PotionEffectType) {
            valueString = ((PotionEffectType) value).getDisplayname();
        } else if (value instanceof MobType) {
            valueString = ((MobType) value).getDisplayname();
        } else {
            if (value != null) valueString = value.toString();
        }
        return super.getListCellRendererComponent(list, valueString, index, isSelected, cellHasFocus);
    }

}
