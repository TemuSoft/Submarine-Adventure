package com.SubmarineAdventure.SA0210;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {
    private ImageView pause;
    private TextView time;
    private ProgressBar oxygen, health;
    private LinearLayout layout_canvas, layout_main, layout_pause_over;

    private ImageView back, shop;
    private TextView active_level, all_coin, status, game_coin, button_text;
    private LinearLayout next_again;

    private LayoutInflater inflate;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private String lang;
    private AlertDialog.Builder builder;
    private Random random;
    private Handler handler;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("arineA5lA0210", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lang = sharedPreferences.getString("lang", "");

        setContentView(R.layout.activity_game);

        builder = new AlertDialog.Builder(this);
        random = new Random();
        handler = new Handler();

        pause = findViewById(R.id.pause);
        oxygen = findViewById(R.id.oxygen);
        health = findViewById(R.id.health);
        time = findViewById(R.id.time);

        back = findViewById(R.id.back);
        shop = findViewById(R.id.shop);
        active_level = findViewById(R.id.active_level);
        all_coin = findViewById(R.id.all_coin);
        game_coin = findViewById(R.id.game_coin);
        status = findViewById(R.id.status);
        button_text = findViewById(R.id.button_text);
        next_again = findViewById(R.id.next_again);

        layout_canvas = findViewById(R.id.layout_canvas);
        layout_main = findViewById(R.id.layout_main);
        layout_pause_over = findViewById(R.id.layout_pause_over);
        inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        pause.setOnClickListener(view -> {
            Player.button(soundMute);
            pauseDialog();
        });

        layout_canvas.removeAllViews();
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        int w = point.x;
        int h = point.y;
        gameView = new GameView(this, w, h, getResources(), 0);
        gameView.setLayoutParams(new LinearLayout.LayoutParams(w, h));
        layout_canvas.addView(gameView);

        layout_canvas.setOnTouchListener(this);
    }


    private void reloading_UI() {
        Runnable r = new Runnable() {
            public void run() {
                if (gameView.isPlaying) {
                    if (!gameView.game_over) {
                        gameView.update();
                        time.setText(Player.formatTime(System.currentTimeMillis() - gameView.game_start_time));
                        oxygen.setProgress((int) gameView.oxygen_remain);
                        health.setProgress((int) gameView.health_remain);
                    }

                    if (gameView.game_over && gameView.game_over_time + 1000 < System.currentTimeMillis())
                        game_over();

                    if (gameView.game_won && gameView.game_won_time + 1000 < System.currentTimeMillis())
                        game_won();

                    reloading_UI();
                }
            }
        };
        handler.postDelayed(r, 20);
    }

    public void pauseDialog() {
        gameView.isPlaying = false;
        layout_pause_over.setVisibility(VISIBLE);

    }


    private void game_over() {
        gameView.isPlaying = false;
        layout_pause_over.setVisibility(VISIBLE);

    }

    private void game_won() {
        gameView.isPlaying = false;
        layout_pause_over.setVisibility(VISIBLE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.isPlaying = false;
        gameView.game_pause_time = System.currentTimeMillis();
        if (!isMute)
            Player.all_screens.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.isPlaying = true;
        isMute = sharedPreferences.getBoolean("isMute", false);
        if (gameView.game_pause_time != 0) {
            long gap = System.currentTimeMillis() - gameView.game_pause_time;
            gameView.game_start_time += gap;
            gameView.game_pause_time = 0;
        }
        reloading_UI();

        if (!isMute)
            Player.all_screens.start();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!gameView.game_over) processActionDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (gameView.joystick_on_hold) processActionMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (gameView.joystick_on_hold) processActionUp(x, y);
                break;
        }
        return true;
    }

    private void processActionDown(int x, int y) {
        Rect clicked = new Rect(x, y, x, y);

        if (Rect.intersects(clicked, gameView.getKnobCollision())) {
            gameView.joystick_on_hold = true;
        }
    }

    private void processActionUp(int xp, int yp) {
        gameView.joystick_on_hold = false;
        gameView.knob_x = gameView.joystick_x + gameView.joystick_radius;
        gameView.knob_y = gameView.joystick_y + gameView.joystick_radius;
        gameView.last_dx = 0;
        gameView.last_dy = 0;
    }


    private void processActionMove(int x, int y) {
        float centerX = gameView.joystick_x + gameView.joystick_radius;
        float centerY = gameView.joystick_y + gameView.joystick_radius;

        float dx = x - centerX;
        float dy = y - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < gameView.joystick_radius) {
            gameView.knob_x = x;
            gameView.knob_y = y;
        } else {
            float angle = (float) Math.atan2(dy, dx);
            gameView.knob_x = centerX + gameView.joystick_radius * (float) Math.cos(angle);
            gameView.knob_y = centerY + gameView.joystick_radius * (float) Math.sin(angle);
        }

        gameView.last_dx = (gameView.knob_x - centerX) / gameView.joystick_radius;
        gameView.last_dy = (gameView.knob_y - centerY) / gameView.joystick_radius;
    }

}