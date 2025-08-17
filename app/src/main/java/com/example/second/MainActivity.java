package com.example.second;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton fabVoice;

    // 預先建立 5 個 Fragment 避免重複建立
    private final Fragment introFragment = new IntroFragment();
    private final Fragment registerFragment = new RegisterFragment();
    private final Fragment progressFragment = new ProgressFragment();
    private final Fragment aiFragment = new AIFragment();
    private final Fragment voiceFragment = new VoiceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 這裡不用設 adjustPan，會在 Manifest 設定
        setContentView(R.layout.activity_main);

        // 取得元件
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabVoice = findViewById(R.id.fab_voice);

        // 預設顯示 Intro
        loadFragment(introFragment);

        // 底部導覽列切換
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_intro) {
                loadFragment(introFragment);
            } else if (id == R.id.nav_register) {
                loadFragment(registerFragment);
            } else if (id == R.id.nav_progress) {
                loadFragment(progressFragment);
            } else if (id == R.id.nav_ai) {
                loadFragment(aiFragment);
            } else {
                return false;
            }
            return true;
        });

        // 中間麥克風按鈕
        fabVoice.setOnClickListener(v -> {
            loadFragment(voiceFragment);
        });
    }

    // 切換 Fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // 點擊鍵盤外的地方隱藏鍵盤
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v != null) {
                hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
