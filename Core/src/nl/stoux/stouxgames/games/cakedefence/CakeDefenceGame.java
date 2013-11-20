package nl.stoux.stouxgames.games.cakedefence;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import nl.stoux.stouxgames.games.cakedefence.CakeDefenceRoutine.CDRound;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerState;
import nl.stoux.stouxgames.util._T;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
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
		
		//Give armor
		for (GamePlayer gP : cd.getPlayers()) {
			if (gP.getState() == PlayerState.playing) {
				routine.giveStartItems(gP.getPlayer());
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
		
		//Give rewards
		CDRound cdRound = roundTask.getRound(); //Get the round
		for (GamePlayer gP : cd.getPlayers()) {
			if (gP.getState() == PlayerState.playing) {
				cdRound.giveReward(gP.getPlayer());
			}
		}
				
		mobCleaner.disableTimeout(); //Disable the timeout feature on the mob cleaner
		roundTask = null;
		
		cooldownTask = _T.runLater_Sync(new Runnable() { //Start the cooldown task
			
			@Override
			public void run() {
				startRound(); //Start round
			}
		}, 300);
		
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
	 * A mob dies with an event
	 * @param ID The ID of the entity
	 * @param event The death event
	 */
	public void mobDies(UUID ID, EntityDeathEvent event) {
		if (!routine.hasMobDrops()) {
			event.getDrops().clear();
		}
		mobDies(ID);
	}
	
	/**
	 * A mob dies
	 * @param ID The ID of the entity
	 */
	public void mobDies(UUID ID) {
		String id = ID.toString();
		if (!spawnedMobs.containsKey(id)) {
			return;
		}
		spawnedMobs.remove(id);
		if (state == GameState.RoundEndedMobs) { //Check if if game is waiting for remaining mobs
			if (spawnedMobs.size() == 0) { //No mobs left -> End round
				giveReward(); //Give reward
			}
		}
	}
	
	/**
	 * Get the routine
	 * @return the routine
	 */
	public CakeDefenceRoutine getRoutine() {
		return routine;
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
		
		
		
		/**
		 * Create a new RoundTask
		 * Should be run each tick
		 * @param round The round of a Routine
		 */
		public RoundTask(CDRound round) {
			this.round = round;
			
			//Set to 0
			tillNextSpawn = 0;
			tick = 0;
			
			//Set bools
			ended = false;
						
			//Calculate ticks per mob
			double ticksDouble = (31.5 * 20) / round.getNumberOfMobsLeft();
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
			if (round.getDifferentMobs() == 0) {
				if (ended == false) {
					endThisRound();
				}
				return;
			}
			
			for (LivingEntity le : round.spawnMob(cd.getSpawnLocation())) {
				spawnedMobs.put(le.getUniqueId().toString(), le); //Add to map
			}
		}
		
		@Override
		public void run() {
			if (tick > 640) { //Check if end of game
				if (ended == true) return;
				endThisRound();
				return;
			} else if (tick == 0) { //Check if start of game
				cd.broadcastToPlayers("The next round has started! " + ChatColor.GRAY + "(Round " + round.getRoundNumber() + "/" + routine.getNrOfRounds() + ")");
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
