package pe.edu.upc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.upc.entity.Negocio;
import pe.edu.upc.iservice.INegocioService;
import pe.edu.upc.repository.NegocioRepository;

@Service
public class NegocioService implements INegocioService {
@Autowired
private NegocioRepository nR;

	@Override
	public List<Negocio> list() {
		return nR.findAll();
	}

	@Override
	public Optional<Negocio> findById(int id) {
		return nR.findById(id);
	}

	@Override
	public int insert(Negocio user) {
		return nR.save(user).getId();
	}

	@Override
	public int delete(int id) {
		try {
			nR.deleteById(id);
		} catch (Exception e) {
			return 0;
		}
		return 1;
	}

}
