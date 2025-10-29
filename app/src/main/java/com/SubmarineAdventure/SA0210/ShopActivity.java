package com.SubmarineAdventure.SA0210;

import static android.view.View.GONE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {
    private LinearLayout layout_vertical;
    private ImageView back;
    private TextView coin;
    private Button btn_body, btn_decor;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isMute, soundMute;
    private Intent intent;
    private String lang, image_uri;
    private LayoutInflater inflate;
    private int available_coin;
    private int[] body = new int[]{R.drawable.body_0, R.drawable.body_1, R.drawable.body_2, R.drawable.body_3, R.drawable.body_4};
    private int active_body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(getResources().getColor(R.color.trans));
        sharedPreferences = getSharedPreferences("hienArsen12o8", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lang = sharedPreferences.getString("lang", "");
        available_coin = sharedPreferences.getInt("available_coin", 0);
        active_body = sharedPreferences.getInt("active_body", 0);
        image_uri = sharedPreferences.getString("image_uri", "");

        setContentView(R.layout.activity_shop);

        layout_vertical = findViewById(R.id.layout_vertical);
        inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        back = findViewById(R.id.back);
        coin = findViewById(R.id.coin);
        btn_body = findViewById(R.id.btn_body);
        btn_decor = findViewById(R.id.btn_decor);

        back.setOnClickListener(view -> {
            Player.button(soundMute);
            finish();
        });

        btn_body.setOnClickListener(View -> {
            Player.button(soundMute);
            update_body_UI();
        });

        btn_decor.setOnClickListener(View -> {
            Player.button(soundMute);
            update_decor_UI();
        });

        update_body_UI();
    }

    private void update_body_UI() {
        coin.setText(available_coin + "");

        layout_vertical.removeAllViews();
        ArrayList<Boolean> bought_body = new ArrayList<>();
        bought_body.add(true);
        for (int i = 1; i < 5; i++)
            bought_body.add(sharedPreferences.getBoolean("bought_body_" + i, false));

        int[] coins = new int[]{0, 25, 50, 80, 120, 150};

        View horizontal = inflate.inflate(R.layout.horizontal, null);
        LinearLayout layout_horizontal = horizontal.findViewById(R.id.horizontal);
        layout_horizontal.removeAllViews();
        for (int i = 0; i < 5; i++) {
            if (i % 3 == 0) {
                horizontal = inflate.inflate(R.layout.horizontal, null);
                layout_horizontal = horizontal.findViewById(R.id.horizontal);
                layout_horizontal.removeAllViews();
            }

            View shop_card = inflate.inflate(R.layout.card_shop, null);

            LinearLayout layout_card = shop_card.findViewById(R.id.layout_card);
            LinearLayout layout_buy = shop_card.findViewById(R.id.layout_buy);
            ImageView image = shop_card.findViewById(R.id.image);
            TextView name = shop_card.findViewById(R.id.name);
            ImageView coin = shop_card.findViewById(R.id.coin);
            TextView amount_but = shop_card.findViewById(R.id.amount_but);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            layout_card.setLayoutParams(params);

            image.setImageResource(body[i]);

            if (i == 4) {
                if (!image_uri.isEmpty()) image.setImageURI(Uri.parse(image_uri));
                coin.setVisibility(GONE);
                amount_but.setText("Set photo");
            } else {
                amount_but.setText(coins[i] + "");
            }

            int finalCounter = i;
            if (bought_body.get(i)) {
                if (i == i) name.setText(getResources().getString(R.string.selected));
                else name.setText(getResources().getString(R.string.select));

                image.setOnClickListener(View -> {
                    Player.button(soundMute);

                    active_body = finalCounter;
                    editor.putInt("active_body", active_body);
                    editor.apply();

                    update_body_UI();
                });
            } else {
                name.setText(getResources().getString(R.string.buy));
                if (coins[i] <= available_coin) {
                    layout_buy.setOnClickListener(View -> {
                        Player.button(soundMute);

                        available_coin -= coins[finalCounter];
                        active_body = finalCounter;
                        editor.putInt("active_body", active_body);
                        editor.putInt("available_coin", available_coin);
                        editor.apply();

                        update_body_UI();
                    });
                }
            }

            layout_vertical.addView(horizontal);
        }
    }

    private void update_decor_UI() {
        layout_vertical.removeAllViews();
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