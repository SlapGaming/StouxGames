package nl.stoux.stouxgames.games.sonic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.sonic.SonicPlayer.SonicRun;
import nl.stoux.stouxgames.player.GamePlayer;
import nl.stoux.stouxgames.util._;
import nl.stoux.stouxgames.util._T;

public class SonicLeaderboard {

	//Command hashset
	private HashSet<String> doingCommand;
	
	//SQL Related
	private Connection con;
	
	//Dates
	private SimpleDateFormat timeFormat;
	private SimpleDateFormat monthFormat;
	private Date startDate;
	private java.sql.Date startDateSQL;
	private Date endDate;
	private java.sql.Date endDateSQL;
	private String monthString;
	
	//The sonic game
	private Sonic sonic;
	
	public SonicLeaderboard(Sonic sonic) {
		this.sonic = sonic;
		timeFormat = new SimpleDateFormat("mm:ss:SS");
		monthFormat = new SimpleDateFormat("MM-yyyy");
		doingCommand = new HashSet<>();
		monthFormat();
	}
	
	/**
	 * Create the month dates
	 */
	private void monthFormat() {
		try {
			String currentMonth = monthFormat.format(new Date()); //Get current month
			startDate = monthFormat.parse(currentMonth); //Parse to first of the month
			String[] splitCurrent = currentMonth.split("-");
			int month = Integer.parseInt(splitCurrent[0]) + 1; //Increase the month
			if (month == 13) { //If moving to next year
				int year = Integer.parseInt(splitCurrent[1]) + 1;
				endDate = monthFormat.parse("01-" + year);
			} else {
				endDate = monthFormat.parse(month + "-" + splitCurrent[1]);
			}
			//Create SQL times
			startDateSQL = new java.sql.Date(startDate.getTime());
			endDateSQL = new java.sql.Date(endDate.getTime());
			monthString = new SimpleDateFormat("MMMM").format(startDate);
		} catch (ParseException e) {
			_.log(Level.SEVERE, GameMode.Sonic, "Failed to parse dates. Monthly leaderboard disabled.");
		}
	}
	
	
	/**
	 * Get a String of the passed time. Format: [Minutes]:[Seconds]:[Milliseconds]
	 * @param startTime The starting time
	 * @param endTime The end time
	 * @return The string
	 */
	public String getTimeString(long startTime, long endTime) {
		return timeFormat.format(new Date(endTime - startTime));
	}
	
	
	/**
	 * Send a player a [Sonic] message
	 * @param p The player
	 * @param msg The message
	 */
	private void sendMessage(CommandSender p, String msg) {
		if (p instanceof Player) {
			if (!((Player) p).isOnline()) return;
		}
		_.msg(p, GameMode.Sonic, msg);
	}
	
	/**
	 * Get a checkpoint string 
	 * @param cp The checkpoint
	 * @param time The time that the player passed the checkpoint
	 * @return The string
	 */
	private String checkpointString(int cp, long time) {
		return "Checkpoint " + ChatColor.GRAY + String.valueOf(cp) + ChatColor.WHITE + " = " + ChatColor.GRAY + getTimeString(0, time) + ChatColor.WHITE;
	}
	
	/**
	 * Check SQL connection when a player joins.
	 * @param sP The player
	 */
	public void playerJoins(final SonicPlayer sP) {
		_T.run_ASync(new Runnable() {
			
			@Override
			public void run() {
				if (!checkConnection()) {
					//Something is wrong, kick the player
					_.badMsg(sP.getPlayer(), "Something is wrong with the game.. Sorry! Contact Stoux if he is online!");
					sonic.playerQuit(sP);
				}
			}
		});
	}
	
	
	/*
	 *******************
	 *  SQL Functions  *
	 *******************
	 */
	/**
	 * Check if the connection is still valid
	 * If not try reconnecting
	 * @return valid
	 */
	public boolean checkConnection() {
		try {
			if (con.isClosed()) {
				_T.run_ASync(new Runnable() {
					@Override
					public void run() {
						connectWithSQL();
					}
				});
				return false;
			}
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	
	/**
	 * Let the leaderboard connect with the SQL server
	 * Create the table if it doesn't exist yet.
	 * @return Succes
	 */
	public boolean connectWithSQL() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mcecon","mecon", "B9eCusTa"); //Connect
			Statement tableStatement = con.createStatement(); //Create new statement to create the table
			tableStatement.executeUpdate("CREATE TABLE IF NOT EXISTS `sonicleaderboard` (" +
					"  `player` varchar(25) NOT NULL," +
					"  `finish_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
					"  `finish` int(10) NOT NULL," +
					"  `checkpoint1` int(10) NOT NULL," +
					"  `checkpoint2` int(10) NOT NULL," +
					"  `checkpoint3` int(10) NOT NULL," +
					"  `checkpoint4` int(10) NOT NULL," +
					"  `checkpoint5` int(10) NOT NULL," +
					"  `jump1` int(10) NOT NULL," +
					"  `jump2` int(10) NOT NULL," +
					"  `jump3` int(10) NOT NULL," +
					"  `jump4` int(10) NOT NULL," +
					"  `jump5` int(10) NOT NULL," +
					"  PRIMARY KEY (`player`,`finish_timestamp`),  KEY `finish` (`finish`)" +
					") ENGINE=InnoDB DEFAULT CHARSET=latin1;");			
			return true;
		} catch (SQLException e) {
			_.log(Level.SEVERE, sonic.getGamemode(), "Failed to connect with leaderboards. SQLException: "+ e.getMessage());
			return false;
		}
	}
	
