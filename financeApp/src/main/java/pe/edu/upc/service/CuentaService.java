package pe.edu.upc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.upc.entity.Cuenta;
import pe.edu.upc.iservice.ICuentaService;
import pe.edu.upc.repository.CuentaRepository;

@Service
public class CuentaService implements ICuentaService {

	@Autowired
	private CuentaRepository cR;

	@Override
	public List<Cuenta> list() {
		return cR.findAll();
	}
	
	@Override
	public int insert(Cuenta user) {
		return cR.save(user).getId();
	}

	@Override
	public int delete(int id) {
		try {
			cR.deleteById(id);
		} catch (Exception e) {
			return 0;
		}
		return 1;
	}

	@Override
	public Optional<Cuenta> findById(int id) {
		return cR.findById(id);
	}

}
