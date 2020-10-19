package pe.edu.upc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.upc.entity.Cuenta;
import pe.edu.upc.entity.Cliente;

@Repository
public interface CuentaRepository  extends JpaRepository<Cuenta,Integer>{
	List<Cuenta> findByOwner(Cliente c);
}
