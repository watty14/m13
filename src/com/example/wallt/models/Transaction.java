package com.example.wallt.models;

import java.util.Calendar;

public class Transaction {

	private double amount;

	private String type;

	private Calendar calendar;

	private String reason;

	public Transaction(double amount, String type) {
		this.amount = amount;
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public String getReason() {
		return reason;
	}

	public String getType() {
		return type;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setType(String type) {
		this.type = type;
	}
}
