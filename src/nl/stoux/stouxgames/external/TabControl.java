package nl.stoux.stouxgames.external;

import java.util.Collection;

import nl.stoux.stouxgames.StouxGames;
import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.mcsg.double0negative.tabapi.TabAPI;

public class TabControl {

	private static boolean enabled;
	private static StouxGames plugin;
	
	/**
	 * Constructor
	 */
	public TabControl() {
		Plugin plugin = _.getPlugin().getServer().getPluginManager().getPlugin("TabAPI");
		if (plugin == null || !(plugin instanceof TabAPI)) {
			enabled = false;
		} else {
			enabled = true;
			plugin = _.getPlugin();
		}
	}
	
	/**
	 * Set the first 5 rows of the Tab
	 * @param p
	 * @param gm
	 */
	public static void setTopTab(GamePlayer gP, AbstractGame game, String players, int specators) {
		if (!enabled) return;
		Player p = gP.getPlayer();
		
		//Clear old stuff
		TabAPI.clearTab(p);
		TabAPI.setPriority(plugin, p, 2);
		
		//Default
		TabAPI.setTabString(plugin, p, 0, 1, ChatColor.GOLD + "- StouxGames -");
		TabAPI.setTabString(plugin, p, 1, 0, ChatColor.YELLOW + " -------------");
		TabAPI.setTabString(plugin, p, 1, 1, ChatColor.YELLOW + "--------------");
		TabAPI.setTabString(plugin, p, 1, 2, ChatColor.YELLOW + "-------------");
		
		//Gamemode
		TabAPI.setTabString(plugin, p, 0, 0, game.getGamemode().getTab());
		TabAPI.setTabString(plugin, p, 0, 2, game.getGamemode().getTab() + " ");
		
		//Players
		TabAPI.setTabString(plugin, p, 2, 0, ChatColor.GRAY + "Status   ->");
		TabAPI.setTabString(plugin, p, 2, 1, game.getGameState().toString());
		TabAPI.setTabString(plugin, p, 2, 2, ChatColor.GRAY + gP.getState().toString());
		TabAPI.setTabString(plugin, p, 3, 0, ChatColor.GRAY + "Players ->");
		TabAPI.setTabString(plugin, p, 3, 1, players);
		TabAPI.setTabString(plugin, p, 3, 2, ChatColor.GRAY + "" + specators + " Spectators");
	}
	
	/**
	 * Update the tab for all players in the collection
	 * @param players
	 */
	public static void updatePlayers(Collection<GamePlayer> players) {
		if (!enabled) return;
		for (GamePlayer gP : players) {
			TabAPI.updatePlayer(gP.getPlayer());
		}
	}
	
	
	/**
	 * Check if TabAPI is enabled
	 * @return enabled
	 */
	public static boolean isEnabled() {
		return false;
	}
	
}
