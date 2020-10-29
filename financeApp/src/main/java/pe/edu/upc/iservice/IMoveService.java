package pe.edu.upc.iservice;

import java.util.List;
import java.util.Optional;

import pe.edu.upc.entity.Cuenta;
import pe.edu.upc.entity.Move;

public interface IMoveService {
	List<Move> list(Cuenta c);
	
	Optional<Move> findById(int id);
	
	Optional<Move> getLast(Cuenta id);
	
	int insert(Move user);
	int delete(int id);
}
