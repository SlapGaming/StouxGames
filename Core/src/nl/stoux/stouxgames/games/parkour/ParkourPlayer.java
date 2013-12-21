package nl.stoux.stouxgames.games.parkour;

import java.util.HashMap;

import org.bukkit.entity.Player;

import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.player.PlayerState;
import nl.stoux.stouxgames.util._;

public class ParkourPlayer extends GamePlayer {

	//Main class
	private Parkour parkour;
	
	//Parkour Run
	private ParkourRun run;
	
	//General info
	/**
	 * Hashmap containing MapID -> Saved Checkpoint
	 */
	private HashMap<Integer, SavedCheckpoint> savedCheckpoints; 
	
	public ParkourPlayer(Player player, Parkour game) {
		super(player, game);
		this.parkour = game;
		savedCheckpoints = new HashMap<>();
		parkour.getSQL().getSavedCheckpoints(this);
	}
	
	/**
	 * Save a checkpoint
	 * @param mapID The ID of the map
	 * @param passedTime The time the player has spend so far
	 * @param lastCheckpoint The last checkpoint the player passed
	 */
	public void addSavedCheckpoint(int mapID, long passedTime, int lastCheckpoint) {
		savedCheckpoints.put(mapID, new SavedCheckpoint(passedTime, lastCheckpoint));
	}
	
	/**
	 * Get the current map the player is playing on
	 * @return The map or null if not on a map
	 */
	public ParkourMap getCurrentMap() {
		if (run == null) return null;
		else return run.getCurrentMap();
	}
	
	/**
	 * Get all the saved checkpoints of this player
	 * @return the saved checkpoints
	 */
	public HashMap<Integer, SavedCheckpoint> getSavedCheckpoints() {
		return savedCheckpoints;
	}
	
	/**
	 * Check if the player is on a parkour map
	 * @return The map
	 */
	public boolean isOnParkourMap() {
		return (run != null);
	}
	
	@Override
	public void playerQuitGame() {
		leaveMap(false);
		if (savedCheckpoints.size() > 0) {
			parkour.getSQL().saveSavedCheckpoints(this);
		}
		super.playerQuitGame();
	}
	
	/**
	 * Get a saved checkpoint for a map
	 * @param mapID The map
	 * @return The saved checkpoint or null if nothing saved
	 */
	public SavedCheckpoint getSavedCheckpoint(int mapID) {
		return savedCheckpoints.get(mapID);
	}
	
	
	
	/**
	 * Player joins a map
	 * @param map The map
	 */
	public void joinMap(ParkourMap map) {
		int mapID = map.getID();
		boolean saved = false;
		if (savedCheckpoints.containsKey(mapID)) {
			SavedCheckpoint scp = savedCheckpoints.get(mapID);
			run = new ParkourRun(map, scp.getPassedTime(), scp.getLastCheckpoint());
			savedCheckpoints.remove(mapID);
			saved = true;
		} else {
			run = new ParkourRun(map);
		}
		setState(PlayerState.playing);
		p.teleport(map.getLobby());
		parkour.broadcastToPlayers(name + " has joined the map " + map.getName());
		if (saved) _.msg(p, GameMode.Parkour, "Your progress of a previous run has been saved! Press the continue button to continue that run.");
	}
	
	/**
	 * Player leaves a parkour map
	 * @param teleport Teleport the player to the parkour lobby
	 */
	public void leaveMap(boolean teleport) {
		setState(PlayerState.lobby); //Set state
		if (!isOnParkourMap()) return; //Not on a map
		if (run.getCurrentMap().hasAllowRestartOnCheckpoint()) {  //Check if save checkpoints is enabled
			if (run.hasSavedCheckpoint()) { //Still has a SavedCheckpoint, just store it agian
				addSavedCheckpoint(run.getCurrentMap().getID(), run.getSavedCheckpointTime(), run.getCurrentCheckpoint());
			} else if (run.hasStarted() && !run.isFinished()) { //Started a run but didn't finish yet
				if (run.getCurrentCheckpoint() > 0) { //Check if not still at the start
					addSavedCheckpoint(run.getCurrentMap().getID(), run.getPassedTime(), run.getCurrentCheckpoint());
				}
			}
		}
		if (run.getFails() > 0) { //Check if any fails were made, if so add
			parkour.getSQL().addFailsToPlayerMap(getName(), run.getCurrentMap().getID(), run.getFails());
		}
		run = null;
		if (teleport) p.teleport(parkour.getLobby()); //Teleport player
	}
	
