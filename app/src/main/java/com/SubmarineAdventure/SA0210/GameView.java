package com.SubmarineAdventure.SA0210;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.Log;
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
    boolean isPlaying = true, onVibrating;
    boolean game_over, game_won, jelly_touched;
    long game_start_time = System.currentTimeMillis(), game_pause_time;
    int shark_main_update_gap = 1500, jelly_duration = 3000;
    long game_over_time, game_won_time, jelly_touched_time;
    int deduction_gap = 900;
    float oxygen_health_deduction = 0.5f;
    long oxygen_health_last_time = System.currentTimeMillis();


    private int xSpeed, ySpeed, main_xSpeed, main_ySpeed;
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
    float sub_angle = 0;
    String image_uri;
    ArrayList<ArrayList<Integer>> wh = new ArrayList<>();
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<ArrayList<Integer>> game_data = new ArrayList<>();
    ArrayList<ArrayList<Long>> game_data_two = new ArrayList<>();
    int depth = 100, time = 1000 * 60 * 3;
    int depth_gap = 15, time_gap = 1000 * 10;
    int playLevel, lastLevelActive;
    int min_x, min_y, max_x, max_y, padding;

    int joystick_radius, joystick_x, joystick_y;
    int move_w_h, move_x, move_y;
    boolean joystick_on_hold = false;
    float knob_x, knob_y;
    float last_dx = 0, last_dy = 0;
    int x_distance, y_distance;
    int pearl_amount, treasure_amount, coin_amount;
    double whole_distance = 0;
    int submarine_direction = 1;
    int main_speed, main_armor;
    int screen_in_meeter = 10;

    public GameView(Context mContext, int scX, int scY, Resources res, int level_amount) {
        super(mContext);
        screenX = scX;
        screenY = scY;
        resources = res;
        context = mContext;
        random = new Random();
        padding = screenX / 4;
        min_x = -padding;
        min_y = -padding;
        max_x = screenX + padding;
        max_y = screenY + padding;
        depth = depth + depth_gap * playLevel;
        time = time + time_gap * playLevel;

        sharedPreferences = context.getSharedPreferences("arineA5lA0210", context.MODE_PRIVATE);
        active_decor = sharedPreferences.getInt("active_decor", 0);
        image_uri = sharedPreferences.getString("image_uri", "");
        playLevel = sharedPreferences.getInt("playLevel", 1);
        lastLevelActive = sharedPreferences.getInt("lastLevelActive", 1);
        onVibrating = sharedPreferences.getBoolean("onVibrating", false);
        main_speed = sharedPreferences.getInt("body_0", 0);
        main_armor = sharedPreferences.getInt("body_1", 0);

        initialize_data(res);
        setSpeed();
        for (int i = 0; i < 10; i++)
            if (random.nextBoolean() || i < 7) add_bitmap(true);
    }

    private void setSpeed() {
        main_xSpeed = screenX / 200;
        main_ySpeed = screenY / 200;
    }

    private void add_bitmap(boolean is_initial) {
        int index = random.nextInt(bitmaps.size());
        int w = wh.get(index).get(0);
        int h = wh.get(index).get(1);
        int x = random.nextInt(max_x - min_x - w);
        int y = random.nextInt(max_y - min_y - h);
        long angel = 0;
        long time = System.currentTimeMillis();
        long gap = shark_main_update_gap + random.nextInt(shark_main_update_gap);
        long x_speed = 0;
        long y_speed = 0;

        boolean has_intersection = false;
        Rect rect = new Rect(x, y, x + w, y + h);
        if (Rect.intersects(rect, getScreenCollision()) && !is_initial) has_intersection = true;

        if (!has_intersection) {
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

            for (int i = 0; i < game_data_two.size(); i++) {
                int xx = Math.toIntExact(game_data_two.get(i).get(0));
                int yy = Math.toIntExact(game_data_two.get(i).get(1));
                int indexx = Math.toIntExact(game_data_two.get(i).get(2));
                int ww = wh.get(indexx).get(0);
                int hh = wh.get(indexx).get(1);

                Rect rect1 = new Rect(xx, yy, xx + ww, yy + hh);
                if (Rect.intersects(rect, rect1)) {
                    has_intersection = true;
                    break;
                }
            }

            if (path_object_intersection(getSubArea(), rect)) has_intersection = true;

            if (Rect.intersects(rect, getJoystickArea())) has_intersection = true;

            if (has_intersection) {
                add_bitmap(is_initial);
            } else if (index == SHARK_VALUE || index == JELLY_VALUE) {
                ArrayList<Long> data = new ArrayList<>();
                data.add((long) x);
                data.add((long) y);
                data.add((long) index);
                data.add(angel);
                data.add(time);
                data.add(gap);
                data.add(x_speed);
                data.add(y_speed);
                game_data_two.add(data);
                update_direction(game_data_two.size() - 1);
            } else {
                ArrayList<Integer> data = new ArrayList<>();
                data.add(x);
                data.add(y);
                data.add(index);
                game_data.add(data);
            }
        } else add_bitmap(is_initial);
    }

    private void update_direction(int i) {
        ArrayList<Long> data = game_data_two.get(i);
        long x = data.get(0);
        long y = data.get(1);
        long index = data.get(2);
        long angle = data.get(3);
        long time = data.get(4);
        long gap = data.get(5);
        long x_speed = data.get(6);
        long y_speed = data.get(7);

        game_data_two.get(i).set(4, System.currentTimeMillis());
        game_data_two.get(i).set(5, (long) (shark_main_update_gap + random.nextInt(shark_main_update_gap)));

        float dx = sub_x - x;
        float dy = sub_y - y;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Always update angle
        game_data_two.get(i).set(3, (long) Math.toDegrees(Math.atan2(dy, dx)));

        if (distance > 0) {
            float normalizedX = dx / distance;
            float normalizedY = dy / distance;

            // Set movement speed
            game_data_two.get(i).set(6, (long) (normalizedX * main_xSpeed * 2 / 5));
            game_data_two.get(i).set(7, (long) (normalizedY * main_ySpeed * 2 / 5));
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
        tr_h = treasure.getHeight() * scale_per_100 / 100;
        data.add(tr_w);
        data.add(tr_h);
        wh.add(data);

        data = new ArrayList<>();
        co_w = coin.getWidth() * scale_per_100 / 100;
        co_h = coin.getHeight() * scale_per_100 / 100;
        data.add(co_w);
        data.add(co_h);
        wh.add(data);

        data = new ArrayList<>();
        shi_w = shimmer.getWidth() * scale_per_100 / 100;
        shi_h = shimmer.getHeight() * scale_per_100 / 100;
        data.add(shi_w);
        data.add(shi_h);
        wh.add(data);

        data = new ArrayList<>();
        ox_w = oxygen.getWidth() * scale_per_100 / 100;
        ox_h = oxygen.getHeight() * scale_per_100 / 100;
        data.add(ox_w);
        data.add(ox_h);
        wh.add(data);

        data = new ArrayList<>();
        je_w = jelly.getWidth() * scale_per_100 / 100;
        je_h = jelly.getHeight() * scale_per_100 / 100;
        data.add(je_w);
        data.add(je_h);
        wh.add(data);

        data = new ArrayList<>();
        sha_w = shark.getWidth() * scale_per_100 / 100;
        sha_h = shark.getHeight() * scale_per_100 / 100;
        data.add(sha_w);
        data.add(sha_h);
        wh.add(data);

        data = new ArrayList<>();
        de_w = death.getWidth();
        de_h = death.getHeight();
        data.add(de_w);
        data.add(de_h);
        wh.add(data);

        data = new ArrayList<>();
        sub_w = submarine.getWidth();
        sub_h = submarine.getHeight();
        data.add(sub_w);
        data.add(sub_h);
        wh.add(data);

        sub_x = screenX * 2 / 5 + random.nextInt(screenX / 5);
        sub_y = screenY * 2 / 5 + random.nextInt(screenY / 5);

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

        for (int i = 0; i < game_data_two.size(); i++) {
            ArrayList<Long> data = game_data_two.get(i);
            long x = data.get(0);
            long y = data.get(1);
            long index = data.get(2);
            long angle = data.get(3);
            long time = data.get(4);
            long gap = data.get(5);
            long x_speed = data.get(6);
            long y_speed = data.get(7);
            int w = bitmaps.get((int) index).getWidth();
            int h = bitmaps.get((int) index).getHeight();

            if (x < 0 - w || x > screenX || y < 0 - h || y > screenY) continue;

            if (index == JELLY_VALUE) angle = 0;

            canvas.save();
            canvas.rotate(angle, x + w / 2f, y + h / 2f);
            canvas.drawBitmap(bitmaps.get((int) index), x, y, paint);
            canvas.restore();
        }

        for (int i = 0; i < game_data.size(); i++) {
            int x = game_data.get(i).get(0);
            int y = game_data.get(i).get(1);
            int index = game_data.get(i).get(2);
            int w = wh.get(index).get(0);
            int h = wh.get(index).get(1);

            if (x < 0 - w || x > screenX || y < 0 - h || y > screenY) continue;

            canvas.drawBitmap(bitmaps.get(index), x, y, paint);
        }

        canvas.save();
        canvas.scale(submarine_direction * -1, 1, sub_x + sub_w / 2, sub_y + sub_h / 2);
        canvas.drawBitmap(submarine, sub_x, sub_y, paint);
        canvas.restore();

        if (game_over) {
            int x = sub_x + sub_w / 2 - de_w / 2;
            int y = sub_y + sub_h / 2 - de_h / 2;
            canvas.drawBitmap(death, x, y, paint);
        }

        // Draw joystick base
        paint.setColor(getResources().getColor(R.color.green));
        canvas.drawCircle(joystick_x + joystick_radius, joystick_y + joystick_radius, joystick_radius, paint);

        // Draw joystick knob
        paint.setColor(getResources().getColor(R.color.dark_blue));
        canvas.drawCircle(knob_x, knob_y, joystick_radius / 2, paint);
    }

    public void update() {
        if (joystick_on_hold) {
            float dx = knob_x - (joystick_x + joystick_radius);
            float dy = knob_y - (joystick_y + joystick_radius);
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > joystick_radius * 0.2f) {
                float movementAngle = (float) Math.atan2(dy, dx);
                sub_angle = (float) Math.toDegrees(movementAngle);

                // Move in the direction of movementAngle (not angle)
                int x_speed = (int) (Math.cos(movementAngle) * main_xSpeed);
                int y_speed = (int) (Math.sin(movementAngle) * main_ySpeed);

                move_left = Integer.compare(x_speed, 0);
                move_up = Integer.compare(y_speed, 0);

                submarine_direction = move_left == 0 ? 1 : move_left;

                if (jelly_touched) {
                    x_speed = x_speed * 1 / 2;
                    y_speed = y_speed * 1 / 2;
                }

                // 6 is the maximun speed
                xSpeed = Math.abs(x_speed + x_speed * main_speed / 6);
                ySpeed = Math.abs(y_speed + y_speed * main_speed / 6);

                x_distance += x_speed;
                y_distance += y_speed;

//                whole_distance = Math.sqrt(Math.pow(x_distance, 2) + Math.pow(y_distance, 2));
                whole_distance = Math.sqrt(Math.pow(y_distance, 2));
                whole_distance = whole_distance * screen_in_meeter / screenY;

                if (whole_distance >= depth) {
                    Player.vibrate((Activity) context, onVibrating, 500);
                    game_won = true;
                    game_won_time = System.currentTimeMillis();
                }

                // Clamp to screen
                // sub_x = Math.max(0, Math.min(rat_x, screenX - rat_w));
                // sub_y = Math.max(0, Math.min(rat_y, screenY - rat_h));
            }
        }

        for (int i = 0; i < game_data_two.size(); i++) {
            ArrayList<Long> data = game_data_two.get(i);
            long x = data.get(0);
            long y = data.get(1);
            long index = data.get(2);
            long angle = data.get(3);
            long time = data.get(4);
            long gap = data.get(5);

            if (time + gap < System.currentTimeMillis()) {
                update_direction(i);
            }
        }

        for (int i = 0; i < game_data_two.size(); i++) {
            ArrayList<Long> data = game_data_two.get(i);
            long x = data.get(0);
            long y = data.get(1);
            long index = data.get(2);
            long angle = data.get(3);
            long time = data.get(4);
            long gap = data.get(5);
            long x_speed = data.get(6);
            long y_speed = data.get(7);
            x += x_speed;
            y += y_speed;

            // To make movment against the submarine
            x += xSpeed * move_left + -1;
            y += ySpeed * move_up + -1;
            game_data_two.get(i).set(0, x);
            game_data_two.get(i).set(1, y);
        }

        for (int i = 0; i < game_data_two.size(); i++) {
            ArrayList<Long> data = game_data_two.get(i);
            long x = data.get(0);
            long y = data.get(1);
            long index = data.get(2);

            if (x < min_x || x > max_x - bitmaps.get((int) index).getWidth()) {
                game_data_two.remove(i);
                add_bitmap(false);
                break;
            }

            if (y < min_y || y > max_y - bitmaps.get((int) index).getWidth()) {
                game_data_two.remove(i);
                add_bitmap(false);
                break;
            }
        }

        for (int i = 0; i < game_data.size(); i++) {
            int x = game_data.get(i).get(0);
            int y = game_data.get(i).get(1);
            int index = game_data.get(i).get(2);
            int w = wh.get(index).get(0);
            int h = wh.get(index).get(1);

            // To make movment against the submarine
            x += xSpeed * move_left;
            y += ySpeed * move_up;
            game_data.get(i).set(0, x);
            game_data.get(i).set(1, y);
        }

        for (int i = 0; i < game_data.size(); i++) {
            int x = game_data.get(i).get(0);
            int y = game_data.get(i).get(1);
            int index = game_data.get(i).get(2);
            int w = wh.get(index).get(0);
            int h = wh.get(index).get(1);

            if (x < min_x || x > max_x - w) {
                game_data.remove(i);
                add_bitmap(false);
                break;
            }

            if (y < min_y || y > max_y - h) {
                game_data.remove(i);
                add_bitmap(false);
                break;
            }
        }

        check_collision();

        if (time < System.currentTimeMillis() - game_start_time || oxygen_remain <= 0 || health_remain <= 0) {
            Player.vibrate((Activity) context, onVibrating, 500);
            game_over = true;
            game_over_time = System.currentTimeMillis();
        }

        if (jelly_touched) {
            if (jelly_touched_time + jelly_duration < System.currentTimeMillis()) {
                jelly_touched_time = 0;
                jelly_touched = false;
            }
        }

        if (oxygen_health_last_time + deduction_gap < System.currentTimeMillis()) {
            oxygen_health_last_time = System.currentTimeMillis();
            oxygen_remain -= oxygen_health_deduction * 2;
            health_remain -= oxygen_health_deduction;

            if (oxygen_remain < 0) oxygen_remain = 0;
            if (health_remain < 0) health_remain = 0;
        }

        invalidate();
    }

    private void check_collision() {
        Path path = getSubArea();
        for (int i = 0; i < game_data_two.size(); i++) {
            ArrayList<Long> data = game_data_two.get(i);
            long x = data.get(0);
            long y = data.get(1);
            long index = data.get(2);
            long angle = data.get(3);
            long time = data.get(4);
            long gap = data.get(5);
            long x_speed = data.get(6);
            long y_speed = data.get(7);

            int ww = wh.get((int) index).get(0);
            int hh = wh.get((int) index).get(1);

            Rect rect1 = new Rect((int) x, (int) y, (int) (x + ww), (int) (y + hh));
            if (path_object_intersection(path, rect1)) {
                Player.vibrate((Activity) context, onVibrating, 100);
                if (index == JELLY_VALUE) {
                    health_remain -= 5;
                    jelly_touched_time = System.currentTimeMillis();
                    jelly_touched = true;
                    game_data_two.remove(i);
                    add_bitmap(false);
                    break;
                } else if (index == SHARK_VALUE) {
                    health_remain -= 10;
                    game_data_two.remove(i);
                    add_bitmap(false);
                    break;
                }
            }
        }

        for (int i = 0; i < game_data.size(); i++) {
            int x = game_data.get(i).get(0);
            int y = game_data.get(i).get(1);
            int index = game_data.get(i).get(2);
            int w = wh.get(index).get(0);
            int h = wh.get(index).get(1);

            Rect rect1 = new Rect(x, y, x + w, y + h);
            if (path_object_intersection(path, rect1)) {
                if (index == PEARL_VALUE) {
                    pearl_amount++;
                    game_data.remove(i);
                    add_bitmap(false);
                    break;
                } else if (index == TREASURE_VALUE) {
                    treasure_amount++;
                    game_data.remove(i);
                    add_bitmap(false);
                    break;
                } else if (index == COIN_VALUE) {
                    coin_amount++;
                    game_data.remove(i);
                    add_bitmap(false);
                    break;
                } else if (index == SHIMMER_VALUE) {
                    // do nothing for now
                    health_remain += 5;
                    if (health_remain > 100) health_remain = 100;
                    game_data.remove(i);
                    add_bitmap(false);
                    break;
                } else if (index == OXYGEN_VALUE) {
                    oxygen_remain += 5;
                    if (oxygen_remain > 100) oxygen_remain = 100;
                    game_data.remove(i);
                    add_bitmap(false);
                    break;
                }
            }
        }
    }

    public Rect getKnobCollision() {
        int r = joystick_radius / 2;
        return new Rect((int) (knob_x - r), (int) (knob_y - r), (int) (knob_x + r), (int) (knob_y + r));
    }

    public Rect getJoystickArea() {
        return new Rect(joystick_x, joystick_y, joystick_x + joystick_radius * 2, joystick_y + joystick_radius * 2);
    }

    public Rect getScreenCollision() {
        return new Rect(0, 0, screenX, screenY);
    }
