package com.example.wallt.presenters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.example.wallt.models.BankAccount;
import com.example.wallt.models.Transaction;
import com.parse.ParseUser;

/**
 * ReportsUtility class allows to to take input dates and types of date to
 * create the proper report for the users.
 *
 * @author Thomas Harris (tharris7@gatech.edu)
 * @version 1.0
 */
public class ReportsUtility {

    /**
     * deposit : Instance Variable for a String.
     */
    private String deposit = "deposit";

    /**
     * withdraw : Instance Variable for a String.
     */
    private String withdraw = "withdraw";

    private ServerUtility instance = ServerUtility.getInstance();

    /**
     * generateSpendingReport creates a report for spending.
     *
     * @param from : starting date of the report.
     * @param to   : ending date of the report.
     * @return String of transactions and reasons.
     */
    public final ArrayList<String> generateSpendingReport(
            final Calendar from, final Calendar to) {
        ArrayList<BankAccount> list = filteredBankAccounts(from, to);

        HashMap<String, Double> map = new HashMap<String, Double>();
        if (list != null) {
            for (BankAccount b : list) {
                ArrayList<Transaction> transactions =
                		(ArrayList<Transaction>) b.getListTrans();
                for (Transaction t : transactions) {
                    String type = t.getType();
                    type = type.toLowerCase();
                    if (type.equals(withdraw)) {
                        String reason1 = t.getReason();
                        reason1 = reason1.toLowerCase();
                        double amount = t.getAmount();
                        if (map.containsKey(reason1)) {
                            double current = map.get(reason1);
                            amount = amount + current;
                        }
                        map.put(reason1, amount);
                    }
                }
            }
        }
        ArrayList<String> aList = new ArrayList<String>();
        aList.add("Spending Category Report for "
        		+ ParseUser.getCurrentUser().getUsername());
        aList.add(from.getTime().toString());
        aList.add(to.getTime().toString());
        for (HashMap.Entry<String, Double> entry : map.entrySet()) {
            String reason = entry.getKey();
            double amount = entry.getValue();
            aList.add(reason);
            aList.add(Double.valueOf(amount).toString());
        }
        return aList;
    }

    /**
     *generateIncomeReport method creates a report based on Income.
     *
     * @param from : starting date of the report.
     * @param to   : ending date of the report.
     * @return String of transactions and reasons.
     */
    public final ArrayList<String> generateIncomeReport(
    		final Calendar from, final Calendar to) {
        ArrayList<BankAccount> list = filteredBankAccounts(from, to);

        HashMap<String, Double> map = new HashMap<String, Double>();
        if (list != null) {
            for (BankAccount b : list) {
                ArrayList<Transaction> transactions =
                		(ArrayList<Transaction>) b.getListTrans();
                for (Transaction t : transactions) {
                    String type = t.getType();
                    type = type.toLowerCase();
                    if (type.equals(deposit)) {
                        String reason = t.getReason();
                        reason = reason.toLowerCase();
                        double amount = t.getAmount();
                        if (map.containsKey(reason)) {
                            double current = map.get(reason);
                            amount = amount + current;
                        }
                        map.put(reason, amount);
                    }
                }
            }
        }
        ArrayList<String> aList = new ArrayList<String>();
        aList.add("Income Category Report for "
        		+ ParseUser.getCurrentUser().getUsername());
        aList.add(from.getTime().toString());
        aList.add(to.getTime().toString());
        for (HashMap.Entry<String, Double> entry : map.entrySet()) {
            String reason = entry.getKey();
            double amount = entry.getValue();
            aList.add(reason);
            aList.add(Double.valueOf(amount).toString());
        }
        return aList;
    }