	/**
	 * Save a player's Sonic run
	 * @param player The player 
	 * @param sR The players Sonicrun
	 */
	public void saveSonicRun(final GamePlayer player, final SonicRun sR) {
		if (System.currentTimeMillis() > endDate.getTime()) { //Check if now = after the last of the calculated month
			_.log(Level.ALL, GameMode.Sonic, "Moved to next month. Recalculating month..");
			monthFormat(); //Re caclulate the month dates
		}
		_T.run_ASync(new Runnable() {
			
			@Override
			public void run() {
				try {
					PreparedStatement prep = con.prepareStatement( //Prepare a new Statement
						"INSERT INTO `mcecon`.`sonicleaderboard` " +
						"(`player`, `finish_timestamp`, `finish`, " +
						"`checkpoint1`, `checkpoint2`, `checkpoint3`, `checkpoint4`, `checkpoint5`, " +
						"`jump1`, `jump2`, `jump3`, `jump4`, `jump5`" +
						") VALUES (" +
						"?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
					);
					prep.setString(1, player.getName()); //Set the name
					long start = sR.getStart();
					prep.setLong(2, sR.getEnd() - start); //Finish time
					int x = 3;
					for (long cp : sR.getCheckpointTimes()) { //Loop thru the 5 checkpoints
						prep.setLong(x, cp - start);
						x++;
					}
					if (x != 8) throw new SQLException("Missed a checkpoint. Cheating or failed coding.");
					boolean jumpCheat = false;
					for (long jump : sR.getJumpTimes()) { //Loop thru jumps
						if (jump == 0) { //Cheated?
							jumpCheat = true;
							prep.setLong(x, -1);
						} else {
							prep.setLong(x, jump - start); //CP1
						}
						x++;
					}
					if (jumpCheat) _.log(Level.WARNING, GameMode.Sonic, player.getName() + " missed a jump. Probably cheating or horrible time?");
					prep.execute();
				} catch (SQLException e) {
					if (player.getPlayer().isOnline()) {
						_.badMsg(player.getPlayer(), "Something went wrong with saving your sonic run. Contact Stoux!");
					}
					_.log(Level.SEVERE, "Failed to save SonicRun. Error: " + e.getMessage());
					checkConnection();
				}
			}
		});
	}
	
	/**
	 * Send 'Player A' 'Player B's sonic time
	 * @param p The requesting Player (A)
	 * @param playername The requested Player (B)
	 * @return Command in progress
	 */
	public boolean sendSonicTime(final CommandSender p, final String playername) {
		if (doingCommand.contains(p.getName())) { //Check if already doing command
			return false;
		}
		doingCommand.add(p.getName());
		_.msg(p, GameMode.Sonic, "Gathering " + playername + "'s stats...");
		_T.run_ASync(new Runnable() {
			
			@Override
			public void run() {
				try {
					String[] messages = new String[7];
					ResultSet allScoresRS = con.createStatement().executeQuery( //Gather all scores
						"SELECT `player`, `finish`, `checkpoint1`, `checkpoint2`, `checkpoint3`, `checkpoint4`, `checkpoint5` " +
						"FROM `sonicleaderboard` " +
						"ORDER BY `sonicleaderboard`.`finish` ASC;"
					);
					boolean playerFound = false;
					HashSet<String> foundPlayers = new HashSet<>();
					int rank = 0;
					while (allScoresRS.next()) { //Loop thru scores
						String foundPlayer = allScoresRS.getString(1); //Get the player
						if (foundPlayers.contains(foundPlayer)) { //If player already found
							continue; //Skip
						}
						foundPlayers.add(foundPlayer);
						rank++;
						if (foundPlayer.equals(playername)) { //Requested player found
							playerFound = true; //Send player's time
							messages[0] = playername + "'s All-Time High Score = " + ChatColor.GREEN + getTimeString(0, allScoresRS.getLong(2)) + ChatColor.WHITE + " | Ranked " + ChatColor.GREEN + "#" + rank;
							messages[1] = checkpointString(1, allScoresRS.getLong(3));
							messages[2] = checkpointString(2, allScoresRS.getLong(4));
							messages[3] = checkpointString(3, allScoresRS.getLong(5));
							messages[4] = checkpointString(4, allScoresRS.getLong(6)); 
							messages[5] = checkpointString(5, allScoresRS.getLong(7));
							break;
						}
					}
					if (!playerFound) { //Player hasn't been found
						sendMessage(p, playername + " hasn't finished Sonic yet.");
						doingCommand.remove(p.getName());
						return;
					}
					
					//Get monthly time
					PreparedStatement prep = con.prepareStatement(
						"SELECT `player`, `finish` " +
						"FROM `sonicleaderboard` " +
						"WHERE `finish_timestamp` BETWEEN ? AND ? " +
						"ORDER BY `sonicleaderboard`.`finish` ASC;"
					);
					//Set dates
					prep.setDate(1, startDateSQL);
					prep.setDate(2, endDateSQL);
					
					playerFound = false; rank = 0; foundPlayers.clear();
					ResultSet monthlyRS = prep.executeQuery();
					while (monthlyRS.next()) { //Loop thru all monthly scores
						String monthlyPlayer = monthlyRS.getString(1);
						if (foundPlayers.contains(monthlyPlayer)) { //Player already found
							continue;
						}
						foundPlayers.add(monthlyPlayer);
						rank++;
						if (monthlyPlayer.equals(playername)) { //If the player
							playerFound = true;
							messages[6] = playername + "'s Monthly (" + monthString + ") High Score = " + ChatColor.GREEN + getTimeString(0, monthlyRS.getLong(2)) + ChatColor.WHITE + " | Ranked " + ChatColor.GREEN + "#" + rank;
							break;
						}
					}
					
					if (!playerFound) {
						messages[6] = playername + " hasn't raced in this month (" + monthString + ") yet.";
					}
					
					for (String msg : messages) { //Send strings
						if (msg == null) continue;
						sendMessage(p, msg);
					}
				} catch (SQLException e) {
					_.log(Level.SEVERE, GameMode.Sonic, "Failed to get " + playername + "'s time! SQLException: " + e.getMessage());
					_.badMsg(p, "Something went wrong gathering " + playername + "'s time. Contact Stoux!");
				}
				doingCommand.remove(p.getName());
			}
		});
		return true;
	}
	
