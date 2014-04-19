package com.example.wallt.views;

import com.example.wallt.R;
import com.example.wallt.R.id;
import com.example.wallt.R.layout;
import com.example.wallt.R.menu;
import com.example.wallt.R.string;
import com.example.wallt.presenters.GestureListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class SettingsActivity extends Fragment {

    private ListView listView;
    private ListAdapter listAdapter;
    private View fragmentView;
    private Activity parentActivity;

    private int myAccountSectionRow;
    private int usernameRow;
    private int emailRow;
    private int accountActionsSectionRow;
    private int logoutRow;
    private int aboutSectionRow;
    private int versionRow;
    private int rowCount;

    @Override
	public final void onCreate(final
			Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        rowCount = 0;
        myAccountSectionRow = rowCount++;
        usernameRow = rowCount++;
        emailRow = rowCount++;
        accountActionsSectionRow = rowCount++;
        logoutRow = rowCount++;
        aboutSectionRow = rowCount++;
        versionRow = rowCount++;
    }

    @Override
	public final View onCreateView(final LayoutInflater inflater,
			final ViewGroup container,
			final Bundle savedInstanceState) {
    	fragmentView = inflater.inflate(R.layout.
    			activity_settings, container, false);
    	parentActivity = getActivity();
    	listAdapter = new ListAdapter(parentActivity);
    	listView = (ListView) fragmentView.findViewById(R.id.listView);
    	listView.setAdapter(listAdapter);
    	listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
            		View view, int i, long l) {
                if (i == logoutRow) {
                    AlertDialog.Builder builder = new AlertDialog.
                    		Builder(parentActivity);
                    builder.setMessage(getString(R.string.AreYouSure));
                    builder.setTitle(getString(R.string.app_name));
                    builder.setPositiveButton(getString(R.string.OK),
                    		new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
						int which) {
						((MainActivity) parentActivity).
						returnToLogin();
						}
					});
                    builder.setNegativeButton(getString(R.string.Cancel), null);
                    builder.show().setCanceledOnTouchOutside(true);
                }
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
	    parentActivity.setTitle(getString(R.string.Settings));
	    inflater.inflate(R.menu.settings, menu);
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

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(final int i) {
            return i == logoutRow;
        }

        @Override
        public int getCount() {
            return rowCount;
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
        public View getView(final int i, View view,
        		final ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
            	LayoutInflater li = (LayoutInflater) mContext.
            		getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	view = li.inflate(R.layout.settings_section_layout,
            			viewGroup, false);
                TextView textView = (TextView) view.findViewById(
                		R.id.settings_section_text);
                if (i == myAccountSectionRow) {
                    textView.setText(getString(R.string.myAccountSection));
                } else if (i == accountActionsSectionRow) {
                    textView.setText(getString(R.string.accountActionsSection));
                } else if (i == aboutSectionRow) {
                    textView.setText(getString(R.string.aboutSection));
                }
            } else if (type == 1) {
            	LayoutInflater li = (LayoutInflater) mContext.
            			getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	view = li.inflate(R.layout.settings_row_two_layout,
            			viewGroup, false);
                TextView title = (TextView) view.findViewById(R.id.
                		settings_row_title);
                TextView subtitle = (TextView) view.findViewById(R.id.
                		settings_row_subtitle);
                View divider = view.findViewById(R.id.settings_row_divider);
                SharedPreferences settings =  parentActivity.
                		getSharedPreferences(getString(
                		R.string.preferences_table), 0);
        		String userStr = settings.getString(getString(R.string.
        				preferences_username), null);
        		String emailStr = settings.getString(getString(R.string.
        				preferences_email), null);
                if (i == usernameRow) {
                    title.setText(getString(R.string.username));
                    subtitle.setText(userStr);
                    divider.setVisibility(View.VISIBLE);
                } else if (i == emailRow) {
                    title.setText(getString(R.string.email));
                    subtitle.setText(emailStr);
                    divider.setVisibility(View.INVISIBLE);
                } else if (i == versionRow) {
                    title.setText(getString(R.string.app_name));
                    subtitle.setText(getString(R.string.app_version));
                    divider.setVisibility(View.INVISIBLE);
                }
            } else if (type == 2) {
            	LayoutInflater li = (LayoutInflater) mContext.
            			getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            	view = li.inflate(R.layout.settings_row_button_layout,
            			viewGroup, false);
                TextView text = (TextView) view.findViewById(R.id.
                		settings_row_text);
                View divider = view.findViewById(R.id.settings_row_divider);
                if (i == logoutRow) {
                    text.setText(getString(R.string.logout_button_label));
                    divider.setVisibility(View.INVISIBLE);
                }
            }
            return view;
        }

        @Override
        public int getItemViewType(final int i) {
            if (i == myAccountSectionRow || i == accountActionsSectionRow
            		|| i == aboutSectionRow) {
                return 0;
            } else if (i == usernameRow || i == emailRow || i == versionRow) {
                return 1;
            } else if (i == logoutRow) {
                return 2;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
