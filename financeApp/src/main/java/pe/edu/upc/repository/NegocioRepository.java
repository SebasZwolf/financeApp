package pe.edu.upc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.upc.entity.Negocio;

@Repository
public interface NegocioRepository extends JpaRepository<Negocio, Integer> {
}
