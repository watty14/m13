package com.example.wallt;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;



public class TransactionActivity extends Fragment {
	
    private View fragmentView;
    private Activity parentActivity;
    private ServerUtility instance;

   private String bankname;
   private String acctNumber;
   private String balance;
   private Button mDeposit;
   private Button mWithdraw;
   private Button mTransactionHistory;
   private EditText mAmount;
   private EditText mReason;
   private ProgressBar mProgressBar;
   private String objectID;
   private BankAccount account;
   
   private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	fragmentView = inflater.inflate(R.layout.activity_transaction, container, false);
    	instance = ServerUtility.getInstance();
    	fragmentView.setOnTouchListener(new GestureListener() {
            public void onSwipeRight() {
                ((MainActivity) parentActivity).finishFragment();
            }
        });
    	
    	listView = (ListView) fragmentView.findViewById(R.id.listView);
    	
    	parentActivity = getActivity();
    	Bundle data = getArguments();
        bankname = data.getString("BANKNAME");
        acctNumber = data.getString("ACCOUNTNUMBER");
        balance = data.getString("BALANCE");
        objectID = data.getString("objectID");;
        mDeposit = (Button) fragmentView.findViewById(R.id.deposit_button);
        mWithdraw = (Button) fragmentView.findViewById(R.id.withdraw_button);
        mTransactionHistory = (Button) fragmentView.findViewById(R.id.transactionhistory_button);
        mAmount = (EditText) fragmentView.findViewById(R.id.amount_field);
        mReason = (EditText) fragmentView.findViewById(R.id.reason_field);
        mProgressBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar1);
        account = new BankAccount(objectID, acctNumber, Double.parseDouble(balance), bankname, null);
        ListAdapter arrayAdapter = new ListAdapter(parentActivity);
        listView.setAdapter(arrayAdapter);
        mDeposit.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDeposit.setVisibility(View.INVISIBLE);
                mWithdraw.setVisibility(View.INVISIBLE);
                new AsyncTaskDeposit().execute();
            }
        });

        mWithdraw.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDeposit.setVisibility(View.INVISIBLE);
                mWithdraw.setVisibility(View.INVISIBLE);
                new AsyncTaskWithdraw().execute();
            }
        });

        mTransactionHistory.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Bundle i = new Bundle();
            	i.putString("ID", objectID);
            	i.putString("BANKNAME", bankname);
			    TransactionHistoryActivity frag = new TransactionHistoryActivity();
			    frag.setArguments(i);
            	((MainActivity) parentActivity).addFragment(frag,
            			getString(R.string.title_activity_transactionhistory));
            }
        });
        return fragmentView;
    }
    
	
	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    parentActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
	    parentActivity.setTitle(getString(R.string.title_activity_transactions));
	    inflater.inflate(R.menu.generated, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (getFragmentManager().getBackStackEntryCount() > 1) {
				((MainActivity) parentActivity).finishFragment();
	        }
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class AsyncTaskDeposit extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String amountStr = mAmount.getText().toString();
            String reasonStr = mReason.getText().toString();
            if (amountStr.equals("")) {
                return false;
            }
            double amount = Integer.parseInt(amountStr);
            return instance.depositAmount(account, amount, reasonStr);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.INVISIBLE);
            if (result) {
                Toast.makeText(parentActivity, "Deposit successful",
                        Toast.LENGTH_LONG).show();
                //((MainActivity) parentActivity).finishFragment();
            } else {
                Toast.makeText(parentActivity, "Failed to desposit money! Try again.",
                        Toast.LENGTH_LONG).show();
            }
            mDeposit.setVisibility(View.VISIBLE);
            mWithdraw.setVisibility(View.VISIBLE);
        }

    }

    /**
     *
     *
     * @author Thomas Harris (tharris7@gatech.edu)
     * @version 1.0
     */
    private class AsyncTaskWithdraw extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String amountStr = mAmount.getText().toString();
            String reasonStr = mReason.getText().toString();
            if (amountStr.equals("")) {
                return false;
            }
            double amount = Integer.parseInt(amountStr);
            return instance.withdrawAmount(account, amount, reasonStr);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.INVISIBLE);
            if (result) {
                Toast.makeText(parentActivity, "Withdrawal successful",
                        Toast.LENGTH_LONG).show();
                //((MainActivity) parentActivity).finishFragment();
            } else {
                
                Toast.makeText(parentActivity, "Failed to withdraw money! Try again.",
                        Toast.LENGTH_LONG).show();
            }
            mDeposit.setVisibility(View.VISIBLE);
            mWithdraw.setVisibility(View.VISIBLE);
        }

    }
    
    private class ListAdapter extends BaseAdapter {
	    private Context mContext;
	
	    public ListAdapter(Context context) {
	        mContext = context;
	    }
	
	    @Override
	    public boolean areAllItemsEnabled() {
	        return false;
	    }
	
	    @Override
	    public boolean isEnabled(int i) {
	        return false;
	    }
	
	    @Override
	    public int getCount() {
	        return 3;
	    }
	
	    @Override
	    public Object getItem(int i) {
	        return null;
	    }
	
	    @Override
	    public long getItemId(int i) {
	        return i;
	    }
	
	    @Override
	    public boolean hasStableIds() {
	        return false;
	    }
	
	    @Override
	    public View getView(int i, View view, ViewGroup viewGroup) {	
	    	LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	view = li.inflate(R.layout.report_twoitem, viewGroup, false);
	    	TextView left = (TextView) view.findViewById(R.id.left);
    		TextView right = (TextView) view.findViewById(R.id.right);
	    	if (i == 0) {
	    		left.setText("Bank Name");
	    		right.setText(bankname);
	    	} else if (i == 1){
	    		left.setText("Account Number");
	    		right.setText(acctNumber);
	    	} else if (i == 2) {
	    		left.setText("Balance");
	    		right.setText(balance);
	    	}
	        return view;
	    }
	
	    @Override
	    public int getItemViewType(int i) {
	        return 0;
	    }
	
	    @Override
	    public int getViewTypeCount() {
	        return 1;
	    }
	
	    @Override
	    public boolean isEmpty() {
	        return false;
	    }
	}

}
