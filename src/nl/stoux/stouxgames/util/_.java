package nl.stoux.stouxgames.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;

import nl.stoux.stouxgames.StouxGames;
import nl.stoux.stouxgames.games.GameController;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.player.PlayerController;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Utility class
 * @author Stoux
 */
public class _ {

	//The main plugin
	private static StouxGames plugin;
	
	//The games world
	private static World gamesWorld;
	
	//Controllers
	private static GameController gameController;
	private static PlayerController playerController;
	
	//WorldGuardPlugin
	private static WorldGuardPlugin worldguard;
	private static LocalWorld weWorld;
	
	//Randomizer
	private static Random random;
	
	/**
	 * Setup the Utility
	 * @param world the GamesWorld
	 * @param stouxGames Main JavaPlugin
	 * @param worldGuardPlugin The WorldGuard plugin
	 * @param pController The player controller
	 */
	public static void setupUtil(StouxGames stouxGames, World world, WorldGuardPlugin worldGuardPlugin, GameController gController, PlayerController pController) {
		plugin = stouxGames;
		gamesWorld = world;
		worldguard = worldGuardPlugin;
		gameController = gController;
		playerController = pController;
		weWorld = new BukkitWorld(gamesWorld);
		random = new Random();
		_T.initialize(stouxGames); //Initialize the other Static Util class
	}
	
	/**
	 * Log to the console
	 * @param level Severity level
	 * @param message The message
	 */
	public static void log(Level level, String message) {
		plugin.getLogger().log(level, message);
	}
	
	/**
	 * Log to the console
	 * @param level Severity level
	 * @param gm The gamemode
	 * @param message The message
	 */
	public static void log(Level level, GameMode gm, String message) {
		plugin.getLogger().log(level, "[" + gm + "] " + message);
	}
	
	/**
	 * Broadcast a message to the server
	 * @param gm The game inside the tag
	 * @param message The message
	 */
	public static void broadcast(GameMode gm, String message) {
		plugin.getServer().broadcastMessage(ChatColor.GOLD + "[" + gm + "] " + ChatColor.WHITE + message);
	}
	
	/*
	 ************************
	 *   Player Utilities   *
	 ************************
	 */
	/**
	 * Remove all the current potion effects from a player
	 * @param p The player
	 */
	public static void removeAllPotions(Player p) {
		HashSet<PotionEffect> effects = new HashSet<>(p.getActivePotionEffects());
		for (PotionEffect effect : effects) {
			p.removePotionEffect(effect.getType());
		}
	}
	
	/**
	 * Give a player a potion for an infinite amount of time with strength 1
	 * @param p The player
	 * @param type The type of potion
	 */
	public static void giveInfinitePotion(Player p, PotionEffectType type) {
		giveInfinitePotion(p, type, 1);
	}
	
	/**
	 * Give a player a potioneffect for an infinte amount of time
	 * @param p The player
	 * @param type The type of potion
	 * @param strength The strength of the potion
	 */
	public static void giveInfinitePotion(Player p, PotionEffectType type, int strength) {
		p.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, strength));
	}
	
	/**
	 * See if the player has a certain permission. Format: stouxgames.[permission]
	 * @param sender The sender
	 * @param permission The [Permission] String
	 * @return has permission
	 */
	public static boolean testPermission(CommandSender sender, String permission) {
		return sender.hasPermission("stouxgames." + permission);
	}
	
	/**
	 * Send a 'bad' message to someone
	 * @param sender The sender
	 * @param message The message
	 */
	public static void badMsg(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.RED + message);
	}
	
	/**
	 * Send a no permission message to the CommandSender
	 * @param sender The command sender
	 */
	public static void noPermission(CommandSender sender) {
		_.badMsg(sender, "You don't have permission to do this.");
	}
	
	/**
	 * Send a message to the CommandSender
	 * @param sender The command sender
	 * @param gm The gamemode of the game the sender is in
	 * @param message the message
	 */
	public static void msg(CommandSender sender, GameMode gm, String message) {
		sender.sendMessage(ChatColor.GOLD + "[" + gm + "] " + ChatColor.WHITE + message);
	}
	
	/**
	 * Check if a sender is a player
	 * @param sender The sender
	 * @return is a player
	 */
	public static boolean isAPlayer(CommandSender sender) {
		return (sender instanceof Player);
	}
	
	/**
	 * Check if the X/Y/Z differ
	 * @param from
	 * @param to
	 * @return Has moved
	 */
	public static boolean hasMoved(Location from, Location to) {
		if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
			return true;
		} else {
			return true;
		}
	}
	
	
	/*
	 ******************
	 *     Getters    *
	 ******************
	 */
	
	/**
	 * Get the games world
	 * @return the world
	 */
	public static World getWorld() {
		return gamesWorld;
	}
	
	/**
	 * Get the plugin
	 * @return the main JavaPlugin
	 */
	public static StouxGames getPlugin() {
		return plugin;
	}
	
	/**
	 * Get the WorldGuardPlugin
	 * @return
	 */
	public static WorldGuardPlugin getWorldGuard() {
		return worldguard;
	}
	
	/**
	 * Get the game controller
	 * @return The controller
	 */
	public static GameController getGameController() {
		return gameController;
	}
	
	/**
	 * Get the player controller
	 * @return The controller
	 */
	public static PlayerController getPlayerController() {
		return playerController;
	}
	
	/**
	 * Get a random integer
	 * @param max The max
	 * @return a random int
	 */
	public static int getRandomInt(int max) {
		return random.nextInt(max);
	}
	
	
	/*
	 * 
	 * WorldGuard
	 * 
	 */
	/**
	 * Get all blocks from a WorldGuard region
	 * @param regionname The regionname
	 * @return 
	 */
	public static ArrayList<Block> getBlocksInRegion(String regionname) {
		ArrayList<Block> blocks = new ArrayList<>();
		ProtectedRegion region = worldguard.getRegionManager(gamesWorld).getRegion(regionname); //Check for region
		if (region == null) return null; //Cancel if region doesn't exist
		if (region instanceof ProtectedPolygonalRegion) { //If a Polygon
			ProtectedPolygonalRegion poly = (ProtectedPolygonalRegion) region; //Cast to PolyRegion
			Polygonal2DRegion wePoly = new Polygonal2DRegion(weWorld, poly.getPoints(), poly.getMinimumPoint().getBlockY(), poly.getMaximumPoint().getBlockY()); //Create WorldEdit Poly Region
			for (BlockVector vector : wePoly) {
				blocks.add(getBlock(vector)); //Fill blocks
			}
		} else { //Else: Cuboid
			ProtectedCuboidRegion cuboid = (ProtectedCuboidRegion) region; //Cast to CuboidRegion
			CuboidRegion weCuboid = new CuboidRegion(weWorld, cuboid.getMinimumPoint(), cuboid.getMaximumPoint()); //Make WorldEdit Cuboid region 
			for (BlockVector vector : weCuboid) {
				blocks.add(getBlock(vector)); //Fill blocks
			}
		}
		return blocks;
	}
	
	/**
	 * Get the block from a vector
	 * @param vector The vector
	 * @return The block
	 */
	private static Block getBlock(BlockVector vector) {
		return gamesWorld.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}
	
}
