package com.example.wallt;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class GeneratedActivity extends Fragment {
	
    private ListView listView;
    private ListAdapter listAdapter;
    private View fragmentView;
    private Activity parentActivity;
	private int fromMonth;
	private int fromYear;
	private int fromDay;
	private int toMonth;
	private int toDay;
	private int toYear;
	private int reportSelected;
	private Calendar from;
	private Calendar to;	
	private ArrayList<String> arrayReport;
	private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	fragmentView = inflater.inflate(R.layout.activity_generated, container, false);
    	parentActivity = getActivity();
    	mProgressBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar1);
    	listView = (ListView) fragmentView.findViewById(R.id.listView);
    	
    	
	    Bundle data = getArguments();
        fromMonth = data.getInt("FROMMONTH");
        fromDay = data.getInt("FROMDAY");
        fromYear = data.getInt("FROMYEAR");
        toMonth = data.getInt("TOMONTH");
        toDay = data.getInt("TODAY");
        toYear = data.getInt("TOYEAR");
        reportSelected = data.getInt("TYPE");
        from = Calendar.getInstance();
        to = Calendar.getInstance();
        from.set(fromYear, fromMonth, fromDay);
        to.set(toYear, toMonth, toDay);
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
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    parentActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
	    parentActivity.setTitle(getString(R.string.title_activity_generated));
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
            return (arrayReport.size() + 1) / 2;
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
	    	if (i == 0) {
	    		view = li.inflate(R.layout.report_title, viewGroup, false);
	    		TextView title = (TextView) view.findViewById(R.id.title);
	    		title.setText(arrayReport.get(0));
	    	} else if (i == 1) {
	    		view = li.inflate(R.layout.report_twoitem, viewGroup, false);
	    		TextView left = (TextView) view.findViewById(R.id.left);
	    		TextView right = (TextView) view.findViewById(R.id.right);
	    	    left.setText(arrayReport.get(1));
	    		right.setText(arrayReport.get(2));
	    	} else {
	    		view = li.inflate(R.layout.report_twoitem, viewGroup, false);
	    		TextView left = (TextView) view.findViewById(R.id.left);
	    		TextView right = (TextView) view.findViewById(R.id.right);
	    		left.setText(arrayReport.get(i * 2 - 1));
	    		right.setText(arrayReport.get(i * 2));
	    		
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
    
    private class AsyncTaskGenerateReport
    	extends AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(final Void... params) {
		    publishProgress();
		    ReportsUtility reports = new ReportsUtility();
		    if (reportSelected == ReportsActivity.spending) {
		        return reports.generateSpendingReport(from, to);
		    } else if (reportSelected == ReportsActivity.income) {
		        return reports.generateIncomeReport(from, to);
		    } else if (reportSelected == ReportsActivity.cashflow) {
		        return reports.generateCashFlowReport(from, to);
		    } else if (reportSelected == ReportsActivity.accounts) {
		        return reports.generateAccountListingReport(from, to);
		    }
		    return null;
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