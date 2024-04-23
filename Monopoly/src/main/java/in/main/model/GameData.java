package in.main.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "game_data")
public class GameData {
	
	@Id
    private int no;
	private String place;
	private String claimed; // only attribute that changes
	private int buyPrice;
	private int rent;
	
}