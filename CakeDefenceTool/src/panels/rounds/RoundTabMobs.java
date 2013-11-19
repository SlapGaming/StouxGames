/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.rounds;

import Frame.MainFrame;
import customelements.groupcombobox.GroupModel;
import customelements.groupcombobox.GroupRenderer;
import customelements.groupcombobox.NonClickableEntry;
import javax.swing.table.DefaultTableModel;
import customelements.multilinetable.MultiLineTableCellRenderer;
import customelements.multilinetable.StringArray;
import entity.collections.MobTypeCollection;
import entity.collections.PotionMobEffectCollection;
import entity.enums.MobType;
import entity.enums.PotionEffectType;
import entity.instance.Mob;
import static entity.instance.Mob.CompareResult.Same;
import entity.instance.PotionEffect;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JOptionPane;

/**
 *
 * @author Leon
 */
public class RoundTabMobs extends javax.swing.JPanel {

    //Mobs
    private ArrayList<Mob> mobs;
    private DefaultTableModel mobModel;
    //Combobox models
    private GroupModel mobTypeModel;
    private MobType selectedMobType;
    private GroupModel potionEffectTypeModel;
    private PotionEffectType selectedPotionType;
    //Potion Effects
    private ArrayList<PotionEffect> potionEffects;
    private DefaultTableModel potionEffectModel;

    /**
     * Creates new form RoundTabMobs
     */
    public RoundTabMobs() {
        initComponents();

        mobs = new ArrayList<>();
        potionEffects = new ArrayList<>();

        //Get mob model
        mobsTable.setDefaultRenderer(Object.class, new MultiLineTableCellRenderer());
        mobModel = (DefaultTableModel) mobsTable.getModel();

        //Get potion effect model
        potionEffectModel = (DefaultTableModel) potionEffectTable.getModel();


        initializeModels();

        //Potion Effect Combobox
        potionEffectComboBox.setModel(potionEffectTypeModel);
        potionEffectComboBox.setRenderer(new GroupRenderer());
        potionEffectComboBox.setSelectedIndex(0);
        selectedPotionType = (PotionEffectType) potionEffectComboBox.getSelectedItem();

        //Mobtype Combobox
        mobTypeComboBox.setModel(mobTypeModel);
        mobTypeComboBox.setRenderer(new GroupRenderer());
        mobTypeComboBox.setSelectedIndex(0);
        selectedMobType = (MobType) mobTypeComboBox.getSelectedItem();

        //Spinners
        amountSpinner.setValue(1);
        levelSpinner.setValue(1);
        lengthSpinner.setValue(1);
    }
    
    protected void editRound(HashSet<Mob> mobs) {
        this.mobs = new ArrayList<>(mobs);
        refreshMobTable();
    }

    private void refreshMobTable() {
        mobModel.setRowCount(0);
        for (Mob mob : mobs) {
            mobModel.addRow(
                    new Object[]{
                mob.getMobType().getDisplayname(),
                mob.getAmount(),
                new StringArray(mob.getPotionEffects())});
        }
    }

    private void refreshPotionTable() {
        potionEffectModel.setRowCount(0);
        for (PotionEffect ef : potionEffects) {
            potionEffectModel.addRow(new Object[]{ef.getType().getDisplayname(), ef.getLevel(), PotionEffect.getLengthString(ef.getLength())});
        }
    }

    private void initializeModels() {
        mobTypeModel = new GroupModel();
        addToModel(mobTypeModel, "Zombies", MobTypeCollection.ZOMBIES);
        addToModel(mobTypeModel, "Skeletons", MobTypeCollection.SKELETONS);
        addToModel(mobTypeModel, "Spiders", MobTypeCollection.SPIDERS);
        addToModel(mobTypeModel, "Slimes", MobTypeCollection.SLIMES);
        addToModel(mobTypeModel, "Creepers", MobTypeCollection.CREEPERS);
        addToModel(mobTypeModel, "Miscellaneous", MobTypeCollection.MISC);

        potionEffectTypeModel = new GroupModel();
        for (PotionEffectType type : PotionMobEffectCollection.MOBONLY) {
            potionEffectTypeModel.addElement(type);
        }
    }

