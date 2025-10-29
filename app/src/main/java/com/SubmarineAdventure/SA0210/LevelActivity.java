package com.SubmarineAdventure.SA0210;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LevelActivity extends AppCompatActivity {
    private LinearLayout layout_vertical, layout_alert;
    private ImageView back;
    private TextView alert_level, alert_previous;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private String lang;
    private int lastLevelActive, playLevel;
    private LayoutInflater inflate;

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
        lastLevelActive = sharedPreferences.getInt("lastLevelActive", 1);
        playLevel = sharedPreferences.getInt("playLevel", 1);

        setContentView(R.layout.activity_level);

        layout_vertical = findViewById(R.id.layout_vertical);
        inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layout_alert = findViewById(R.id.layout_alert);
        alert_level = findViewById(R.id.alert_level);
        alert_previous = findViewById(R.id.alert_previous);
        back = findViewById(R.id.back);

        back.setOnClickListener(View -> {
            Player.button(soundMute);
            finish();
        });

        layout_alert.setVisibility(GONE);
        update_level_UI();

        layout_vertical.setOnClickListener(View -> {
            Player.button(soundMute);
            layout_alert.setVisibility(GONE);
        });
    }

    private void update_level_UI() {
        layout_vertical.removeAllViews();

        int counter = 1;
        for (int i = 0; i < 7; i++) {
            View horizontal = inflate.inflate(R.layout.horizontal, null);
            LinearLayout layout_horizontal = (LinearLayout) horizontal.findViewById(R.id.horizontal);
            layout_horizontal.removeAllViews();

            for (int j = 0; j < 5; j++) {
                View level_card = inflate.inflate(R.layout.card_level, null);

                LinearLayout layout_card = level_card.findViewById(R.id.layout_card);
                LinearLayout card = level_card.findViewById(R.id.card);
                LinearLayout layout_star = level_card.findViewById(R.id.layout_star);
                TextView level = level_card.findViewById(R.id.level);
                ImageView star_one = level_card.findViewById(R.id.star_one);
                ImageView star_two = level_card.findViewById(R.id.star_two);
                ImageView star_thre = level_card.findViewById(R.id.star_three);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                layout_card.setLayoutParams(params);

                int star_amount = sharedPreferences.getInt("level_star_" + counter, 0);
                star_one.setVisibility(GONE);
                star_two.setVisibility(GONE);
                star_thre.setVisibility(GONE);
                if (star_amount == 0) {
                    layout_star.setVisibility(GONE);
                } else {
                    layout_star.setVisibility(VISIBLE);
                    star_one.setVisibility(VISIBLE);
                    if (star_amount > 1) star_two.setVisibility(VISIBLE);
                    if (star_amount > 2) star_thre.setVisibility(VISIBLE);
                }
                level.setText(counter + "");

                if (counter < lastLevelActive) {
                    card.setBackgroundResource(R.drawable.played_level);
                    level.setTextColor(getResources().getColor(R.color.secondary));
                } else if (counter == lastLevelActive) {
                    card.setBackgroundResource(R.drawable.not_played_level);
                    level.setTextColor(getResources().getColor(R.color.primary));
                } else {
                    card.setBackgroundResource(R.drawable.locked_level);
                    level.setTextColor(getResources().getColor(R.color.secondary));
                }

                int finalCounter = counter;
                layout_card.setOnClickListener(View -> {
                    Player.button(soundMute);
                    if (finalCounter <= lastLevelActive) {
                        editor.putInt("playLevel", finalCounter);
                        editor.apply();

                        intent = new Intent(LevelActivity.this, GameActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        layout_alert.setVisibility(VISIBLE);
                        alert_level.setText("Level " + finalCounter);
                        alert_previous.setText("Level " + (finalCounter - 1) + " needed to open");
                    }
                });

                layout_horizontal.addView(level_card);
                counter++;
            }

            layout_vertical.addView(horizontal);
        }
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