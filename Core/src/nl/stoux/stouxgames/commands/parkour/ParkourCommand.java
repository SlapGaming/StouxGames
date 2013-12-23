package nl.stoux.stouxgames.commands.parkour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.commands.exception.Message;
import nl.stoux.stouxgames.commands.exception.UsageException;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.parkour.Parkour;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParkourCommand extends AbstractCommand {

	public ParkourCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean handle() throws CommandException {
		Parkour parkour = (Parkour) _.getGameController().getGame(GameMode.Parkour);
		isGameRunning(parkour);
		
		if (args.length == 0) { //Send help command
			throw new UsageException("/parkour help");
		}
		
		switch (args[0]) {
		case "maps": case "map": //Get all maps that have leaderboards
			checkPermission("parkour.maps");
			ArrayList<Integer> ids = new ArrayList<>(parkour.getLeaderboardMaps().keySet());
			Collections.sort(ids);
			for (int id : ids) {
				parkour.sendMapInfo(sender, id);
			}
			break;
			
		case "leaderboard": case "lb": //Get the leaderboard
			checkPermission("parkour.leaderboard");
			if (args.length == 1) { //No extra arguments given -> Check if player
				if (sender instanceof Player) {
					GamePlayer gp = _.getPlayerController().getGamePlayer(sender.getName());
					if (gp != null) {
						if (gp.getGame() == parkour) {
							ParkourPlayer pp = (ParkourPlayer) gp;
							if (pp.isOnParkourMap()) {
								parkour.getSQL().sendLeaderboard(sender, pp.getCurrentMap().getID()); //Is ParkourPlayer on map. Send leaderboard of that map.
								return true;
							}
						}
					}
				}
				throw new UsageException("/parkour leaderboard <Map ID | Map Name> " + ChatColor.GRAY + "(no ID needed if on a parkourmap)");
			} else {
				int mapID = parseMapID(parkour, 1);
				parkour.getSQL().sendLeaderboard(sender, mapID);
			}
			break;
			
		case "time":
			checkPermission("parkour.time");
			if (args.length < 3) { //Check if correct usage
				throw new UsageException("/parkour time <Player> <Map ID | Map Name>");
			}
			
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
			if (!p.hasPlayedBefore()) { //Check if player exists
				throw new CommandException("This player has never been on the server before!");
			}
			
			int mapID = parseMapID(parkour, 2);
			parkour.getSQL().sendPlayerTime(sender, p.getName(), mapID);			
			break;
			
		case "fails":
			_.badMsg(sender, "Sorry, this command is not supported yet!");
			//checkPermission("parkour.fails");			
			break;
		
		case "help": //Send help info
			checkPermission("parkour.help"); //Check for permission
			int page = 1;
			if (args.length > 1) {
				try {
					page = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					throw new CommandException("This is not a valid number: " + args[1]);
				}
			}
			
			String[] helpMessages;
			if (page <= 1) { //Page 1
				helpMessages = new String[]{ //Create messages
						ChatColor.YELLOW + "---" + ChatColor.GREEN + " Parkour Help " + ChatColor.YELLOW + "---",
						ChatColor.YELLOW + "/checkpoint " + ChatColor.WHITE + ": Warp to the last checkpoint of the map you're on.",
						ChatColor.YELLOW + "/leavemap " + ChatColor.WHITE + ": Leave the map you're currently on.",
						ChatColor.YELLOW + "/mapinfo " + ChatColor.WHITE + ": Get information about the map you're currently on.",
						ChatColor.YELLOW + "/resetrun " + ChatColor.WHITE + ": Reset your current run (will reset your current time & teleport back to start).",
						ChatColor.YELLOW + "---" + ChatColor.GREEN + " Page 1 of 2 " + ChatColor.YELLOW + "---"
				};	
			} else { //Page 2
				helpMessages = new String[]{ //Create messages
						ChatColor.YELLOW + "---" + ChatColor.GREEN + " Parkour Help " + ChatColor.YELLOW + "---",					
						ChatColor.YELLOW + "/parkour maps " + ChatColor.WHITE + ": Get a list of all the maps with leaderboards.",
						ChatColor.YELLOW + "/parkour leaderboard <Map ID | Map Name> " + ChatColor.WHITE + ": Get the leaderboard of a certain map.",
						ChatColor.YELLOW + "/parkour time <player> <Map ID | Map Name> " + ChatColor.WHITE + ": Get your best time on a map.",
						ChatColor.YELLOW + "/parkour fails [Map ID | Map Name] " + ChatColor.WHITE + ": Get your total amount of fails (on a map).",
						ChatColor.YELLOW + "---" + ChatColor.GREEN + " Page 2 of 2 " + ChatColor.YELLOW + "---"
				};
			}
			
			for (String msg : helpMessages) {
				_.msg(sender, GameMode.Sonic, msg); //Send messages
			}
			break;
		
		default:
			throw new UsageException("/parkour help");
		}
		
		return true;
	}
	
	/**
	 * Do all the standard Parkour checks for this player
	 * This includes:
	 * - standardChecks(permission) | Standard player checks
	 * - checkCorrectGame
	 * - onParkourMap() (if specified)
	 * 
	 * @param permission The permission that needs to be checked
	 * @param onMapCheck Check if the player is on a parkour map
	 * @return The parkour player instance of this player
	 * @throws CommandException if any of the checks fail
	 */
	protected ParkourPlayer parkourCheck(String permission, boolean onMapCheck) throws CommandException {
		GamePlayer gp = standardChecks("parkour." + permission); //Standard Player checks
		checkCorrectGame(gp, GameMode.Parkour); //Game check
		ParkourPlayer pp = (ParkourPlayer) gp; //Cast to ParkourPlayer
		if (onMapCheck) { //If Map Check
			if (!pp.isOnParkourMap()) throw new CommandException(Message.notOnParkourMap); //Check if on a map
		}
		return pp;
	}
	
	/**
	 * Get the ID of a map given by the commandsender based on the arguments.
	 * @param p The Parkour Game
	 * @param firstArg Number of the first arg that should be counted as Map ID
	 * @return The ID of the map
	 * @throws CommandException if no Map ID is found
	 */
	private int parseMapID(Parkour p, int firstArg) throws CommandException {		
		HashMap<Integer, String[]> leaderboardMaps = p.getLeaderboardMaps(); //Get maps
		
		try {
			if (args.length > firstArg + 1) throw new NumberFormatException(); //More than 1 ID argument. Cannot be single int ID.
			
			if (args[firstArg].substring(0, 1).equals("#")) { //Check if begins with HashTag (Number)
				args[firstArg] = args[firstArg].substring(1);
			}
			
			int mapID = Integer.parseInt(args[firstArg]); //Assuming int
			
			if (leaderboardMaps.containsKey(mapID))	{ //Check if valid map ID
				return mapID;
			} else {
				throw new CommandException(Message.invalidMapID);
			}
		} catch (NumberFormatException e) { //not a valid int. Going to assume a map name has been entered
			String name = args[firstArg].toLowerCase();
			if (args.length > firstArg + 1) { //Multiple arg name
				for (int x = firstArg + 1; x < args.length; x++) { //Loop thru rest to add to string
					name += " " + args[x].toLowerCase();
				}
			}
			
			for (Entry<Integer, String[]> entry : leaderboardMaps.entrySet()) { //Loop thru maps
				if (entry.getValue()[0].toLowerCase().equals(name)) { //Try to find map
					return entry.getKey();
				}
			}
			
			throw new CommandException(Message.invalidMapID); //No map found with ID
		}
	}
	
	
}
