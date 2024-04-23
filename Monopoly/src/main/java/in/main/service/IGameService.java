package in.main.service;

public interface IGameService {
	
	public void initializePlayers(); 
	public void initializePlaces();
	public String createGame();
	public String doTransaction(char player);
}
