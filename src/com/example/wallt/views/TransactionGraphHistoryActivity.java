package com.example.wallt.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.example.wallt.models.BankAccount;
import com.example.wallt.models.Transaction;
import com.example.wallt.presenters.ServerUtility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.Calendar;

public class TransactionGraphHistoryActivity {

	private String objectID;
	private String bankname;
	private Bundle data;
	private ArrayList<Transaction> transactionList;
	private double[] y;
	private int[] x;

	public void putBundle (Bundle bundle) {
		data = bundle;
	}

	public Intent getIntent(final Context context) {
		objectID = (String) data.getString("ID");
	    bankname = (String) data.getString("BANKNAME");
	    AsyncTaskGenerateGraph asyn = new AsyncTaskGenerateGraph();
	    transactionList = asyn.doInBackground();

		x = new int[transactionList.size()];
		y = new double[transactionList.size()];

		for (int i = 0; i < transactionList.size(); i++) {
			if (transactionList.get(i) != null) {
				x[i] = i;
				if (transactionList.get(i).getType().
						equals("withdraw")) {
					y[i] = -transactionList.get(i).
							getAmount();
				} else {
					y[i] = transactionList.get(i).
							getAmount();
				}
			}
		}
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i] + " " + y[i]);
		}

		TimeSeries series = new TimeSeries("Balance");
		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);

		}

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);

		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesTextSize(30);
		renderer.setColor(Color.WHITE);
		renderer.setPointStyle(PointStyle.SQUARE);
		renderer.setLineWidth(5);
		renderer.setFillPoints(true);

		XYMultipleSeriesRenderer mRenderer =
				new XYMultipleSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setAxisTitleTextSize(30);
		mRenderer.setLabelsTextSize(15);
	    mRenderer.setLegendTextSize(15);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setZoomButtonsVisible(true);

		mRenderer.setYTitle("Amount");

		Intent intent = ChartFactory.getLineChartIntent(
				context, dataset,
	            mRenderer, "Transaction History");

		return intent;
	}

	private class AsyncTaskGenerateGraph extends
			AsyncTask<Void, Void, ArrayList<Transaction>> {

		private ArrayList<Transaction> lists;
		@Override
		protected ArrayList<Transaction> doInBackground(
				final Void... params) {
		    BankAccount b = new BankAccount(
		    		objectID, null, 0, "", null);
		    lists = ServerUtility.getInstance().getTransactions(b);
		    return lists;
		}

		@Override
		protected void onProgressUpdate(final Void... params) {
		}

		@Override
		protected void onPostExecute(
				final ArrayList<Transaction> aList) {
		    super.onPostExecute(aList);
		}
	}
}
