package in.main.service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import in.main.dao.IGameRepo;
import in.main.dao.IPlayerRepo;
import in.main.model.GameData;
import in.main.model.Player;

@Service
public class GameService implements IGameService {

	// Roll the dies
	public static int rollDie() {
        Random rand = new Random();
        int die1 = rand.nextInt(6) + 1;
        int die2 = rand.nextInt(6) + 1;
        return die1 + die2;
    }
	@Autowired
	private IGameRepo gameRepo;
	
	@Autowired
	private IPlayerRepo playerRepo;
	
	public void initializePlayers() {	
		// Set all initial values of players
		Player p1 = new Player();
		p1.setId(1);
		p1.setCash(1000);
		p1.setCurrentPosition(0);
		p1.setRound(1);
		
		Player p2 = new Player();
		p2.setId(2);
		p2.setCash(1000);
		p2.setCurrentPosition(0);
		p2.setRound(1);
		
		// save initial player values to database
		playerRepo.saveAll(Arrays.asList(p1, p2));	
	}
	
	public void initializePlaces() {	
		// Set all values in the "claimed" column of GameData table to "unclaimed"
        List<GameData> gameDataList = gameRepo.findAll();
        for (GameData gameData : gameDataList) {
            gameData.setClaimed("unclaimed");
        }
        gameRepo.saveAll(gameDataList);
	}
	
	public String createGame() {
		String message = "Game Created Sucessfully";
		initializePlayers();
		initializePlaces();
		return message;
	}
	
	public String doTransaction(char player) {
		String response = null;
		// do the transaction
		
		
		return response;
	}
}
