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
	* 	Get Cash Details of Players
	*/
	public int getCash(int id) {
		Player player = playerRepo.getById(id);
		return player.getCash();
	}
	
   /*
	*  Create Game function initializes both the player and game data table using utility functions 
	*/
	public String createGame() {
		
		String message = "Game Created Sucessfully";
		Utilities utils = new Utilities(); // to use utility functions like rollDie
		
		// get game and players data and update them with initial values
		List<Player>  players = utils.initializePlayers();
		List<GameData> gameDataList = gameRepo.findAll();
        
		// fill claimed column 
		for (GameData gameData : gameDataList)  gameData.setClaimed("unclaimed");
        
		// save to database
		playerRepo.saveAll(players);
		gameRepo.saveAll(gameDataList);	
		return message;
	}
	
   /*
    *  This function does all the transactions related to Player, common for both players
	*/
	
	@SuppressWarnings("deprecation")
	public String playerTransaction(String playerId) {
		
		String response = "";
		String opponent = playerId.equals("A") ? "B" : "A";
		int rolledNumber = Utilities.rollDie();
		int id  = playerId.equals("A") ? 1 : 2;
		int oid = playerId.equals("A") ? 2 : 1;
		
		// Get the player details first
		Player player = playerRepo.getById(id);
		Player otherPlayer = playerRepo.getById(oid);
		
		int currentPosition = player.getCurrentPosition();
		int newPosition = (currentPosition + rolledNumber) % 11;
		boolean newRound = false;
		
		player.setCurrentPosition(newPosition); // update new position
		if(newPosition < currentPosition) {
			newRound = true;
			player.setRound(player.getRound() + 1); // update rounds instantly
		}
		
		// save position and rounds (if updated)
		playerRepo.save(player);
		
		// in-case of the negative balance or rounds > 50
		if(player.getCash() <= 0 || otherPlayer.getCash() <= 0 || player.getRound() > 50) 
			return "Game over, start new game";
		
	    // Check Game-over based on rounds
	    if (player.getRound() == 50) 
	    {
	       if (player.getCash() > otherPlayer.getCash()) {		
	    	   response += "Die rolled " + rolledNumber + " and it's round 50. Player " + playerId +" has higher cash $";
	           response += player.getCash() + ". Game over, you win !";
	           
	           // save to database and return
	           playerRepo.save(player);
	           return response;	
	       }
	       else if(player.getCash() < otherPlayer.getCash()) {
	            response += "Die rolled " + rolledNumber + " and it's round 50. Player " + opponent + " has higher cash $";
	            response += otherPlayer.getCash() + ". Game over, you lose !";
	            
	            // save to database and return
	            playerRepo.save(player);
	            return response;
	       }
	            
	       else {
	            response += "Die rolled " + rolledNumber + " and it's round 50. Both have same cash $";
	            response += player.getCash() + ". Game over, it's a tie!";
	            
	            // save to database and return
	            playerRepo.save(player);
	            return response;
	            }
		}
	    
	   /*
		*	Get the place details to do transaction 
		*/
		GameData gd = gameRepo.getOne(newPosition);
		String owner = gd.getClaimed();
		String landedPlace = gd.getPlace();
		
		int rent = gd.getRent();
		int buyPrice = gd.getBuyPrice();
		
	  /*
	   * 	Case 1: Player lands on his own place, so no need to pay anything
	   *   --------
	   */  
	   if(owner.equals(playerId)) {
			
		   // if the player lands in his own place no charges
		   response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Place belongs to you so no charges.";
		   response += " Remaining balance is $" + player.getCash();
		   if(newRound) 
		   {
			   player.setCash(player.getCash() + 200); // increase the balance
			   response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + player.getCash();
		   }
		   // save the modifications to the database and return response
		   playerRepo.save(player);
		   return response;
		}
		
	   
	  /*
	   * 	Case 2: Player lands on "Start" so give bonus
	   *   --------
	   */
	   else if(newPosition == 0) {
			
		  response += "Die rolled " + rolledNumber + " and landed on " + landedPlace;
		  player.setCash(player.getCash() + 200); // increase the balance
		  response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + player.getCash();
			
		   // save the modifications to the database and return response
		   playerRepo.save(player);
		   return response;
		}
	   
		
	   /*
		* 	Case 3: Player lands on his unclaimed place, purchase if affordable
		*  --------
		*/
		else if(owner.equals("unclaimed")) {
			
			// if affordable
			if(player.getCash() >= buyPrice) 
			{
				player.setCash(player.getCash() - buyPrice);
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Unclaimed place and hence bought for $";
				response += buyPrice + ". Remaining balance is $" + player.getCash();
				gd.setClaimed(playerId);
			}
			
			else {
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Unclaimed place but couldn't buy due to insufficient funds";
			}
			
			// bonus 200 for crossing start
			if(newRound) {
				player.setCash(player.getCash() + 200); // increase the balance
				response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + player.getCash();
			}
			
			// finally save to the database
			gameRepo.save(gd);
			playerRepo.save(player);
			return response;	
		}
		
	   
	   /*
		* 	Case 4: Player lands on opponent's place, pay the rent or lose the game
		*  --------
		*/
		else { 
			// rent should be credited to the other player on which the current player is landed
		
			if(rent <= player.getCash()) {
				// pay rent, do cash exchange
				player.setCash(player.getCash() - rent);
				otherPlayer.setCash(otherPlayer.getCash() + rent);
				
				
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Paid rent of $";
				response += rent + ". Remaining balance is $" + player.getCash();
				
				// add bonus after paying rent
				if(newRound) {
					player.setCash(player.getCash() + 200); // increase the balance
					response += ". Also you gained $200 for crossing \"start\". Remaining balance is $" + player.getCash();
				}	
				
				// finally save to the database
				gameRepo.save(gd);
				playerRepo.save(player);
				playerRepo.save(otherPlayer);
				return response;
			}
			
			else {
				
				// still do the transaction and display -ve balance
				player.setCash(player.getCash() - rent);
				otherPlayer.setCash(otherPlayer.getCash() + rent);
				
				// even though rent is payed, the game is over after that 
				response += "Die rolled " + rolledNumber + " and landed on " + landedPlace + ". Paid rent of $";
				response += rent + ". Remaining balance is $" + player.getCash();
				response += ". Game over, you lose!";
				
				// finally save to the database
				gameRepo.save(gd);
				playerRepo.save(player);
				return response;
			}
		}
	}
}
