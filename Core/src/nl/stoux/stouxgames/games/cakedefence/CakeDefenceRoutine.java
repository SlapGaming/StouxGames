package nl.stoux.stouxgames.games.cakedefence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;

import nl.stoux.stouxgames.util._;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import entity.collections.RewardCollection;
import entity.collections.Round;
import entity.enums.EnchantmentType;
import entity.enums.ItemEnum;
import entity.enums.PotionItemType;
import entity.instance.Item;
import entity.instance.Mob;
import entity.instance.PotionEffect;
import entity.instance.PotionItem;
import entity.main.GeneralSettings;
import entity.main.Routine;
import entity.main.StartReward;

public class CakeDefenceRoutine {
	
	/*
	 * General info
	 */
	private int nrOfRounds;
	private String author;
	private String routinename;
	private boolean mobDrops;
	private boolean playerDrops;
	
	/*
	 * Start info
	 */
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack[] inventory;
	
	/*
	 * Rounds
	 */
	private ArrayList<CDRound> rounds;
	
	public CakeDefenceRoutine(Routine r) {
		//General settings
		GeneralSettings gs = r.getGeneralSettings();
		author = gs.getAuthor();
		routinename = gs.getRoutinename();
		mobDrops = gs.isMobDropOnDeath();
		playerDrops = gs.isPlayerDropOnDeath();
		
		//Rounds info
		nrOfRounds = r.getRounds().size();
		
		//Parse start info
		StartReward sr = r.getStartReward();
		helmet = parseItem(sr.getHead());
		chestplate = parseItem(sr.getChest());
		leggings = parseItem(sr.getLegs());
		boots = parseItem(sr.getBoots());
		inventory = parseRewardCollection(sr.getStartInventory());
		
		//Parse rounds
		rounds = new ArrayList<>();
		int roundNumber = 1;
		for (Round round : r.getRounds()) {
			rounds.add(new CDRound(round, roundNumber));
			roundNumber++;
		}
	}
	
	private ItemStack[] parseRewardCollection(RewardCollection rc) {
		if (rc == null) return null;
		ArrayList<Item> items = rc.getItems();
		ItemStack[] stack = new ItemStack[items.size()];
		int x = 0;
		for (Item i : items) {
			stack[x] = parseItem(i);
			x++;
		}
		return stack;
	}
	
	private ItemStack parseItem(Item item) {		
		if (item == null) return null;
		ItemStack stack;
		if (item instanceof PotionItem) {
			PotionItem potion = item.getPotion();
			int itemID = item.getItemType().getItemID();
			int amount = item.getAmount();
			PotionItemType type = potion.getType();
			if (potion.isSplash()) {
				stack = new ItemStack(itemID, amount, (short) type.getSplashValue());
			} else {
				stack = new ItemStack(itemID, amount, (short) type.getDataValue());
			}
		} else {
			if (item.getItemType() == ItemEnum.SUPER_GOLDEN_APPLE) {
				return new ItemStack(item.getItemType().getItemID(), item.getAmount(), (short) 1);
			}
			stack = new ItemStack(item.getItemType().getItemID(), item.getAmount());
			if (item.hasDurability()) {
				short maxDurability = stack.getType().getMaxDurability();
				int foundDurability = item.getDurability();
				if (foundDurability != 100) {
					int durabiltiy = (int) ((double) maxDurability - ((double) foundDurability * ((double ) maxDurability / (double) 100)));
					stack.setDurability((short) durabiltiy);
				}
			}
			if (item.hasEnchants()) {
				for (Entry<EnchantmentType, Integer> entry : item.getEnchants().entrySet()) {
					Enchantment e = Enchantment.getByName(entry.getKey().toString());
					stack.addEnchantment(e, entry.getValue());
				}
			}
		}
		return stack;
	}

	/**
	 * Get Author
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Get the routinename
	 * @return the name
	 */
	public String getRoutinename() {
		return routinename;
	}

	/**
	 * Check if mob drops are enabled
	 * @return enabled
	 */
	public boolean hasMobDrops() {
		return mobDrops;
	}

	/**
	 * Check if player drops are enabled
	 * @return enabled
	 */
	public boolean hasPlayerDrops() {
		return playerDrops;
	}

	/**
	 * Get the number of rounds this routine has
	 * @return number
	 */
	public int getNrOfRounds() {
		return nrOfRounds;
	}
	
	/*
	 *******************
	 * Start Inventory *
	 *******************
	 */
	/**
	 * Give the player their starting inventory for this routine
	 * @param p The player
	 */
	public void giveStartItems(Player p) {
		PlayerInventory pi = p.getInventory();
		if (helmet != null) pi.setHelmet(helmet);
		if (chestplate != null) pi.setChestplate(chestplate);
		if (leggings != null) pi.setLeggings(leggings);
		if (boots != null) pi.setBoots(boots);
		giveReward(inventory, pi);
	}
	
