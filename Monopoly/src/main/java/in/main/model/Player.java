package in.main.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Player {
	
	@Id
	private int id;
	private int cash;
	private int currentPosition;
	private int round;
}
