package pe.edu.upc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pe.edu.upc.entity.Cliente;

@Repository
public interface ClienteRepository  extends JpaRepository<Cliente,Integer>{

	@Query(value = "select c" + 
			"from Cliente c" + 
			"where c.Name like %?1% or c.Lname like %?1% ", nativeQuery = true)
	List<Cliente> fetchByName(String name);
	
	Optional<Cliente> findByDni(String dni);
}
