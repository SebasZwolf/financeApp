package pe.edu.upc.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.upc.entity.Cuenta;
import pe.edu.upc.entity.Move;
import pe.edu.upc.iservice.IMoveService;
import pe.edu.upc.repository.MoveRepository;

@Service
public class MoveService implements IMoveService {

	@Autowired
	private MoveRepository mR;
	
	@Override
	public List<Move> list(Cuenta cuenta) {
		return mR.fetchFrom(cuenta.getId(), cuenta.getStart());
	}
	
	@Override
	public List<Move> listFrom(Cuenta cuenta, Date date){
		return mR.fetchFrom(cuenta.getId(), date);
	}

	@Override
	public Optional<Move> findById(int id) {
		return mR.findById(id);
	}

	@Override
	public int insert(Move user) {
		return mR.save(user).getId();
	}

	@Override
	public int delete(int id) {
		try {
			mR.deleteById(id);
		} catch (Exception e) {
			return 0;
		}
		return 1;
	}

	@Override
	public Optional<Move> getLast(Cuenta account) {
		return mR.getLast(account.getId());
	}

}
