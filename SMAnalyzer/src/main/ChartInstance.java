package main;

import java.awt.Color;
import java.text.DecimalFormat;
import static org.jfree.chart.ChartColor.LIGHT_GREEN;
import static org.jfree.chart.ChartColor.LIGHT_RED;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

public class ChartInstance {

    public JFreeChart Chart(String chartTitle, int pos, int neg, int net) {
        PieDataset dataset = createPieDataset(pos, neg, net);

        JFreeChart pieChart = createPieChart(dataset, chartTitle);
        return pieChart;
    }
    //values for pie chart
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
            plot.setForegroundAlpha(0.5f); //sets the transparency of the chart -> 0 to 1
            PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator("{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
            plot.setLabelGenerator(gen);

            return chart;
        }
    }

