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
	
	@Column(name="comision_apertura", precision = 5, scale = 4)
	private float comApertura;
	@Column(name="comision_renovacion", precision = 5, scale = 4)
	private float comRenovar;
	@Column(name="comision_saldo_medio_disponible", precision = 5, scale = 4)
	private float comDisponibildiad;
	@Column(name="comision_mantenimiento", precision = 5, scale = 2)
	private float comMantenimiento;
	
	@Column(name="interez_deudor", precision = 5, scale = 4)
	private float intDeudor;
	@Column(name="interez_excedido", precision = 5, scale = 4)
	private float intExcedente;
	@Column(name="interez_acreedor", precision = 5, scale = 4)
	private float intAcreedor;
	public float getComApertura() {
		return comApertura;
	}
	public void setComApertura(float comApertura) {
		this.comApertura = comApertura;
	}
	public float getComRenovar() {
		return comRenovar;
	}
	public void setComRenovar(float comRenovar) {
		this.comRenovar = comRenovar;
	}
	public float getComDisponibildiad() {
		return comDisponibildiad;
	}
	public void setComDisponibildiad(float comDisponibildiad) {
		this.comDisponibildiad = comDisponibildiad;
	}
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
	public float getIntAcreedor() {
		return intAcreedor;
	}
	public void setIntAcreedor(float intAcreedor) {
		this.intAcreedor = intAcreedor;
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
