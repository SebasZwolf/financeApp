package pe.edu.upc.iservice;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import pe.edu.upc.entity.Move;

public interface IMoveService {
	List<Move> list(Date date);
	
	Optional<Move> findById(int id);
	
	int insert(Move user);
	int delete(int id);
}
