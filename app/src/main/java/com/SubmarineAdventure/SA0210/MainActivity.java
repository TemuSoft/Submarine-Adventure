package com.SubmarineAdventure.SA0210;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ImageView shop, setting, stat;
    private LinearLayout play;
    private TextView coin;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
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
        lang = sharedPreferences.getString("lang", "");
        available_coin = sharedPreferences.getInt("available_coin", 0);

        setContentView(R.layout.activity_main);

        Player.all_screens(this, R.raw.all_screens);
        Player.button(this, R.raw.button);

        shop = findViewById(R.id.shop);
        setting = findViewById(R.id.setting);
        stat = findViewById(R.id.statistics);
        play = findViewById(R.id.play);
        coin = findViewById(R.id.coin);

        shop.setOnClickListener(view -> {
            Player.button(soundMute);
            intent = new Intent(MainActivity.this, ShopActivity.class);
            startActivity(intent);
        });

        play.setOnClickListener(view -> {
            Player.button(soundMute);
            intent = new Intent(MainActivity.this, LevelActivity.class);
            startActivity(intent);
        });

        setting.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        stat.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(MainActivity.this, StatsActivity.class);
            startActivity(intent);
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
        finishAffinity();
    }
}