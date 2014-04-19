package com.example.wallt.views;

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

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.example.wallt.presenters.ReportsUtility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;



public class GeneratedActivity {

	private int fromMonth;
	private int fromYear;
	private int fromDay;
	private int toMonth;
	private int toDay;
	private int toYear;
	private int reportSelected;
	private Calendar from;
	private Calendar to;
	private Bundle data;
	private ArrayList<String> report;

	public void putBundle (Bundle bundle) {
		data = bundle;
	}
	
	public Intent getIntent(Context context) {
		
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
        
        AsyncTaskGenerateReport asyn = new AsyncTaskGenerateReport();
        report = asyn.doInBackground();
        
        for (String stuff : report) {
        	System.out.println(stuff);
        }

		int[] values = { 1, 2, 3, 4, 5 };
		CategorySeries series = new CategorySeries("Pie Graph");
		int k = 0;
		for (int value : values) {
			series.add("Section " + ++k, value);
		}

		int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };

		DefaultRenderer renderer = new DefaultRenderer();
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		renderer.setChartTitle("Pie Chart Demo");
		renderer.setChartTitleTextSize(7);
		renderer.setZoomButtonsVisible(true);

		Intent intent = ChartFactory.getPieChartIntent(context, series, renderer, "Pie");
		return intent;
	}
	
	private class AsyncTaskGenerateReport extends AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(
				final Void... params) {
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
		}

		@Override
		protected void onPostExecute(final ArrayList<String> aList) {
		    super.onPostExecute(aList);
		}
	}
}