package nl.stoux.stouxgames.player;

public enum PlayerState {

	joining("Joining"),
	lobby("In lobby"),
	lobbySpectator("In lobby"),
	lobbyPlayer("In lobby"),
	spectating("Spectating"),
	playing("Playing"),
	gameover("Game Over");
	
	private String state;
	
	private PlayerState(String state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return state;
	}
	
}
