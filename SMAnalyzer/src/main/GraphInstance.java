package main;

import java.awt.Color;
import java.text.DecimalFormat;
import static org.jfree.chart.ChartColor.LIGHT_GREEN;
import static org.jfree.chart.ChartColor.LIGHT_RED;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
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

public class GraphInstance {

    public JFreeChart Graph(String chartTitle, boolean threeD, int pos, int neg, int net) {
        PieDataset dataset = createPieDataset(pos, neg, net);

        JFreeChart pieChart = createPieChart(dataset, chartTitle);
        return pieChart;
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
        dataset.addValue(5, negativeString, wordTres);
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
    public JFreeChart createPieChart(PieDataset dataset, String title) {

            JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
            PiePlot plot = (PiePlot) chart.getPlot();
            //You can put different colors or comment these out to have the original colors
            plot.setSectionPaint("Positive", LIGHT_GREEN);
            plot.setSectionPaint("Neutral", Color.WHITE);
            plot.setSectionPaint("Negative", LIGHT_RED);
            plot.setExplodePercent("Positive", 0.10);
            plot.setExplodePercent("Neutral", 0.10);
            plot.setExplodePercent("Negative", 0.10);
            plot.setSimpleLabels(true);
            plot.setStartAngle(0);
            plot.setDirection(Rotation.CLOCKWISE);
            plot.setForegroundAlpha(0.5f); //sets the transparency of the graph -> 0 to 1
            PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator("{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
            plot.setLabelGenerator(gen);

            return chart;
        }
    }

