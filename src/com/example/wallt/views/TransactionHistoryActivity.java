package com.example.wallt.views;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.wallt.R;
import com.example.wallt.R.id;
import com.example.wallt.R.layout;
import com.example.wallt.R.menu;
import com.example.wallt.R.string;
import com.example.wallt.models.BankAccount;
import com.example.wallt.presenters.GestureListener;
import com.example.wallt.presenters.ReportsUtility;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TransactionHistoryActivity extends Fragment {

	private ListView listView;
	private ListAdapter listAdapter;
	private View fragmentView;
	private Activity parentActivity;
	private Calendar from;
	private Calendar to;
	private ArrayList<String> arrayReport;
	private ProgressBar mProgressBar;
	private String objectID;
	private String bankname;

	@Override
	public final void onCreate(final Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}

	@Override
	public final View onCreateView(
			final LayoutInflater inflater,
			final ViewGroup container,
			final Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.
				activity_transaction_history, container, false);
		parentActivity = getActivity();
		mProgressBar = (ProgressBar) fragmentView.
				findViewById(R.id.progressBar1);
		listView = (ListView) fragmentView.findViewById(R.id.listView);


	    Bundle data = getArguments();
	    objectID = data.getString("ID");
	    bankname = data.getString("BANKNAME");

	    new AsyncTaskGenerateReport().execute();

		listView.setAdapter(listAdapter);

		fragmentView.setOnTouchListener(new GestureListener() {
	        public void onSwipeRight() {
	            ((MainActivity) parentActivity).finishFragment();
	        }
	    });
	    listView.setOnTouchListener(new GestureListener() {
	        public void onSwipeRight() {
	            ((MainActivity) parentActivity).finishFragment();
	        }
	    });

	    return fragmentView;
	}

	@Override
	public final void onCreateOptionsMenu(final Menu menu,
			final MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    parentActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
	    parentActivity.setTitle(getString(R.string.
	    		title_activity_transactionhistory));
	    inflater.inflate(R.menu.generated, menu);
	}

	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (getFragmentManager().getBackStackEntryCount() > 1) {
			   ((MainActivity) parentActivity).finishFragment();
	        }
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class ListAdapter extends BaseAdapter {
	    private Context mContext;

	    public ListAdapter(final Context context) {
	        mContext = context;
	    }

	    @Override
	    public boolean areAllItemsEnabled() {
	        return false;
	    }

	    @Override
	    public boolean isEnabled(final int i) {
	        return false;
	    }

	    @Override
	    public int getCount() {
	        return (arrayReport.size() + 1) / 2;
	    }

	    @Override
	    public Object getItem(final int i) {
	        return null;
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
	    public View getView(int i, View view, ViewGroup viewGroup) {
	    	LayoutInflater li = (LayoutInflater) mContext.
	    			getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	if (i == 0) {
	    		view = li.inflate(R.layout.report_title,
	    				viewGroup, false);
	    		TextView title = (TextView) view.
	    				findViewById(R.id.title);
	    		title.setText(arrayReport.get(0));
	    	} else {
	    		view = li.inflate(R.layout.report_twoitem,
	    				viewGroup, false);
	    		TextView left = (TextView) view.findViewById(R.id.left);
	    		TextView right = (TextView) view.findViewById(
	    				R.id.right);
	    		left.setText(arrayReport.get(i * 2 - 1));
	    		right.setText(arrayReport.get(i * 2));

	    	}
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

	private class AsyncTaskGenerateReport
		extends AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(
				final Void... params) {
		    publishProgress();
		    ReportsUtility reports = new ReportsUtility();
		    BankAccount b = new BankAccount(objectID, null,
		    		0, bankname, null);
		    return reports.generateTransactionHistory(b, from, to);
		}

		@Override
		protected void onProgressUpdate(final Void... params) {
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(final ArrayList<String> aList) {
		    super.onPostExecute(aList);
		    mProgressBar.setVisibility(View.INVISIBLE);
		    if (aList != null) {
	            arrayReport = aList;
	            final ListAdapter arrayAdapter =
	                new ListAdapter(parentActivity);
	            listView.setAdapter(arrayAdapter);
	        }
		}
	}
}