package com.example.second;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HospitalDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_detail);

        // 設定標題
        setTitle("醫院詳細介紹");

        // 開啟 ActionBar 返回箭頭
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // 按返回箭頭時結束此頁面
        finish();
        return true;
    }
}
