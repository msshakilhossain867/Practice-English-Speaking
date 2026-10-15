package com.citomoltd.englishspeakingpracticeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DbHelper dbHelper;
    ArrayList<CategoryModel> categoryList;
    CategoryAdapter adapter;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewCategories);
        adContainer = findViewById(R.id.adContainer);


        dbHelper = new DbHelper(this);
        int value = getIntent().getIntExtra("topic", 0);
        dbHelper.copyDatabase(); // ✅ ডাটাবেস কপি হবে যদি আগে না হয়
        // Load Banner Ad
        AdHelper.loadBannerAd(this, adContainer);


        if (value == 1) {
            categoryList = dbHelper.getCategories(); // ক্যাটাগরি লোড
        } else if (value == 2) {
            categoryList = dbHelper.getRegularCategories();
        }


        adapter = new CategoryAdapter(categoryList, CategoryActivity.this, category -> {
            Intent intent = new Intent(CategoryActivity.this, ListActivity.class);
            intent.putExtra("category_id", category.getId());
            intent.putExtra("topic", value);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int dividerHeight = 5; // px
        recyclerView.addItemDecoration(new RandomColorDividerItemDecoration(dividerHeight));
        recyclerView.setHasFixedSize(true);


        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Optionally, you can reload the banner ad when the activity resumes
        AdHelper.loadBannerAd(this, adContainer);
    }
}
