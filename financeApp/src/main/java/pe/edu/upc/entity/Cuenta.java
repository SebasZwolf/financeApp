package pe.edu.upc.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="client_accounts")
public class Cuenta {
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	
	@Column(name="account_balance", nullable = false)
	private float balance;
	
	@ElementCollection
	@CollectionTable(name = "pagos_pagados", joinColumns = @JoinColumn(name="client_account_id"))
	private Set<Integer> pagados;
	
	@OneToOne
	@MapsId
	private Cliente owner;
	
	@Column(name="top_limit")
	private float maxvalue;
	
	@Column(name="payment_period_lenght", nullable = false)
	@Min(value = 1)
	private int payment_period = 1;
	
	@OneToOne(mappedBy = "BankAccount", cascade = CascadeType.ALL, optional = false)
	private DetalleCuenta detail;
	
	@Column(name = "account_start", nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date start = new Date();
	
	@Column(name="account_moneda", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private Moneda moneda = Moneda.sol;
	
	@OneToMany(mappedBy = "account")
	private Set<Move> historial;
	
	
	public Set<Integer> getPagados() {
		return pagados;
	}
		
	public Set<Move> getHistorial() {
		return historial;
	}
	public Moneda getMoneda() {
		return moneda;
	}
	public void setMoneda(Moneda moneda) {
		this.moneda = moneda;
	}
	public DetalleCuenta getDetail() {
		return detail;
	}
	public void setDetail(DetalleCuenta detail) {
		this.detail = detail;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public int getId() {
		return Id;
	}
	public Cliente getOwner() {
		return owner;
	}
	public void setOwner(Cliente owner) {
		this.owner = owner;
	}
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}
	public float getMaxvalue() {
		return maxvalue;
	}
	public void setMaxvalue(float maxvalue) {
		this.maxvalue = maxvalue;
	}
	public int getPayment_period() {
		return payment_period;
	}
	public void setPayment_period(int payment_period) {
		this.payment_period = payment_period;
	}
}
