package in.main.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import in.main.model.Player;

public class Utilities {
	
	// Roll the dies
	public static int rollDie() {
	   Random rand = new Random();
	   int die1 = rand.nextInt(6) + 1;
	   int die2 = rand.nextInt(6) + 1;
	   return die1 + die2;
	}
		
	public List<Player> initializePlayers(){
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
		
		List<Player> list = new ArrayList<Player>();
		list.add(p1);
		list.add(p2);
		return list;	
	}
	
}
