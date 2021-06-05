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
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import java.util.ArrayList;
import java.util.List;


public class PieChartFragment extends Fragment {

    public PieChartFragment() {
        // Required empty public constructor
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        setPieChart();

        return view;
    }

    private int getIntValue(String name) {
        if (getActivity() != null)
            return new Utils().getStoredInteger(getActivity(), name);
        else return 0;
    }

    private void setPieChart() {
        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view_pie);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar_pie));

        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
//                Toast.makeText(getActivity(), event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });


        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Normal", getIntValue("statusNormalAverage")));
        data.add(new ValueDataEntry("Elevated", getIntValue("statusElevatedAverage")));
        data.add(new ValueDataEntry("Hypertension stage 1", getIntValue("statusHypertensionStage1")));
        data.add(new ValueDataEntry("Hypertension stage 2", getIntValue("statusHypertensionStage2")));
        data.add(new ValueDataEntry("Hypertension crisis", getIntValue("statusHypertensiveCrisisAverage")));

        pie.data(data);

        pie.title("Blood pressure stages");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Values")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);

    }

}