package pe.edu.upc.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="client_accounts")
public class Cuenta {
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false )
	@JoinColumn(name="client_id", nullable = false)
	private Cliente owner;
	
	@Column(name="top_limit")
	private float limit;
	
	@OneToOne(mappedBy = "BankAccount", cascade = CascadeType.ALL, optional = false)
	private DetalleCuenta detail;
	
	@Column(name = "account_start", nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date start;
	
	@Column(name = "account_endt", nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date end;
	
	@Column(name="account_is_renovated")
	private boolean open;

	@OneToMany(mappedBy = "account")
	private Set<Move> historial;

	public float getLimit() {
		return limit;
	}

	public void setLimit(float limit) {
		this.limit = limit;
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

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
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
}
