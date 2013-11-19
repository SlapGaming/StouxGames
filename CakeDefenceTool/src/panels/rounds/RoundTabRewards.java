/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.rounds;

import Frame.MainFrame;
import customelements.multilinetable.MultiLineTableCellRenderer;
import entity.collections.RewardCollection;
import entity.collections.Round;
import entity.enums.EnchantmentType;
import entity.instance.Item;
import entity.instance.PotionItem;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import panels.interfaces.RewardCallable;
import panels.rewards.RewardPanel;

/**
 *
 * @author Leon
 */
public class RoundTabRewards extends javax.swing.JPanel implements RewardCallable {

    private HashMap<RewardTable, ArrayList<Item>> itemArrays;
    private HashMap<RewardTable, JTable> tableMap;
    
    
    /**
     * Creates new form RoundTabRewards
     */
    public RoundTabRewards() {
        itemArrays = new HashMap<>();
        tableMap = new HashMap<>();
        initComponents();
        
        //Fill TableMap
        tableMap.put(RewardTable.EndReward, endRewardTable);
        tableMap.put(RewardTable.GoodReward, goodRewardTable);
        tableMap.put(RewardTable.MediocreReward, mediocreRewardTable);
        tableMap.put(RewardTable.BadReward, badRewardTable);
        
        //Fill combo box
        tableChooserComboBox.setModel(new DefaultComboBoxModel(RewardTable.values()));
        
        //Fill itemArrays map
        for (RewardTable table : RewardTable.values()) {
            itemArrays.put(table, new ArrayList<Item>());
            //Set to multiline
            tableMap.get(table).setDefaultRenderer(Object.class, new MultiLineTableCellRenderer());
        }
        
        //Set add item Panel
        MainFrame.setPanel(rewardAdderPanel, new RewardPanel(this));
    }
    
    protected void editRound(Round r) {
        //Item Arrays
        itemArrays.put(RewardTable.GoodReward, r.getGood().getItems());
        itemArrays.put(RewardTable.MediocreReward, r.getMediocre().getItems());
        itemArrays.put(RewardTable.BadReward, r.getBad().getItems());
        itemArrays.put(RewardTable.EndReward, r.getEveryone().getItems());
        
        //Refresh tables
        refreshTable(RewardTable.GoodReward);
        refreshTable(RewardTable.MediocreReward);
        refreshTable(RewardTable.BadReward);
        refreshTable(RewardTable.EndReward);
    }
    
    private void removeRow(RewardTable table, int row) {
        if (row == -1) return;
        int result = JOptionPane.showOptionDialog(MainFrame.getFrame(), "Are you sure you to remove this item?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (result == 0) {
            itemArrays.get(table).remove(row);
            refreshTable(table);
        }
    }
    
    private void refreshTable(RewardTable table) {
        DefaultTableModel model = (DefaultTableModel) tableMap.get(table).getModel();
        model.setRowCount(0);
        for(Item item : itemArrays.get(table)) { //Loop thru items
            Object[] tableRow = new Object[3];
            tableRow[0] = item.getItemType().getDisplayname();
            tableRow[1] = item.getAmount();
            if (item.isPotion()) {
                PotionItem potion = item.getPotion();
                String extra = potion.getType().getDisplayname();
                if (potion.isSplash()) {
                    extra = extra + " (Splash)";
                }
                tableRow[2] = extra;
            } else {
                String extra = "";
                if (item.hasEnchants()) {
                    for (Map.Entry<EnchantmentType, Integer> entry : item.getEnchants().entrySet()) {
                        if (!extra.equals("")) {
                            extra = extra + "\n";
                        }
                        extra = extra + entry.getKey().getDisplayname() + " | Level: " + entry.getValue();
                    }
                }
                if (item.hasDurability()) {
                    if (!extra.equals("")) {
                        extra = extra + "\n";
                    }
                    extra = extra + "Durability: " + item.getDurability() + "%";
                }
                tableRow[2] = extra;
            }
            model.addRow(tableRow);
        }
    }
    
