package pe.edu.upc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="bank_account_detail")
public class DetalleCuenta {
	@Id
	@Column(name="id")
	private int Id;
	
	@OneToOne
	@MapsId
	private Cuenta BankAccount;
	
	@Column(name="comision_mantenimiento", precision = 5, scale = 2)
	private float comMantenimiento;
	
	@Column(name="interez_deudor", precision = 5, scale = 4)
	private float intDeudor;
	@Column(name="interez_excedido", precision = 5, scale = 4)
	private float intExcedente;
	
	public float getComMantenimiento() {
		return comMantenimiento;
	}
	public void setComMantenimiento(float comMantenimiento) {
		this.comMantenimiento = comMantenimiento;
	}
	public float getIntDeudor() {
		return intDeudor;
	}
	public void setIntDeudor(float intDeudor) {
		this.intDeudor = intDeudor;
	}
	public float getIntExcedente() {
		return intExcedente;
	}
	public void setIntExcedente(float intExcedente) {
		this.intExcedente = intExcedente;
	}
	public int getId() {
		return Id;
	}
	public Cuenta getBankAccount() {
		return BankAccount;
	}
	public void setBankAccount(Cuenta bankAccount) {
		BankAccount = bankAccount;
	}
	
}
