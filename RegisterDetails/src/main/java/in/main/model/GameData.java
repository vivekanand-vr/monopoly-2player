package in.main.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class GameData {
	
	@Id
    private int no;
	
	private String place;
	private String claimed;
	private int buyPrice;
	private int rent;
	
}