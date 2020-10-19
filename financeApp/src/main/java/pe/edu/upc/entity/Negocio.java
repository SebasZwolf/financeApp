package pe.edu.upc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

@Entity
@Table(name="bank_client_business")
public class Negocio {
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	
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
		Negocio other = (Negocio) obj;
		if (Id != other.Id)
			return false;
		return true;
	}

	@Column(name = "business_ruc", unique = true, length = 13)
	@Pattern(regexp = "^([0-9]{13})$", message = "use solo numeros!")
	private String ruc;
	
	@Column(name = "business_name", length = 30)
	private String name;
	
	@Column(name = "business_description", length = 100)
	private String desc;
	
	@Column(name = "business_director_name", length = 30)
	private String director;
	
	@Column(name = "business_address", length = 30)
	private String address;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getRuc() {
		return ruc;
	}

	public void setRuc(String d) {
		ruc = d;
	}

	public String getName() {
		return name;
	}

	public void setName(String d) {
		name = d;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String d) {
		desc = d;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String d) {
		director = d;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String d) {
		address = d;
	}
	
	
	
	
}
