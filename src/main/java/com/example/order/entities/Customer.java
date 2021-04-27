package com.example.order.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "TB_CUSTOMER")
@PrimaryKeyJoinColumn(name = "USER_ID")
public class Customer extends User {

	private String email;

	@OneToMany
	@JoinColumn(name = "CUSTOMER_USER_ID")
	private List<Address> addresses = new ArrayList<>();

	@OneToMany(mappedBy = "customer")
	private List<Order> orders = new ArrayList<>();

	public Customer(String email) {
		this.email = email;
	}

	public Customer(Long id, String name, String email) {
		super(id, name);
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void addAddress(Address address) {
		this.addresses.add(address);
	}

}
