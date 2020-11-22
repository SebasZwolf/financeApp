package pe.edu.upc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upc.entity.Cuenta;
import pe.edu.upc.entity.Cliente;

@Repository
public interface CuentaRepository  extends JpaRepository<Cuenta,Integer>{
	List<Cuenta> findByOwner(Cliente c);


	@Modifying
	@Query(value = "insert into pagos_pagados values(:account_id, :period_number)", nativeQuery = true)
	@Transactional
	void insertPaidPeriod(@Param("account_id") int owner, @Param("period_number") int period);
}