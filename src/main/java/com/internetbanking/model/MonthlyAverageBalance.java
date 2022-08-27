package com.internetbanking.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
public class MonthlyAverageBalance {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column
	private String city;
	@Column
	private Double monthlyBalance;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "accountId"/* , referencedColumnName = "person_id" */)

	private Account account;
	@Column
	private Double averageBalanceId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Double getAverageBalanceId() {
		return averageBalanceId;
	}

	public void setAverageBalanceId(Double averageBalanceId) {
		this.averageBalanceId = averageBalanceId;
	}

	public Account getAccount() {
		return account;
	}

	public void setMonthlyBalance(Double monthlyBalance) {
		this.monthlyBalance = monthlyBalance;
	}

	public Double getMonthlyBalance() {
		return monthlyBalance;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
