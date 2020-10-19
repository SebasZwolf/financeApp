package pe.edu.upc.iservice;

import java.util.List;
import java.util.Optional;

import pe.edu.upc.entity.Cliente;

public interface IClienteService {
	List<Cliente> list(Optional<String> param);
	
	Optional<Cliente> findById(int id);
	
	Optional<Cliente> findByDni(String uname);
	
	int insert(Cliente user);
	int delete(int id);
}