//
//    public Rect getSubmarineCollision() {
//        int mw = sub_w / 6;
//        int mh = sub_h / 6;
//
//        return new Rect(sub_x + mw, sub_y + mh, sub_x + sub_w - mw, sub_y + sub_h - mh);
//    }

    public Path getSubArea() {
        int mw = sub_w / 6;
        int mh = sub_h / 6;

        int[] xy0 = xyPrimePoint(sub_x + mw, sub_y + mh, (int) sub_angle, sub_x + sub_w / 2, sub_y + sub_h / 2);
        int[] xy1 = xyPrimePoint(sub_x - mw + sub_w, sub_y + mh, (int) sub_angle, sub_x + sub_w / 2, sub_y + sub_h / 2);
        int[] xy2 = xyPrimePoint(sub_x - mw + sub_w, sub_y - mh + sub_h, (int) sub_angle, sub_x + sub_w / 2, sub_y + sub_h / 2);
        int[] xy3 = xyPrimePoint(sub_x + mw, sub_y + sub_h - mh, (int) sub_angle, sub_x + sub_w / 2, sub_y + sub_h / 2);
        Path path = new Path();
        path.moveTo(xy0[0], xy0[1]);
        path.lineTo(xy1[0], xy1[1]);
        path.lineTo(xy2[0], xy2[1]);
        path.lineTo(xy3[0], xy3[1]);

        return path;
    }


    private boolean path_object_intersection(Path path, Rect object) {
        boolean path_object_intersection = false;
        PathMeasure pm = new PathMeasure(path, false);
        float distance = 0f;
        float speed = pm.getLength() / 10;
        float[] aCoordinates = new float[2];

        while (distance < pm.getLength()) {
            pm.getPosTan(distance, aCoordinates, null);
            int xc = (int) aCoordinates[0];
            int yc = (int) aCoordinates[1];

            Rect rect = new Rect(xc, yc, xc, yc);
            if (Rect.intersects(rect, object)) {
                path_object_intersection = true;
                break;
            }
            distance = distance + speed;
        }

        return path_object_intersection;
    }

    public int[] xyPrimePoint(int x, int y, int angle, int cx, int cy) {
        double radians = Math.toRadians(angle);

        int x1 = x - cx;
        int y1 = y - cy;
        int x2 = (int) (x1 * Math.cos(radians) - y1 * Math.sin(radians));
        int y2 = (int) (x1 * Math.sin(radians) + y1 * Math.cos(radians));

        x = x2 + cx;
        y = y2 + cy;

        return new int[]{x, y};
    }
}