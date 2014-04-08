package com.example.wallt;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class TransactionGraphHistoryActivity{
	public Intent getIntent(Context context) {
		int[] x = { 1, 2, 3, 4}; // x values!
		int[] y =  { 30, 34, 45, 57 }; // y values!
		
		TimeSeries series = new TimeSeries("Balance"); 
		for( int i = 0; i < x.length; i++)
		{
			series.add(x[i], y[i]);
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesTextSize(30);
		renderer.setColor(Color.BLACK);
		renderer.setPointStyle(PointStyle.SQUARE);
		renderer.setLineWidth(3);
		renderer.setFillPoints(true);
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); // Holds a collection of XYSeriesRenderer and customizes the graph
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setChartTitle("Tranaction History");
		
		Intent intent = ChartFactory.getLineChartIntent(context, dataset,
	                                        mRenderer, "Transaction History");
		
		return intent;
	}
	
	
}
