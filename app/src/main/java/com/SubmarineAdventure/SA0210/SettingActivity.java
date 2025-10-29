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

public class SettingActivity extends AppCompatActivity {
    private ImageView sound, music,vibration, back;
    private TextView sound_text, music_text, vibration_text, coin;
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
        sharedPreferences = getSharedPreferences("arineA5lA0210;", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        onVibrating = sharedPreferences.getBoolean("onVibrating", false);
        lang = sharedPreferences.getString("lang", "");
        available_coin = sharedPreferences.getInt("available_coin", 0);

        setContentView(R.layout.activity_setting);

        sound = findViewById(R.id.sound);
        music = findViewById(R.id.music);
        vibration = findViewById(R.id.vibration);
        sound_text = findViewById(R.id.sound_text);
        music_text = findViewById(R.id.music_text);
        vibration_text = findViewById(R.id.vibration_text);
        back = findViewById(R.id.back);
        coin = findViewById(R.id.coin);

        back.setOnClickListener(view -> {
            Player.button(soundMute);
            finish();
        });

        sound.setOnClickListener(View -> {
            Player.button(soundMute);

            soundMute = !soundMute;
            if (soundMute) {
                Player.button.pause();
            } else {
                Player.button.start();
            }

            editor.putBoolean("soundMute", soundMute);
            editor.apply();

            update_UI();
        });

        music.setOnClickListener(View -> {
            Player.button(soundMute);

            isMute = !isMute;
            if (isMute) {
                Player.all_screens.pause();
            } else {
                Player.all_screens.start();
            }

            editor.putBoolean("isMute", isMute);
            editor.apply();

            update_UI();
        });

        vibration.setOnClickListener(View -> {
            Player.button(soundMute);

            onVibrating = !onVibrating;
            if (onVibrating) {
                Player.vibrate(SettingActivity.this, onVibrating, 500);
            }

            editor.putBoolean("onVibrating", onVibrating);
            editor.apply();

            update_UI();
        });
        update_UI();
        coin.setText(available_coin + "");
    }

    private void update_UI() {
        sound.setImageResource(R.drawable.off);
        sound_text.setText(getResources().getString(R.string.off));
        sound_text.setTextColor(getResources().getColor(R.color.blur));

        music.setImageResource(R.drawable.off);
        music_text.setText(getResources().getString(R.string.off));
        music_text.setTextColor(getResources().getColor(R.color.blur));

        vibration.setImageResource(R.drawable.off);
        vibration_text.setText(getResources().getString(R.string.off));
        vibration_text.setTextColor(getResources().getColor(R.color.blur));

        if (!soundMute) {
            sound.setImageResource(R.drawable.on);
            sound_text.setText(getResources().getString(R.string.on));
            sound_text.setTextColor(getResources().getColor(R.color.primary));
        }

        if (!isMute){
            music.setImageResource(R.drawable.on);
            music_text.setText(getResources().getString(R.string.on));
            music_text.setTextColor(getResources().getColor(R.color.primary));
        }

        if (onVibrating){
            vibration.setImageResource(R.drawable.on);
            vibration_text.setText(getResources().getString(R.string.on));
            vibration_text.setTextColor(getResources().getColor(R.color.primary));
        }

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