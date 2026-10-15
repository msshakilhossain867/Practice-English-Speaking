package com.citomoltd.englishspeakingpracticeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DbHelper dbHelper;
    ArrayList<ItemModel> bookmarkList;
    ItemAdapter adapter;
    private FrameLayout adContainer;
    TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookmark);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = findViewById(R.id.recyclerViewBookmarks);
        adContainer = findViewById(R.id.adContainer);
        textEmpty = findViewById(R.id.emptybook);
        dbHelper = new DbHelper(this);
        bookmarkList = dbHelper.getBookmarkedItemsFromAllTables(); // UPDATE

        AdHelper.loadBannerAd(this, adContainer);


        if (bookmarkList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        }

        adapter = new ItemAdapter(bookmarkList, BookmarkActivity.this, new ItemAdapter.OnItemClickListener() { // UPDATE
            @Override
            public void onItemClick(ItemModel item) {
                Intent intent = new Intent(BookmarkActivity.this, SummaryViewActivity.class);
                intent.putExtra("item_id", item.getId());
                intent.putExtra("item_name", item.getName());
                intent.putExtra("image", item.getImg());
                intent.putExtra("topic", 1);
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(ItemModel item) { // UPDATE
              /*  dbHelper.updateFavorite(item.getId(), item.isFavorite() == 1 ? 0 : 1);
                refreshData(); // UPDATE*/

                String itemName = item.getName(); // ধরুন name ফিল্ডে আছে
                int newStatus = item.isFavorite() == 1 ? 0 : 1;

// এবার কল করুন
                dbHelper.updateFavorite(itemName, newStatus, "items", "regular_item");
                refreshData();
            }

            @Override
            public void onBookmarkClick(ItemModel item) { // UPDATE

                String itemName = item.getName(); // ধরুন name ফিল্ডে আছে
                int newStatus = item.isBookmarked() == 1 ? 0 : 1;

// এবার কল করুন
                dbHelper.updateBookmark(itemName, newStatus, "items", "regular_item");
                refreshData();
            }


        });
        int dividerHeight = 10; // px
        recyclerView.addItemDecoration(new RandomColorDividerItemDecoration(dividerHeight));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void refreshData() {
        bookmarkList.clear();
        bookmarkList.addAll(dbHelper.getBookmarkedItemsFromAllTables());

        if (bookmarkList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Optionally, you can reload the banner ad when the activity resumes
        AdHelper.loadBannerAd(this, adContainer);
    }

}