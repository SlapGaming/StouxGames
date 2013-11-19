/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.rewards;

import Frame.MainFrame;
import customelements.groupcombobox.GroupModel;
import customelements.groupcombobox.GroupRenderer;
import customelements.groupcombobox.NonClickableEntry;
import entity.collections.ItemCollection;
import entity.enums.EnchantmentType;
import entity.enums.ItemEnum;
import entity.enums.PotionItemType;
import entity.instance.Item;
import entity.instance.PotionItem;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import panels.interfaces.RewardCallable;

/**
 *
 * @author Leon
 */
public class RewardPanel extends javax.swing.JPanel {
    
    
    private RewardCallable caller;
    private JPanel subPanel;
    
    private boolean allowSplit = true;
    
    /**
     * Creates new form RewardPanel
     */
    public RewardPanel(RewardCallable caller) {
        this.caller = caller;
        initComponents();
        itemComboBox.setModel(createGroupModel());
        itemComboBox.setRenderer(new GroupRenderer());
        itemComboBox.setSelectedIndex(0);
        amountSpinner.setValue(1);
    }
    
    private GroupModel createGroupModel() {
        GroupModel model = new GroupModel();
        //Add armor
        model.addElement(new NonClickableEntry("Leather Armor"));
            addToModel(model, ItemCollection.LEATHER);
        model.addElement(new NonClickableEntry("Chainmail Armor"));
            addToModel(model, ItemCollection.CHAINMAIL);
        model.addElement(new NonClickableEntry("Iron Armor"));
            addToModel(model, ItemCollection.IRON);
        model.addElement(new NonClickableEntry("Diamond Armor"));
            addToModel(model, ItemCollection.DIAMOND);
        model.addElement(new NonClickableEntry("Gold Armor"));
            addToModel(model, ItemCollection.GOLD);
        
        //Weapons
        model.addElement(new NonClickableEntry("Swords"));
            addToModel(model, ItemCollection.SWORDS);
        model.addElement(new NonClickableEntry("Bow & Arrow"));
            addToModel(model, ItemCollection.BOW_ARROW);

        //Potions
        model.addElement(new NonClickableEntry("Potions"));
            addToModel(model, ItemCollection.POTION);
            
        //Food & Other
        model.addElement(new NonClickableEntry("Food"));
            addToModel(model, ItemCollection.FOOD);
        model.addElement(new NonClickableEntry("Other"));
            addToModel(model, ItemCollection.OTHER);
        return model;
    }
    
    private void addToModel(DefaultComboBoxModel model, ItemEnum[] items) {
        for (ItemEnum item : items)  {
            model.addElement(item);
        }
    }
    
    public void setItemGroup(int group) {
        ItemEnum[] items;
        switch (group) {
            case 1: //Helmet
                items = ItemCollection.HELMET;
                break;
            case 2: //Chestplate
                items = ItemCollection.CHESTPLATE;
                break;
            case 3: //Legs
                items = ItemCollection.LEGGINGS;
                break;
            case 4: //Boots
                items = ItemCollection.BOOTS;
                break;
            default:
                itemComboBox.setModel(createGroupModel());
                itemComboBox.setSelectedIndex(0);
                return;
        }
        GroupModel model = new GroupModel();
        for (ItemEnum item : items) {
            model.addElement(item);
        }
        itemComboBox.setModel(model);
        itemComboBox.setSelectedIndex(0);
    }
    
