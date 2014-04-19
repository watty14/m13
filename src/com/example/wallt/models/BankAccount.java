package com.example.wallt.models;

import java.util.ArrayList;

public class BankAccount {

	private String objectId;
	private String accountNumber;
	private double balance;
	private String bankName;
	private String[] transactions;
	private ArrayList<Transaction> listTrans;

	public BankAccount(String objectId,
		final String accountNumber, final double balance,
		final String bankName, final String[] transactions) {
		this.accountNumber = accountNumber;
		this.objectId = objectId;
		this.balance = balance;
		this.bankName = bankName;
		this.transactions = transactions;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String[] getTransactions() {
		return transactions;
	}

	public void setTransactions(String[] transactions) {
		this.transactions = transactions;
	}

	public ArrayList<Transaction> getListTrans() {
		return listTrans;
	}

	public void setListTrans(ArrayList<Transaction> listTrans) {
		this.listTrans = listTrans;
	}

}