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
    boolean game_over, game_won;
    long game_start_time = System.currentTimeMillis(), game_pause_time;
    int shark_main_update_gap = 1500;
    long game_over_time, game_won_time;

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
    ArrayList<ArrayList<Integer>> wh = new ArrayList<>();
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<ArrayList<Integer>> game_data = new ArrayList<>();
    ArrayList<ArrayList<Long>> shark_data = new ArrayList<>();


    int joystick_radius, joystick_x, joystick_y;
    int move_w_h, move_x, move_y;
    boolean joystick_on_hold = false;
    float knob_x, knob_y;
    float last_dx = 0, last_dy = 0;
    float rat_angle = 0;

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
        for (int i = 0; i < 10; i++)
            if (random.nextBoolean() || i < 6) add_bitmap();
    }

    private void add_bitmap() {
        int index = random.nextInt(bitmaps.size());
        int w = wh.get(index).get(0);
        int h = wh.get(index).get(1);
        int x = random.nextInt(screenX - w);
        int y = random.nextInt(screenY - h);
        long angel = 0;
        long time = System.currentTimeMillis();
        long gap = shark_main_update_gap + random.nextInt(shark_main_update_gap);
        long x_speed = 0;
        long y_speed = 0;

        boolean has_intersection = false;
        Rect rect = new Rect(x, y, x + w, y + h);
        for (int i = 0; i < game_data.size(); i++) {
            int xx = game_data.get(i).get(0);
            int yy = game_data.get(i).get(1);
            int indexx = game_data.get(i).get(2);
            int ww = wh.get(indexx).get(0);
            int hh = wh.get(indexx).get(1);

            Rect rect1 = new Rect(xx, yy, xx + ww, yy + hh);
            if (Rect.intersects(rect, rect1)) {
                has_intersection = true;
                break;
            }
        }

        for (int i = 0; i < shark_data.size(); i++) {
            int xx = Math.toIntExact(shark_data.get(i).get(0));
            int yy = Math.toIntExact(shark_data.get(i).get(1));
            int ww = wh.get(SHARK_VALUE).get(0);
            int hh = wh.get(SHARK_VALUE).get(1);

            Rect rect1 = new Rect(xx, yy, xx + ww, yy + hh);
            if (Rect.intersects(rect, rect1)) {
                has_intersection = true;
                break;
            }
        }

        if (Rect.intersects(rect, getSubmarineCollision())) has_intersection = true;

        if (Rect.intersects(rect, getJoystickArea())) has_intersection = true;

        if (has_intersection) {
            add_bitmap();
        } else if (index == SHARK_VALUE) {
            ArrayList<Long> data = new ArrayList<>();
            data.add((long) x);
            data.add((long) y);
            data.add(angel);
            data.add(time);
            data.add(gap);
            data.add(x_speed);
            data.add(y_speed);
            shark_data.add(data);
            update_cat_direction(shark_data.size() - 1);
        } else {
            ArrayList<Integer> data = new ArrayList<>();
            data.add(x);
            data.add(y);
            data.add(index);
            game_data.add(data);
        }
    }

    private void update_cat_direction(int i) {
        ArrayList<Long> data = shark_data.get(i);
        long x = data.get(0);
        long y = data.get(1);
        long angle = data.get(2);
        long time = data.get(3);
        long gap = data.get(4);
        long x_speed = data.get(5);
        long y_speed = data.get(6);

        shark_data.get(i).set(3, System.currentTimeMillis());
        shark_data.get(i).set(4, (long) (shark_main_update_gap + random.nextInt(shark_main_update_gap)));

        float dx = sub_x - x;
        float dy = sub_y - y;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Always update angle
        shark_data.get(i).set(2, (long) Math.toDegrees(Math.atan2(dy, dx)));

        if (distance > 0) {
            float normalizedX = dx / distance;
            float normalizedY = dy / distance;

            // Set movement speed
            shark_data.get(i).set(5, (long) (normalizedX * xSpeed * 2 / 5));
            shark_data.get(i).set(6, (long) (normalizedY * ySpeed * 2 / 5));
        }
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

        ArrayList<Integer> data = new ArrayList<>();
        pr_w = pearl.getWidth() * scale_per_100 / 100;
        pr_h = pearl.getHeight() * scale_per_100 / 100;
        data.add(pr_w);
        data.add(pr_h);
        wh.add(data);

        data = new ArrayList<>();
        tr_w = treasure.getWidth() * scale_per_100 / 100;
        tr_h = treasure.getWidth() * scale_per_100 / 100;
        data.add(tr_w);
        data.add(tr_h);
        wh.add(data);

        data = new ArrayList<>();
        co_w = coin.getWidth() * scale_per_100 / 100;
        co_h = coin.getWidth() * scale_per_100 / 100;
        data.add(co_w);
        data.add(co_h);
        wh.add(data);

        data = new ArrayList<>();
        shi_w = shimmer.getWidth() * scale_per_100 / 100;
        shi_h = shimmer.getWidth() * scale_per_100 / 100;
        data.add(shi_w);
        data.add(shi_h);
        wh.add(data);

        data = new ArrayList<>();
        ox_w = oxygen.getWidth() * scale_per_100 / 100;
        ox_h = oxygen.getWidth() * scale_per_100 / 100;
        data.add(ox_w);
        data.add(ox_h);
        wh.add(data);

        data = new ArrayList<>();
        je_w = jelly.getWidth() * scale_per_100 / 100;
        je_h = jelly.getWidth() * scale_per_100 / 100;
        data.add(je_w);
        data.add(je_h);
        wh.add(data);

        data = new ArrayList<>();
        sha_w = shark.getWidth() * scale_per_100 / 100;
        sha_h = shark.getWidth() * scale_per_100 / 100;
        data.add(sha_w);
        data.add(sha_h);
        wh.add(data);

        data = new ArrayList<>();
        de_w = death.getWidth();
        de_h = death.getWidth();
        data.add(de_w);
        data.add(de_h);
        wh.add(data);

        data = new ArrayList<>();
        sub_w = submarine.getWidth() * scale_per_100 / 100;
        sub_h = submarine.getWidth() * scale_per_100 / 100;
        data.add(sub_w);
        data.add(sub_h);
        wh.add(data);

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

        joystick_radius = screenX / 10;
        joystick_x = screenX / 2 - joystick_radius;
        joystick_y = screenY - joystick_radius * 3;
        move_w_h = joystick_radius * 4;
        move_x = joystick_x - joystick_radius;
        move_y = joystick_y - joystick_radius;
        knob_x = joystick_x + joystick_radius;
        knob_y = joystick_y + joystick_radius;
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.TRANSPARENT);

        // Draw joystick base
        paint.setColor(getResources().getColor(R.color.green));
        canvas.drawCircle(joystick_x + joystick_radius, joystick_y + joystick_radius, joystick_radius, paint);

        // Draw joystick knob
        paint.setColor(getResources().getColor(R.color.dark_blue));
        canvas.drawCircle(knob_x, knob_y, joystick_radius / 2, paint);

        for (int i = 0; i < shark_data.size(); i++) {
            long x = shark_data.get(i).get(0);
            long y = shark_data.get(i).get(1);
            long angle = shark_data.get(i).get(2);
            long time = shark_data.get(i).get(3);
            long gap = shark_data.get(i).get(4);
            long x_speed = shark_data.get(i).get(5);
            long y_speed = shark_data.get(i).get(6);

            canvas.save();
            canvas.rotate(angle, x + sha_w / 2f, y + sha_h / 2f);
            canvas.drawBitmap(bitmaps.get(SHARK_VALUE), x, y, paint);
            canvas.restore();
        }

        for (int i = 0; i < game_data.size(); i++) {
            int x = game_data.get(i).get(0);
            int y = game_data.get(i).get(1);
            int index = game_data.get(i).get(2);
            int w = wh.get(index).get(0);
            int h = wh.get(index).get(1);

            canvas.drawBitmap(bitmaps.get(index), x, y, paint);
        }

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
        for (int i = 0; i < shark_data.size(); i++) {
            long x = shark_data.get(i).get(0);
            long y = shark_data.get(i).get(1);
            long angle = shark_data.get(i).get(2);
            long time = shark_data.get(i).get(3);
            long gap = shark_data.get(i).get(4);
            long x_speed = shark_data.get(i).get(5);
            long y_speed = shark_data.get(i).get(6);
        }


        for (int i = 0; i < game_data.size(); i++) {
            int x = game_data.get(i).get(0);
            int y = game_data.get(i).get(1);
            int index = game_data.get(i).get(2);
            int w = wh.get(index).get(0);
            int h = wh.get(index).get(1);
        }

        invalidate();
    }


    public Rect getKnobCollision() {
        int r = joystick_radius / 2;
        return new Rect((int) (knob_x - r), (int) (knob_y - r), (int) (knob_x + r), (int) (knob_y + r));
    }

    public Rect getJoystickArea() {
        return new Rect(joystick_x, joystick_y, joystick_x + joystick_radius * 2, joystick_y + joystick_radius * 2);
    }

    public Rect getSubmarineCollision() {
        int mw = sub_w / 6;
        int mh = sub_h / 6;

        return new Rect(sub_x + mw, sub_y + mh, sub_x + sub_w - mw, sub_y + sub_h - mh);
    }
}