    /**
     * generateCashFlowReport method creates a report based on cash flow.
     *
     * @param from : starting date of the report.
     * @param to   : ending date of the report.
     * @return String of transactions and reasons.
     */
    public final ArrayList<String> generateCashFlowReport(
    		final Calendar from, final Calendar to) {
        ArrayList<BankAccount> list2 = filteredBankAccounts(from, to);
        double income = 0;
        double expenses = 0;
        if (list2 != null) {
            for (BankAccount b : list2) {
                ArrayList<Transaction> transactions =
                		(ArrayList<Transaction>) b.getListTrans();
                for (Transaction t : transactions) {
                    String type = t.getType();
                    type = type.toLowerCase();
                    if (type.equals(deposit)) {
                        income = income + t.getAmount();
                    } else if (type.equals(withdraw)) {
                        expenses = expenses - t.getAmount();
                    }
                }
            }
        }
        ArrayList<String> aList = new ArrayList<String>();
        aList.add("Cash Flow Report for "
        		+ ParseUser.getCurrentUser().getUsername());
        aList.add(from.getTime().toString());
        aList.add(to.getTime().toString());
        aList.add("Income");
        aList.add(Double.valueOf(income).toString());
        aList.add("Expenses");
        aList.add(Double.valueOf(expenses).toString());
        aList.add("Flow");
        aList.add(Double.valueOf(income - expenses).toString());
        return aList;
    }

    /**
     * generateAccountListingReport method creates a report
     * based on AccountList.
     *
     * @param from : starting date of the report.
     * @param to   : ending date of the report.
     * @return String of transactions and reasons.
     */
    public final ArrayList<String> generateAccountListingReport(
    		final Calendar from, final Calendar to) {
        ArrayList<BankAccount> list = filteredBankAccounts(from, to);
        ArrayList<String> aList = new ArrayList<String>();
        aList.add("Account Listings Report for "
        		+ ParseUser.getCurrentUser().getUsername());
        aList.add(from.getTime().toString());
        aList.add(to.getTime().toString());
        if (list != null) {
            for (BankAccount b : list) {
                String name = b.getBankName();
                double balance = b.getBalance();
                aList.add(name);
                aList.add(Double.valueOf(balance).toString());
            }
        }
        return aList;
    }

    /**
     * generateTransactionHistory method creates a transaction history
     * for an bank account.
     *
     * @param account : Bank account object
     * @param from : starting date of the report.
     * @param to   : ending date of the report.
     * @return String of transactions and reasons.
     */
    public final ArrayList<String> generateTransactionHistory(
    		final BankAccount account,
            final Calendar from, final Calendar to) {
        ArrayList<Transaction> initialList =  instance.getTransactions(account);
        //initialList = filterByDate(initialList, from, to);
        ArrayList<String> aList = new ArrayList<String>();
        aList.add("Transaction History Report for " + account.getBankName());
        //aList.add(from.getTime().toString());
        //aList.add(to.getTime().toString());
        if (initialList != null) {
            for (Transaction b : initialList) {
            	String reason = b.getReason();
            	String amount = Double.valueOf(b.getAmount()).toString();
            	aList.add(reason);
            	if (b.getType().equals(withdraw)) {
            		aList.add("-" + amount);
            	} else {
            		aList.add(amount);
            	}
                ////String name = b.getBankName();
                //double balance = b.getBalance();
                //aList.add(name);
                //aList.add(Double.valueOf(balance).toString());
            }
        }
        return aList;
    }

    /**
     * filteredBankAccounts method filters out the date range.
     *
     * @param from : starting date of the report.
     * @param to   : ending date of the report.
     * @return ArrayList<BankAccount> : list of bankaccounts with
     * 				valid informations
     */
    private ArrayList<BankAccount> filteredBankAccounts(
    		final Calendar from, final Calendar to) {
        ArrayList<BankAccount> accounts = instance.getReportData();
        if (accounts != null) {
	        for (BankAccount b : accounts) {
	            ArrayList<Transaction> newList =
	            		filterByDate((ArrayList<Transaction>)
	            		b.getListTrans(), from, to);
	            b.setListTrans(newList);
	        }
        }
        return accounts;
    }

    /**
     * filterByDate method is a helper method for filteredBankAccounts which
     * helps filter out bank accounts by date ranges.
     *
     * @param list : list of bank accounts.
     * @param from : starting date of the report.
     * @param to   : ending date of the report.
     * @return ArrayList<Transactions> : List of transactions.
     */
    private ArrayList<Transaction> filterByDate(
    	final ArrayList<Transaction> list, final Calendar from,
    	final Calendar to) {
        ArrayList<Transaction> finalList = new ArrayList<Transaction>();
        for (Transaction t : list) {
            Calendar thisDate = t.getCalendar();
            if (from.compareTo(thisDate) <= 0 && to.compareTo(thisDate) >= 0) {
                finalList.add(t);
            }
        }
        if (finalList.size() == 0) {
        	return list;
        } else {
        	return finalList;
        }
    }
}
