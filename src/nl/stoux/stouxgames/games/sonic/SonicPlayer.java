package nl.stoux.stouxgames.games.sonic;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffectType;


import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;

public class SonicPlayer extends GamePlayer {
	
	private SonicRun sonicRun;
	private Sonic sonic;
	
	public SonicPlayer(Player player, Sonic game) {
		super(player, game);
		sonic = game;
	}
	
	/**
	 * Give the player the boosts & armor
	 */
	public void becomeSonic() {
		//Give potions
		_.giveInfinitePotion(p, PotionEffectType.SPEED);
		_.giveInfinitePotion(p, PotionEffectType.JUMP);
		_.giveInfinitePotion(p, PotionEffectType.REGENERATION, 999999);
		_.giveInfinitePotion(p, PotionEffectType.DAMAGE_RESISTANCE, 3);
		
		//Make boots
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		//Add Enchants to boots
		boots.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 10);
		boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);
		boots.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 10);
		boots.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		
		//Set color
		LeatherArmorMeta itemMeta = (LeatherArmorMeta) boots.getItemMeta();
		itemMeta.setColor(Color.BLUE);
		itemMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.ITALIC + "Sonic Shoes");
		boots.setItemMeta(itemMeta);
		
		//Add the boots to the player's inventory
		p.getInventory().setBoots(boots);
	}

	/**
	 * Reset the Sonic Run to null
	 */
	public void resetSonicRun() {
		sonicRun = null;
	}
	
	/**
	 * Get this person's run
	 * @return The sonic run
	 */
	public SonicRun getSonicRun() {
		return sonicRun;
	}
	
	public void newRun() {
		sonicRun = new SonicRun(this);
		sonicRun.start();
	}
	
	
	/**
	 * A new SonicRun
	 * Made when the player starts a new round, all data will be saved in here.
	 * @author Stoux
	 *
	 */
	public class SonicRun {
		
		//The sonic player it is connected with
		private SonicPlayer sP;
		
		//Is racing
		private boolean racing;
		
		//Currents
		private int currentCheckpoint;
		private int currentJump;
		
		//Checkpoint times
		private long start;
		private long[] cpTimes;
		private long end;
		
		//Jump times
		private long[] jumpTimes;
		
		public SonicRun(SonicPlayer sP) {
			this.sP = sP;
			currentCheckpoint = 0;
			currentJump = 0;
			cpTimes = new long[5];
			jumpTimes = new long[5];
			racing = false;
		}
		
		/**
		 * Started racing
		 */
		public boolean start() {
			if (racing == false) { //If indeed racing
				start = System.currentTimeMillis(); //Save the time
				racing = true;
				sonic.broadcastToPlayers(name + " has started racing!"); //Broadcast to all the players
				return false;
			} else {
				return false;
			}
		}
		
		/**
		 * Passed the finish
		 * @return allowed to pass
		 */
		public boolean end() {
			if (racing == true && currentCheckpoint == 5) { //If racing & the last checkpoint was 5
				end = System.currentTimeMillis();
				racing = false;
				sonic.getLeaderboard().saveSonicRun(sP, sonicRun);
				sonic.broadcastToPlayers(name + " has finished racing in a time of " + sonic.getLeaderboard().getTimeString(start, end) + "!"); //Broadcast finishing time
				sonic.teleportToSonicLobby(sP);
				return true;
			} else {
				_.badMsg(p, "You haven't passed all checkpoints!");
				sonic.teleportToRaceStart(sP);
				return false;
			}
		}
		
		/**
		 * Player passed a checkpoint
		 * @param checkpoint Number of the checkpoint
		 * @return allowed to pass
		 */
		public boolean passedCheckpoint(int checkpoint) {
			if (racing == true && (checkpoint - 1) == currentCheckpoint) { //Passed the previous one correct.
				cpTimes[currentCheckpoint] = System.currentTimeMillis(); //Save the time
				sonic.broadcastToPlayers(name + " has passed checkpoint " + checkpoint + " with a time of " + sonic.getLeaderboard().getTimeString(start, cpTimes[currentCheckpoint]) + "!"); //Broadcast
				currentCheckpoint = checkpoint;
				return true;
			} else {
				if (racing == true) {
					_.badMsg(p, "You have passed the wrong checkpoint & you have been returned to the start!");
				} else {
					_.badMsg(p, "You weren't racing! You need to pass the start first!");
				}
				sonic.teleportToRaceStart(sP);
				return false;
			}
		}
		
		/**
		 * Player passed a jump
		 * @param jumpNumber
		 */
		public void passedJump(int jumpNumber) {
			if (jumpNumber > currentJump) {
				jumpTimes[currentJump] = System.currentTimeMillis();
				currentJump = jumpNumber;
			}
		}
		
		/**
		 * Get the last checkpoint the player has past
		 * @return number
		 */
		public int getCurrentCheckpoint() {
			return currentCheckpoint;
		}
		
		/**
		 * Get the last jump the player has past
		 * @return number
		 */
		public int getCurrentJump() {
			return currentJump;
		}
		
		/**
		 * Check if the player is racing
		 * @return is racing
		 */
		public boolean isRacing() {
			return racing;
		}
		
		
		
		/*
		 * Getters
		 */
		public long getStart() {
			return start;
		}
		
		public long getEnd() {
			return end;
		}
		
		public long[] getCheckpointTimes() {
			return cpTimes;
		}
		
		public long[] getJumpTimes() {
			return jumpTimes;
		}
		
		
	}
	

}
