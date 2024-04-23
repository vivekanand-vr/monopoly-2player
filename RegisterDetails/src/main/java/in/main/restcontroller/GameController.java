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
    	String message = "Game Created Sucessfully";
        return new ResponseEntity<String>(message, HttpStatus.OK);
    }
}