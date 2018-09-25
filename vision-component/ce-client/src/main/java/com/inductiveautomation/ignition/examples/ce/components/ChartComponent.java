package com.inductiveautomation.ignition.examples.ce.components;

import java.awt.*;

import com.inductiveautomation.vision.api.client.components.model.AbstractVisionPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartComponent extends AbstractVisionPanel {

    public ChartComponent() {
        super(new BorderLayout());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "rowKey", "colKey");
        dataset.addValue(2.0, "rowKey", "colKey");
        dataset.addValue(3.0, "rowKey", "colKey");
        dataset.addValue(4.0, "rowKey", "colKey");
        dataset.addValue(5.0, "rowKey", "colKey");

        JFreeChart chart = ChartFactory.createLineChart("My Line Chart", "X Axis", "Y Axis", dataset, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);

        add(chartPanel, BorderLayout.CENTER);
    }

}
