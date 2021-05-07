package com.example.order.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "TB_SELLER")
@PrimaryKeyJoinColumn(name = "USER_ID")
public class Seller extends User {

	private String department;

	@OneToMany(mappedBy = "seller")
	private List<Order> orders = new ArrayList<>();

	public Seller() {

	}

	public Seller(String department) {
		this.department = department;
	}

	public Seller(Long id, String name, String department) {
		super(id, name);
		this.department = department;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void addOrder(Order order) {
		this.orders.add(order);
	}

}
