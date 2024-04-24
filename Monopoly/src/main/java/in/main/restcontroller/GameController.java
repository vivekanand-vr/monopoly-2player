package in.main.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
    	String transactionMessage = service.doTransaction1("A"); 
    	return new ResponseEntity<String>(transactionMessage, HttpStatus.OK);
    }
    
    
    @PostMapping(value = "/roll-die/p2")
    public ResponseEntity<String> playerBTurn(){
    	
    	// Player 2 transaction call
    	String transactionMessage = service.doTransaction2("B"); 
    	return new ResponseEntity<String>(transactionMessage, HttpStatus.OK);
    }
}