package pe.edu.upc.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="user_table")
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	@Column(name="user_name", length = 30, unique = true)
	private String uname;
	@Column(name="user_password", length = 200)
	private String upass;
	@Column(name="user_is_admin", updatable = false)
	private boolean isadmin;
	@Column(name="user_is_active", updatable = true)
	private boolean isenabled;
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "account", orphanRemoval = true)
	private Cliente client;
	

	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String d) {
		uname = d;
	}
	public String getUpass() {
		return upass;
	}
	public void setUpass(String d) {
		upass = d;
	}
	public boolean isIsadmin() {
		return isadmin;
	}
	public void setIsadmin(boolean d) {
		isadmin = d;
	}
	public boolean isIsenabled() {
		return isenabled;
	}
	public void setIsenabled(boolean d) {
		isenabled = d;
	}
	public Cliente getClient() {
		return client;
	}
	public void setClient(Cliente d) {
		client = d;
	}
}
