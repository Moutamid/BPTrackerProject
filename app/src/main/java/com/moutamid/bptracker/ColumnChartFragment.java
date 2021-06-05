package com.moutamid.bptracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import java.util.ArrayList;
import java.util.List;

public class ColumnChartFragment extends Fragment {

    public ColumnChartFragment() {
        // Required empty public constructor
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_column_chart, container, false);

        setColumnChart();

        return view;
    }

    private int getIntValue(String name) {
        if (getActivity() != null)
            return new Utils().getStoredInteger(getActivity(), name);
        else return 0;
    }

    private void setColumnChart() {
        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view_column);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar_column));

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Systolic", getIntValue("systolicAverage")));
        data.add(new ValueDataEntry("Diastolic", getIntValue("diastolicAverage")));
        data.add(new ValueDataEntry("Pulse", getIntValue("pulseAverage")));

//        data.add(new ValueDataEntry("Lip gloss", 110430));
//        data.add(new ValueDataEntry("Lipstick", 128000));
//        data.add(new ValueDataEntry("Nail polish", 143760));
//        data.add(new ValueDataEntry("Eyebrow pencil", 170670));
//        data.add(new ValueDataEntry("Eyeliner", 213210));
//        data.add(new ValueDataEntry("Eyeshadows", 249980));

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Blood pressure readings");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Pressure type");
        cartesian.yAxis(0).title("Reading value");

        anyChartView.setChart(cartesian);

    }

}