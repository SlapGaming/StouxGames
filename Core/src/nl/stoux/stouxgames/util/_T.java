package nl.stoux.stouxgames.util;

import nl.stoux.stouxgames.StouxGames;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

/**
 * Utility Class that handles the tasks/scheduler
 * @author Stoux
 */
public class _T {

	private static StouxGames plugin;
	private static BukkitScheduler scheduler;
	
	/**
	 * Initialize the static field
	 * @param p The main plugin
	 */
	protected static void initialize(StouxGames p) {
		plugin = p;
		scheduler = p.getServer().getScheduler();
	}
	
	
	/**
	 * Get the bukkit scheduler
	 * @return The scheduler
	 */
	public static BukkitScheduler getScheduler() {
		return scheduler;
	}
	
	/*
	 * Sync methods
	 */
	
	/**
	 * Run a task
	 * @param run The runnable
	 * @return The new BukkitTask
	 */
	public static BukkitTask run_Sync(Runnable run) {
		return scheduler.runTask(plugin, run);
	}
	
	/**
	 * Run a task after a delay
	 * @param run The runnable
	 * @param delay The delay (ticks)
	 * @return The new BukkitTask
	 */
	public static BukkitTask runLater_Sync(Runnable run, int delay) {
		return scheduler.runTaskLater(plugin, run, delay);
	}
	
	/**
	 * Run a task on a timer
	 * @param run The runnable
	 * @param delay The delay (ticks)
	 * @param period The period (ticks)
	 * @return The new BukkitTask
	 */
	public static BukkitTask runTimer_Sync(Runnable run, int delay, int period) {
		return scheduler.runTaskTimer(plugin, run, delay, period);
	}
	
	/*
	 * A-Sync methods
	 */
	/**
	 * Run a task async
	 * @param run The runnable
	 * @return The new BukkitTask
	 */
	public static BukkitTask run_ASync(Runnable run) {
		return scheduler.runTaskAsynchronously(plugin, run);
	}
	
	/**
	 * Run a task after a delay async
	 * @param run The runnable
	 * @param delay The delay (ticks)
	 * @return The new BukkitTask
	 */
	public static BukkitTask runLater_ASync(Runnable run, int delay) {
		return scheduler.runTaskLaterAsynchronously(plugin, run, delay);
	}
	
	/**
	 * Run a task on a timer async
	 * @param run The runnable
	 * @param delay The delay (ticks)
	 * @param period The period (ticks)
	 * @return The new BukkitTask
	 */
	public static BukkitTask runTimer_ASync(Runnable run, int delay, int period) {
		return scheduler.runTaskTimer(plugin, run, delay, period);
	}

}
