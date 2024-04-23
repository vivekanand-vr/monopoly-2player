package in.main.dao;

import in.main.model.GameData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGameRepo extends JpaRepository<GameData, Integer> {
	/*
	 *  All methods are given by JpaRepository --> check outline for reference
	 */
}
