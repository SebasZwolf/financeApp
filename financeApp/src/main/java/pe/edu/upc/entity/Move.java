package pe.edu.upc.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="history_records")
public class Move {
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	
	@Column(name="commit_value")
	private double value;
	
	@Column(name = "commit_date", nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date commit_date;

	@ManyToOne
	@JoinColumn(nullable = false, name = "move_bank_account")
	private Cuenta account;
		
	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Date getCommit_date() {
		return commit_date;
	}

	public void setCommit_date(Date commit_date) {
		this.commit_date = commit_date;
	}

	public Cuenta getAccount() {
		return account;
	}

	public void setAccount(Cuenta account) {
		this.account = account;
	}	
	
	
}
