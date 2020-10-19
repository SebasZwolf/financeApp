package pe.edu.upc.iservice;

import java.util.List;
import java.util.Optional;

import pe.edu.upc.entity.Negocio;

public interface INegocioService {
	List<Negocio> list();
	
	Optional<Negocio> findById(int id);
	
	int insert(Negocio user);
	int delete(int id);
}
