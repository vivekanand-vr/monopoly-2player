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
	
   /*
	* Create Game function initializes both the player and game data table using utility functions 
	*/
	public String createGame() {
		
		String message = "Game Created Sucessfully";
		Utilities utils = new Utilities(); // to use utility functions like rollDie
		
		// get game and players data and update them with initial values
		List<Player>  players = utils.initializePlayers();
		List<GameData> gameDataList = gameRepo.findAll();
        
		for (GameData gameData : gameDataList) {
            gameData.setClaimed("unclaimed");
        }
	
		// save to database
		playerRepo.saveAll(players);
		gameRepo.saveAll(gameDataList);	
		return message;
	}
	
   /*
    * This function does all the transactions of Player 1
	*/
	
	@SuppressWarnings("deprecation")
	public String doTransaction1(String player) {
		
		String response = "";
		int rolledNumber = Utilities.rollDie();
		
		// Get the player details first
		Player currentPlayer = playerRepo.getById(1);
		int currentPosition = currentPlayer.getCurrentPosition();
		int newPosition = (currentPosition + rolledNumber) % 11; 
	
		boolean newRound = false;
		if(newPosition < currentPosition) {
			newRound = true;
		}
		
		// in-case of the negative balance or rounds >= 50
		if(currentPlayer.getCash() < 0 || currentPlayer.getRound() > 50) {
			return "Game over, start new game";
		}
		
		// update position and save
		currentPlayer.setCurrentPosition(newPosition);
		playerRepo.save(currentPlayer);
		
		
		// Check for completing a round, update the position 
	    if (newPosition < currentPosition) {
	        currentPlayer.setRound(currentPlayer.getRound() + 1); 
	        
	     // update rounds as well as current position
	     playerRepo.save(currentPlayer);  
	        
	        // Game over based on rounds
	        if (currentPlayer.getRound() == 50) {
	            
	        	// Check for winner based on cash
	            Player otherPlayer = playerRepo.getById(2); 
	            if (currentPlayer.getCash() > otherPlayer.getCash()) {
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Player A has higher cash $";
	            	response += currentPlayer.getCash() + ". Game over, you win !";
	            	
	            	playerRepo.save(currentPlayer);
	            	return response;
	            	
	            }
	            else if(currentPlayer.getCash() < otherPlayer.getCash()) {
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Player B has higher cash $";
	            	response += otherPlayer.getCash() + ". Game over, you lose !";
	            	
	            	playerRepo.save(currentPlayer);
	            	return response;
	            }
	            
	            else {
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Both have same cash $";
	            	response += currentPlayer.getCash() + ". Game over, it's a tie!";
	            	
	            	playerRepo.save(currentPlayer);
	            	return response;
	            }
	            
	        }
	    }
		
	    // get the place details to do transaction
		GameData gd = gameRepo.getOne(newPosition);
		String owner = gd.getClaimed();
		String landedPlace = gd.getPlace();
		int rent = gd.getRent();
		int buyPrice = gd.getBuyPrice();
		
		/*
		 * 	Case 1: Player lands on his own place, so no need to pay anything
		 */
		if(owner.equals(player)) {
			// if the player lands in his own place no charges
			response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Place belongs to you so no charges.";
			response += " Remaining balance is $" + currentPlayer.getCash();
			if(newRound) {
				currentPlayer.setCash(currentPlayer.getCash() + 200); // increase the balance
				response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + currentPlayer.getCash();
			}
			
			// save the modifications to the database and return response
			playerRepo.save(currentPlayer);
			return response;
		}
		
		/*
		 * 	Case 2: Player lands on "Start" so give bonus
		 */
		else if(newPosition == 0) {
			response += "Die rolled " + rolledNumber + " and landed on " + landedPlace;
			currentPlayer.setCash(currentPlayer.getCash() + 200); // increase the balance
			response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + currentPlayer.getCash();
			
			// save the modifications to the database and return response
			playerRepo.save(currentPlayer);
			return response;
		}
		
		/*
		 * 	Case 3: Player lands on his unclaimed place, purchase if affordable
		 */
		else if(owner.equals("unclaimed")) {
			// if affordable
			if(currentPlayer.getCash() >= buyPrice) {
				currentPlayer.setCash(currentPlayer.getCash() - buyPrice);
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Unclaimed place and hence bought for $";
				response += buyPrice + ". Remaining balance is $" + currentPlayer.getCash();
				gd.setClaimed("A");
			}
			
			else {
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Unclaimed place but couldn't buy due to insufficient funds";
			}
			
			// bonus 200 for crossing start
			if(newRound) {
				currentPlayer.setCash(currentPlayer.getCash() + 200); // increase the balance
				response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + currentPlayer.getCash();
			}
			
			// finally save to the database
			gameRepo.save(gd);
			playerRepo.save(currentPlayer);
			return response;
			
		}
		
		/*
		 * 	Case 4: Player lands on opponent's place, pay the rent or lose the game
		 */
		else { 
			// rent should be credited to the other player on which the current player is landed
			Player opponent = playerRepo.getById(2);
			
			if(rent <= currentPlayer.getCash()) {
				// pay rent, do cash exchange
				currentPlayer.setCash(currentPlayer.getCash() - rent);
				opponent.setCash(opponent.getCash() + rent);
				
				
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Paid rent of $";
				response += rent + ". Remaining balance is $" + currentPlayer.getCash();
				
				// add bonus after paying rent
				if(newRound) {
					currentPlayer.setCash(currentPlayer.getCash() + 200); // increase the balance
					response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + currentPlayer.getCash();
				}
				
				
				// finally save to the database
				gameRepo.save(gd);
				playerRepo.save(currentPlayer);
				playerRepo.save(opponent);
				return response;
			}
			
			else {
				// even though rent is payed, the game is over after that 
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Paid rent of $";
				response += rent + ". Remaining balance is $" + currentPlayer.getCash();
				response += ". Game over, you lose!";
				
				// finally save to the database
				gameRepo.save(gd);
				playerRepo.save(currentPlayer);
				return response;
			}
		}
	}
	
	
	
   /*
	*  This function does all the transactions of Player 2
	*/
	
	@SuppressWarnings("deprecation")
	public String doTransaction2(String player) {
		
		String response = "";
		int rolledNumber = Utilities.rollDie();
		
		// Get the player details first
		Player currentPlayer = playerRepo.getById(2);
		int currentPosition = currentPlayer.getCurrentPosition();
		int newPosition = (currentPosition + rolledNumber) % 11;
		
		boolean newRound = false;
		if(newPosition < currentPosition) {
			newRound = true;
		}
		
		// in-case of the negative balance or rounds >= 50
		if(currentPlayer.getCash() < 0  || currentPlayer.getRound() > 50) {
			return "Game over, start new game";
		}
		
		// update position and save
		currentPlayer.setCurrentPosition(newPosition);
		playerRepo.save(currentPlayer);
		
		// Check for completing a round, update the position 
	    if (newPosition < currentPosition) {
	        currentPlayer.setRound(currentPlayer.getRound() + 1); 
	        
	        
	     // update rounds as well as current position
	     playerRepo.save(currentPlayer);  
	        
	     	// Game over based on rounds
	        if (currentPlayer.getRound() == 50) {

	            Player otherPlayer = playerRepo.getById(1); 
	            
	            if (currentPlayer.getCash() > otherPlayer.getCash()) {
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Player B has higher cash $";
	            	response += currentPlayer.getCash() + ". Game over, you win !";
	            	
	            	return response;
	            	
	            }
	            else if(currentPlayer.getCash() < otherPlayer.getCash()) {
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Player A has higher cash $";
	            	response += otherPlayer.getCash() + ". Game over, you lose !";
	            	
	            	return response;
	            }
	            
	            else {
	            	// its a tie case
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Both have same cash $";
	            	response += currentPlayer.getCash() + ". Game over, it's a tie!";
	            	
	            	return response;
	            }
	            
	        }
	    }
		
	    // get the place details to do transaction
		GameData gd = gameRepo.getOne(newPosition);
		String owner = gd.getClaimed();
		String landedPlace = gd.getPlace();
		int rent = gd.getRent();
		int buyPrice = gd.getBuyPrice();
		
		/*
		 * 	Case 1: Player lands on his own place, so no need to pay anything
		 */
		if(owner.equals(player)) {
			// if the player lands in his own place no charges
			response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Place belongs to you so no charges.";
			response += " Remaining balance is $" + currentPlayer.getCash();
			if(newRound) {
				currentPlayer.setCash(currentPlayer.getCash() + 200); // increase the balance
				response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + currentPlayer.getCash();
			}
			
			// save the modifications to the database and return response
			playerRepo.save(currentPlayer);
			return response;
		}
		
		/*
		 * 	Case 2: Player lands on "Start" so give bonus
		 */
		else if(newPosition == 0) {
			response += "Die rolled " + rolledNumber + " and landed on " + landedPlace;
			currentPlayer.setCash(currentPlayer.getCash() + 200); // increase the balance
			response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + currentPlayer.getCash();
			
			// save the modifications to the database and return response
			playerRepo.save(currentPlayer);
			return response;
		}
		
		/*
		 * 	Case 3: Player lands on his unclaimed place, purchase if affordable
		 */
		else if(owner.equals("unclaimed")) {
			// if affordable
			if(currentPlayer.getCash() >= buyPrice) {
				currentPlayer.setCash(currentPlayer.getCash() - buyPrice);
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Unclaimed place and hence bought for $";
				response += buyPrice + ". Remaining balance is $" + currentPlayer.getCash();
				gd.setClaimed("B");
			}
			
			else {
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Unclaimed place but couldn't buy due to insufficient funds";
			}
			
			// bonus 200 for crossing start
			if(newRound) {
				currentPlayer.setCash(currentPlayer.getCash() + 200); // increase the balance
				response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + currentPlayer.getCash();
			}
			
			// finally save to the database
			gameRepo.save(gd);
			playerRepo.save(currentPlayer);
			return response;
			
		}
		
		/*
		 * 	Case 4: Player lands on opponent's place, pay the rent or lose the game
		 */
		else { 
			
			// rent should be credited to the other player on which the current player is landed
			Player opponent = playerRepo.getById(1);
						
			if(rent <= currentPlayer.getCash()) 
			{
				// pay rent, do cash exchange
				currentPlayer.setCash(currentPlayer.getCash() - rent);
				opponent.setCash(opponent.getCash() + rent);
							
							
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Paid rent of $";
				response += rent + ". Remaining balance is $" + currentPlayer.getCash();
							
				// add bonus after paying rent
				if(newRound) {
					currentPlayer.setCash(currentPlayer.getCash() + 200); // increase the balance
					response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + currentPlayer.getCash();
					}
							
							
				// finally save to the database
				gameRepo.save(gd);
				playerRepo.save(currentPlayer);
				playerRepo.save(opponent);
				return response;
			}
			
			else {
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Paid rent of $";
				response += rent + ". Remaining balance is $" + currentPlayer.getCash();
				response += ". Game over, you lose!";
				
				// finally save to the database
				gameRepo.save(gd);
				playerRepo.save(currentPlayer);
				return response;
			}
		}
	}
}
