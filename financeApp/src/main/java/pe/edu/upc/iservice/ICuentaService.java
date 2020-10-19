package pe.edu.upc.iservice;

import java.util.List;
import java.util.Optional;

import pe.edu.upc.entity.Cuenta;

public interface ICuentaService {
	List<Cuenta> list();
	
	int insert(Cuenta user);
	int delete(int id);
}
