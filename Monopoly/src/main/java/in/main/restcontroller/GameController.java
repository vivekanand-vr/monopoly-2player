package in.main.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import in.main.dto.PlayerDTO;
import in.main.service.IGameService;

@RestController
public class GameController {
	
	@Autowired
	private IGameService service;
	
    @PostMapping(value = "/create-game")
    public ResponseEntity<String> submitFormData() 
    {
    	String message = service.createGame();
        return new ResponseEntity<String>(message, HttpStatus.OK);
    }
     
    @PostMapping(value = "/roll-die/p1")
    public ResponseEntity<String> playerATurn(){
    	
    	// Player 1 transaction call
    	String transactionMessage = service.playerTransaction("A"); 
    	return new ResponseEntity<String>(transactionMessage, HttpStatus.OK);
    }
    
    @PostMapping(value = "/roll-die/p2")
    public ResponseEntity<String> playerBTurn(){
    	
    	// Player 2 transaction call
    	String transactionMessage = service.playerTransaction("B"); 
    	return new ResponseEntity<String>(transactionMessage, HttpStatus.OK);
    }
    
    @GetMapping("/get-cash-details")
    public PlayerDTO getCashDetails() {
     	
        int cashA = service.getCash(1); // Get cash details of player A
        int cashB = service.getCash(2); // Get cash details of player B
        
        // Create a CashDetailsDTO object and set the cash details
        PlayerDTO cashDetails = new PlayerDTO();
        cashDetails.setCashA(cashA);
        cashDetails.setCashB(cashB);

        return cashDetails;
    }
}