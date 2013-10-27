package nl.stoux.stouxgames.games;

import org.bukkit.ChatColor;

public enum GameMode {

	Spleef("Spleef"),
	TNTRun("TNT Run"),
	CD("Cake Defence"),
	Parkour("Parkour"),
	Sonic("Sonic"),
	Virus("Virus");
	
	private String fullname;
	private String tab;
	/**
	 * Make the gamemode
	 * @param fullname the full name
	 */
	private GameMode(String fullname) {
		this.fullname = fullname;
		int l = 13 - fullname.length();
		String spaces = "";
		while (l > 0) {
			spaces = spaces + " ";
			l--;
		}
		tab = ChatColor.AQUA + spaces + fullname;
	}
	
	@Override
	public String toString() {
		return fullname;
	}
	
	public String getTab() {
		return tab;
	}
	
}
