package com.citomoltd.englishspeakingpracticeapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_db.db";

    private static final int DATABASE_VERSION = 1;
    private static String DB_PATH;
    private final Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";


    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // কিছুই করার দরকার নেই, কারণ ডাটাবেস assets থেকে কপি করা হবে
        copyDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ডাটাবেস আপডেট হলে আগের ফাইল ডিলিট করে নতুনটি কপি করা হবে
        File dbFile = new File(DB_PATH + DATABASE_NAME);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        copyDatabase();
    }

    public void copyDatabase() {
        try {
            File dbFile = new File(DB_PATH + DATABASE_NAME);
            if (!dbFile.exists()) {
                this.getReadableDatabase().close();
                copyDBFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error copying database: " + e.getMessage());
        }
    }

    private void copyDBFile() throws IOException {
        InputStream inputStream = context.getAssets().open(DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream outputStream = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public boolean checkDatabase() {
        File dbFile = new File(DB_PATH + DATABASE_NAME);
        return dbFile.exists();
    }


    // ✅ **একটি নির্দিষ্ট আইটেমের সামারি রিটার্ন করার মেথড**
    @SuppressLint("Range")
    public String getQuizSummary(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT summary FROM items WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(itemId)});

        String summary = "";
        if (cursor.moveToFirst()) {
            summary = cursor.getString(cursor.getColumnIndex("summary"));
        }
        cursor.close();
        db.close();
        return summary;
    }

    @SuppressLint("Range")
    public String getQuizSummaryRegular(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT summary FROM regular_item WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(itemId)});

        String summary = "";
        if (cursor.moveToFirst()) {
            summary = cursor.getString(cursor.getColumnIndex("summary"));
        }
        cursor.close();
        db.close();
        return summary;
    }


    public void updateFavorite(String itemName, int isFavorite, String tableName, String table2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_favorite", isFavorite);

        // যদি tableName এর মধ্যে থাকে, তাহলে সেটাতে আপডেট করো
        if (tableName.equals("items")) {
            db.update("items", values, "name = ?", new String[]{itemName});
        }

        // যদি table2 এর মধ্যে থাকে, তাহলে সেটাতে আপডেট করো
        if (table2.equals("regular_item")) {
            db.update("regular_item", values, "name = ?", new String[]{itemName});
        }

        db.close();
    }


    public void updateBookmark(String itemName, int isBookmarked, String tableName, String table2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_bookmarked", isBookmarked);

        // যদি tableName এর মধ্যে থাকে, তাহলে সেটাতে আপডেট করো
        if (tableName.equals("items")) {
            db.update("items", values, "name = ?", new String[]{itemName});
        }

        // যদি table2 এর মধ্যে থাকে, তাহলে সেটাতে আপডেট করো
        if (table2.equals("regular_item")) {
            db.update("regular_item", values, "name = ?", new String[]{itemName});
        }

        db.close();
    }


    public ArrayList<ItemModel> getFavoriteItems() {
        ArrayList<ItemModel> favoriteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // items table
        Cursor cursor1 = db.rawQuery("SELECT * FROM items WHERE is_favorite = 1", null);
        while (cursor1.moveToNext()) {
            int id = cursor1.getInt(0);
            String name = cursor1.getString(2);
            String summary = cursor1.getString(3);
            @SuppressLint("Range") String img = cursor1.getString(cursor1.getColumnIndex("img"));
            favoriteList.add(new ItemModel(id, name, summary, 1, cursor1.getInt(5), img)); // tableName পাঠানো
        }
        cursor1.close();

        // regular_item table
        Cursor cursor2 = db.rawQuery("SELECT * FROM regular_item WHERE is_favorite = 1", null);
        while (cursor2.moveToNext()) {
            int id = cursor2.getInt(0);
            String name = cursor2.getString(2);
            String summary = cursor2.getString(3);
            @SuppressLint("Range") String img = cursor2.getString(cursor2.getColumnIndex("img"));
            favoriteList.add(new ItemModel(id, name, summary, 1, cursor2.getInt(5), img));
        }
        cursor2.close();

        return favoriteList;
    }


    public ArrayList<ItemModel> getBookmarkedItemsFromAllTables() {
        ArrayList<ItemModel> bookmarkList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // ১. items টেবিল থেকে
        Cursor cursor1 = db.rawQuery("SELECT * FROM items WHERE is_bookmarked = 1", null);
        while (cursor1.moveToNext()) {
            int id = cursor1.getInt(0);
            String name = cursor1.getString(2);
            String summary = cursor1.getString(3);
            @SuppressLint("Range") String img = cursor1.getString(cursor1.getColumnIndex("img"));
            bookmarkList.add(new ItemModel(id, name, summary, cursor1.getInt(4), 1, img));
        }
        cursor1.close();

        // ২. story_items টেবিল থেকেও একইভাবে
        Cursor cursor2 = db.rawQuery("SELECT * FROM regular_item WHERE is_bookmarked = 1", null);


        while (cursor2.moveToNext()) {
            int id = cursor2.getInt(0);
            String name = cursor2.getString(2);
            String summary = cursor2.getString(3);
            @SuppressLint("Range") String img = cursor2.getString(cursor2.getColumnIndex("img"));
            bookmarkList.add(new ItemModel(id, name, summary, cursor2.getInt(4), 1, img));
        }
        cursor2.close();


        return bookmarkList;
    }


    public ArrayList<CategoryModel> getCategories() {
        ArrayList<CategoryModel> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categories", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String img = cursor.getString(cursor.getColumnIndex("img"));
            @SuppressLint("Range") String img2 = cursor.getString(cursor.getColumnIndex("img2"));
            @SuppressLint("Range") String img3 = cursor.getString(cursor.getColumnIndex("img3"));
            @SuppressLint("Range") String img4 = cursor.getString(cursor.getColumnIndex("img4"));

            categoryList.add(new CategoryModel(id, name, img, img2, img3, img4));
        }

        cursor.close();
        db.close();
        return categoryList;
    }

    public ArrayList<CategoryModel> getRegularCategories() {
        ArrayList<CategoryModel> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM regular_cat", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String img = cursor.getString(2);
            String img2 = cursor.getString(3);
            String img3 = cursor.getString(4);
            String img4 = cursor.getString(5);
            categoryList.add(new CategoryModel(id, name, img, img2, img3, img4));
        }

        cursor.close();
        db.close();
        return categoryList;
    }

    // ✅ **সব বুকমার্ক আইটেম লোড করার মেথড**


}