	private static void giveReward(ItemStack[] stack, PlayerInventory pi) {
		if (stack != null) {
			pi.addItem(stack);
		}
	}
		
	
	/*
	 **********
	 * Rounds *
	 **********
	 */
	public CDRound getRound(int round) {
		return rounds.get(round - 1);
	}
	
	public class CDRound {
		
		private int roundNumber;
		
		private ItemStack[] badReward;
		private ItemStack[] mediocreReward;
		private ItemStack[] goodReward;
		private ItemStack[] endReward;
		
		private ArrayList<CDMob> mobs;
		private int numberOfMobs;
		private int differentMobs;
		
		public CDRound(Round round, int roundNumber) {
			this.roundNumber = roundNumber;
			badReward = parseRewardCollection(round.getBad());
			mediocreReward = parseRewardCollection(round.getMediocre());
			goodReward = parseRewardCollection(round.getGood());
			endReward = parseRewardCollection(round.getEveryone());
			
			mobs = new ArrayList<>();
			numberOfMobs = 0;
			differentMobs = 0;
			
			for (Mob mob : round.getMobs()) {
				mobs.add(new CDMob(mob));
				numberOfMobs = numberOfMobs + mob.getAmount();
				differentMobs++;
			}
		}
		
		/**
		 * Spawn a random mob
		 * @param loc The location for the mob
		 * @return the spawned mobs
		 */
		public LivingEntity[] spawnMob(Location loc) {
			CDMob mob = mobs.get(_.getRandomInt(differentMobs));
			LivingEntity[] entities = mob.spawnMob(loc);
			if (mob.getAmount() == 0) {
				differentMobs--;
				mobs.remove(mob);
			}
			numberOfMobs--;
			return entities;
		}
		
		/**
		 * Give the player a/the reward for this round
		 * @param p The player
		 */
		public void giveReward(Player p) {
			PlayerInventory pi = p.getInventory();
			giveReward(endReward, pi); //Give standard reward
			
			int random = _.getRandomInt(20); //10% for Good  --  25% for Mediocre  --  45% for Bad
			if 		(random >= 0 && random <= 1) 	giveReward(goodReward, 		pi);
			else if	(random >= 2 && random <= 6) 	giveReward(mediocreReward, 	pi);
			else if (random >= 7 && random <= 15) 	giveReward(badReward, 		pi);
			
		}
		
		/**
		 * Get the number of different mobs left
		 * @return number of different mobs
		 */
		public int getDifferentMobs() {
			return differentMobs;
		}
		
		/**
		 * Get the number of mobs that still need to spawn
		 * @return mobs left
		 */
		public int getNumberOfMobsLeft() {
			return numberOfMobs;
		}
		
		/**
		 * Get the number of this round
		 * @return the number
		 */
		public int getRoundNumber() {
			return roundNumber;
		}
		
		private void giveReward(ItemStack[] stack, PlayerInventory pi) {
			CakeDefenceRoutine.giveReward(stack, pi);
		}
	}
	
	public class CDMob {		
		
		private int amount; 
		private MobType type;
		private boolean hasPotionEffects;
		private HashSet<org.bukkit.potion.PotionEffect> potionEffects;
		
		public CDMob(Mob mob) {
			amount = mob.getAmount();
			type = MobType.valueOf(mob.getMobType().toString());
			hasPotionEffects = mob.hasPotionEffects();
			if (hasPotionEffects) {
				potionEffects = new HashSet<>();
				for (PotionEffect effect : mob.getPotionEffects()) {
					PotionEffectType type = PotionEffectType.getByName(effect.getType().toString());
					long length = effect.getLength() * 20;
					if (length > Integer.MAX_VALUE || effect.getLength() == -1) length = Integer.MAX_VALUE;
					potionEffects.add(type.createEffect((int) length, effect.getLevel()));
				}
			}
		}
		
		/**
		 * Spawn an instance of this mob
		 * @param loc the location
		 * @return the spawned mobs
		 */
		public LivingEntity[] spawnMob(Location loc) {
			LivingEntity[] entities = MobType.spawnMob(type, loc);
			if (hasPotionEffects) {
				for (LivingEntity le : entities) {
					le.addPotionEffects(potionEffects);
				}
			}
			amount--;
			return entities;
		}
		
		/**
		 * Get amount of mobs left
		 * @return amount
		 */
		public int getAmount() {
			return amount;
		}
		
		/**
		 * Get the mob type of this mob
		 * @return the type
		 */
		public MobType getType() {
			return type;
		}
		
	}
}
