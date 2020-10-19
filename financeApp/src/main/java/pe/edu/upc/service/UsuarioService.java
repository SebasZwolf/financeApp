package pe.edu.upc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.upc.entity.Usuario;
import pe.edu.upc.iservice.IUsuarioService;
import pe.edu.upc.repository.UsuarioRepository;

@Service
public class UsuarioService implements IUsuarioService {
	@Autowired
	private UsuarioRepository uR;
	
	@Override
	public List<Usuario> list() {
		return uR.findAll();
	}

	@Override
	public List<Usuario> listByRole(boolean admin) {
		return uR.findByIsadmin(admin);
	}

	@Override
	public Optional<Usuario> findById(int id) {
		return uR.findById(id);
	}

	@Override
	public Optional<Usuario> findByUname(String uname) {
		return uR.findByUname(uname);
	}

	@Override
	public int insert(Usuario user) {
		return uR.save(user).getId();
	}

	@Override
	public int delete(int id) {
		try {
			uR.deleteById(id);
		} catch (Exception e) {
			return 0;
		}
		return 1;
	}

}
