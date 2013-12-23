package nl.stoux.stouxgames.external;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import nl.stoux.stouxgames.util._;

public abstract class SQLClass {

	protected Connection con;
	protected boolean keepAlive;
	protected boolean isAlive;
	
	
	/**
	 * Check if this connection is alive
	 * @return alive
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/**
	 * Check if this SQLClass should be kept alive
	 * @return kept alive
	 */
	public boolean hasKeepAlive() {
		return keepAlive;
	}
	
	/**
	 * Set the current alive state
	 * @param isAlive
	 */
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
	/**
	 * Set keep alive
	 * @param keepAlive
	 */
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}
	
	/**
	 * Make the SQL Class connect with the MySQL Database
	 * @return succes
	 */
	public abstract boolean connect();
	
	/**
	 * Disconnect the SQL Class from the MySQL Database
	 */
	public void disconnect() {
		try {
			if (con != null) {
				if (!con.isClosed()) {
					con.close();
				}
			}
		} catch (SQLException e) {
			_.log(Level.WARNING, "Failed to disconnect SQL? Exception: " + e.getMessage());
		}
	}
	
	/**
	 * Ping the SQL Server. 
	 * This must be done in A-Sync!
	 * @throws SQLException 
	 */
	public void ping() throws SQLException {
		if (keepAlive) {
			setAlive(con.isValid(5));
			if (!isAlive) connect();
		}
	}

}