    @Override
    public void addReward(Item item) {
        RewardTable table = (RewardTable) tableChooserComboBox.getSelectedItem();
        if (table != null) {
            itemArrays.get(table).add(item);
        }
    }

    @Override
    public void update() {
        RewardTable table = (RewardTable) tableChooserComboBox.getSelectedItem();
        refreshTable(table);
    }
    
    private void tableClicked(RewardTable table, MouseEvent event) {
        if (event.getClickCount() == 2) {
            removeRow(table, tableMap.get(table).rowAtPoint(event.getPoint()));
        }
    }
    
    private void tableKeyReleased(RewardTable table, KeyEvent event) {
        if (event.getKeyCode() == 127) {
            removeRow(table, tableMap.get(table).getSelectedRow());
        }
    }
    
    public RewardCollection getGoodRewards() {
        return new RewardCollection(itemArrays.get(RewardTable.GoodReward));
    }
    public RewardCollection getMediocreRewards() {
        return new RewardCollection(itemArrays.get(RewardTable.MediocreReward));
    }
    public RewardCollection getBadRewards() {
        return new RewardCollection(itemArrays.get(RewardTable.BadReward));
    }
    public RewardCollection getEndRewards() {
        return new RewardCollection(itemArrays.get(RewardTable.EndReward));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rewardsLabel = new javax.swing.JLabel();
        topRewardsPanel = new javax.swing.JPanel();
        endRewardPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        endRewardTable = new javax.swing.JTable();
        endRewardLabel = new javax.swing.JLabel();
        goodRewardPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        goodRewardTable = new javax.swing.JTable();
        goodRewardLabel = new javax.swing.JLabel();
        bottomRewardsPanel = new javax.swing.JPanel();
        badRewardPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        badRewardTable = new javax.swing.JTable();
        badRewardLabel = new javax.swing.JLabel();
        mediocreRewardPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        mediocreRewardTable = new javax.swing.JTable();
        mediocreRewardLabel = new javax.swing.JLabel();
        rewardChooserPanel = new javax.swing.JPanel();
        tableChooserComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        rewardAdderPanel = new javax.swing.JPanel();

        rewardsLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        rewardsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rewardsLabel.setText("Rewards");

        topRewardsPanel.setLayout(new javax.swing.BoxLayout(topRewardsPanel, javax.swing.BoxLayout.LINE_AXIS));

        endRewardTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Amount", "Extra"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        endRewardTable.getTableHeader().setReorderingAllowed(false);
        endRewardTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                endRewardTableMouseClicked(evt);
            }
        });
        endRewardTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                endRewardTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(endRewardTable);
        endRewardTable.getColumnModel().getColumn(0).setMinWidth(75);
        endRewardTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        endRewardTable.getColumnModel().getColumn(0).setMaxWidth(150);
        endRewardTable.getColumnModel().getColumn(1).setMinWidth(50);
        endRewardTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        endRewardTable.getColumnModel().getColumn(1).setMaxWidth(100);

        endRewardLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        endRewardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        endRewardLabel.setText("End Reward");
        endRewardLabel.setToolTipText("Everyone will get this reward at the end of the round.");

        javax.swing.GroupLayout endRewardPanelLayout = new javax.swing.GroupLayout(endRewardPanel);
        endRewardPanel.setLayout(endRewardPanelLayout);
        endRewardPanelLayout.setHorizontalGroup(
            endRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(endRewardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(endRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                    .addComponent(endRewardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        endRewardPanelLayout.setVerticalGroup(
            endRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, endRewardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(endRewardLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        topRewardsPanel.add(endRewardPanel);

        goodRewardTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Amount", "Extra"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        goodRewardTable.getTableHeader().setReorderingAllowed(false);
        goodRewardTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                goodRewardTableMouseClicked(evt);
            }
        });
        goodRewardTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                goodRewardTableKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(goodRewardTable);
        goodRewardTable.getColumnModel().getColumn(0).setMinWidth(75);
        goodRewardTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        goodRewardTable.getColumnModel().getColumn(0).setMaxWidth(150);
        goodRewardTable.getColumnModel().getColumn(1).setMinWidth(50);
        goodRewardTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        goodRewardTable.getColumnModel().getColumn(1).setMaxWidth(100);

        goodRewardLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        goodRewardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        goodRewardLabel.setText("Good Reward");

        javax.swing.GroupLayout goodRewardPanelLayout = new javax.swing.GroupLayout(goodRewardPanel);
        goodRewardPanel.setLayout(goodRewardPanelLayout);
        goodRewardPanelLayout.setHorizontalGroup(
            goodRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, goodRewardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(goodRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                    .addComponent(goodRewardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        goodRewardPanelLayout.setVerticalGroup(
            goodRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, goodRewardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(goodRewardLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        topRewardsPanel.add(goodRewardPanel);

        bottomRewardsPanel.setLayout(new javax.swing.BoxLayout(bottomRewardsPanel, javax.swing.BoxLayout.LINE_AXIS));

        badRewardTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Amount", "Extra"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        badRewardTable.getTableHeader().setReorderingAllowed(false);
        badRewardTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                badRewardTableMouseClicked(evt);
            }
        });
        badRewardTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                badRewardTableKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(badRewardTable);
        badRewardTable.getColumnModel().getColumn(0).setMinWidth(75);
        badRewardTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        badRewardTable.getColumnModel().getColumn(0).setMaxWidth(150);
        badRewardTable.getColumnModel().getColumn(1).setMinWidth(50);
        badRewardTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        badRewardTable.getColumnModel().getColumn(1).setMaxWidth(100);

        badRewardLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        badRewardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        badRewardLabel.setText("Bad Reward");

        javax.swing.GroupLayout badRewardPanelLayout = new javax.swing.GroupLayout(badRewardPanel);
        badRewardPanel.setLayout(badRewardPanelLayout);
        badRewardPanelLayout.setHorizontalGroup(
            badRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, badRewardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(badRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                    .addComponent(badRewardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        badRewardPanelLayout.setVerticalGroup(
            badRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, badRewardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(badRewardLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bottomRewardsPanel.add(badRewardPanel);

        mediocreRewardTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Amount", "Extra"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        mediocreRewardTable.getTableHeader().setReorderingAllowed(false);
        mediocreRewardTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mediocreRewardTableMouseClicked(evt);
            }
        });
        mediocreRewardTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mediocreRewardTableKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(mediocreRewardTable);
        mediocreRewardTable.getColumnModel().getColumn(0).setMinWidth(75);
        mediocreRewardTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        mediocreRewardTable.getColumnModel().getColumn(0).setMaxWidth(150);
        mediocreRewardTable.getColumnModel().getColumn(1).setMinWidth(50);
        mediocreRewardTable.getColumnModel().getColumn(1).setPreferredWidth(75);
        mediocreRewardTable.getColumnModel().getColumn(1).setMaxWidth(100);

        mediocreRewardLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        mediocreRewardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mediocreRewardLabel.setText("Mediocre Reward");
        mediocreRewardLabel.setToolTipText("Everyone will get this reward at the end of the round.");

        javax.swing.GroupLayout mediocreRewardPanelLayout = new javax.swing.GroupLayout(mediocreRewardPanel);
        mediocreRewardPanel.setLayout(mediocreRewardPanelLayout);
        mediocreRewardPanelLayout.setHorizontalGroup(
            mediocreRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mediocreRewardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mediocreRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                    .addComponent(mediocreRewardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mediocreRewardPanelLayout.setVerticalGroup(
            mediocreRewardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mediocreRewardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mediocreRewardLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bottomRewardsPanel.add(mediocreRewardPanel);

        tableChooserComboBox.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tableChooserComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "End Reward", "Good Reward", "Mediocre Reward", "Bad Reward" }));
        tableChooserComboBox.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jLabel1.setText("Add to:");

        javax.swing.GroupLayout rewardChooserPanelLayout = new javax.swing.GroupLayout(rewardChooserPanel);
        rewardChooserPanel.setLayout(rewardChooserPanelLayout);
        rewardChooserPanelLayout.setHorizontalGroup(
            rewardChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rewardChooserPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableChooserComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        rewardChooserPanelLayout.setVerticalGroup(
            rewardChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rewardChooserPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rewardChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableChooserComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout rewardAdderPanelLayout = new javax.swing.GroupLayout(rewardAdderPanel);
        rewardAdderPanel.setLayout(rewardAdderPanelLayout);
        rewardAdderPanelLayout.setHorizontalGroup(
            rewardAdderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        rewardAdderPanelLayout.setVerticalGroup(
            rewardAdderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topRewardsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bottomRewardsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(rewardsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rewardChooserPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rewardAdderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rewardsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(topRewardsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomRewardsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rewardChooserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rewardAdderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void endRewardTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_endRewardTableMouseClicked
        tableClicked(RewardTable.EndReward, evt);
    }//GEN-LAST:event_endRewardTableMouseClicked

    private void goodRewardTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_goodRewardTableMouseClicked
        tableClicked(RewardTable.GoodReward, evt);
    }//GEN-LAST:event_goodRewardTableMouseClicked

    private void badRewardTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_badRewardTableMouseClicked
        tableClicked(RewardTable.BadReward, evt);
    }//GEN-LAST:event_badRewardTableMouseClicked

    private void mediocreRewardTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mediocreRewardTableMouseClicked
        tableClicked(RewardTable.MediocreReward, evt);
    }//GEN-LAST:event_mediocreRewardTableMouseClicked

    private void endRewardTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_endRewardTableKeyReleased
        tableKeyReleased(RewardTable.EndReward, evt);
    }//GEN-LAST:event_endRewardTableKeyReleased

    private void goodRewardTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_goodRewardTableKeyReleased
        tableKeyReleased(RewardTable.GoodReward, evt);
    }//GEN-LAST:event_goodRewardTableKeyReleased

    private void badRewardTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_badRewardTableKeyReleased
        tableKeyReleased(RewardTable.BadReward, evt);
    }//GEN-LAST:event_badRewardTableKeyReleased

    private void mediocreRewardTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mediocreRewardTableKeyReleased
        tableKeyReleased(RewardTable.MediocreReward, evt);
    }//GEN-LAST:event_mediocreRewardTableKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel badRewardLabel;
    private javax.swing.JPanel badRewardPanel;
    private javax.swing.JTable badRewardTable;
    private javax.swing.JPanel bottomRewardsPanel;
    private javax.swing.JLabel endRewardLabel;
    private javax.swing.JPanel endRewardPanel;
    private javax.swing.JTable endRewardTable;
    private javax.swing.JLabel goodRewardLabel;
    private javax.swing.JPanel goodRewardPanel;
    private javax.swing.JTable goodRewardTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel mediocreRewardLabel;
    private javax.swing.JPanel mediocreRewardPanel;
    private javax.swing.JTable mediocreRewardTable;
    private javax.swing.JPanel rewardAdderPanel;
    private javax.swing.JPanel rewardChooserPanel;
    private javax.swing.JLabel rewardsLabel;
    private javax.swing.JComboBox tableChooserComboBox;
    private javax.swing.JPanel topRewardsPanel;
    // End of variables declaration//GEN-END:variables

    private enum RewardTable {
        EndReward("End Reward"),
        GoodReward("Good Reward"),
        MediocreReward("Mediocre Reward"),
        BadReward("Bad Reward");
        
        private String s;
        private RewardTable(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        } 
    }

}
