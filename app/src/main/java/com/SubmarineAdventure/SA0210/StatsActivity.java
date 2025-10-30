package com.SubmarineAdventure.SA0210;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Random;

public class StatsActivity extends AppCompatActivity {
    private LinearLayout collect_artifact, dive;
    private TextView collect_artifact_status, dive_status;

    private LineChart lineChart;
    private BarChart barChart;

    private ImageView back;
    private TextView coin;
    private Button reset_progress;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute, onVibrating;
    private Intent intent;
    private String lang;
    private int available_coin;
    private boolean dive_500m_achieved;
    private int total_artifact_collected;
    private int distance_traveled, treasure_amount, pearl_amount, coin_amount, shimmer_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(getResources().getColor(R.color.trans));
        sharedPreferences = getSharedPreferences("arineA5lA0210", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        onVibrating = sharedPreferences.getBoolean("onVibrating", false);
        lang = sharedPreferences.getString("lang", "");
        available_coin = sharedPreferences.getInt("available_coin", 0);
        dive_500m_achieved = sharedPreferences.getBoolean("dive_500m_achieved", false);
        total_artifact_collected = sharedPreferences.getInt("total_artifact_collected", 0);
        distance_traveled = sharedPreferences.getInt("distance_traveled", 0);
        treasure_amount = sharedPreferences.getInt("treasure_amount", 0);
        pearl_amount = sharedPreferences.getInt("pearl_amount", 0);
        coin_amount = sharedPreferences.getInt("coin_amount", 0);
        shimmer_amount = sharedPreferences.getInt("shimmer_amount", 0);

        treasure_amount = new Random().nextInt(50);
        pearl_amount = new Random().nextInt(50);
        coin_amount = new Random().nextInt(50);

        setContentView(R.layout.activity_stats);

        back = findViewById(R.id.back);
        coin = findViewById(R.id.coin);
        reset_progress = findViewById(R.id.reset_progress);

        dive = findViewById(R.id.dive);
        collect_artifact = findViewById(R.id.collect_artifact);
        dive_status = findViewById(R.id.dive_status);
        collect_artifact_status = findViewById(R.id.collect_artifact_status);

        lineChart = findViewById(R.id.lineChart);
        barChart = findViewById(R.id.barChart);

        setupLineChart();
        setupBarChart();

        back.setOnClickListener(view -> {
            Player.button(soundMute);
            finish();
        });

        reset_progress.setOnClickListener(View -> {
            Player.button(soundMute);

            editor.putInt("distance_traveled", 0);
            editor.putInt("treasure_amount", 0);
            editor.putInt("pearl_amount", 0);
            editor.putInt("coin_amount", 0);
            editor.putInt("shimmer_amount", 0);

            editor.putBoolean("dive_500m_achieved", false);
            editor.putInt("total_artifact_collected", 0);
            editor.apply();

            intent = new Intent(StatsActivity.this, StatsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        if (dive_500m_achieved) {
            dive.setBackgroundResource(R.drawable.green_rect);
            dive_status.setText(getResources().getString(R.string.collected));
        } else {
            dive.setBackgroundResource(R.drawable.light_blue_rect);
            dive_status.setText(getResources().getString(R.string.in_progress));
        }

        if (total_artifact_collected >= 100) {
            collect_artifact.setBackgroundResource(R.drawable.green_rect);
            collect_artifact_status.setText(getResources().getString(R.string.collected));
        } else {
            collect_artifact.setBackgroundResource(R.drawable.light_blue_rect);
            collect_artifact_status.setText(getResources().getString(R.string.in_progress));
        }


        coin.setText(available_coin + "");
    }
    private void setupLineChart() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 0));
        entries.add(new Entry(1, 10));
        entries.add(new Entry(2, 40));
        entries.add(new Entry(3, 70));
        entries.add(new Entry(4, 100));
        entries.add(new Entry(5, 50));
        entries.add(new Entry(6, 20));
        entries.add(new Entry(7, 40));

        int primary = getResources().getColor(R.color.primary);
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(primary);
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        LineData data = new LineData(dataSet);
        lineChart.setData(data);

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setDrawBorders(false);

        lineChart.getXAxis().setEnabled(false);

        lineChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(getResources().getColor(R.color.light_blue));
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setTextColor(primary);
        leftAxis.setAxisLineColor(primary);

        lineChart.invalidate();
    }

    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, treasure_amount));
        entries.add(new BarEntry(1, pearl_amount));
        entries.add(new BarEntry(2, coin_amount));

        int primary = getResources().getColor(R.color.primary);
        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColor(primary);
        barDataSet.setDrawValues(false);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.4f);

        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setTouchEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setEnabled(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextColor(primary);
        leftAxis.setAxisLineColor(primary);
        leftAxis.setAxisMinimum(0f);

        barChart.getAxisRight().setEnabled(false);

        barChart.setFitBars(false);

        barChart.setViewPortOffsets(60f, 20f, 60f, 20f);

        barChart.invalidate();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!isMute) Player.all_screens.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMute = sharedPreferences.getBoolean("isMute", false);
        if (!isMute) Player.all_screens.start();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}