package nl.stoux.stouxgames.pressurepads;

import java.util.HashMap;
import java.util.logging.Level;

import nl.stoux.stouxgames.games.GameController;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.storage.YamlStorage;
import nl.stoux.stouxgames.util._;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PressurePadHandler {

	//Saved stuff
	private YamlStorage yaml;
	private FileConfiguration config;
	
	//Plate locations
	private HashMap<Location, PressurePadType> pads;
	private HashMap<PressurePadType, Vector> spartas;
	
	//Games
	private GameController gameController;
	
	public PressurePadHandler(GameController games) {
		this.gameController = games;
		yaml = new YamlStorage("Pads");
		config = yaml.getConfig();
		pads = new HashMap<>();
		spartas = new HashMap<>();
		checkPads();
	}
	
	/**
	 * Reset the class (reload the YML, check for pads)
	 */
	public void reset() {
		yaml = new YamlStorage("Pads");
		config = yaml.getConfig();
		pads = new HashMap<>();
		spartas = new HashMap<>();
		checkPads();
	}
	
	/**
	 * Handle a pressed tab event in the games world
	 * @param p The player
	 * @param loc The pad location
	 */
	public void handlePadPressed(Player p, Location loc) {
		if (!pads.containsKey(loc)) { //Check if the pad is known
			return;
		}
		if (_.getPlayerController().isGamePlayer(p.getName())) {
			_.badMsg(p, "You are already in a game, huh? Return to your game.");
			return;
		}
		PressurePadType type = pads.get(loc);
		switch (type) {
		case CDJoin:
			gameController.getGame(GameMode.CD).playerJoins(p);
			break;
		case CDSpectate:
			gameController.getGame(GameMode.CD).playerJoinsSpectate(p);
			break;
		case ParkourJoin:
			notSupported(p);
			break;
		case SonicJoin:
			gameController.getGame(GameMode.Sonic).playerJoins(p);
			break;
		case SpleefJoin:
			gameController.getGame(GameMode.Spleef).playerJoins(p);
			break;
		case SpleefSpectate:
			gameController.getGame(GameMode.Spleef).playerJoinsSpectate(p);
			break;
		case TNTRunJoin:
			gameController.getGame(GameMode.TNTRun).playerJoins(p);
			break;
		case TNTRunSpectate:
			gameController.getGame(GameMode.TNTRun).playerJoinsSpectate(p);
			break;
		case HungerGamesJoin:
			notSupported(p);
			break;
		case HungerGamesSpectate:
			notSupported(p);
			break;
	    
		//Sparta pads
		case SpartaCD: case SpartaHungerGames: case SpartaParkour: case SpartaSonic: case SpartaSpleef: case SpartaTNTRun:
			Vector v = spartas.get(type);
			if (v != null) {
				p.setVelocity(v);
			}
			break;
		default:
			break;
		}
	}
	
	private void notSupported(Player p) {
		_.badMsg(p, "This pad is not supported yet.");
	}
	
	/**
	 * Check all the pads
	 */
	private void checkPads() {
		//Loop all values
		for (PressurePadType type : PressurePadType.values()) {
			if (type.isSparta()) {
				getSpartaLocation(type);
			} else {
				getPlateLocation(type);
			}
		}
	}
		
	/**
	 * Get the location of a plate
	 * @param pad
	 * @return The location
	 */
	private void getPlateLocation(PressurePadType type) {
		String pad = type.toString().toLowerCase();
		if (!config.contains(pad)) return; //Check if in config
		
		findPressurePlate(pad, type); //Get the plate
	}
	
	/**
	 * Find the pressure plate
	 * @param pad String to config
	 * @param type type of pad
	 * @return block location or null
	 */
	private Location findPressurePlate(String pad, PressurePadType type) {
		//Get the coords
		int x = config.getInt(pad + ".x");
		int y = config.getInt(pad + ".y");
		int z = config.getInt(pad + ".z");
		
		//Get the block
		Block b = _.getWorld().getBlockAt(new Location(_.getWorld(), x, y, z));
		
		if (b.getType() == Material.STONE_PLATE || b.getType() == Material.WOOD_PLATE) { //Check if a pressure plate
			pads.put(b.getLocation(), type); //Put the location in the map
			return b.getLocation();
		} else {
			_.log(Level.WARNING, "A pressure pad has not been found: " + type.toString()); //Failed to find pressureplate
			return null;
		}
	}
	
	private void getSpartaLocation(PressurePadType type) {
		String pad = type.toString().toLowerCase();
		if (!config.contains(pad + ".pad") || !config.contains(pad + ".spartato")) return; //Check if in config
		
		Location from = findPressurePlate(pad + ".pad", type);
		
		if (from == null) return;
		
		//Get the coords
		int x = config.getInt(pad + ".spartato.x");
		int y = config.getInt(pad + ".spartato.y");
		int z = config.getInt(pad + ".spartato.z");
		if (x == 0 && y == 0 && z == 0) return;
		
		
		//Get the block
		Block b = _.getWorld().getBlockAt(new Location(_.getWorld(), x, y, z));
		Location to = b.getLocation();
		
		//Calculate vector
		double dX = from.getX() - to.getBlockX();
		double dY = from.getY() - to.getY();
		double dZ = from.getZ() - to.getZ();
		
		double yaw = Math.atan2(dZ, dX);
		double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
		
		double vX = Math.sin(pitch) * Math.cos(yaw);
		double vY = Math.sin(pitch) * Math.sin(yaw);
		double vZ = Math.cos(pitch);
		
		Vector vector = new Vector(vX, vZ, vY);
		
		int multiplier = config.getInt(pad + ".spartato.multi");
		
		spartas.put(type, vector.multiply(multiplier));
	}
	
	/**
	 * All types of pressure pads
	 * @author Leon
	 */
	public enum PressurePadType {
		//Sparta's
		SpartaSpleef(true),
		SpartaSonic(true),
		SpartaCD(true),
		SpartaParkour(true),
		SpartaTNTRun(true),
		SpartaHungerGames(true),
		
		
		//Arena's
		SpleefJoin,
		SpleefSpectate,
		
		SonicJoin,
		
		CDJoin,
		CDSpectate,
		
		ParkourJoin,
		
		TNTRunJoin,
		TNTRunSpectate,
		
		HungerGamesJoin,
		HungerGamesSpectate;
		
		private boolean sparta;
		private PressurePadType() {
			sparta = false;
		}
		private PressurePadType(boolean sparta) {
			this.sparta = sparta;
		}
		
		public boolean isSparta() {
			return sparta;
		}
		
		
	}
	
	

}
