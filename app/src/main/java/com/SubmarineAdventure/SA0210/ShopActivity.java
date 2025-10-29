package com.SubmarineAdventure.SA0210;

import static android.view.View.GONE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
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
    private int[] decor = new int[]{R.drawable.decor_0, R.drawable.decor_1, R.drawable.decor_2, R.drawable.decor_3, R.drawable.decor_4};
    private int active_decor;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 101;
    private Uri cameraImageUri;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

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
        active_decor = sharedPreferences.getInt("active_decor", 0);
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
            btn_body.setBackgroundResource(R.drawable.btn_blue);
            btn_decor.setBackgroundResource(R.drawable.btn_gray);
            update_body_UI();
        });

        btn_decor.setOnClickListener(View -> {
            Player.button(soundMute);
            btn_body.setBackgroundResource(R.drawable.btn_gray);
            btn_decor.setBackgroundResource(R.drawable.btn_blue);
            update_decor_UI();
        });

        update_body_UI();
    }

    private void update_body_UI() {
        coin.setText(available_coin + "");
        layout_vertical.removeAllViews();

        int[] body = new int[]{sharedPreferences.getInt("body_0", 0), sharedPreferences.getInt("body_1", 0)};
        int[] values = new int[]{25, 1};
        String[] names = new String[]{"Speed", "Armor"};
        int required_coin = 30;

        View horizontal = null;
        LinearLayout layout_horizontal = null;
        for (int i = 0; i < 2; i++) {
            if (i % 2 == 0) {
                horizontal = inflate.inflate(R.layout.horizontal, null);
                layout_horizontal = horizontal.findViewById(R.id.horizontal);
                layout_horizontal.removeAllViews();
            }

            View shop_card = inflate.inflate(R.layout.card_body, null);

            LinearLayout layout_card = shop_card.findViewById(R.id.layout_card);
            LinearLayout layout_buy = shop_card.findViewById(R.id.layout_buy);
            TextView name = shop_card.findViewById(R.id.name);
            TextView value = shop_card.findViewById(R.id.value);
            ImageView coin = shop_card.findViewById(R.id.coin);
            TextView amount_but = shop_card.findViewById(R.id.amount_but);

            name.setText(names[i]);
            value.setText("+" + values[i]);
            amount_but.setText(required_coin + "");

            ArrayList<ImageView> rects = new ArrayList<>();
            ImageView rect_1 = shop_card.findViewById(R.id.rect_1);
            ImageView rect_2 = shop_card.findViewById(R.id.rect_2);
            ImageView rect_3 = shop_card.findViewById(R.id.rect_3);
            ImageView rect_4 = shop_card.findViewById(R.id.rect_4);
            ImageView rect_5 = shop_card.findViewById(R.id.rect_5);
            ImageView rect_6 = shop_card.findViewById(R.id.rect_6);
            rects.add(rect_1);
            rects.add(rect_2);
            rects.add(rect_3);
            rects.add(rect_4);
            rects.add(rect_5);
            rects.add(rect_6);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            layout_card.setLayoutParams(params);

            for (int j = 1; j <= rects.size(); j++) {
                if (j <= body[i]) rects.get(j - 1).setImageResource(R.drawable.active_rect);
                else rects.get(j - 1).setImageResource(R.drawable.inactive_rect);
            }

            if (body[i] == 6 || required_coin > available_coin) {
                layout_buy.setEnabled(false);
                layout_buy.setAlpha(0.3f);
            }

            int finalI = i;
            layout_buy.setOnClickListener(View -> {
                Player.button(soundMute);

                available_coin -= required_coin;
                editor.putInt("available_coin", available_coin);
                editor.putInt("body_" + finalI, body[finalI] + 1);
                editor.apply();

                update_body_UI();
            });

            layout_horizontal.addView(shop_card);

            if (i == 1) layout_vertical.addView(horizontal);
        }
    }

    private void update_decor_UI() {
        coin.setText(available_coin + "");
        layout_vertical.removeAllViews();

        ArrayList<Boolean> bought_decor = new ArrayList<>();
        bought_decor.add(true);
        for (int i = 1; i < 4; i++)
            bought_decor.add(sharedPreferences.getBoolean("bought_decor_" + i, false));
        bought_decor.add(true);

        int[] coins = new int[]{0, 25, 50, 80, 120, 0};

        View horizontal = null;
        LinearLayout layout_horizontal = null;
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

            image.setImageResource(decor[i]);

            if (i == 4) {
                if (!image_uri.isEmpty()) image.setImageURI(Uri.parse(image_uri));
                coin.setVisibility(GONE);
                amount_but.setText("Set photo");

                layout_buy.setOnClickListener(View -> {
                    Player.button(soundMute);

                    showImageOptionsDialog();
                });
            } else {
                amount_but.setText(coins[i] + "");
            }

            int finalCounter = i;
            if (bought_decor.get(i)) {
                if (i != 4) {
                    layout_buy.setAlpha(0.3f);
                    layout_buy.setEnabled(false);
                }

                if (i == active_decor) name.setText(getResources().getString(R.string.selected));
                else name.setText(getResources().getString(R.string.select));

                image.setOnClickListener(View -> {
                    Player.button(soundMute);

                    active_decor = finalCounter;
                    editor.putInt("active_decor", active_decor);
                    editor.apply();

                    update_decor_UI();
                });
            } else {
                name.setText(getResources().getString(R.string.buy));
                if (coins[i] <= available_coin) {
                    layout_buy.setOnClickListener(View -> {
                        Player.button(soundMute);

                        available_coin -= coins[finalCounter];
                        active_decor = finalCounter;
                        editor.putBoolean("bought_decor_" + finalCounter, true);
                        editor.putInt("active_body", active_decor);
                        editor.putInt("available_coin", available_coin);
                        editor.apply();

                        update_decor_UI();
                    });
                }
            }

            layout_horizontal.addView(shop_card);
            if (i != 0 && i % 2 == 0) layout_vertical.addView(horizontal);
        }
    }


    public void showImageOptionsDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Reset Image"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Image Options");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    checkCameraPermissionAndOpenCamera();
                    break;
                case 1:
                    open_image_picker();
                    break;
                case 2:
                    resetProfileImage();
                    break;
            }
        });
        builder.show();
    }

    public void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showPermissionExplanationDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }

        } else {
            open_camera(); // Permission already granted
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this).setTitle("Camera Permission Needed").setMessage("This app needs access to your camera to take profile photos.").setPositiveButton("Allow", (dialog, which) -> {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
    }


    public void resetProfileImage() {
        image_uri = null;
        editor.remove("image_uri");
        editor.apply();

        image_uri = "";
        update_decor_UI();
    }


    public void open_image_picker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void open_camera() {
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile_" + System.currentTimeMillis() + ".jpg");

        cameraImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void requestCameraPermission() {
        if (hasPermission(Manifest.permission.CAMERA)) {
            open_camera();
        } else {
            requestPermission(Manifest.permission.CAMERA);
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                open_camera();
            } else {
                showPermissionExplanationDialog(); // Ask again if denied
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            image_uri = imageUri.toString();
            editor.putString("image_uri", image_uri);
            editor.apply();

            update_decor_UI();
        }

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            image_uri = cameraImageUri.toString();
            editor.putString("image_uri", image_uri);
            editor.apply();

            update_decor_UI();
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