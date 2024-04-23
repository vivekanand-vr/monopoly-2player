package in.main.dao;

import in.main.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPlayerRepo extends JpaRepository<Player, Integer> {
	
}