	/**
	 * Send a commandsender the leaderboard
	 * @param p The sender
	 * @param monthly Send the monthly leaderboard
	 * @return Can execute command
	 */
	public boolean sendLeaderboard(final CommandSender p, final boolean monthly) {
		final String playername = p.getName();
		if (doingCommand.contains(playername)) {
			return false;
		}
		doingCommand.add(playername);
		if (!monthly) { //Send the player a message
			_.msg(p, GameMode.Sonic, "Gathering leaderboard");
		} else {
			_.msg(p, GameMode.Sonic, "Gathering monthly leaderboard");
		}
		_T.run_ASync(new Runnable() {
			
			@Override
			public void run() {
				HashSet<String> foundPlayers = new HashSet<>();
				try {
					String between = "";
					if (monthly) { //If monthly add the between SQL statement
						between = "WHERE `finish_timestamp` BETWEEN ? AND ? ";
					}
					PreparedStatement prep = con.prepareStatement( //Create the statement
						"SELECT `player`, `finish` " +
						"FROM `sonicleaderboard` " +
						between +
						"ORDER BY `sonicleaderboard`.`finish` ASC " +
						"LIMIT 0, 10;"
					);
					if (monthly) { //If monthly set the parameter
						prep.setDate(1, startDateSQL);
						prep.setDate(2, endDateSQL);
					}
					
					ResultSet leaderboardRS = prep.executeQuery(); //Execute the query
					
					int rank = 1;
					while (leaderboardRS.next()) {
						String foundPlayer = leaderboardRS.getString(1);
						if (foundPlayers.contains(foundPlayer)) { //Check if already processed a time of this player
							continue;
						}
						foundPlayers.add(foundPlayer);
						if (rank == 1) {
							if (monthly) { //Send top string
								sendMessage(p, ChatColor.YELLOW + " --- " + ChatColor.GOLD + "Sonic Monthly (" + monthString + ") Leaderboard " + ChatColor.YELLOW + "---");
							} else {
								sendMessage(p, ChatColor.YELLOW + " --- " + ChatColor.GOLD + "Sonic All-Time Leaderboard " + ChatColor.YELLOW + "---");
							}
						}
						sendMessage(p, rank + ". " + ChatColor.YELLOW + foundPlayer + ChatColor.WHITE + " - Time: " + ChatColor.YELLOW + getTimeString(0, leaderboardRS.getLong(2))); //Send time
						rank++;
					}
					
					if (rank == 1) {
						_.badMsg(p, "There are no records yet..");
					}
				} catch (SQLException e) {
					_.log(Level.SEVERE, GameMode.Sonic, "Failed to generate leaderboard! Monthly: " + monthly + ". SQLException: " + e.getMessage());
					_.badMsg(p, "Something went wrong generating the leaderboard. Contact Stoux!");
					checkConnection();
				}
				doingCommand.remove(playername);
			}
		});
		return true;
	}


}
