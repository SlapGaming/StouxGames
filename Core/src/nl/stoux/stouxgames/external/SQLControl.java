package nl.stoux.stouxgames.external;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.logging.Level;

import nl.stoux.stouxgames.util._;
import nl.stoux.stouxgames.util._T;

public class SQLControl {

	private HashSet<SQLClass> sqlClasses;
	
	public SQLControl() {
		sqlClasses = new HashSet<>();
	}
	
	/**
	 * Add a SQL Class to the controller
	 * @param sqlClass
	 */
	public void addSQLClass(SQLClass sqlClass) {
		if (sqlClass.hasKeepAlive()) {
			sqlClasses.add(sqlClass);
		}
	}
	
	/**
	 * Remove a SQL Class from the controller
	 * @param sqlClass The class
	 */
	public void removeSQLClass(SQLClass sqlClass) {
		sqlClasses.remove(sqlClass);
	}
	
	public void startPinging(int minutes) {
		int ticks = minutes * 60 * 20;
		_T.runTimer_ASync(new Runnable() {
			
			@Override
			public void run() {
				for (SQLClass sql : sqlClasses) {
					try {
						sql.ping();
					} catch (SQLException e) {
						_.log(Level.SEVERE, "Failed to ping! Exception: " + e.getMessage());
					}
				}
			}
		}, ticks, ticks);
	}

}
