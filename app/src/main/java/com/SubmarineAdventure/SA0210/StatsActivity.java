package com.SubmarineAdventure.SA0210;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {
    private ImageView back;
    private TextView coin;
    private Button reset_progress;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute, onVibrating;
    private Intent intent;
    private String lang;
    private int available_coin;

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

        setContentView(R.layout.activity_stats);

        back = findViewById(R.id.back);
        coin = findViewById(R.id.coin);
        reset_progress = findViewById(R.id.reset_progress);

        back.setOnClickListener(view -> {
            Player.button(soundMute);
            finish();
        });

        reset_progress.setOnClickListener(View -> {
            Player.button(soundMute);


        });

        coin.setText(available_coin + "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isMute)
            Player.all_screens.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isMute = sharedPreferences.getBoolean("isMute", false);
        if (!isMute)
            Player.all_screens.start();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}