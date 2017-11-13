/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import static org.jfree.chart.ChartColor.DARK_GREEN;
import static org.jfree.chart.ChartColor.DARK_MAGENTA;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

/**
 *
 * @author Amber
 */
public class GraphInstance {
          public final int BAR_CHART = 0;
          public final int PIE_CHART = 1;
   public JFreeChart Graph(int chartType, String chartTitle, boolean threeD, int pos, int neg, int net) {   
       /* if (chartType == BAR_CHART) {

            JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Word", "Percentage", createBarDataset(pos, neg, net), PlotOrientation.VERTICAL, true, true, false);
            final CategoryPlot plot = barChart.getCategoryPlot();
            final BarRenderer renderer = (BarRenderer) plot.getRenderer();

            renderer.setSeriesPaint(0, Color.black);
            renderer.setSeriesPaint(1, Color.magenta);
            renderer.setSeriesPaint(2, Color.cyan);
            return barChart;
        } else if (chartType == PIE_CHART) {*/
            PieDataset dataset = createPieDataset(pos, neg, net);

            JFreeChart pieChart = createPieChart(dataset, chartTitle, threeD);
            return pieChart;
        /*} else {
            return null;
        }*/
    }

    //values for bar chart
    private CategoryDataset createBarDataset(int pos, int neg, int net) {

        final String wordUno = "Uno";
        final String wordDos = "Dos";
        final String wordTres = "Tres";

        final String positiveString = "Positive";
        final String neutralString = "Neutral";
        final String negativeString = "Negative";

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(15, negativeString, wordUno);
        dataset.addValue(22, neutralString, wordUno);
        dataset.addValue(66, positiveString, wordUno);

        dataset.addValue(75, negativeString, wordDos);
        dataset.addValue(11, neutralString, wordDos);
        dataset.addValue(20, positiveString, wordDos);
        dataset.addValue(5,  negativeString, wordTres);
        dataset.addValue(15, neutralString, wordTres);
        dataset.addValue(88, positiveString, wordTres);

        return dataset;
    }

    //values for pie graph
    public PieDataset createPieDataset(int pos, int neg, int net) {
        DefaultPieDataset result = new DefaultPieDataset();

        result.setValue("Positive", pos);
        result.setValue("Neutral", net);
        result.setValue("Negative", neg);

        return result;
    }

    //uncomment 3D to get the 3D version
    public JFreeChart createPieChart(PieDataset dataset, String title, boolean threeD) {

        if (threeD) {
            JFreeChart chart = ChartFactory.createPieChart3D(title, dataset, true, true, false);
            PiePlot3D plot = (PiePlot3D) chart.getPlot();
            //You can put different colors or comment these out to have the original colors
            plot.setSectionPaint("Positive", DARK_GREEN);
            plot.setSectionPaint("Neutral", DARK_MAGENTA);
            plot.setSectionPaint("Negative", Color.black);

            plot.setStartAngle(0);
            plot.setDirection(Rotation.CLOCKWISE);
            plot.setForegroundAlpha(0.5f); //sets the transparency of the graph -> 0 to 1


            return chart;
        } else {
            JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
            PiePlot plot = (PiePlot) chart.getPlot();
            //You can put different colors or comment these out to have the original colors
            plot.setSectionPaint("Positive", DARK_GREEN);
            plot.setSectionPaint("Neutral", DARK_MAGENTA);
            plot.setSectionPaint("Negative", Color.black);

            plot.setStartAngle(0);
            plot.setDirection(Rotation.CLOCKWISE);
            plot.setForegroundAlpha(0.5f); //sets the transparency of the graph -> 0 to 1


            return chart;
        }
    }  
}
