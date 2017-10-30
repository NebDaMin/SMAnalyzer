package main;

import java.awt.Color;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset; 
import org.jfree.data.category.DefaultCategoryDataset; 
import org.jfree.ui.ApplicationFrame; 
import org.jfree.ui.RefineryUtilities; 

public class Graph
{
   public final int BAR_CHART = 0;
   public final int PIE_CHART = 1;
   public Graph(int chartType, String chartTitle) 
   {
       if(chartType == BAR_CHART)
       {
           //title, categoryAxisLabel, valueAxisLabel, dataset, orientation, legend, tooltips, urls 
           JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Word", "Percentage", createDataset(BAR_CHART), PlotOrientation.VERTICAL, true, true, false);
            final CategoryPlot plot = barChart.getCategoryPlot();
            final BarRenderer renderer = (BarRenderer) plot.getRenderer();
      
            renderer.setSeriesPaint(0, Color.black);
            renderer.setSeriesPaint(1, Color.magenta);
            renderer.setSeriesPaint(2, Color.cyan);  
       }
       else if(chartType == PIE_CHART)
       {
       }
       else
       {
           
       }
   }
   
   //values for bar chart
   private CategoryDataset createDataset(int chartType) 
   {
  
        final String wordUno = "Uno";        
        final String wordDos = "Dos";        
        final String wordTres = "Tres";        
      
        final String pos = "Positive";
        final String nue = "Nuetral";
        final String neg = "Negative";
      
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();  

        dataset.addValue(15, neg, wordUno);        
        dataset.addValue(22, nue, wordUno);        
        dataset.addValue(66, pos, wordUno);                  

        dataset.addValue(75, neg, wordDos);        
        dataset.addValue(11, nue, wordDos);       
        dataset.addValue(20, pos, wordDos); 

        dataset.addValue(5, neg, wordTres);        
        dataset.addValue(15, nue, wordTres);        
        dataset.addValue(88, pos, wordTres);

        return dataset; 
   }
}