package nl.stoux.stouxgames;

import java.util.logging.Level;

import nl.stoux.stouxgames.commands.CommandHandler;
import nl.stoux.stouxgames.external.TabControl;
import nl.stoux.stouxgames.games.GameController;
import nl.stoux.stouxgames.games.sonic.Sonic;
import nl.stoux.stouxgames.games.spleef.Spleef;
import nl.stoux.stouxgames.games.tntrun.TNTRun;
import nl.stoux.stouxgames.listener.EventListener;
import nl.stoux.stouxgames.player.PlayerController;
import nl.stoux.stouxgames.pressurepads.PressurePadHandler;
import nl.stoux.stouxgames.pressurepads.PressurePadListener;
import nl.stoux.stouxgames.util._;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 * Main StouxGames plugin class
 * @author Stoux
 */
public class StouxGames extends JavaPlugin {
	
	//External
	//WorldGuard
	private WorldGuardPlugin worldguard;
	//Essentials
	private Essentials essentials;
	//TabAPI & TagAPI
	//Stuff
	
	//The game world
	private World world;
	
	//Controllers
	private PlayerController playerController;
	private GameController gameController;
	private PressurePadHandler pressurePadHandler;
		
	
	/*
	 **************
	 *  Overrides *
	 **************
	 */
	@Override
	public void onEnable() {
		//Get the plugin manager;
		PluginManager pm = getServer().getPluginManager();
		
		//Get worldguard | If not found -> Disable
		if (!getWorldGuard()) {
			getLogger().log(Level.SEVERE, "WorldGuard has not been found. Shutting down plugin.");
			pm.disablePlugin(this);
			return;
		}
		
		//Get the game world | If not found -> Disable
		world = getServer().getWorld("world_sonic");
		if (world == null) {
			getLogger().log(Level.SEVERE, "Game world has not been found. Shutting down plugin.");
			pm.disablePlugin(this);
			return;
		}
		
		//Create the game folder
		getDataFolder().mkdirs();
		
		//Create the controllers
		setupControllers();
		
		//Create the util classes
		initializeUtil();
		
		//Initialize Externals
		new TabControl();
		Plugin foundEss = pm.getPlugin("Essentials"); //Find essentials
		if (foundEss != null && foundEss instanceof Essentials) {
			essentials = (Essentials) foundEss;
		}
		
		//Initialize the games
		initializeGames();
		
		//Create PressurePlate handler & Register it's listener
		pressurePadHandler = new PressurePadHandler(gameController);
		pm.registerEvents(new PressurePadListener(pressurePadHandler), this);
		
		//Register events
		pm.registerEvents(new EventListener(playerController), this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		return CommandHandler.handle(sender, cmd, args);
	}
	
	/**
	 * Create the controllers
	 */
	private void setupControllers() {
		playerController = new PlayerController();
		gameController = new GameController();
	}
	
	/**
	 * Initialize the static UTIL classes
	 */
	private void initializeUtil() {
		_.setupUtil(this, world, worldguard, gameController, playerController);
	}
	
	/**
	 * Initialize the games
	 */
	private void initializeGames() {
		gameController.addGame(new Spleef());
		gameController.addGame(new TNTRun());
		gameController.addGame(new Sonic());
	}
	
	/**
	 * Find the WorldGuard plugin
	 * @return found
	 */
	private boolean getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			getLogger().severe("Failed to get WorldGuard. Shutting Down.");
			getServer().getPluginManager().disablePlugin(this);
			return false;
		}
		
		worldguard = (WorldGuardPlugin) plugin;
		return true;
	}
	
	/**
	 * Get the WorldGuard plugin
	 * @return {@link WorldGuardPlugin}
	 */
	public WorldGuardPlugin getWorldguard() {
		return worldguard;
	}
	
	/**
	 * Get the essentials plugin
	 * @return the plugin
	 */
	public Essentials getEssentials() {
		return essentials;
	}

}
