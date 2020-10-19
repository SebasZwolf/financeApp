package pe.edu.upc.iservice;

import java.util.List;
import java.util.Optional;

import pe.edu.upc.entity.Usuario;

public interface IUsuarioService {
	List<Usuario> list();
	
	List<Usuario> listByRole(boolean admin);
	
	Optional<Usuario> findById(int id);
	
	Optional<Usuario> findByUname(String uname);
	
	int insert(Usuario user);
	int delete(int id);
}
