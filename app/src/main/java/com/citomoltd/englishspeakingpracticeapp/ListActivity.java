package com.citomoltd.englishspeakingpracticeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DbHelper dbHelper;
    ArrayList<ItemModel> itemList;
    ItemAdapter adapter;
    int categoryId;
    int value;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerViewItems);
        adContainer = findViewById(R.id.adContainer);
        dbHelper = new DbHelper(this);
        dbHelper.copyDatabase();



        categoryId = getIntent().getIntExtra("category_id", -1);
        value = getIntent().getIntExtra("topic", 0);
        itemList = new ArrayList<>();
        if (value == 1) {
            loadItems();
        } else if (value == 2) {
            loadItemsRegular();
        }

        AdHelper.loadBannerAd(this, adContainer);


        adapter = new ItemAdapter(itemList, ListActivity.this, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ItemModel item) {

                if (isInternetAvailable()) {
                    AdHelper.showAdOnButtonClick(ListActivity.this, new AdHelper.AdListener() {
                        @Override
                        public void onAdClosed() {
                            // Start activity after ad
                            Intent intent = new Intent(ListActivity.this, SummaryViewActivity.class);
                            intent.putExtra("item_id", item.getId());
                            intent.putExtra("item_name", item.getName());
                            intent.putExtra("image", item.getImg());
                            intent.putExtra("topic", value);
                            startActivity(intent);
                        }
                    });
                } else {
                    showNoInternetDialog(item);
                }

            }


            @Override
            public void onFavoriteClick(ItemModel item) {  // ⭐ ফেভারিট টগল
                if (value == 1) {
                    toggleFavorite(item);
                } else {
                    toggleFavorite_regular(item);
                }

            }

            @Override
            public void onBookmarkClick(ItemModel item) {  // 📌 বুকমার্ক টগল
                if (value == 1) {
                    toggleBookmark(item);
                } else {
                    toggleBookmark_regular(item);
                }

            }
        });
      /*  DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.divider)));


        recyclerView.addItemDecoration(decoration);*/

        int dividerHeight = 10; // px
        recyclerView.addItemDecoration(new RandomColorDividerItemDecoration(dividerHeight));


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadItems() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM items WHERE category_id = " + categoryId, null);
        while (cursor.moveToNext()) {


            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String summary = cursor.getString(cursor.getColumnIndexOrThrow("summary"));
            int isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite"));
            int isBookmarked = cursor.getInt(cursor.getColumnIndexOrThrow("is_bookmarked"));
            @SuppressLint("Range") String img = cursor.getString(cursor.getColumnIndex("img"));

            itemList.add(new ItemModel(id, name, summary, isFavorite, isBookmarked, img));
        }
        cursor.close();
    }

    private void loadItemsRegular() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM regular_item WHERE category_id = " + categoryId, null);
        while (cursor.moveToNext()) {


            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String summary = cursor.getString(cursor.getColumnIndexOrThrow("summary"));
            int isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite"));
            int isBookmarked = cursor.getInt(cursor.getColumnIndexOrThrow("is_bookmarked"));
            @SuppressLint("Range") String img = cursor.getString(cursor.getColumnIndex("img"));

            itemList.add(new ItemModel(id, name, summary, isFavorite, isBookmarked, img));
        }
        cursor.close();
    }


    private void toggleFavorite_regular(ItemModel item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int newStatus = item.isFavorite() == 1 ? 0 : 1;  // 1 হলে 0 হবে, 0 হলে 1 হবে

        db.execSQL("UPDATE regular_item SET is_favorite = " + newStatus + " WHERE id = " + item.getId());
        item.setFavorite(newStatus);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, newStatus == 1 ? "Added to Favorites" : "Removed from Favorites", Toast.LENGTH_SHORT).show();
    }

    private void toggleBookmark_regular(ItemModel item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int newStatus = item.isBookmarked() == 1 ? 0 : 1;  // 1 হলে 0 হবে, 0 হলে 1 হবে

        db.execSQL("UPDATE regular_item SET is_bookmarked = " + newStatus + " WHERE id = " + item.getId());
        item.setBookmarked(newStatus);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, newStatus == 1 ? "Bookmarked" : "Bookmark Removed", Toast.LENGTH_SHORT).show();
    }

    private void toggleFavorite(ItemModel item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int newStatus = item.isFavorite() == 1 ? 0 : 1;  // 1 হলে 0 হবে, 0 হলে 1 হবে

        db.execSQL("UPDATE items SET is_favorite = " + newStatus + " WHERE id = " + item.getId());
        item.setFavorite(newStatus);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, newStatus == 1 ? "Added to Favorites" : "Removed from Favorites", Toast.LENGTH_SHORT).show();
    }

    private void toggleBookmark(ItemModel item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int newStatus = item.isBookmarked() == 1 ? 0 : 1;  // 1 হলে 0 হবে, 0 হলে 1 হবে

        db.execSQL("UPDATE items SET is_bookmarked = " + newStatus + " WHERE id = " + item.getId());
        item.setBookmarked(newStatus);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, newStatus == 1 ? "Bookmarked" : "Bookmark Removed", Toast.LENGTH_SHORT).show();
    }


    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        }
        return false;
    }

    private void showNoInternetDialog(final ItemModel item) {
        new AlertDialog.Builder(ListActivity.this)
                .setTitle("No Internet Connection")
                .setMessage("An internet connection is required to proceed. Please check your connection and try again.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (isInternetAvailable()) {
                            Intent intent = new Intent(ListActivity.this, SummaryViewActivity.class);
                            intent.putExtra("item_id", item.getId());
                            intent.putExtra("item_name", item.getName());
                            intent.putExtra("image", item.getImg());
                            intent.putExtra("topic", value);
                            startActivity(intent);
                        } else {
                            showNoInternetDialog(item); // Show dialog again if still no internet
                        }
                    }
                })
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Optionally, you can reload the banner ad when the activity resumes
        AdHelper.loadBannerAd(this, adContainer);
    }


}