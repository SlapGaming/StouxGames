package nl.stoux.stouxgames.player;

import nl.stoux.stouxgames.external.TabControl;
import nl.stoux.stouxgames.games.AbstractGame;
import nl.stoux.stouxgames.util._;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mcsg.double0negative.tabapi.TabAPI;

/**
 * Game player class.
 * @author Stoux
 */
public class GamePlayer {

	//The player
	protected Player p;
	protected String name;
	protected String tag;
	
	//Game
	protected AbstractGame game;
	
	//State
	protected PlayerState state;

	
	public GamePlayer(Player player, AbstractGame game) {
		this.p = player;
		this.game = game;
		name = p.getName();
		state = PlayerState.joining;
	}
	
	/**
	 * Get the {@link Player}
	 * @return
	 */
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * Reset the player's stats, inventory, xp, potions etc
	 * Teleport to GamesWorld spawn location
	 */
	public void resetPlayer() {
		if (p.isDead()) return;
		//Wipe XP
		p.setExp(0);
		p.setLevel(0);
		
		//Health etc
		p.setHealth(20);
		p.setFoodLevel(20);
		
		//Wipe all potions
		_.removeAllPotions(p);
		
		//Wipe the inventory
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
				
		//Remove Vehicles
		p.eject();
		if (p.getPassenger() != null) {
			p.getPassenger().eject();
		}
		
		//Set player stats
		p.setGameMode(GameMode.SURVIVAL);
		p.setFlying(false);
	}
	
	/**
	 * Player quit a game
	 */
	public void playerQuitGame() {
		resetPlayer();
		
		//Clear the player's tab
		if (TabControl.isEnabled()) {
			TabAPI.clearTab(p);
			TabAPI.updatePlayer(p);
		}
		
		//Teleport player
		p.teleport(_.getWorld().getSpawnLocation());
	}	
	
	/*
	 * 
	 * Player Info
	 * 
	 */
	
	/**
	 * Get the players tag
	 * @return the tag or null
	 */
	public String getTag() {
		return tag;
	}
	
	/**
	 * Set the players tag
	 * @param tag the new tag
	 */
	public void setTag(String newTag) {
		this.tag = newTag;
	}
	
	/**
	 * Get the players state
	 * @return state
	 */
	public PlayerState getState() {
		return state;
	}
	
	/**
	 * Set a player's state
	 * @param newState the new state
	 */
	public void setState(PlayerState newState) {
		this.state = newState;
	}
	
	/**
	 * Get the game this player is playing
	 * @return The game
	 */
	public AbstractGame getGame() {
		return game;
	}
	
	/**
	 * Gets the player's name
	 * @return
	 */
	public String getName() {
		return name;
	}

}