    public void setAllowSplit(boolean allowSplit) {
        this.allowSplit = allowSplit;
    }
    
    
    private void addReward() {
        //Get item
        ItemEnum selectedItem;
        Object itemValue = itemComboBox.getSelectedItem();
        if (itemValue != null) {
            selectedItem = (ItemEnum) itemValue;
        } else {
            throwError("You need to select an item!");
            return;
        }

        //Calculate amount
        int amount;
        boolean multipleStacks = false;
        Object spinnerValue = amountSpinner.getValue();
        if (spinnerValue != null) {
            amount = (int) spinnerValue;
            if (amount < 1 /* || amount > item.getMaxStacksize() */) {
                throwError("The amount needs to be atleast 1.");
                return;
            }
            if (amount > selectedItem.getMaxAmount()) {
                if (allowSplit) {
                    String options[] = new String[]{"Split", "Set to max stacksize", "Cancel"};
                    int result = JOptionPane.showOptionDialog(MainFrame.getFrame(), "<html><center>Warning! The max stacksize of this item is: " + selectedItem.getMaxAmount() + ".<br>"
                            + "Do you want to split the reward up into multiple stacks, set it to the max stacksize or cancel?</center></html>"
                            , "Warning!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    switch (result) {
                        case 0:
                            multipleStacks = true;
                            break;
                        case 1: //Set to max stacksize
                            amount = selectedItem.getMaxAmount();
                            break;
                        case 2: case -1:
                            return;
                    }
                } else {
                    throwError("The max stack size of this item is " + selectedItem.getMaxAmount());
                    amountSpinner.setValue(selectedItem.getMaxAmount());
                    return;
                }
            }
        } else {
            throwError("No item amount given.");
            return;
        }
       
        int durability = -1;
        
        if (selectedItem == ItemEnum.POTION) {
            RewardPotionSubPanel castPanel = (RewardPotionSubPanel) subPanel;
            boolean splash = castPanel.isSplash();
            PotionItemType type = castPanel.getPotionType();
            int x = 1;
            while (x <= amount) {
                caller.addReward(new PotionItem(type, splash));
                x++;
            }
        } else {
            HashMap<EnchantmentType, Integer> enchants = null;
            if (subPanel instanceof RewardItemSubPanel) {
                RewardItemSubPanel castPanel = (RewardItemSubPanel) subPanel;
                
                //Check for enchants
                if (selectedItem.hasEnchants()) {
                    enchants = castPanel.getEnchantments();
                }
                
                //Get durability
                durability = castPanel.getDurability();
            }
            if (multipleStacks) {
                double devided = (double) amount / (double) selectedItem.getMaxAmount();
                int maxStacks = (int) Math.floor(devided);
                int x = 1;
                while (x <= maxStacks) {
                    caller.addReward(new Item(selectedItem, selectedItem.getMaxAmount(), enchants, durability));
                    x++;
                }
                amount = amount - (maxStacks * selectedItem.getMaxAmount());
            }
            if (amount > 0) {
                caller.addReward(new Item(selectedItem, amount, enchants, durability));
            }
        }
        caller.update();
    }

    private void throwError(String error) {
        MainFrame.throwError(error);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator3 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        amountSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        addRewardButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        itemComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        extraPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(0, 0, 0)));

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        amountSpinner.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        amountSpinner.setRequestFocusEnabled(false);
        amountSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                amountSpinnerStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Amount");

        addRewardButton.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        addRewardButton.setText("Add Item");
        addRewardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRewardButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Item");

        itemComboBox.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        itemComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemComboBoxActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("<html><center>Selecting a different item will wipe<br>all enchantsments/the potion effect.</center></html>");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(amountSpinner)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(addRewardButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(itemComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
            .addComponent(jSeparator2)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itemComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(amountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRewardButton))
        );

        javax.swing.GroupLayout extraPanelLayout = new javax.swing.GroupLayout(extraPanel);
        extraPanel.setLayout(extraPanelLayout);
        extraPanelLayout.setHorizontalGroup(
            extraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        extraPanelLayout.setVerticalGroup(
            extraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(extraPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(extraPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private Object currentSelected;
    
    private void itemComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemComboBoxActionPerformed
        Object selectedItem = itemComboBox.getSelectedItem();
        if (currentSelected == null || currentSelected != selectedItem) {
            ItemEnum item = (ItemEnum) selectedItem;
            if (item == ItemEnum.POTION) {
                MainFrame.setPanel(extraPanel, (subPanel=new RewardPotionSubPanel()));
            } else {
                if (item.hasEnchants()) {
                    MainFrame.setPanel(extraPanel, (subPanel=new RewardItemSubPanel(item.getAllowedEnchants())));
                } else {
                    MainFrame.setPanel(extraPanel, (subPanel=new RewardEmptySubPanel()));
                }
            }
        }
    }//GEN-LAST:event_itemComboBoxActionPerformed

    private void addRewardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRewardButtonActionPerformed
        addReward();
    }//GEN-LAST:event_addRewardButtonActionPerformed

    private void amountSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_amountSpinnerStateChanged
        Integer value = (Integer) amountSpinner.getValue();
        if (value != null) {
            if (value < 1) {
                amountSpinner.setValue(1);
            } else if (value > 500) {
                amountSpinner.setValue(500);
            }
        }
    }//GEN-LAST:event_amountSpinnerStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRewardButton;
    private javax.swing.JSpinner amountSpinner;
    private javax.swing.JPanel extraPanel;
    private javax.swing.JComboBox itemComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables
}
