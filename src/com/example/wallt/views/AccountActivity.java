package com.example.wallt.views;

import java.util.ArrayList;
import java.util.List;

import com.example.wallt.R;
import com.example.wallt.R.id;
import com.example.wallt.R.layout;
import com.example.wallt.R.menu;
import com.example.wallt.R.string;
import com.example.wallt.models.BankAccount;
import com.example.wallt.presenters.ServerUtility;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

    /**
     * Class that defines the behavior of the account fragment.
     *
     * @author Thomas Harris (tharris7@gatech.edu)
     * @version 1.0
     */
    public class AccountActivity extends Fragment {

    private View fragment;
    private ListView listView;
    private ArrayList<BankAccount> list;
    private Activity parentActivity;
    private ServerUtility instance;
    private List<BankAccount> accounts;

    /**
     * Instance of progress tracker.
     */
    private ProgressBar mProgressBar;

    @Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

    @Override
	public final View onCreateView(final LayoutInflater inflater, final
    		ViewGroup container, final Bundle savedInstanceState) {
	    fragment = inflater.inflate(R.layout.activity_account, container,
	    			false);
	    instance = ServerUtility.getInstance();
	    parentActivity = getActivity();
	    listView = (ListView) fragment.findViewById(R.id.listView);
	    mProgressBar = (ProgressBar) fragment.findViewById(R.id.
	    		progressBar1);
		new AsyncTaskGetAccounts().execute();
	    listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> arg0,
            		final View arg1, final int arg2, final long arg3) {
            	Bundle i = new Bundle();
            	i.putString("BANKNAME", accounts.get(arg2).getBankName());
            	i.putString("ACCOUNTNUMBER", accounts.get(arg2).
            				getAccountNumber());
            	i.putString("BALANCE", Double.valueOf(accounts.get(arg2).
            				getBalance()).toString());
            	i.putString("objectID", accounts.get(arg2).getObjectId());
            	TransactionActivity frag = new TransactionActivity();
			    frag.setArguments(i);
            	((MainActivity) parentActivity).addFragment(frag,
            		getString(R.string.title_activity_transactions));
            }
        });

		return fragment;
	}

	@Override
	public final void onCreateOptionsMenu(final Menu menu,
				final MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
	    getActivity().setTitle(getString(R.string.title_activity_account));
	    inflater.inflate(R.menu.account, menu);
	}

	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.account_menu_refresh: {
        	new AsyncTaskGetAccounts().execute();
            break;
        }
        case R.id.account_menu_add: {
        	((MainActivity) parentActivity).
        		addFragment(new NewAccountActivity(),
                getString(R.string.NewAccount));
            break;
        }

        case R.id.account_menu_reports: {
        	((MainActivity) parentActivity).
        			addFragment(new ReportsActivity(),
                	getString(R.string.Reports));
            break;
        }

        case R.id.account_menu_settings: {
        	((MainActivity) parentActivity).
        			addFragment(new SettingsActivity(),
        			getString(R.string.Settings));
            break;
        }

        default:
            super.onOptionsItemSelected(item);
        }
        return true;

    }

	public List<BankAccount> getDataForListView() {
		ArrayList<BankAccount> list = new ArrayList<BankAccount>();
		return list;
	}

	public BankAccount getObject(int position) {
		 return list.get(position);
	 }

	private class ListAdapter extends BaseAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(final int i) {
            return true;
        }

        @Override
        public int getCount() {
            return accounts.size();
        }

        @Override
        public Object getItem(final int i) {
            return accounts.get(i);
        }

        @Override
        public long getItemId(final int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(final int i, View view,
        				final ViewGroup viewGroup) {
        	LayoutInflater li = (LayoutInflater) mContext.
        		getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	view = li.inflate(R.layout.account_row_layout,
        			viewGroup, false);
            TextView bankName = (TextView) view.findViewById(R.id.bankName);
            TextView accountNumber = (TextView) view.
            			findViewById(R.id.accountNumber);
            TextView balance = (TextView) view.findViewById(R.id.balance);
            bankName.setText(accounts.get(i).getBankName());
            accountNumber.setText(accounts.get(i).getAccountNumber());
            balance.setText(Double.valueOf(accounts.get(i).
            					getBalance()).toString());
            return view;
        }

        @Override
        public int getItemViewType(final int i) {
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

	private class AsyncTaskGetAccounts extends
    	AsyncTask<Void, Void, ArrayList<BankAccount>> {

	    @Override
	    protected ArrayList<BankAccount> doInBackground(
	    			final Void... params) {
	        publishProgress();
	        return instance.getBankAccounts();
	    }

	    @Override
	    protected void onProgressUpdate(final Void... params) {
	        mProgressBar.setVisibility(View.VISIBLE);
	    }

	    @Override
	    protected void onPostExecute(
	    		final ArrayList<BankAccount> list) {
	        super.onPostExecute(list);
	        mProgressBar.setVisibility(View.INVISIBLE);
	        if (list != null) {
	            accounts = list;
	            final ListAdapter arrayAdapter =
	                new ListAdapter(parentActivity);
	            listView.setAdapter(arrayAdapter);
	        }
	    }
	}
}