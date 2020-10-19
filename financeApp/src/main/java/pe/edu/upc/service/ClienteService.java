package pe.edu.upc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.upc.entity.Cliente;
import pe.edu.upc.iservice.IClienteService;
import pe.edu.upc.repository.ClienteRepository;

@Service
public class ClienteService implements IClienteService {
@Autowired
private ClienteRepository cR;
	
	@Override
	public List<Cliente> list(Optional<String> param) {
		if(param.isPresent())
			return cR.fetchByName(param.get());
		
		return cR.findAll();
	}

	@Override
	public Optional<Cliente> findById(int id) {
		return cR.findById(id);
	}

	@Override
	public Optional<Cliente> findByDni(String uname) {
		return cR.findByDni(uname);
	}

	@Override
	public int insert(Cliente user) {
		return cR.save(user).getId();
	}

	@Override
	public int delete(int id) {
		try {
			cR.deleteById(id);;			
		} catch (Exception e) {
			return 0;
		}
		return 1;
	}

}
