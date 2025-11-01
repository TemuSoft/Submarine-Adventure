package com.SubmarineAdventure.SA0210;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
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
    private TextView active_level, all_coin, status, game_coin, button_text, next_opened;
    private LinearLayout next_again, layout_coin;

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
    private int available_coin, last_game_played;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("arineA5lA0210", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lang = sharedPreferences.getString("lang", "");
        available_coin = sharedPreferences.getInt("available_coin", 0);
        last_game_played = sharedPreferences.getInt("last_game_played", 0);

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
        layout_coin = findViewById(R.id.layout_coin);
        next_opened = findViewById(R.id.next_opened);

        layout_canvas = findViewById(R.id.layout_canvas);
        layout_main = findViewById(R.id.layout_main);
        layout_pause_over = findViewById(R.id.layout_pause_over);
        inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        pause.setOnClickListener(view -> {
            Player.button(soundMute);
            pauseDialog();
        });

        back.setOnClickListener(View -> {
            Player.button(soundMute);
            finish();
        });

        shop.setOnClickListener(view -> {
            Player.button(soundMute);
            intent = new Intent(GameActivity.this, ShopActivity.class);
            startActivity(intent);
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

        active_level.setText(getResources().getString(R.string.level) + " " + gameView.playLevel);
        all_coin.setText(available_coin + "");
    }


    private void reloading_UI() {
        Runnable r = new Runnable() {
            public void run() {
                if (gameView.isPlaying) {
                    if (!gameView.game_over && !gameView.game_won) {
                        gameView.update();
                        time.setText(Player.formatTime(System.currentTimeMillis() - gameView.game_start_time));
                        oxygen.setProgress((int) gameView.oxygen_remain);
                        health.setProgress((int) gameView.health_remain);
                    }

                    if (gameView.game_won && gameView.game_won_time + 1000 < System.currentTimeMillis())
                        game_won();
                    else if (gameView.game_over && gameView.game_over_time + 1000 < System.currentTimeMillis())
                        game_over();


                    reloading_UI();
                }
            }
        };
        handler.postDelayed(r, 20);
    }

    public void pauseDialog() {
        gameView.isPlaying = false;
        layout_main.setVisibility(GONE);
        layout_canvas.setVisibility(INVISIBLE);
        layout_pause_over.setVisibility(VISIBLE);

        game_coin.setText("+" + gameView.coin_amount);
        status.setText(getResources().getString(R.string.game_paused));
        button_text.setText(getResources().getString(R.string.resume));
        next_again.setOnClickListener(View -> {
            Player.button(soundMute);

            gameView.isPlaying = true;
            layout_main.setVisibility(VISIBLE);
            layout_canvas.setVisibility(VISIBLE);
            layout_pause_over.setVisibility(GONE);

            reloading_UI();
        });
        next_opened.setVisibility(INVISIBLE);
        layout_coin.setVisibility(INVISIBLE);
    }


    private void game_over() {
        gameView.isPlaying = false;
        layout_main.setVisibility(GONE);
        layout_canvas.setVisibility(INVISIBLE);
        layout_pause_over.setVisibility(VISIBLE);
        gameView.coin_amount = 0;

        game_coin.setText("+" + gameView.coin_amount);
        status.setText(getResources().getString(R.string.level_failed));
        button_text.setText(getResources().getString(R.string.play_again));
        next_again.setOnClickListener(View -> {
            Player.button(soundMute);

            intent = new Intent(GameActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });
        next_opened.setVisibility(INVISIBLE);
        layout_coin.setVisibility(VISIBLE);
    }

    private void game_won() {
        gameView.isPlaying = false;
        layout_main.setVisibility(GONE);
        layout_canvas.setVisibility(INVISIBLE);
        layout_pause_over.setVisibility(VISIBLE);

        if (gameView.playLevel == 35) {
            next_again.setAlpha(0.3F);
            next_again.setEnabled(false);
            next_opened.setVisibility(INVISIBLE);
        }else {
            next_opened.setText("Level " + (gameView.playLevel + 1) + " is now Opened");
            next_opened.setVisibility(VISIBLE);
        }

        gameView.playLevel++;
        if (gameView.playLevel > 35) gameView.playLevel = 35;
        if (gameView.lastLevelActive < gameView.playLevel) {
            gameView.lastLevelActive = gameView.playLevel;
        }

        if (gameView.whole_distance >= 500) editor.putBoolean("dive_500m_achieved", true);
        editor.putInt("whole_distance_" + last_game_played, (int) gameView.whole_distance);
        editor.putInt("last_game_played", last_game_played + 1);
        editor.putInt("playLevel", gameView.playLevel);
        editor.putInt("lastLevelActive", gameView.lastLevelActive);
        editor.putInt("available_coin", available_coin + gameView.coin_amount);
        editor.putInt("pearl_amount", sharedPreferences.getInt("pearl_amount", 0) + gameView.pearl_amount);
        editor.putInt("treasure_amount", sharedPreferences.getInt("treasure_amount", 0) + gameView.treasure_amount);
        editor.putInt("coin_amount", sharedPreferences.getInt("coin_amount", 0) + gameView.coin_amount);
        editor.apply();

        game_coin.setText("+" + gameView.coin_amount);
        status.setText(getResources().getString(R.string.level_completed));
        button_text.setText(getResources().getString(R.string.next_level));
        next_again.setOnClickListener(View -> {
            Player.button(soundMute);

            intent = new Intent(GameActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });
        layout_coin.setVisibility(VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.isPlaying = false;
        gameView.game_pause_time = System.currentTimeMillis();
        if (!isMute) Player.all_screens.pause();
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

        if (!isMute) Player.all_screens.start();
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
                if (!gameView.game_over && !gameView.game_won) processActionDown(x, y);
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
        gameView.move_left = 0;
        gameView.move_up = 0;
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