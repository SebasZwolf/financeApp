package pe.edu.upc.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.edu.upc.entity.Cuenta;
import pe.edu.upc.entity.Move;

@Repository
public interface MoveRepository extends JpaRepository<Move, Integer> {
	
	@Query("select m from Move m where m.commit_date >= :operationDate and m.account = :owner")
	List<Move> fetchFrom( @Param("operationDate") Date date, @Param("owner") Cuenta account);
}
