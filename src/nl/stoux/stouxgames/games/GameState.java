package nl.stoux.stouxgames.games;

public enum GameState {

	disabled("Disabled"),
	enabled("Enabled"),
	lobby("Lobby"),
	lobbyJoining("Lobby"),
	starting("Starting"),
	playing("Playing"),
	finished("Finished"),
	setup("Setup");
	
	private String name;
	
	private GameState(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
