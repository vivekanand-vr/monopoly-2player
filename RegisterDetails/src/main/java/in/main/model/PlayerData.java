package in.main.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class PlayerData {
	
	@Id
	private int id;
	private int cash;
}
