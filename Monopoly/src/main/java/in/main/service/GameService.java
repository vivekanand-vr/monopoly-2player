package in.main.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import in.main.dao.IGameRepo;
import in.main.dao.IPlayerRepo;
import in.main.model.GameData;
import in.main.model.Player;
import in.main.util.Utilities;

@Service
public class GameService implements IGameService {

	
	@Autowired
	private IGameRepo gameRepo;
	
	@Autowired
	private IPlayerRepo playerRepo;
	
	public String createGame() {
		
		String message = "Game Created Sucessfully";
		Utilities utils = new Utilities();
		// get game and playeres data and update them with initial values
		List<Player>  players = utils.initializePlayers();
		List<GameData> gameDataList = gameRepo.findAll();
        for (GameData gameData : gameDataList) {
            gameData.setClaimed("unclaimed");
        }
		
        // reset initial values of both places and players for new game 
		playerRepo.saveAll(players);
		gameRepo.saveAll(gameDataList);
		
		return message;
	}
	
	public String doTransaction1(char player) {
		String response = null;
		// do the transaction
		int rolledNumber = Utilities.rollDie(); // static function.
		
		// Get the player details first
		Player currentPlayer = playerRepo.getById(1);
		int currentPosition = currentPlayer.getCurrentPosition();
		int newPosition = (currentPosition + rolledNumber) % 11; // as there are only 11 positions on board
		if(newPosition < currentPosition) {
			// is player completed a round, increment round and update cash
			currentPlayer.setCash(newPosition);
		}
		
		// Check for completing a round
	    if (newPosition < currentPosition) {
	        currentPlayer.setRound(currentPlayer.getRound() + 1); // Increment rounds
	        
	        // Game over based on rounds
	        if (currentPlayer.getRound() >= 50) {
	            // Check for winner based on cash
	            Player otherPlayer = playerRepo.getById(2); // Assuming other player's ID is 2
	            if (currentPlayer.getCash() > otherPlayer.getCash()) {
	                response = "Player " + player + " wins!"; // Current player wins
	            } else if (currentPlayer.getCash() < otherPlayer.getCash()) {
	                response = "Player " + otherPlayer + " wins!"; // Other player wins
	            } else {
	                response = "It's a tie!"; // Both players have equal cash
	            }
	            return response;
	        }
	    }
			
		GameData gd = gameRepo.getOne(rolledNumber);
		
		return response;
	}
	
	public String doTransaction2(char player) {
		String response = null;
		// do the transaction
		
		
		return response;
	}
}
