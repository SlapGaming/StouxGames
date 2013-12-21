package nl.stoux.stouxgames.games.parkour;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.commands.exception.Message;
import nl.stoux.stouxgames.external.SQLClass;
import nl.stoux.stouxgames.games.GameController;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.parkour.ParkourPlayer.SavedCheckpoint;
import nl.stoux.stouxgames.util._;
import nl.stoux.stouxgames.util._T;

public class ParkourSQL extends SQLClass {
	
	private Parkour parkour;
	
	public ParkourSQL(Parkour parkour) {
		this.parkour = parkour;
		doingCommand = new HashSet<String>();
	}
	
	/**
	 * Connect with SQL
	 * @return connected
	 */
	public boolean connect() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mcecon","mecon", "B9eCusTa"); //Connect
			//TODO Create Tables
			Statement tableStatement = con.createStatement(); //Create new statement to create the table
			tableStatement.executeUpdate(
					"CREATE TABLE IF NOT EXISTS `parkour_maps` ( " +
					"`id` int(11) NOT NULL AUTO_INCREMENT, " +
					"`name` varchar(255) NOT NULL, " +
					"`author` varchar(255) NOT NULL, " +
					"`filename` varchar(255) NOT NULL, " +
					"PRIMARY KEY (`id`), " +
					"UNIQUE KEY `filename` (`filename`) ) " +
					"ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;"
				);
			tableStatement.executeUpdate(
					"CREATE TABLE IF NOT EXISTS `parkour_progress` ( " +
					"`player` varchar(255) NOT NULL, " +
					"`map_id` int(11) NOT NULL, " +
					"`checkpoint` int(11) NOT NULL, " +
					"`passed_time` bigint(20) NOT NULL, " +
					"PRIMARY KEY (`player`,`map_id`) ) " +
					"ENGINE=InnoDB DEFAULT CHARSET=latin1;"
				);
			tableStatement.executeUpdate(
					"CREATE TABLE IF NOT EXISTS `parkour_times` ( " +
					"`player` varchar(255) NOT NULL, " +
					"`map_id` int(11) NOT NULL, " +
					"`finish_datetime` datetime NOT NULL, " +
					"`finish_time` bigint(20) NOT NULL, " +
					"PRIMARY KEY (`player`,`map_id`,`finish_datetime`) ) " +
					"ENGINE=InnoDB DEFAULT CHARSET=latin1;"
				);
			tableStatement.executeUpdate(
					"CREATE TABLE IF NOT EXISTS `parkour_fails` ( " +
					"`player` varchar(25) NOT NULL, " +
					"`map_id` int(11) NOT NULL, " +
					"`fails` int(11) NOT NULL, " +
					"PRIMARY KEY (`player`,`map_id`) ) " +
					"ENGINE=InnoDB DEFAULT CHARSET=latin1;"
				);
			keepAlive = true;
			isAlive = true;
			return true;
		} catch (SQLException e) {
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to connect to SQL. Exception: " + e.getMessage());
			keepAlive = false;
			isAlive = false;
			return false;
		}
	}
	
	/**
	 * Try to disconnect
	 */
	public void disconnect() {
		try {
		if (con != null) {
			if (!con.isClosed()) {
				con.close();
			}
		}
		} catch (SQLException e) {
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to disconnect? I don't even.");
		}
	}
	
	/**
	 * Check the current connection
	 * If not connected try to connect (This doesn't happen A-Sync unless called in A-Sync thread).
	 * @return Connected
	 */
	public boolean checkConnection() {
		try {
			if (con.isClosed() || !isAlive) return connect();
			else return true;
		} catch (SQLException e) {
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to check connection. Exception: " + e.getMessage());
			return false;
		}
	}
		
	
	/*
	 **********************
	 * Checkpoint related *
	 **********************
	 */
	
	/**
	 * Load all the saved checkpoints for this player
	 * @param player The player
	 */
	public void getSavedCheckpoints(final ParkourPlayer player) {
		_T.run_ASync(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (!checkConnection()) throw new SQLException("SQL not connected.");
					PreparedStatement prepStat = con.prepareStatement("SELECT `map_id`, `passed_time`, `checkpoint` FROM `parkour_progress` WHERE `player` = ? ;");
					prepStat.setString(1, player.getName());
					ResultSet rs = prepStat.executeQuery();
					while (rs.next()) {
						player.addSavedCheckpoint(rs.getInt(1), rs.getLong(2), rs.getInt(3));
					}
					prepStat = con.prepareStatement("DELETE FROM `parkour_progress` WHERE `player` = ? ;");
					prepStat.setString(1, player.getName());
					prepStat.executeUpdate();
				} catch (SQLException e) {
					_.log(Level.SEVERE, GameMode.Parkour, "Something went wrong getting Saved Checkpoints. Exception: "+ e.getMessage());
				}
			}
		});
	}
	
	/**
	 * Remove the player's saved checkpoint for a specific map
	 * @param player The player
	 * @param mapID The ID of the map
	 */
	public void deleteSavedCheckpoint(final String player, final int mapID) {
		_T.run_ASync(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (!checkConnection()) throw new SQLException("SQL not connected.");
					PreparedStatement prepStat = con.prepareStatement("DELETE FROM `mcecon`.`parkour_progress` WHERE `parkour_progress`.`player` = ? AND `parkour_progress`.`map_id` = ? ;");
					prepStat.setString(1, player);
					prepStat.setInt(2, mapID);
					prepStat.executeUpdate();
				} catch (SQLException e) {
					_.log(Level.SEVERE, GameMode.Parkour, "Failed to remove checkpoint of player " + player + ", map: " + mapID + ". SQLException: " + e.getMessage());
				}
			}
		});
	}
	
	/**
	 * Save a player's checkpoints
	 * @param player The player
	 */
	public void saveSavedCheckpoints(final ParkourPlayer player) {
		if (_.getPlugin().isEnabled()) {
			_T.run_ASync(new Runnable() {
				@Override
				public void run() {
					saveCheckpoints(player);
				}
			});
		} else {
			saveCheckpoints(player);
		}
	}
	
	/**
	 * Save checkpoints
	 */
	private void saveCheckpoints(ParkourPlayer player) {
		String playername = player.getName();
		HashMap<Integer, SavedCheckpoint> savedCheckpoints = player.getSavedCheckpoints();
		if (savedCheckpoints.size() < 1) return;
		String single = "(?, ?, ?, ?)";
		String values = "";
		for (int x = 0; x < savedCheckpoints.size(); x++) { //Create the Values string
			if (values.equals("")) {
				values = single;
			} else {
				values += "," + single;
			}
		}
		try {
			if (!checkConnection()) throw new SQLException("SQL not connected.");
			PreparedStatement prepStat = con.prepareStatement("INSERT INTO `mcecon`.`parkour_progress` (`player`, `map_id`, `checkpoint`, `passed_time`) VALUES " + values + ";"); //Prepare the statement
			int x = 1;
			for (Entry<Integer, SavedCheckpoint> entry : savedCheckpoints.entrySet()) { //Set all values
				prepStat.setString(x++, playername);
				prepStat.setInt(x++, entry.getKey());
				prepStat.setInt(x++, entry.getValue().getLastCheckpoint());
				prepStat.setLong(x++, entry.getValue().getPassedTime());
			}
			prepStat.executeUpdate(); //Execute the update
		} catch (SQLException e) {
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to save checkpoints of player " + playername + ". SQLException: " + e.getMessage());
			if (player.getPlayer().isOnline()) {
				_.msg(player.getPlayer(), GameMode.Parkour, "Failed to save your checkpoints.. Something is wrong! Warn Stoux");
			}
		}
	}
	
	
	/*
	 *********
	 * Fails *
	 *********
	 */
	
	/**
	 * Add the number of fails to the player
	 * @param player The player
	 * @param mapID The Map
	 * @param fails Number of fails
	 */
	public void addFailsToPlayerMap(final String player, final int mapID, final int fails) {
		if (_.getPlugin().isEnabled()) {
			_T.run_ASync(new Runnable() {
				
				@Override
				public void run() {
					addFails(player, mapID, fails);
				}
			});
		} else {
			addFails(player, mapID, fails);
		}
	}
	
	private void addFails(String player, int mapID, int fails) {
		try {
			if (!checkConnection()) throw new SQLException("SQL not connected.");
			PreparedStatement prepStat = con.prepareStatement(
					"INSERT INTO `mcecon`.`parkour_fails` (`player`, `map_id`, `fails`) " +
					"VALUES (?, ?, ?) " +
					"ON DUPLICATE KEY UPDATE `fails` = `fails` + ? ;"
				);
			//Prepare the statement
			prepStat.setString(1, player);
			prepStat.setInt(2, mapID);
			prepStat.setInt(3, fails);
			prepStat.setInt(4, fails);
			prepStat.executeUpdate();
		} catch (SQLException e) {
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to add fails to '" + player + "'. Exception: " + e.getMessage());
		}
	}
	
	
	/*
	 ************
	 * Finishes *
	 ************
	 */
	
	/**
	 * A player finishes a parkour map
	 * @param player The player
	 * @param mapID The Map ID
	 * @param time The time 
	 */
	public void playerFinished(final String player, final int mapID, final long time) {
		_T.run_ASync(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (!checkConnection()) throw new SQLException("SQL not connected.");
					PreparedStatement prepStat = con.prepareStatement(
							"INSERT INTO `mcecon`.`parkour_times` (`player`, `map_id`, `finish_datetime`, `finish_time`) " +
							"VALUES (?, ?, NOW(), ?);");
					prepStat.setString(1, player);
					prepStat.setInt(2, mapID);
					prepStat.setLong(3, time);
					prepStat.executeUpdate();
				} catch (SQLException e) {
					_.log(Level.SEVERE, GameMode.Parkour, "Failed to save finish time! Exception: " + e.getMessage());
					Player p = _.getPlugin().getServer().getPlayerExact(player);
					if (p != null) {
						if (p.isOnline()) {
							_.msg(p, GameMode.Parkour, "Failed to save your parkour time! Warn Stoux!");
						}
					}
				}
				
			}
		});
	}
	
	
	/*
	 ************
	 * Map Info *
	 ************
	 */
	
	/**
	 * Get String array with info about the map. Array: {Name, Author, Filename}
	 * @param ID Map ID
	 * @return The array or null
	 * @throws NullPointerException Throws NullPointer when an SQL Exception happend
	 */
	public String[] getMapInfo(int ID) throws NullPointerException {
		try {
			if (!checkConnection()) throw new SQLException("SQL not connected.");
			PreparedStatement prepStat = con.prepareStatement(
					"SELECT `name`, `author`, `filename` FROM `parkour_maps` WHERE `id` = ?;"
				);
			prepStat.setInt(1, ID);
			ResultSet rs = prepStat.executeQuery();
			if (rs.next()) {
				String[] array = new String[3];
				array[0] = rs.getString(1);
				array[1] = rs.getString(2);
				array[2] = rs.getString(3);
				return array;
			}
			return null;
		} catch (SQLException e) {
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to get map info! Exception: " + e.getMessage());
			throw new NullPointerException();
		}
	}
	
	/**
	 * Update map info
	 * @param ID The map ID
	 * @param name The name of the map
	 * @param author The name of the author
	 * @return Succes
	 */
	public boolean updateMapInfo(int ID, String name, String author) {
		try {
			if (!checkConnection()) throw new SQLException();
			PreparedStatement prepStat = con.prepareStatement(
					"UPDATE `parkour_maps` SET `name` = ?,`author` = ? WHERE `id` = ?"
				);
			prepStat.setString(1, name);
			prepStat.setString(2, author);
			prepStat.setInt(3, ID);
			
			prepStat.executeUpdate();
			return true;
		} catch (SQLException e) {
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to update map info! Exception: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Add a map
	 * @param name The name of the map
	 * @param author The name of the author
	 * @param filename The filename
	 * @return The id of the new map or -1
	 */
	public int addNewMap(String name, String author, String filename) {
		try {
			if (!checkConnection()) throw new SQLException();
			PreparedStatement prepStat = con.prepareStatement(
					"INSERT INTO `mcecon`.`parkour_maps` (`name`, `author`, `filename`) " +
							"VALUES (?, ?, ?);"
					, Statement.RETURN_GENERATED_KEYS
				);
			prepStat.setString(1, name);
			prepStat.setString(2, author);
			prepStat.setString(3, filename);
			
			prepStat.executeUpdate();
			ResultSet genKey = prepStat.getGeneratedKeys(); //Get Generated Key (ID)
			if (genKey.next()) {
				return genKey.getInt(1);
			} else {
				throw new SQLException("Failed to generate Key!");
			}
		} catch (SQLException e) {
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to add map! Exception: " + e.getMessage());
			return -1;
		}
	}
	
	/*
	 ****************
	 * Leaderboards *
	 ****************
	 */
	
	private HashSet<String> doingCommand;
	
	/**
	 * Check if a player is already doing a command
	 * @param player The player
	 * @throws CommandException if player already doing a command
	 */
	public void checkDoingCommand(String player) throws CommandException {
		if (doingCommand.contains(player)) {
			throw new CommandException(Message.alreadyRunningCommand);
		}
	}
	
	/**
	 * Add a sender to the doing command list (will only add if player)
	 * @param sender the sender
	 * @throws CommandException 
	 */
	private void addDoingCommand(CommandSender sender) throws CommandException {
		if (sender instanceof Player) {
			checkDoingCommand(sender.getName());
			doingCommand.add(sender.getName());
		}
	}
	
	/**
	 * Remove a sender from the doing command list (will only remove if player)
	 * @param sender the sender
	 */
	private void removeDoingCommand(CommandSender sender) {
		if (sender instanceof Player) {
			doingCommand.remove(sender.getName());
		}
	}
	
	/**
	 * Get all the maps that should be able to be called using ID, both from currently loaded as from the DB.
	 * @param activeMaps The currently loaded maps
	 * @return A map filled with the Leaderboards enabled maps.
	 */
	protected HashMap<Integer, String[]> generateMapsCommand(HashSet<ParkourMap> activeMaps) {
		try {
			// Get all maps in database
			HashMap<Integer, String[]> foundMaps = new HashMap<>();
			ResultSet rs = con
					.createStatement()
					.executeQuery("SELECT `id`, `name`, `author` FROM `parkour_maps` ORDER BY `id` ASC ;");
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String author = rs.getString(3);
				foundMaps.put(id, new String[] { name, author });
			}

			// Find which maps have records
			if (activeMaps.size() == foundMaps.size()) { // All maps in DB are
				return foundMaps;
			} else {
				// Get all Map IDs that have times
				ResultSet rs2 = con.createStatement().executeQuery("SELECT DISTINCT `map_id` FROM `parkour_times` ;");
				HashSet<Integer> foundIds = new HashSet<>();
				while (rs2.next()) {
					foundIds.add(rs2.getInt(1));
				}

				// Fill HashMap with maps
				HashMap<Integer, String[]> leaderboardMaps = new HashMap<>();
				
				for (ParkourMap activeMap : activeMaps) { // Current active maps
					leaderboardMaps.put(activeMap.getID(), new String[]{activeMap.getName(), activeMap.getAuthor()});
				}

				for (int id : foundIds) { // Maps with times
					if (!leaderboardMaps.containsKey(id)) {
						String[] map = foundMaps.get(id);
						leaderboardMaps.put(id, map);
					}
				}
				
				return leaderboardMaps;
			}
		} catch (SQLException e) {
			_.log(Level.SEVERE, GameMode.Parkour, "Failed to get maps. Exception: " + e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * Send the leaderboard of a certain map to the commandsender
	 * @param sender The commandsender
	 * @param mapID The ID of the map. This ID must be valid!
	 * @throws CommandException if sender already doing a Parkour SQL Command
	 */
	public void sendLeaderboard(final CommandSender sender, final int mapID) throws CommandException {
		addDoingCommand(sender);
		
		_T.run_ASync(new Runnable() {
			
			@Override
			public void run() {
				try {
					//Query SQL
					PreparedStatement prepStat = con.prepareStatement("SELECT `player`, min(`finish_time`) as `time` FROM `parkour_times` where `map_id` = ? GROUP BY `player` ORDER BY `time` ASC LIMIT 0, 10 ;");
					prepStat.setInt(1, mapID);
					ResultSet rs = prepStat.executeQuery();
					
					//Get GameController & Send Map Info
					GameController gc = _.getGameController();
					parkour.sendMapInfo(sender, mapID);
					
					int rank = 1;
					while (rs.next())  { //Loop thru scores -> Send score
						_.msg(sender, GameMode.Parkour, rank++ + ". " + ChatColor.YELLOW + rs.getString(1) + ChatColor.WHITE + " - time: " + ChatColor.GREEN + gc.getTimeString(rs.getLong(2)));  
					}
					
					if (rank == 1) { //If no scores (rank still on 1) -> Send message
						_.msg(sender, GameMode.Parkour, "There are no records for this map yet!");
					}
				} catch (SQLException e) {
					_.log(Level.SEVERE, GameMode.Parkour, "Failed to get leaderboard for map! Exception: "+ e.getMessage());
					_.badMsg(sender, "Failed to get leaderboard :(");
				} finally {
					removeDoingCommand(sender);
				}
			}
		});
	}
	
	/**
	 * Send best time of a player on a certain map to a commandsender
	 * @param sender the commandsender
	 * @param lookupPlayer The player that needs to be looked up
	 * @param mapID The ID of the map
	 * @throws CommandException if already doing a command
	 */
	public void sendPlayerTime(final CommandSender sender, final String lookupPlayer, final int mapID) throws CommandException {
		addDoingCommand(sender);
		
		_T.run_ASync(new Runnable() {
			
			@Override
			public void run() {
				try {
					long time = -1L; //Set value
					
					//Get Min time
					PreparedStatement timeStat = con.prepareStatement("SELECT MIN(`finish_time`) as `time` FROM `parkour_times` WHERE `map_id` = ? AND `player` = ? ;");
					timeStat.setInt(1, mapID);
					timeStat.setString(2, lookupPlayer);
					ResultSet timeRS = timeStat.executeQuery(); //Execute Query
					
					if (timeRS.next()) { 
						time = timeRS.getLong(1);
						if (timeRS.wasNull()) { //Check if time was null. If that is the case -> Hasn't set a time
							_.msg(sender, GameMode.Parkour, "This player doesn't have a time on this map yet.");
							return;
						}
					} else {
						_.badMsg(sender, "Something went wrong gathering the time..");
						return;
					}
					
					//Calculate rank
					PreparedStatement rankStat = con.prepareStatement("SELECT COUNT(DISTINCT `player`) + 1 as `rank` FROM `parkour_times` WHERE `map_id` = ? AND `finish_time` < ? ;");
					rankStat.setInt(1, mapID);
					rankStat.setLong(2, time);
					ResultSet rankRS = rankStat.executeQuery();
					
					int rank = -1;
					if (rankRS.next()) {
						rank = rankRS.getInt(1);
					}
					
					parkour.sendMapInfo(sender, mapID); //Send map info
					_.msg(sender, GameMode.Parkour, "Player: " + ChatColor.YELLOW + lookupPlayer + ChatColor.WHITE + 
							" - time: " + ChatColor.GREEN + _.getGameController().getTimeString(time) + ChatColor.WHITE + 
							" - rank: " + ChatColor.GREEN + "#" + rank);					
				} catch (SQLException e) {
					_.log(Level.SEVERE, GameMode.Parkour, "Failed to get time for player " + lookupPlayer + " on map " + mapID + "! Exception: "+ e.getMessage());
				} finally {
					removeDoingCommand(sender);
				}
			}
		});
	}
	
	
	
	
	

}
