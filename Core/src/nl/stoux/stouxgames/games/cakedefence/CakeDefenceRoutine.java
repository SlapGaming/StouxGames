package nl.stoux.stouxgames.games.cakedefence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.stoux.stouxgames.storage.YamlStorage;
import nl.stoux.stouxgames.util._;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class CakeDefenceRoutine {

	//Setup
	private Pattern enchantPattern;
	private Pattern damagePattern;
	
	//File info
	private String filename;
	private YamlStorage yaml;
	private FileConfiguration config;

	//Routine info
	private String routinename;
	private String author;
	private int nrOfRounds;
	
	//General Settings
	private boolean mobDrops;
	private boolean playerDrops;
	
	//Start Armor
	private boolean startStuff;
	private ItemStack[] startArmor;
	private ItemStack[] startInventory;
	
	//Round stuff
	private HashMap<Integer, CDRound> rounds;
	
	public CakeDefenceRoutine(String filename) {
		this.filename = filename;
		enchantPattern = Pattern.compile("[(].*[)]");
        damagePattern = Pattern.compile("[{].*[}]");
	}
	
	public boolean setup() {
		yaml = new YamlStorage("cakedefence" + System.getProperty("file.separator") + filename);
		config = yaml.getConfig();
		
		startStuff = false;
		
		//Get general info
		routinename = config.getString("name");
		nrOfRounds = config.getInt("rounds");
		author = config.getString("author");
		if (author == null) {
			author = "Unknown";
		}
		
		//Get settings
		mobDrops = config.getBoolean("mob-drop-ondeath");
		playerDrops = config.getBoolean("player-drop-ondeath");
		
		
		if (routinename == null || nrOfRounds < 1) { //Check name & rounds
			_.log(Level.SEVERE, "Invalid Cake Defence routine: " + routinename + ". (Routine name/rounds)");
			return false;
		}
		
		//Start stuff
		if (config.contains("start")) {
			startArmor = new ItemStack[4];
			startStuff = true;
			int x = 0;
			while (x < 4) { //Fill armor
				String path = "";
				switch (x) {
				case 0: path = "boots";	break;
				case 1: path = "legs";	break;
				case 2: path = "body";	break;
				case 3: path = "helmet"; break;
				}
				ItemStack[] stack = getItems("start." + path);
				if (stack != null) {
					if (stack.length > 0) {
						startArmor[x] = stack[0];
					}
				}
				x++;
			}
			startInventory = getItems("start.inventory");
		}
		
		//Round stuff
		rounds = new HashMap<>();
		int round = 1;
		while (round <= nrOfRounds) {
			rounds.put(round, new CDRound(round));
			round++;
		}
		return true;
	}
	
	/**
	 * Get the items specified in a path
	 * @param path The path
	 * @return The stack of items
	 */
	private ItemStack[] getItems(String path) {
		String itemString = config.getString(path); //Get string
		if (itemString == null) return null; //Check if not null
		String items = itemString.replace("[","").replace("]", "");
		String[] item = items.split(",");
		ArrayList<ItemStack> array = new ArrayList<>();
		for (String i : item) {
			try {
				array.add(parseItem(i));
			} catch (Exception e) {
				_.log(Level.WARNING, "Failed to parse item. Item: " + i + " | Exception: " + e.getMessage());
			}
		}
		return array.toArray(new ItemStack[array.size()]);
	}
	
	/**
	 * Parse the found string to an ItemStack
	 * @param s The string
	 * @return The ItemStack
	 * @throws Exception any exception can be thrown
	 */
	private ItemStack parseItem(String s) throws Exception {
		String[] split = s.split(":");
		
		
		//Matchers
		Matcher e = enchantPattern.matcher(s);
		Matcher d = damagePattern.matcher(s);
		
		//Strings
		String enchant = null;
		String damage = null;
		String item = split[0];
		
		//Enchants
		if (e.find()) {
			enchant = item.substring(e.start(), e.end());
		}
		
		//Damage
		if (d.find()) {
			damage = item.substring(d.start(), d.end());
		}
		
		//Remove enchants from Item String
		if (enchant != null) {
			item = item.replace(enchant, "");
		}
		
		//Remove damage from Item String
		if (damage != null) {
			item = item.replace(damage, "");
		}
		
		//Amount
		int amount = Integer.parseInt(split[1]);
		
		
		
		//Create item
		ItemStack stack;
		
		if (item.contains("=")) { //Check if data value found
			String[] splitItem = item.split("=");
			stack = new ItemStack(Integer.parseInt(splitItem[0]), amount, Short.parseShort(splitItem[1]));
		} else {
			stack = new ItemStack(Integer.parseInt(item), amount);
		}
		
		if (damage != null) { //Put damage counter on it
			damage = damage.replace("{", "").replace("}", "").replace("%", "");
			int procent = Integer.parseInt(damage);
			int maxDurability = stack.getType().getMaxDurability();
			int durability = (int) Math.ceil(((maxDurability / (double) 100) * procent));
			stack.setDurability((short) (maxDurability - durability));
		}
		
		if (enchant != null) { //Put enchants on the piece
			for (Entry<Enchantment, Integer> entry : getEnchantments(enchant).entrySet()) {
				stack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
			}
		}
		return stack;
	}
	
	/**
	 * Get a hashmap of all the enchantments in the string
	 * @param enchs The string of enchantments
	 * @return The HashMap with enchants - Key: Enchantment | Value: Level
	 */
	private HashMap<Enchantment, Integer> getEnchantments(String enchs) {
		String fullEnch = enchs.replace("(", "").replace(")", "").replace("|", "=");
		String[] split = fullEnch.split("=");
		HashMap<Enchantment, Integer> eMap = new HashMap<>();
		for (String s : split) {
			String[] enchanment = s.split("-");
			Enchantment e;
			switch (enchanment[0].toLowerCase()) {
			//Melee
			case "sharpness": case "sharp":
				e = Enchantment.DAMAGE_ALL;
				break;
			case "smite":
				e = Enchantment.DAMAGE_UNDEAD;
				break;
			case "bane_of_arthropods": case "arthropods": case "bane":
				e = Enchantment.DAMAGE_ARTHROPODS;
				break;
			case "knockback": case "knock":
				e = Enchantment.KNOCKBACK;
				break;
			case "fire_aspect":
				e = Enchantment.FIRE_ASPECT;
				break;
			case "looting":
				e = Enchantment.LOOT_BONUS_MOBS;
				break;				
				
			//Bows
			case "power":
				e = Enchantment.ARROW_DAMAGE;
				break;
			case "infinity":
				e = Enchantment.ARROW_INFINITE;
				break;
			case "flame":
				e = Enchantment.ARROW_FIRE;
				break;
			case "punch":
				e = Enchantment.ARROW_KNOCKBACK;
				
			//Armor
			case "protection":
				e = Enchantment.PROTECTION_ENVIRONMENTAL;
				break;
			case "blast": case "blast_protection":
				e = Enchantment.PROTECTION_EXPLOSIONS;
				break;
			case "projectile": case "projectile_protection":
				e = Enchantment.PROTECTION_PROJECTILE;
				break;
			case "fire_protection":
				e = Enchantment.PROTECTION_FIRE;
				break;
			case "fall_protection": case "feather_falling":
				e = Enchantment.PROTECTION_FALL;
				break;
			case "aqua_affinity": case "aqua":
				e = Enchantment.WATER_WORKER;
				break;
			case "respiration":
				e = Enchantment.OXYGEN;
				break;
			case "thorns":
				e = Enchantment.THORNS;
				break;
				
			//Other
			case "unbreaking":
				e = Enchantment.DURABILITY;
				break;
			default: 
				_.log(Level.WARNING, "Invalid Enchantment found: " + enchanment[0]);
				continue;
			}
			int level = Integer.parseInt(enchanment[1]);
			if (e.getMaxLevel() < level) level = e.getMaxLevel(); //Max level check
			eMap.put(e, level); //Put enchantment in the map
		}
		return eMap;
	}
	
	public class CDRound {
		
		//This round
		private int round;
		
		//Hashmap with the mobs. Key: Type of mob | Value: Number that needs to spawn
		private HashMap<MobType, Integer> mobs;
		
		//Rewards map. 0 == Bad, 1 == Mediocre, 2 == Good
		private HashMap<Integer, ItemStack[]> rewards;
		
		//Itemstack with the price everyone gets
		private ItemStack[] everyoneReward;
		
		/**
		 * Create a new Cake Defence Round -- Belongs to a routine
		 * @param round The round number
		 */
		public CDRound(int round) {
			this.round = round;
			
			//Create maps
			rewards = new HashMap<>();
			mobs = new HashMap<>();
			
			//Parse mobs
			ConfigurationSection mobSection = config.getConfigurationSection("round" + round + ".mobs");
			for (String key : mobSection.getKeys(false)) {
				MobType type = MobType.valueOf(key);
				int amount = mobSection.getInt(key);
				if (type == null || amount < 1) {
					System.out.println("Failed to get mob. Value found: '" + key + "'");
					continue;
				}
				mobs.put(type, amount);
			}
			
			//Parse rewards
			everyoneReward = getItems("round" + round + ".reward.everyone");
			
			int x = 0;
			while (x < 3) { //Loop thru reward levels
				String reward = null;
				switch (x) {
				case 0: reward = "bad"; break;
				case 1: reward = "mediocre"; break;
				case 2: reward = "good"; break;
				}
				ItemStack[] stack = getItems("round" + round + ".reward." + reward); //Get reward from config
				if (stack != null) {
					rewards.put(x, stack);
				}
				x++;
			}
		}
		
		/**
		 * Get the number of different mobs
		 * @return the number
		 */
		public int getDifferentMobs() {
			return mobs.size();
		}
		
		/**
		 * Get the current round number
		 * @return the number
		 */
		public int getRound() {
			return round;
		}
		
		/**
		 * Get this round's reward
		 * @return The Reward (ItemStack array) or null if no reward
		 */
		public ItemStack[] getEveryoneReward() {
			return everyoneReward;
		}
		
		/**
		 * Get the reward of this round
		 * Levels: 0 == Bad | 1 == Mediocre | 2 == Good
		 * @param level The reward level
		 * @return The ItemStack array with the rewards or null
		 */
		public ItemStack[] getReward(int level) {
			return rewards.get(level);
		}
		
		/**
		 * Returns a new HashMap with mobs
		 * @return the mobs
		 */
		public HashMap<MobType, Integer> getMobs() {
			return new HashMap<MobType, Integer>(mobs);
		}
		
	}
	

	/*
	 * Public methods
	 */
	
	/**
	 * Get the author of this routine
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * Get the name of this routine
	 * @return the name
	 */
	public String getRoutinename() {
		return routinename;
	}
	
	/**
	 * The mobs should drop items when they die
	 * @return yes/no
	 */
	public boolean hasMobDrops() {
		return mobDrops;
	}
	
	/**
	 * Players should drop items (inventory) when they die 
	 * @return yes/no
	 */
	public boolean hasPlayerDrops() {
		return playerDrops;
	}
	
	/**
	 * Get number of rounds
	 * @return number
	 */
	public int getNrOfRounds() {
		return nrOfRounds;
	}
	
	/**
	 * Get the Cake Defence Round of this routine
	 * @param round The round
	 * @return The CDRound or null
	 */
	public CDRound getRound(int round) {
		return rounds.get(round);
	}	
	
	/**
	 * Check if there is any start armor/inventory
	 * @return yes/no
	 */
	public boolean hasStartStuff() {
		return startStuff;
	}
	
	/**
	 * Get the Start Armor array
	 * [0] == Boots | [1] == Legs | [2] == Body | [3] == Helmet
	 * @return The array with ItemStacks. These can be null.
	 */
	public ItemStack[] getStartArmor() {
		return startArmor;
	}
	
	/**
	 * Get the Start inventory array
	 * @return The itemstack array or null
	 */
	public ItemStack[] getStartInventory() {
		return startInventory;
	}
	
}
