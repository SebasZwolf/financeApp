package pe.edu.upc.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import pe.edu.upc.entity.Move;

@Repository
public interface MoveRepository extends JpaRepository<Move, Integer> {
	
	@Query(value = "select m.* from history_records m"
			+ " where m.move_bank_account = :owner"
			+ " and m.commit_date >= :startDate"
			+ " order by m.commit_date asc", nativeQuery = true)
	List<Move> fetchFrom( @Param("owner") int owner, @Param("startDate") Date date);
	
	@Query(value = "select m.* from history_records m"
			+ " where m.move_bank_account = :owner"
			+ " order by m.commit_date desc limit 1", nativeQuery = true)
	Optional<Move> getLast( @Param("owner") int owner);
}