	/**
	 * Get this player's run
	 * @return the run
	 */
	public ParkourRun getRun() {
		return run;
	}
		
	public class ParkourRun {
		
		//Passed time from old checkpoint
		private long passedTime;
		
		//Time Info
		private long startTime;
		private int checkpoint;
		private long finishTime;
		
		//Other Stats
		private int fails;
		
		//Current Map Info
		private ParkourMap currentMap;
		
		public ParkourRun(ParkourMap currentMap) {
			this.currentMap = currentMap;
			reset();
			fails = 0;
		}
		
		public ParkourRun(ParkourMap currentMap, long passedTime, int currentCheckpoint) {
			this.currentMap = currentMap;	
			this.passedTime = passedTime;
			checkpoint = currentCheckpoint;
			startTime = -1;
			finishTime = -1;
			fails = 0;
		}
		
		/**
		 * Start racing
		 */
		public void start() {
			startTime = System.currentTimeMillis();
			if (passedTime != -1) {
				startTime -= passedTime;
				passedTime = -1;
			}
		}
		
		/**
		 * Check if the player started
		 * @return has started
		 */
		public boolean hasStarted() {
			return (startTime != -1);
		}
		
		/**
		 * Reset the player's time
		 */
		public void reset() {
			checkpoint = 0;
			startTime = -1;
			passedTime = -1;
			finishTime = -1;
		}
		
		/**
		 * The player finishes a run
		 * @return The time passed
		 */
		public long finish() {
			finishTime = System.currentTimeMillis() - startTime;
			parkour.getSQL().playerFinished(getName(), currentMap.getID(), finishTime);
			return finishTime;
		}
		
		/**
		 * Check if this run is finished
		 * @return finished
		 */
		public boolean isFinished() {
			return (finishTime > 0);
		}
		
		/**
		 * Get the player's current checkpoint
		 * @return The checkpoint
		 */
		public int getCurrentCheckpoint() {
			return checkpoint;
		}
		
		/**
		 * Get the time that has passed since the player started racing
		 * Or if the player finished return the total time spend on the run
		 * @return The time
		 */
		public long getPassedTime() {
			if (finishTime < 0) {
				return System.currentTimeMillis() - startTime;
			} else {
				return finishTime;
			}
		}
				
		/**
		 * Set the current checkpoint
		 * @param checkpoint
		 */
		public void setCheckpoint(int checkpoint) {
			this.checkpoint = checkpoint;
		}
				
		/**
		 * Get Current Map
		 * @return The map
		 */
		public ParkourMap getCurrentMap() {
			return currentMap;
		}
		
		/**
		 * Causes the player's fails to go up by 1
		 */
		public void incrementFails() {
			fails++;
		}
		
		/**
		 * Get the number of times this player failed this run
		 * @return fails
		 */
		public int getFails() {
			return fails;
		}
		
		/**
		 * Check if the player has a saved checkpoint stored
		 * @return stored
		 */
		public boolean hasSavedCheckpoint() {
			return (passedTime != -1);
		}
		
		/**
		 * Get the saved checkpoint time
		 * @return time still stored
		 */
		public long getSavedCheckpointTime() {
			return passedTime;
		}
		
	}
	
	public class SavedCheckpoint {
		private long passedTime;
		private int lastCheckpoint;
		
		public SavedCheckpoint(long passedTime, int lastCheckpoint) {
			this.passedTime = passedTime;
			this.lastCheckpoint = lastCheckpoint;
		}
		
		/**
		 * Get the last checkpoint the player passed
		 * @return The last checkpoint
		 */
		public int getLastCheckpoint() {
			return lastCheckpoint;
		}
		
		/**
		 * Get passed time
		 * @return the time
		 */
		public long getPassedTime() {
			return passedTime;
		}
	}
	

}
