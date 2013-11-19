/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.rewards;

import Frame.MainFrame;
import customelements.groupcombobox.GroupModel;
import customelements.groupcombobox.GroupRenderer;
import entity.enums.EnchantmentType;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SpinnerListModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Leon
 */
public class RewardItemSubPanel extends javax.swing.JPanel {

    private ArrayList<Enchantment> enchants;
    
    /**
     * Creates new form RewardItemPanel
     */
    public RewardItemSubPanel(EnchantmentType[] types) {
        initComponents();
        enchants = new ArrayList<>();
        GroupModel model = new GroupModel();
        for (EnchantmentType type : types) {
            model.addElement(type);
        }
        enchantmentTypeComboBox.setModel(model);
        enchantmentTypeComboBox.setRenderer(new GroupRenderer());
        enchantmentTypeComboBox.setSelectedIndex(0);
        
        levelSpinner.setValue(1);
        
        ArrayList<String> spinnerValues = new ArrayList<>();
        int x = 1;
        while (x <= 100) {
            spinnerValues.add(x + "%");
            x++;
        }
        durabilitySpinner.setModel(new SpinnerListModel(spinnerValues));
        durabilitySpinner.setValue("100%");
    }
    
    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) enchantmentsTable.getModel();
        model.setRowCount(0);
        for (Enchantment e : enchants) {
            model.addRow(new Object[] {e.type.getDisplayname(), e.level});
        }
    }
    
    public class Enchantment {
        public EnchantmentType type;
        public int level;

        public Enchantment(EnchantmentType type, int level) {
            this.type = type;
            this.level = level;
        }
    }
   
    public void addEnchantment() {
        EnchantmentType type = (EnchantmentType) enchantmentTypeComboBox.getSelectedItem();
        int level = (int) levelSpinner.getValue();
        Enchantment foundEnchant = null;
        for (Enchantment e : enchants) {
            if (e.type == type) {
                foundEnchant = e;
            }
        }
        if (foundEnchant != null) {
            if (foundEnchant.level == level) {
                MainFrame.throwError("This enchantment is already added.");
                return;
            }
            String[] options = new String[] {"Change to " + level, "Keep on " + foundEnchant.level};
            int result = JOptionPane.showOptionDialog(MainFrame.getFrame(), "<html><center>Warning! The enchantment type you wanted to add is already added.<br>"
                        + "Do you want to change the level of the enchant to " + level + " or keep it on " + foundEnchant.level + "?</center></html>"
                        , "Warning!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            switch (result) {
                case -1: case 1:
                    return;
                case 0: //Change
                    foundEnchant.level = level;
                    refreshTable();
                    break;
            }
        } else {
            enchants.add(new Enchantment(type, level));
            refreshTable();
        }
    }
    
    /**
     * Get the enchantments map
     * @return the map
     */
    public HashMap<EnchantmentType, Integer> getEnchantments() {
        if (enchants.isEmpty()) return null;
        HashMap<EnchantmentType, Integer> chantMap = new HashMap<>();
        for (Enchantment e : enchants) {
            chantMap.put(e.type, e.level);
        }
        return chantMap;
    }
    
    /**
     * Get the durability of the item
     * @return 
     */
    public int getDurability() {
        String value = (String) durabilitySpinner.getValue();
        return Integer.parseInt(value.replace("%", ""));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enchantmentTypeComboBox = new javax.swing.JComboBox();
        levelLabel = new javax.swing.JLabel();
        levelSpinner = new javax.swing.JSpinner();
        addEnchantmentButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        enchantmentsTable = new javax.swing.JTable();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        durabilitySpinner = new javax.swing.JSpinner();

        enchantmentTypeComboBox.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        enchantmentTypeComboBox.setToolTipText("");
        enchantmentTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enchantmentTypeComboBoxActionPerformed(evt);
            }
        });

        levelLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        levelLabel.setText("Level:");

        levelSpinner.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        levelSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                levelSpinnerStateChanged(evt);
            }
        });

        addEnchantmentButton.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        addEnchantmentButton.setText("Add Enchantment");
        addEnchantmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEnchantmentButtonActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        enchantmentsTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        enchantmentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Enchantment Type", "Level"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        enchantmentsTable.setToolTipText("Double click to remove an enchantment.");
        enchantmentsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(enchantmentsTable);
        enchantmentsTable.getColumnModel().getColumn(0).setMinWidth(100);
        enchantmentsTable.getColumnModel().getColumn(1).setMinWidth(50);
        enchantmentsTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        enchantmentsTable.getColumnModel().getColumn(1).setMaxWidth(100);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Durability");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Enchantment");

        durabilitySpinner.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        durabilitySpinner.setToolTipText("Enter the procentage of durabilty");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(enchantmentTypeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(levelLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(levelSpinner))
                    .addComponent(addEnchantmentButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .addComponent(jSeparator3)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(durabilitySpinner, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(enchantmentTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(levelSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(levelLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addEnchantmentButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(durabilitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jSeparator2))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addEnchantmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEnchantmentButtonActionPerformed
        addEnchantment();
    }//GEN-LAST:event_addEnchantmentButtonActionPerformed

    private void levelSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_levelSpinnerStateChanged
        Object selectedValue = enchantmentTypeComboBox.getSelectedItem();
        if (selectedValue != null) {
            EnchantmentType type = (EnchantmentType) selectedValue;
            int level = (int) levelSpinner.getValue();
            if (level > type.getMaxLevel()) {
                levelSpinner.setValue(type.getMaxLevel());
            } else if (level < 1) {
                levelSpinner.setValue(1);
            }
        }
    }//GEN-LAST:event_levelSpinnerStateChanged

    private EnchantmentType oldType;
    
    private void enchantmentTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enchantmentTypeComboBoxActionPerformed
        Object value = enchantmentTypeComboBox.getSelectedItem();
        if (value != null) {
            EnchantmentType type = (EnchantmentType) value;
            if (type != oldType) {
                levelSpinner.setValue(1);
            }
        }
    }//GEN-LAST:event_enchantmentTypeComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEnchantmentButton;
    private javax.swing.JSpinner durabilitySpinner;
    private javax.swing.JComboBox enchantmentTypeComboBox;
    private javax.swing.JTable enchantmentsTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel levelLabel;
    private javax.swing.JSpinner levelSpinner;
    // End of variables declaration//GEN-END:variables
}
