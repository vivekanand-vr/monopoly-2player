package in.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import in.main.dao.IGameRepo;
import in.main.model.GameData;

@Service
public class GameService implements IGameService {
	
	@Autowired
	private IGameRepo repo;
	
}
