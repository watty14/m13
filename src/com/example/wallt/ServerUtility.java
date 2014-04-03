package com.example.wallt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ServerUtility {
	
	//Table names
	
    /**
    *
    */
   public static final String TABLE_USER = "User";

   /**
    *
    */
   public static final String TABLE_OWNER = "AccountOwners";

   /**
    *
    */
   public static final String TABLE_BANKACCOUNT = "BankAccount";

   /**
    *
    */
   public static final String TABLE_TRANSACTIONS = "Transactions";

   //Column names

   /**
    *
    */
   public static final String COLUMN_ID = "objectId";

   /**
    *
    */
   public static final String COLUMN_USERNAME = "username";

   /**
    *
    */
   public static final String COLUMN_BANKACCOUNTS = "bankaccounts";

   /**
    *
    */
   public static final String COLUMN_BANKNAME = "bankname";

   /**
    *
    */
   public static final String COLUMN_ACCOUNTNUMBER = "accountnumber";

   /**
    *
    */
   public static final String COLUMN_BALANCE = "balance";

   /**
    *
    */
   public static final String COLUMN_TRANSACTIONS = "transactions";

   /**
    *
    */
   public static final String COLUMN_AMOUNT = "amount";

   /**
    *
    */
   public static final String COLUMN_TRANSACTIONTYPE = "transactiontype";

   /**
    *
    */
   public static final String COLUMN_DATE = "createdAt";

   /**
    *
    */
   public static final String COLUMN_TRANSACTIONREASON = "reason";

    private static ServerUtility instance = null;
    
    private final String applicationID =
        "Cnmqzdi7YSCSGPBixx11Mjvu6tF8mpzTZ9yObsM1";
    
    private final String clientKey =
        "xeJa7O6dzR8oulCfD31cmUwI3frlngCFSY1jSEr8";

    protected ServerUtility() {
        //protects from instantiating attempts
    }

    public static ServerUtility getInstance() {
        if (instance == null) {
            instance = new ServerUtility();
        }
        return instance;
    }

    public void initalize(Context context) {
        Parse.initialize(context, applicationID, clientKey);
    }

    public boolean isAlreadyLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }
    
    public void logOutUser() {
    	ParseUser.logOut();
    }

    public boolean logInUser(String username, String password) {
        ParseUser user = null;
        try {
            user = ParseUser.logIn(username, password);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return user != null;
    }

    public Boolean signUpUser(String username, String email, String password) {
        ParseUser user = new ParseUser();
        boolean created = false;
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        try {
            user.signUp();
            addOwner(username);
            created = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return created;
    }
    
    public String getCurrentUsername() {
    	return ParseUser.getCurrentUser().getUsername();
    }
    
    public String getCurrentEmail() {
    	return ParseUser.getCurrentUser().getEmail();
    }
    
    public ArrayList<BankAccount> getBankAccounts() {
        String username = ParseUser.getCurrentUser().getUsername();
        String[] references = getBankAccountReferences(username);
        ArrayList<BankAccount> list = null;
        if (references != null) {
            list = (ArrayList<BankAccount>) getBankAccountsHelper(references);
        }
        return list;
    }
    
    public boolean createNewBankAccount(BankAccount account) {
        boolean created = false;
        String username = ParseUser.getCurrentUser().getUsername();
        ParseObject owner = getOwner(username);
        if (owner != null) {
            String accountID = createAccount(account);
            if (accountID != null) {
                updateAccounts(owner, accountID);
                created = true;
            }
        }
        return created;
    }
    
    private String[] getBankAccountReferences(String username) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_OWNER);
        query.whereEqualTo(COLUMN_USERNAME, username);
        JSONArray jsonReferences = null;
        try {
            jsonReferences = query.getFirst().getJSONArray(COLUMN_BANKACCOUNTS);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        String[] references = null;
        if (jsonReferences != null) {
            references = new String[jsonReferences.length()];
            try {
                for (int i = 0; i < references.length; i++) {
                    references[i] = jsonReferences.getString(i);
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return references;
    }
    
    private static List<BankAccount> getBankAccountsHelper(String[] linkReferences) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_BANKACCOUNT);
        query.whereContainedIn(COLUMN_ID, Arrays.asList(linkReferences));
        List<ParseObject> results = null;
        try {
            results = query.find();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        ArrayList<BankAccount> list = new ArrayList<BankAccount>();
        for (ParseObject result : results) {
            String objectId = result.getObjectId();
            String accountNumber = result.getString(COLUMN_ACCOUNTNUMBER);
            double balance = result.getNumber(COLUMN_BALANCE).doubleValue();
            String bankName = result.getString(COLUMN_BANKNAME);
            JSONArray references = result.getJSONArray(COLUMN_TRANSACTIONS);
            String[] transactions = null;
            if (references != null) {
                transactions = new String[references.length()];
                try {
                    for (int i = 0; i < transactions.length; i++) {
                        transactions[i] = references.getString(i);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            BankAccount account = new BankAccount(objectId, accountNumber, balance,
                    bankName, transactions);
            list.add(account);
        }
        return list;
    }
    
    public boolean withdrawAmount(BankAccount account, double amount, String reason) {
        boolean deposited = false;
        if (amount <= account.getBalance()) {
            String transactionID = createWithdrawTransaction(amount, reason);
            updateBankAccountWithWithdraw(account, transactionID, amount);
            deposited = true;
        }
        return deposited;
    }
    
    public boolean depositAmount(BankAccount account, double amount, String reason) {
        boolean deposited = false;
        String transactionID = createDepositTransaction(amount, reason);
        updateBankAccountWithDeposit(account, transactionID, amount);
        deposited = true;
        return deposited;
    }
    
    private String createDepositTransaction(double amount, String reason) {
        ParseObject deposit = new ParseObject(TABLE_TRANSACTIONS);
        String transactionId = null;
        deposit.put(COLUMN_AMOUNT, amount);
        deposit.put(COLUMN_TRANSACTIONTYPE, "deposit");
        deposit.put(COLUMN_TRANSACTIONREASON, reason);
        try {
            deposit.save();
            transactionId = deposit.getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return transactionId;
    }
    
    private void updateBankAccountWithDeposit(BankAccount account, String transactionID, double amount) {
        double newBalance = account.getBalance() + amount;
        String accountID = account.getObjectId();
        JSONArray array = null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_BANKACCOUNT);
        ParseObject result;
        try {
            result = query.get(accountID);
            array = result.getJSONArray(COLUMN_TRANSACTIONS);
            try {
                if (array.get(0).equals("")) {
                    array.put(0, transactionID);
                } else {
                    array.put(transactionID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            result.put(COLUMN_BALANCE, newBalance);
            result.put(COLUMN_TRANSACTIONS, array);
            result.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    
    public ArrayList<Transaction> getTransactions(BankAccount account) {
        ParseObject result = queryFirst(TABLE_BANKACCOUNT, COLUMN_ID, account.getObjectId());
        JSONArray transactions = getJSONArray(result, COLUMN_TRANSACTIONS);
        ArrayList<Transaction> list = new ArrayList<Transaction>();
        if (transactions != null) {
            for (int i = 0; i < transactions.length(); i++) {
                try {
                    list.add(retrieveTransaction(transactions.getString(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
    
    private static Transaction retrieveTransaction(String objectID) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_TRANSACTIONS);
        ParseObject result = null;
        Transaction t = null;
        try {
            result = query.get(objectID);
            t = new Transaction(result.getNumber(COLUMN_AMOUNT).doubleValue(),
                    result.getString(COLUMN_TRANSACTIONTYPE));
            t.setReason(result.getString(COLUMN_TRANSACTIONREASON));
            Date date = result.getCreatedAt();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            t.setCalendar(cal);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return t;
    }

    /**
     *
     *
     * @param table
     * @param key
     * @param value
     * @return
     */
    private ParseObject queryFirst(String table, String key, Object value) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(table);
        ParseObject result = null;
        query.whereEqualTo(key, value);
        try {
            result = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     *
     * @param object
     * @param column
     * @return
     */
    private JSONArray getJSONArray(ParseObject object, String column) {
        JSONArray array = object.getJSONArray(column);
        try {
            if (array.get(0).equals("")) {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }
    
    private void updateBankAccountWithWithdraw(BankAccount account, String transactionID, double amount) {
        double newBalance = account.getBalance() - amount;
        String accountID = account.getObjectId();
        JSONArray array = null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_BANKACCOUNT);
        ParseObject result;
        try {
            result = query.get(accountID);
            array = result.getJSONArray(COLUMN_TRANSACTIONS);
            try {
                if (array.get(0).equals("")) {
                    array.put(0, transactionID);
                } else {
                    array.put(transactionID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            result.put(COLUMN_BALANCE, newBalance);
            result.put(COLUMN_TRANSACTIONS, array);
            result.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    
    private String createWithdrawTransaction(double amount, String reason) {
        ParseObject deposit = new ParseObject(TABLE_TRANSACTIONS);
        String transactionId = null;
        deposit.put(COLUMN_AMOUNT, amount);
        deposit.put(COLUMN_TRANSACTIONTYPE, "withdraw");
        deposit.put(COLUMN_TRANSACTIONREASON, reason);
        try {
            deposit.save();
            transactionId = deposit.getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return transactionId;
    }
    
    private void updateAccounts(ParseObject owner, String accountID) {
        JSONArray array = owner.getJSONArray(COLUMN_BANKACCOUNTS);
        if (array == null) {
            array = new JSONArray();
        }
        array.put(accountID);
        owner.put(COLUMN_BANKACCOUNTS, array);
        try {
            owner.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    private String createAccount(BankAccount account) {
        ParseObject bankAccount = new ParseObject(TABLE_BANKACCOUNT);
        String objectId = null;
        String bankName = account.getBankName();
        String accountNumber = account.getAccountNumber();
        Number balance = account.getBalance();
        bankAccount.put(COLUMN_BANKNAME, bankName);
        bankAccount.put(COLUMN_ACCOUNTNUMBER, accountNumber);
        bankAccount.put(COLUMN_BALANCE, balance);
        JSONArray blankArray = new JSONArray();
        blankArray.put("");
        bankAccount.put(COLUMN_TRANSACTIONS, blankArray);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_BANKACCOUNT);
        query.whereEqualTo(COLUMN_BANKNAME, bankName);
        query.whereEqualTo(COLUMN_ACCOUNTNUMBER, accountNumber);
        ParseObject retrieved = null;
        try {
            retrieved = query.getFirst();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        if (retrieved == null)
            try {
                bankAccount.save();
                objectId = bankAccount.getObjectId();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        return objectId;
    }
    
    private ParseObject getOwner(String username) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_OWNER);
        query.whereEqualTo(COLUMN_USERNAME, username);
        ParseObject reference = null;
        try {
            reference = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reference;
    }
    
    private void addOwner(String username) {
        ParseObject owner = new ParseObject(TABLE_OWNER);
        owner.put(COLUMN_USERNAME, username);
        try {
            owner.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<BankAccount> getReportData() {
		String username = ParseUser.getCurrentUser().getUsername();
		String[] references = getBankAccountReferences(username);
		ArrayList<BankAccount> list = null;
		if (references != null) {
			list = (ArrayList<BankAccount>) getBankAccountsHelper(references);
			for (BankAccount i : list) {
				ArrayList<Transaction> transactions = getTransactions(i);
				i.setListTrans(transactions);
			}
		}
		//displayReportData(list);
		return list;
	}


}