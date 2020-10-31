package pe.edu.upc.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import ch.qos.logback.core.subst.Token.Type;

@Entity
@Table(name="bank_client")
public class Cliente {
	@Id
	private int Id;
	
	@OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, optional = false)
	private Cuenta cuenta;
	
	@Column(name = "client_name", length = 30)
	private String name;
	
	@Column(name = "client_lname", length = 30)
	private String lname;
		
	@Column(name = "client_dni", unique = true, length = 8)
	@Pattern(regexp = "^([0-9]{8})$", message = "use solo numeros!")
	private String dni;
	
	@Column(name = "client_telf_number", unique = true, length = 9)
	@Pattern(regexp = "^([0-9]{9})$", message = "use solo numeros!")
	private String telf;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "user_id")
	private Usuario account;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		if (Id != other.Id)
			return false;
		return true;
	}

	public Cuenta getCuenta() {
		return cuenta;
	}
	public void setCuenta(Cuenta cuenta) {
		this.cuenta = cuenta;
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String d) {
		name = d;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String d) {
		lname = d;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String d) {
		dni = d;
	}
	public String getTelf() {
		return telf;
	}
	public void setTelf(String d) {
		telf = d;
	}
	public Usuario getAccount() {
		return account;
	}
	public void setAccount(Usuario d) {
		account = d;
	}
	
}
