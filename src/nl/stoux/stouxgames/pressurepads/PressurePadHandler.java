package nl.stoux.stouxgames.pressurepads;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import nl.stoux.stouxgames.games.GameController;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.storage.YamlStorage;
import nl.stoux.stouxgames.util._;

public class PressurePadHandler {

	//Saved stuff
	private YamlStorage yaml;
	private FileConfiguration config;
	
	//Plate locations
	private HashMap<Location, PressurePadType> pads;
	
	//Games
	private GameController gameController;
	
	public PressurePadHandler(GameController games) {
		this.gameController = games;
		yaml = new YamlStorage("Pads");
		config = yaml.getConfig();
		pads = new HashMap<>();
		checkPads();
	}
	
	/**
	 * Reset the class (reload the YML, check for pads)
	 */
	public void reset() {
		yaml.reloadConfig();
		pads = new HashMap<>();
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
		switch (pads.get(loc)) {
		case CDJoin:
			notSupported(p);
			break;
		case CDSpectate:
			notSupported(p);
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
		case VirusJoin:
			notSupported(p);
			break;
		case VirusSpectate:
			notSupported(p);
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
			getPlateLocation(type.toString().toLowerCase(), type);
		}
	}
	
	/**
	 * Get the location of a plate
	 * @param pad
	 * @return The location
	 */
	private void getPlateLocation(String pad, PressurePadType type) {
		//Get the coords
		int x = config.getInt(pad + ".x");
		int y = config.getInt(pad + ".y");
		int z = config.getInt(pad + ".z");
		
		//Get the block
		Block b = _.getWorld().getBlockAt(new Location(_.getWorld(), x, y, z));
		
		if (b.getType() == Material.STONE_PLATE || b.getType() == Material.WOOD_PLATE) { //Check if a pressure plate
			pads.put(b.getLocation(), type); //Put the location in the map
		} else {
			_.log(Level.WARNING, "A pressure pad has not been found: " + type.toString()); //Failed to find pressureplate
		}
	}
	
	/**
	 * All types of pressure pads
	 * @author Leon
	 */
	public enum PressurePadType {
		SpleefJoin,
		SpleefSpectate,
		SonicJoin,
		CDJoin,
		CDSpectate,
		ParkourJoin,
		TNTRunJoin,
		TNTRunSpectate,
		VirusJoin,
		VirusSpectate;
	}
	
	

}