    private void addToModel(GroupModel model, String header, Object[] objects) {
        model.addElement(new NonClickableEntry(header));
        for (Object o : objects) {
            model.addElement(o);
        }
    }

    private void throwError(String error) {
        MainFrame.throwError(error);
    }

    public void resetMobAdder() {
        lengthInfiniteCheckbox.setSelected(false);
        levelSpinner.setValue(1);
        lengthSpinner.setValue(1);
        lengthSpinner.setEnabled(true);
    }

    public void removeMobTableRow() {
        int row = mobsTable.getSelectedRow();
        if (row != -1) {
            String[] options = new String[]{"Remove the mob", "Cancel"};
            int result = JOptionPane.showOptionDialog(MainFrame.getFrame(), "<html><center>Are you sure you want to remove this mob?</center></html>", "Choose an option!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            switch (result) {
                case -1:
                case 1:
                    return;
                case 0:
                    mobs.remove(row);
                    break;
            }
            refreshMobTable();
        }
    }

    public void removePotionEffectTableRow() {
        int row = potionEffectTable.getSelectedRow();
        if (row != -1) {
            String[] options = new String[]{"Remove the potion effect", "Cancel"};
            int result = JOptionPane.showOptionDialog(MainFrame.getFrame(), "<html><center>Are you sure you want to remove this potion effect?</center></html>", "Choose an option!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            switch (result) {
                case -1:
                case 1:
                    return;
                case 0:
                    potionEffects.remove(row);
                    break;
            }
            refreshPotionTable();
        }
    }

