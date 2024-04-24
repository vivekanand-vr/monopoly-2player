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
	 * 	Create Game function initializes both the player data as well as the game data, 
	 *  using the utility functions from the utilities class with all the initial
	 *  or default values and ready to begin the game
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
	 * 	"doTransaction1" function is for player 1, within which the dies are rolled and 
	 * 	 player cash, places are purchased, rent is payed, and based on negative balance of the player
	 * 	 or the player with highest cash on 50th round is declared winner;
	 */
	
	@SuppressWarnings("deprecation")
	public String doTransaction1(String player) {
		String response = "";
		// do the transaction
		int rolledNumber = Utilities.rollDie(); // static function.
		
		// Get the player details first
		Player currentPlayer = playerRepo.getById(1);
		int currentPosition = currentPlayer.getCurrentPosition();
		int newPosition = (currentPosition + rolledNumber) % 11; // using mod to move in circular fashion
		
		boolean newRound = false;
		if(newPosition < currentPosition) {
			// after a round is completed make newRound as true to add additional message and to update balance later
			newRound = true;
		}
		
		// Check for completing a round, update the position 
	    if (newPosition < currentPosition || currentPosition == 0) {
	        currentPlayer.setRound(currentPlayer.getRound() + 1); // Increment rounds
	        currentPlayer.setCurrentPosition(newPosition); // update current position
	        
	     // Game over based on rounds
	        if (currentPlayer.getRound() >= 50) {
	            // Check for winner based on cash
	            Player otherPlayer = playerRepo.getById(2); // Assuming other player's ID is 2
	            if (currentPlayer.getCash() > otherPlayer.getCash()) {
	            	// current player is the winner add message to response
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Player A has higher cash $";
	            	response += currentPlayer.getCash() + ". Game over, you win !";
	            	
	            	// add logic for saving it into database
	            	
	            }
	            else if(currentPlayer.getCash() < otherPlayer.getCash()) {
	            	// other player is the winner add message to response
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Player B has higher cash $";
	            	response += otherPlayer.getCash() + ". Game over, you lose !";
	            	
	            	// add logic for saving it into database
	            }
	            
	            else {
	            	// its a tie case
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Both have same cash $";
	            	response += currentPlayer.getCash() + ". Game over, it's a tie!";
	            	
	            	// add logic for saving it into database
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
		
		else if(newPosition == 0) {
			response += "Die rolled " + rolledNumber + " and landed on " + landedPlace;
			currentPlayer.setCash(currentPlayer.getCash() + 200); // increase the balance
			response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + currentPlayer.getCash();
			
			// save the modifications to the database and return response
			playerRepo.save(currentPlayer);
			return response;
		}
		
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
		
		else { // in case the place is owned by other player
			if(rent <= currentPlayer.getCash()) {
				currentPlayer.setCash(currentPlayer.getCash() - rent);
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
	
	/*
	 * 	 Same logic of transaction but on player 2
	 */
	
	public String doTransaction2(String player) {
		String response = "";
		// do the transaction
		int rolledNumber = Utilities.rollDie(); // static function.
		
		// Get the player details first
		Player currentPlayer = playerRepo.getById(2);
		int currentPosition = currentPlayer.getCurrentPosition();
		int newPosition = (currentPosition + rolledNumber) % 11; // using mod to move in circular fashion
		
		boolean newRound = false;
		if(newPosition < currentPosition) {
			// after a round is completed make newRound as true to add additional message and to update balance later
			newRound = true;
		}
		
		// Check for completing a round, update the position 
	    if (newPosition < currentPosition || currentPosition == 0) {
	        currentPlayer.setRound(currentPlayer.getRound() + 1); // Increment rounds
	        currentPlayer.setCurrentPosition(newPosition); // update current position
	        
	     // Game over based on rounds
	        if (currentPlayer.getRound() >= 50) {
	            // Check for winner based on cash
	            Player otherPlayer = playerRepo.getById(1); // Assuming other player's ID is 2
	            if (currentPlayer.getCash() > otherPlayer.getCash()) {
	            	// current player is the winner add message to response
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Player B has higher cash $";
	            	response += currentPlayer.getCash() + ". Game over, you win !";
	            	
	            	// add logic for saving it into database
	            	
	            }
	            else if(currentPlayer.getCash() < otherPlayer.getCash()) {
	            	// other player is the winner add message to response
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Player A has higher cash $";
	            	response += otherPlayer.getCash() + ". Game over, you lose !";
	            	
	            	// add logic for saving it into database
	            }
	            
	            else {
	            	// its a tie case
	            	response += "Die rolled " + rolledNumber + " and it's round 50. Both have same cash $";
	            	response += currentPlayer.getCash() + ". Game over, it's a tie!";
	            	
	            	// add logic for saving it into database
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
		
		else if(newPosition == 0) {
			response += "Die rolled " + rolledNumber + " and landed on " + landedPlace;
			currentPlayer.setCash(currentPlayer.getCash() + 200); // increase the balance
			response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + currentPlayer.getCash();
			
			// save the modifications to the database and return response
			playerRepo.save(currentPlayer);
			return response;
		}
		
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
		
		else { // in case the place is owned by other player
			if(rent <= currentPlayer.getCash()) {
				currentPlayer.setCash(currentPlayer.getCash() - rent);
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
