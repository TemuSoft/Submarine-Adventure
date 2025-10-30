package com.SubmarineAdventure.SA0210;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    private SharedPreferences sharedPreferences;
    private int screenX, screenY;
    private Resources resources;
    private Random random;
    boolean isPlaying = true;
    boolean game_ovr, game_won;
    long game_start_time = System.currentTimeMillis(), game_pause_time;

    int score;
    private int xSpeed, ySpeed;
    private Context context;

    int[] subs = new int[]{R.drawable.decor_0, R.drawable.decor_1, R.drawable.decor_2, R.drawable.decor_3, R.drawable.decor_4};
    int PEARL_VALUE = 0, TREASURE_VALUE = 1, COIN_VALUE = 2, SHIMMER_VALUE = 3, OXYGEN_VALUE = 4, JELLY_VALUE = 5, SHARK_VALUE = 6;
    int scale_per_100 = 70;
    float health_remain = 100, oxygen_remain = 100;
    Bitmap pearl, treasure, coin, shimmer, oxygen, jelly, shark, death, submarine;
    int pr_w, pr_h, tr_w, tr_h;
    int co_w, co_h, shi_w, shi_h;
    int ox_w, ox_h, je_w, je_h;
    int sha_w, sha_h, de_w, de_h;
    int sub_x, sub_y, sub_w, sub_h;
    int move_left, move_up;
    int active_decor;
    String image_uri;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<ArrayList<Integer>> game_data = new ArrayList<>();

    public GameView(Context mContext, int scX, int scY, Resources res, int level_amount) {
        super(mContext);
        screenX = scX;
        screenY = scY;
        resources = res;
        context = mContext;
        random = new Random();

        sharedPreferences = context.getSharedPreferences("arineA5lA0210", context.MODE_PRIVATE);
        active_decor = sharedPreferences.getInt("active_decor", 0);
        image_uri = sharedPreferences.getString("image_uri", "");

        initialize_data(res);
        setSpeed();
    }

    private void initialize_data(Resources res) {
        pearl = BitmapFactory.decodeResource(res, R.drawable.pearl);
        treasure = BitmapFactory.decodeResource(res, R.drawable.treasure);
        coin = BitmapFactory.decodeResource(res, R.drawable.coin);
        shimmer = BitmapFactory.decodeResource(res, R.drawable.shimmer);
        oxygen = BitmapFactory.decodeResource(res, R.drawable.oxygen);
        jelly = BitmapFactory.decodeResource(res, R.drawable.jelly);
        shark = BitmapFactory.decodeResource(res, R.drawable.shark);
        death = BitmapFactory.decodeResource(res, R.drawable.death);
        submarine = BitmapFactory.decodeResource(res, subs[active_decor]);
        if (active_decor == 4) {
            try {
                URL url = new URL(image_uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                submarine = BitmapFactory.decodeStream(input);
            } catch (Exception e) {

            }
        }

        pr_w = pearl.getWidth() * scale_per_100 / 100;
        pr_h = pearl.getHeight() * scale_per_100 / 100;

        tr_w = treasure.getWidth() * scale_per_100 / 100;
        tr_h = treasure.getWidth() * scale_per_100 / 100;

        co_w = coin.getWidth() * scale_per_100 / 100;
        co_h = coin.getWidth() * scale_per_100 / 100;

        shi_w = shimmer.getWidth() * scale_per_100 / 100;
        shi_h = shimmer.getWidth() * scale_per_100 / 100;

        ox_w = oxygen.getWidth() * scale_per_100 / 100;
        ox_h = oxygen.getWidth() * scale_per_100 / 100;

        je_w = jelly.getWidth() * scale_per_100 / 100;
        je_h = jelly.getWidth() * scale_per_100 / 100;

        sha_w = shark.getWidth() * scale_per_100 / 100;
        sha_h = shark.getWidth() * scale_per_100 / 100;

        de_w = death.getWidth();
        de_h = death.getWidth();

        sub_w = submarine.getWidth() * scale_per_100 / 100;
        sub_h = submarine.getWidth() * scale_per_100 / 100;

        sub_x = random.nextInt(screenX - sub_w * 5) + sub_w * 2;
        sub_y = random.nextInt(screenX - sub_h * 7) + sub_h * 2;

        pearl = Bitmap.createScaledBitmap(pearl, pr_w, pr_h, false);
        treasure = Bitmap.createScaledBitmap(treasure, tr_w, tr_h, false);
        coin = Bitmap.createScaledBitmap(coin, co_w, co_h, false);
        shimmer = Bitmap.createScaledBitmap(shimmer, shi_w, shi_h, false);
        oxygen = Bitmap.createScaledBitmap(oxygen, ox_w, ox_h, false);
        jelly = Bitmap.createScaledBitmap(jelly, je_w, je_h, false);
        shark = Bitmap.createScaledBitmap(shark, sha_w, sha_h, false);
        death = Bitmap.createScaledBitmap(death, de_w, de_h, false);
        submarine = Bitmap.createScaledBitmap(submarine, sub_w, sub_h, false);

        bitmaps.add(pearl);
        bitmaps.add(treasure);
        bitmaps.add(coin);
        bitmaps.add(shimmer);
        bitmaps.add(oxygen);
        bitmaps.add(jelly);
        bitmaps.add(shark);
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.TRANSPARENT);


        canvas.save();
        canvas.scale(move_left, 1, sub_x + sub_w / 2, sub_y + sub_h / 2);
        canvas.drawBitmap(submarine, sub_x, sub_y, paint);
        canvas.restore();

    }

    private void setSpeed() {
        xSpeed = screenX / 80;
        ySpeed = screenY / 80;
    }

    public void update() {

        invalidate();
    }

    public Rect getSubmarineCollision(){
        int mw = sub_w / 6;
        int mh = sub_h / 6;

        return new Rect(sub_x + mw, sub_y + mh, sub_x + sub_w - mw, sub_y + sub_h - mh);
    }
}