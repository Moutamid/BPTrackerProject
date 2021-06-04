package com.moutamid.bptracker.ui.charts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.moutamid.bptracker.R;

import java.util.ArrayList;
import java.util.List;

public class ChartsFragment extends Fragment {
    private static final String TAG = "ChartsFragment";

    private View root;

    private RelativeLayout pieLayout, columnLayout;
    private ImageView pieBtn, columnBtn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_charts, container, false);

        pieLayout = root.findViewById(R.id.pieLayout);
        columnLayout = root.findViewById(R.id.columnLayout);

        pieBtn = root.findViewById(R.id.pieBtn);
        columnBtn = root.findViewById(R.id.column_btn);

        pieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pieLayout.getVisibility() == View.GONE) {
                    pieLayout.setVisibility(View.VISIBLE);
                    pieBtn.setBackgroundResource(R.drawable.bg_charts_options);
                    columnBtn.setBackgroundResource(0);
                    columnLayout.setVisibility(View.GONE);
                } else {
                    pieBtn.setBackgroundResource(0);
                    columnBtn.setBackgroundResource(R.drawable.bg_charts_options);
                    pieLayout.setVisibility(View.GONE);
                    columnLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        setColumnChart();
        setPieChart();

        return root;
    }

    private void setColumnChart() {
        AnyChartView anyChartView = root.findViewById(R.id.any_chart_view_column);
        anyChartView.setProgressBar(root.findViewById(R.id.progress_bar_column));

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Rouge", 80540));
        data.add(new ValueDataEntry("Foundation", 94190));
        data.add(new ValueDataEntry("Mascara", 102610));
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
                .format("${%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Top 10 Cosmetic Products by Revenue");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Product");
        cartesian.yAxis(0).title("Revenue");

        anyChartView.setChart(cartesian);

    }

    private void setPieChart() {
        AnyChartView anyChartView = root.findViewById(R.id.any_chart_view_pie);
        anyChartView.setProgressBar(root.findViewById(R.id.progress_bar_pie));

        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
//                Toast.makeText(getActivity(), event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });


        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Apples", 2));
        data.add(new ValueDataEntry("Pears", 3));
        data.add(new ValueDataEntry("Bananas", 4));
        data.add(new ValueDataEntry("Orange", 1));

        pie.data(data);

        pie.title("Fruits imported in 2015 (in kg)");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Retail channels")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);

    }
}