    public ArrayList<Mob> getMobs() {
        return mobs;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        mobsTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        mobTypeComboBox = new javax.swing.JComboBox();
        mobTypeLabel = new javax.swing.JLabel();
        amountLabel = new javax.swing.JLabel();
        amountSpinner = new javax.swing.JSpinner();
        jPanel4 = new javax.swing.JPanel();
        addMobButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        potionEffectTable = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        potionEffectLabel = new javax.swing.JLabel();
        potionEffectComboBox = new javax.swing.JComboBox();
        levelLabel = new javax.swing.JLabel();
        levelSpinner = new javax.swing.JSpinner();
        lengthLabel = new javax.swing.JLabel();
        lengthSpinner = new javax.swing.JSpinner();
        lengthInfiniteCheckbox = new javax.swing.JCheckBox();
        addPotionEffectButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();

        mobsTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        mobsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mob Type", "Amount", "Potion Effect"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        mobsTable.setToolTipText("Double click an item or press 'DEL' to remove/edit the item.");
        mobsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        mobsTable.setColumnSelectionAllowed(true);
        mobsTable.getTableHeader().setReorderingAllowed(false);
        mobsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mobsTableMouseClicked(evt);
            }
        });
        mobsTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mobsTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(mobsTable);
        mobsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        mobsTable.getColumnModel().getColumn(0).setMinWidth(100);
        mobsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        mobsTable.getColumnModel().getColumn(0).setMaxWidth(300);
        mobsTable.getColumnModel().getColumn(1).setMinWidth(75);
        mobsTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        mobsTable.getColumnModel().getColumn(1).setMaxWidth(75);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(0, 0, 0)));
        jPanel2.setMinimumSize(new java.awt.Dimension(950, 0));

        mobTypeComboBox.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        mobTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mobTypeComboBoxActionPerformed(evt);
            }
        });

        mobTypeLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        mobTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mobTypeLabel.setText("Select Mob Type:");

        amountLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        amountLabel.setText("Amount");

        amountSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                amountSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mobTypeLabel)
                    .addComponent(amountLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mobTypeComboBox, 0, 154, Short.MAX_VALUE)
                    .addComponent(amountSpinner))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mobTypeLabel)
                    .addComponent(mobTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(amountLabel)
                    .addComponent(amountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        addMobButton.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        addMobButton.setText("Add Mob");
        addMobButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMobButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addMobButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addMobButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        potionEffectTable.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        potionEffectTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Potion Effect Type", "Level", "Length"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        potionEffectTable.setToolTipText("Double click or press 'DEL' to remove a Potion Effect");
        potionEffectTable.setColumnSelectionAllowed(true);
        potionEffectTable.setMinimumSize(new java.awt.Dimension(200, 75));
        potionEffectTable.getTableHeader().setReorderingAllowed(false);
        potionEffectTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                potionEffectTableMouseClicked(evt);
            }
        });
        potionEffectTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                potionEffectTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(potionEffectTable);
        potionEffectTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        potionEffectTable.getColumnModel().getColumn(0).setMinWidth(100);
        potionEffectTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        potionEffectTable.getColumnModel().getColumn(1).setMinWidth(50);
        potionEffectTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        potionEffectTable.getColumnModel().getColumn(1).setMaxWidth(100);
        potionEffectTable.getColumnModel().getColumn(2).setMinWidth(50);
        potionEffectTable.getColumnModel().getColumn(2).setPreferredWidth(75);
        potionEffectTable.getColumnModel().getColumn(2).setMaxWidth(100);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        potionEffectLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        potionEffectLabel.setText("Potion Effect:");

        potionEffectComboBox.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        potionEffectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                potionEffectComboBoxActionPerformed(evt);
            }
        });

        levelLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        levelLabel.setText("Level:");

        levelSpinner.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        levelSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                levelSpinnerStateChanged(evt);
            }
        });

        lengthLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lengthLabel.setText("Length (Seconds):");

        lengthSpinner.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lengthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lengthSpinnerStateChanged(evt);
            }
        });

        lengthInfiniteCheckbox.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lengthInfiniteCheckbox.setText("Infinite");
        lengthInfiniteCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lengthInfiniteCheckboxActionPerformed(evt);
            }
        });

        addPotionEffectButton.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        addPotionEffectButton.setText("Add Potion Effect");
        addPotionEffectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPotionEffectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addPotionEffectButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(potionEffectLabel)
                            .addComponent(levelLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(levelSpinner)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(lengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lengthInfiniteCheckbox))
                            .addComponent(potionEffectComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lengthLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(potionEffectLabel)
                    .addComponent(potionEffectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(levelLabel)
                    .addComponent(levelSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lengthLabel)
                    .addComponent(lengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lengthInfiniteCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addPotionEffectButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Added Mobs");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mobsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mobsTableMouseClicked
        System.out.println(evt.getClickCount());
        if (evt.getClickCount() == 2) {
            removeMobTableRow();
        }
    }//GEN-LAST:event_mobsTableMouseClicked

    private void lengthInfiniteCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lengthInfiniteCheckboxActionPerformed
        if (lengthInfiniteCheckbox.isSelected()) {
            lengthSpinner.setValue(1);
            lengthSpinner.setEnabled(false);
        } else {
            lengthSpinner.setValue(1);
            lengthSpinner.setEnabled(true);
        }
    }//GEN-LAST:event_lengthInfiniteCheckboxActionPerformed

    private void lengthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lengthSpinnerStateChanged
        Integer value = (Integer) lengthSpinner.getValue();
        if (value != null) {
            if (value < 1) {
                lengthSpinner.setValue(1);
            }
        }
    }//GEN-LAST:event_lengthSpinnerStateChanged

    private void levelSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_levelSpinnerStateChanged
        Integer value = (Integer) levelSpinner.getValue();
        if (value != null) {
            if (value < 1) {
                levelSpinner.setValue(1);
            } else if (value > 10) {
                levelSpinner.setValue(10);
            }
        }
    }//GEN-LAST:event_levelSpinnerStateChanged

    private void amountSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_amountSpinnerStateChanged
        Integer value = (Integer) amountSpinner.getValue();
        if (value != null) {
            if (value < 1) {
                amountSpinner.setValue(1);
            } else if (value > 25) {
                amountSpinner.setValue(25);
            }
        }
    }//GEN-LAST:event_amountSpinnerStateChanged

    private void potionEffectTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_potionEffectTableMouseClicked
        if (evt.getClickCount() == 2) {
            removePotionEffectTableRow();
        }
    }//GEN-LAST:event_potionEffectTableMouseClicked

    private void addPotionEffectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPotionEffectButtonActionPerformed
        PotionEffectType type = (PotionEffectType) potionEffectComboBox.getSelectedItem();
        PotionEffect foundEffect = null;
        for (PotionEffect potionEffect : potionEffects) {
            if (potionEffect.getType() == type) {
                foundEffect = potionEffect;
            }
        }
        Integer level = (Integer) levelSpinner.getValue();
        int length;
        if (lengthInfiniteCheckbox.isSelected()) {
            length = -1;
        } else {
            length = (Integer) lengthSpinner.getValue();
        }

        if (foundEffect != null) {
            String[] options = null;
            String questionFirst = "Warning! The potion effect you wanted to add is already added!";
            String questionSecond = null;
            boolean update = true;
            if (foundEffect.getLevel() == level && foundEffect.getLength() == length) {
                throwError("This potion effect is already added!");
                return;
            } else if (foundEffect.getLength() != length && foundEffect.getLevel() != level) {
                questionSecond = "Do you want to change just the length, just the level or both?";
                options = new String[]{"Change the level to " + level, "Change the length to " + PotionEffect.getLengthString(length), "Change both", "Keep both the same"};
            } else if (foundEffect.getLength() != length) {
                questionSecond = "Do you want to change the length from " + PotionEffect.getLengthString(foundEffect.getLength())
                        + " to " + PotionEffect.getLengthString(length) + " or keep it the same?";
                options = new String[]{"Change it to " + PotionEffect.getLengthString(length), "Keep it on " + PotionEffect.getLengthString(foundEffect.getLength())};
            } else if (foundEffect.getLevel() != level) {
                questionSecond = "Do you want to change the level from " + foundEffect.getLevel() + " to " + level + " or keep it the same?";
                options = new String[]{"Change it to " + level, "Keep it on " + foundEffect.getLevel()};
            }

            int result = JOptionPane.showOptionDialog(MainFrame.getFrame(), "<html><center>" + questionFirst + "<br>"
                    + questionSecond + "</center></html>", "Warning!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (options.length == 2) {
                switch (result) {
                    case -1:
                    case 1:
                        update = false;
                        break;
                    case 0:
                        foundEffect.setLength(length);
                        foundEffect.setLevel(level);
                        break;
                }
            } else {
                switch (result) {
                    case -1:
                    case 3:
                        update = false;
                        break;
                    case 0:
                        foundEffect.setLevel(level);
                        break;
                    case 1:
                        foundEffect.setLength(length);
                        break;
                    case 2:
                        foundEffect.setLength(length);
                        foundEffect.setLevel(level);
                        break;
                }
            }
            if (update) {
                refreshPotionTable();
            }
            return;
        }
        potionEffects.add(new PotionEffect(type, level, length));
        refreshPotionTable();
    }//GEN-LAST:event_addPotionEffectButtonActionPerformed

    private void addMobButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMobButtonActionPerformed
        Mob mob;
        Integer spinnerValue = (Integer) amountSpinner.getValue();
        MobType type = (MobType) mobTypeComboBox.getSelectedItem();
        if (potionEffects.isEmpty()) {
            mob = new Mob(type, spinnerValue);
        } else {
            ArrayList<PotionEffect> newPotionsEffects = new ArrayList<>();
            for (PotionEffect pe : potionEffects) {
                newPotionsEffects.add(new PotionEffect(pe.getType(), pe.getLevel(), pe.getLength()));
            }
            mob = new Mob(type, spinnerValue, newPotionsEffects);
        }

        boolean sameType = false;
        Mob sameMob = null;
        for (Mob foundMob : mobs) {
            Mob.CompareResult result = foundMob.compare(mob);
            switch (result) {
                case SameType:
                case SameTypeAmount:
                    sameType = true;
                    break;
                case Same:
                case SameTypePotions: //Different amount
                    sameMob = foundMob;
                    break;
            }
            if (foundMob.getMobType() == mob.getMobType()) {
                sameType = true;
            }
        }

        if (sameMob != null) {
            int left = 25 - sameMob.getAmount();
            if (left < mob.getAmount()) {
                String secondPart;
                if (left == 0) {
                    secondPart = "You can't add anymore!";
                } else {
                    secondPart = "You can only add " + left + " more!";
                }
                throwError("<html><center>You already have this mob added! " + secondPart);
                return;
            }
            int result = JOptionPane.showOptionDialog(MainFrame.getFrame(),
                    "<html><center>There is already a mob of the same type in this list which has the same potions.<br>"
                    + "Do you want to increase the amount of this mob with " + mob.getAmount() + "?</center></html>",
                    "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (result == -1 || result == 1) {
                return;
            } else {
                sameMob.setAmount(sameMob.getAmount() + mob.getAmount());
                refreshMobTable();
                return;
            }
        } else if (sameType) {
            int result = JOptionPane.showOptionDialog(MainFrame.getFrame(),
                    "<html><center>There is already a mob of the same type in this list.<br>Are you sure you want to continue?</center></html>",
                    "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (result == -1 || result == 1) {
                return;
            }
        }

        mobs.add(mob);
        refreshMobTable();
    }//GEN-LAST:event_addMobButtonActionPerformed

    private void potionEffectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_potionEffectComboBoxActionPerformed
        PotionEffectType foundType = (PotionEffectType) potionEffectComboBox.getSelectedItem();
        if (selectedPotionType != foundType) {
            selectedPotionType = foundType;
            resetMobAdder();
        }
    }//GEN-LAST:event_potionEffectComboBoxActionPerformed

    private void mobTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mobTypeComboBoxActionPerformed
        MobType foundType = (MobType) mobTypeComboBox.getSelectedItem();
        if (selectedMobType != foundType) {
            selectedMobType = foundType;
            resetMobAdder();
            amountSpinner.setValue(1);
            potionEffects.clear();
            refreshPotionTable();
        }
    }//GEN-LAST:event_mobTypeComboBoxActionPerformed

    private void mobsTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mobsTableKeyReleased
        if (evt.getKeyCode() == 127) {
            removeMobTableRow();
        }
    }//GEN-LAST:event_mobsTableKeyReleased

    private void potionEffectTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_potionEffectTableKeyReleased
        if (evt.getKeyCode() == 127) {
            removePotionEffectTableRow();
        }
    }//GEN-LAST:event_potionEffectTableKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addMobButton;
    private javax.swing.JButton addPotionEffectButton;
    private javax.swing.JLabel amountLabel;
    private javax.swing.JSpinner amountSpinner;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JCheckBox lengthInfiniteCheckbox;
    private javax.swing.JLabel lengthLabel;
    private javax.swing.JSpinner lengthSpinner;
    private javax.swing.JLabel levelLabel;
    private javax.swing.JSpinner levelSpinner;
    private javax.swing.JComboBox mobTypeComboBox;
    private javax.swing.JLabel mobTypeLabel;
    private javax.swing.JTable mobsTable;
    private javax.swing.JComboBox potionEffectComboBox;
    private javax.swing.JLabel potionEffectLabel;
    private javax.swing.JTable potionEffectTable;
    // End of variables declaration//GEN-END:variables
}
