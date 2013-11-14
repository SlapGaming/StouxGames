package nl.stoux.stouxgames.games.cakedefence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import nl.stoux.stouxgames.games.cakedefence.CakeDefenceRoutine.CDRound;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerState;
import nl.stoux.stouxgames.util._;
import nl.stoux.stouxgames.util._T;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CakeDefenceGame extends BukkitRunnable {

	//Main class
	private CakeDefence cd;
	private CakeDefenceRoutine routine;
	
	//Game state
	private GameState state;
	
	//Round info
	private int round;
	private RoundTask roundTask;
	private BukkitTask roundBukkitTask;
	
	//Other tasks
	private BukkitTask cooldownTask;
	private MobCleaner mobCleaner;
	private BukkitTask mobCleaningTask;
	
	//Spawned Mobs
	public ConcurrentHashMap<String,LivingEntity> spawnedMobs;
	
	
	public CakeDefenceGame(CakeDefence cd, CakeDefenceRoutine routine) {
		this.cd = cd;
		this.routine = routine;
		
		state = GameState.Starting;
		spawnedMobs = new ConcurrentHashMap<>();
	}
	
	@Override
	public void run() {
		round = 1;
		
		//Create & Start the mob cleaner
		mobCleaner = new MobCleaner(10);
		mobCleaningTask = _T.runTimer_Sync(mobCleaner, 10, 10);
		
		//Get armor
		ItemStack boots = null, legs = null, body = null, helmet = null;		
		ItemStack[] startArmor = routine.getStartArmor();
		if (startArmor != null) {
			boots = startArmor[0];
			legs = startArmor[1];
			body = startArmor[2];
			helmet = startArmor[3];
		}
		ItemStack[] startInventory = routine.getStartInventory();
		
		if (startArmor != null || startInventory != null) {
			//Give armor
			for (GamePlayer gP : cd.getPlayers()) {
				if (gP.getState() == PlayerState.playing) {
					PlayerInventory inv = gP.getPlayer().getInventory(); //Get de player's inventory
					if (boots != null) inv.setBoots(boots); //Set boots
					if (legs != null) inv.setLeggings(legs); //Set legs
					if (body != null) inv.setChestplate(body); //Set body
					if (helmet != null) inv.setHelmet(helmet); //set helmet
					if (startInventory != null) inv.addItem(startInventory); //Give inventory
				}
			}
		}
		
		//Start the first round
		cooldownTask = _T.runLater_Sync(new Runnable() {
			@Override
			public void run() {
				startRound();
			}
		}, 200);
	}
	
	/**
	 * Start the round
	 */
	private void startRound() {
		state = GameState.RoundRunning; //Set State
		
		//Get the round
		CDRound cdRound = routine.getRound(round);
		
		//Open spawners
		cd.openSpawners(cdRound.getDifferentMobs());
		
		//Start the new round
		roundTask = new RoundTask(cdRound);
		roundBukkitTask = _T.runTimer_Sync(roundTask, 1, 1);
	}
	
	/**
	 * End the round
	 */
	private void endRound() {
		roundBukkitTask.cancel();
		
		//Close the spawners
		cd.closeSpawners();
		
		if (spawnedMobs.size() == 0) {
			//No mobs left -> Enter cooldown
			giveReward();
		} else {
			//Still some mobs alive
			state = GameState.RoundEndedMobs;
			mobCleaner.setTimeout();
		}
	}
	
	/**
	 * Give the rewards
	 * Initialze cooldown
	 */
	private void giveReward() {
		round++;
		if (round > routine.getNrOfRounds()) { //Check if game fully ended
			shutdown();
			return;
		}
		
		//Broadcast & Set state
		state = GameState.Cooldown;
		cd.broadcastToPlayers("All mobs have been eliminated, good job! Get ready for the next round!");
		
		CDRound cdRound = roundTask.getRound(); //Get the round
		ItemStack[] everyone = cdRound.getEveryoneReward(); //Get the 'everyone' reward
		for (GamePlayer gP : cd.getPlayers()) {
			if (gP.getState() == PlayerState.playing) {
				PlayerInventory inv = gP.getPlayer().getInventory(); //Get the player's inventory
				if (everyone != null) { //Give the everyone reward if not null
					inv.addItem(everyone);
				}
				ItemStack[] personalReward = cdRound.getReward(_.getRandomInt(3)); //Get personal reward (based on level)
				if (personalReward != null ) { //Give the reward if not null
					inv.addItem(personalReward);
				}
			}
		}
		
		mobCleaner.disableTimeout(); //Disable the timeout feature on the mob cleaner
		roundTask = null;
		
		cooldownTask = _T.runLater_Sync(new Runnable() { //Start the cooldown task
			
			@Override
			public void run() {
				startRound(); //Start round
			}
		}, 200);
		
	}
	
	/**
	 * Shut down the game incase there are no players left
	 */
	public void shutdown() {
		//Kill tasks
		if (roundBukkitTask instanceof BukkitTask) roundBukkitTask.cancel();
		if (cooldownTask instanceof BukkitTask) cooldownTask.cancel();
		if (mobCleaningTask instanceof BukkitTask) mobCleaningTask.cancel();
		
		//Kill the remaining mobs
		killAllMobs();
		
		//End game
		cd.endGame();
	}
	
	/*
	 * Mob functions
	 */
	/**
	 * Kill all the living mobs & clear the map
	 */
	private void killAllMobs() {
		//Kill mobs
		for (LivingEntity entity : spawnedMobs.values()) {
			entity.remove();
		}
		spawnedMobs.clear(); //Clear map
	}
		
	/**
	 * A mob dies
	 * @param id The entity's {@link UUID}
	 */
	public void mobDies(UUID id) {
		if (!spawnedMobs.containsKey(id.toString())) {
			return;
		}
		spawnedMobs.remove(id.toString());
		if (state == GameState.RoundEndedMobs) { //Check if if game is waiting for remaining mobs
			if (spawnedMobs.size() == 0) { //No mobs left -> End round
				giveReward(); //Give reward
			}
		}
	}
	
	
	private class RoundTask extends BukkitRunnable {
		
		//Info
		private boolean ended;
		
		//This round
		private CDRound round;
		
		//Current tick
		private int tick;
		
		//Mobspawns
		private int spawnPerTicks;
		private int tillNextSpawn;
		
		//Mobs
		private int differentEntities = 0;
		private ArrayList<MobType> entities;
		private HashMap<MobType, Integer> mobs;
		private int totalMobsLeft;
		
		
		/**
		 * Create a new RoundTask
		 * Should be run each tick
		 * @param round The round of a Routine
		 */
		public RoundTask(CDRound round) {
			this.round = round;
			mobs = round.getMobs();
			entities = new ArrayList<>();
			
			//Set to 0
			totalMobsLeft = 0;
			differentEntities = 0;
			tillNextSpawn = 0;
			tick = 0;
			
			//Set bools
			ended = false;
			
			for (Entry<MobType, Integer> entry : mobs.entrySet()) { //Add types to the arraylist
				entities.add(entry.getKey());
				differentEntities++;
				totalMobsLeft = totalMobsLeft + entry.getValue();
			}
			
			//Calculate ticks per mob
			double ticksDouble = (31.5 * 20) / totalMobsLeft;
			spawnPerTicks = (int) Math.floor(ticksDouble);
			
			//Check if not too low
			if (spawnPerTicks > 100) {
				spawnPerTicks = 100;
			}
		}
		
		/**
		 * Spawn a mob
		 */
		private void spawnMob() {
			if (differentEntities == 0) {
				if (ended == false) {
					endThisRound();
				}
				return;
			}
			MobType type = entities.get(_.getRandomInt(differentEntities)); //Get the Type
			int mobsLeft = mobs.get(type); //Get number of mobs that still need be spawned (of this type)
			mobsLeft--; //Decrease by 1
			if (mobsLeft == 0) { //If no more need to be spawned after this remove the mob from the lists
				mobs.remove(type);
				entities.remove(type);
				differentEntities--;
			} else { //Else update the amount left
				mobs.put(type, mobsLeft);
			}
			
			//Spawn mobs			
			for (LivingEntity le : MobType.spawnMob(type, cd.getSpawnLocation())) {
				spawnedMobs.put(le.getUniqueId().toString(), le); //Add to map
			}
			
			//Process
			totalMobsLeft--;
		}
		
		@Override
		public void run() {
			if (tick > 640) { //Check if end of game
				if (ended == true) return;
				endThisRound();
				return;
			} else if (tick == 0) { //Check if start of game
				cd.broadcastToPlayers("The next round has started! " + ChatColor.GRAY + "(Round " + round.getRound() + "/" + routine.getNrOfRounds() + ")");
				tillNextSpawn = 1;
			}
			
			tillNextSpawn--; //Decrease tick
			if (tillNextSpawn <= 0) { //Next spawn is now)
				spawnMob(); //Spawn the mob
				tillNextSpawn = spawnPerTicks; //Reset the timer
			}			
			
			tick++; //Increase total ticks;
		}
		
		/**
		 * End this round
		 */
		private void endThisRound() {
			ended = true;
			endRound();
		}
				
		/**
		 * Get this round's CDRound
		 * @return the round
		 */
		public CDRound getRound() {
			return round;
		}
		
	}
	
	private class MobCleaner extends BukkitRunnable {
		
		//Interval
		private int interval;
		
		//Timeout kill
		private boolean timeout;
		private int loopLeft;
		
		//The dead region
		private ProtectedRegion lavaRegion;
		
		/**
		 * Create a new MobCleaner task
		 * @param interval ticks
		 */
		public MobCleaner(int interval) {
			this.interval = interval;
			lavaRegion = cd.getLavaRegion();
		}
		
		@Override
		public void run() {			
			if (spawnedMobs.size() > 0) {
				//Loop thru entities
				for (LivingEntity e : spawnedMobs.values()) {
					Location loc = e.getLocation(); //Get it's location
					if (lavaRegion.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) { //Check if it is in the Lava region
						mobDies(e.getUniqueId());
						e.remove(); //Remove the mob
					}
				}
			}
			
			//Check if timeout is running
			if (timeout) {
				loopLeft--; //decrease timer
				if (loopLeft <= 0) { //Kill the game
					killAllMobs();
					giveReward();
				}
			}	
		}
		
		
		/*
		 * Timeout functions
		 */
		/**
		 * Set the timeout function
		 * This will kill all the mobs & end the round
		 */
		public void setTimeout() {
			timeout = true;
			loopLeft = 300 * (20 / interval);
		}
		
		/**
		 * Disable the timeout
		 */
		public void disableTimeout() {
			timeout = false;
			loopLeft = 0;
		}
		
	}
	
	private enum GameState {
		Starting,
		Cooldown,
		RoundRunning,
		RoundEndedMobs;
	}

}
