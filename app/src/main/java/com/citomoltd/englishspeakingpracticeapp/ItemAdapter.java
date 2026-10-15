package com.citomoltd.englishspeakingpracticeapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {


    private List<ItemModel> items;
    private OnItemClickListener listener;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(ItemModel item);

        void onFavoriteClick(ItemModel item);

        void onBookmarkClick(ItemModel item);
    }

    public ItemAdapter(List<ItemModel> items, Context context, OnItemClickListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel item = items.get(position);
        holder.textItem.setText(item.getName());


        int images = context.getResources().getIdentifier(item.getImg(), "drawable", context.getPackageName());


        if (images != 0) {
            holder.image.setImageResource(images);
        } else {
            holder.image.setImageResource(R.drawable.beginner); // fallback image
        }

       /* // ফেভারিট চেক করা থাকলে স্টার রঙিন হবে
        holder.btnFavorite.setImageResource(item.isFavorite() == 1 ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);
        holder.btnBookmark.setImageResource(item.isBookmarked() == 1 ? R.drawable.baseline_bookmark_24 : R.drawable.baseline_bookmark_border_24);
*/

        // ফেভারিট চেক
        holder.btnFavorite.setImageResource(item.isFavorite() == 1 ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24); // UPDATE
        holder.btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(item)); // UPDATE

        // বুকমার্ক চেক
        holder.btnBookmark.setImageResource(item.isBookmarked() == 1 ? R.drawable.baseline_bookmark_24 : R.drawable.baseline_bookmark_border_24); // UPDATE
        holder.btnBookmark.setOnClickListener(v -> listener.onBookmarkClick(item)); // UPDATE

     /*   holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(item));
        holder.btnBookmark.setOnClickListener(v -> listener.onBookmarkClick(item));*/

        // আইটেম ক্লিক লিসনার
        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(item);
        });
        // UPDATE


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textItem;
        ImageView btnFavorite, btnBookmark;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textItem = itemView.findViewById(R.id.textItem);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);  // XML থেকে লোড
            btnBookmark = itemView.findViewById(R.id.btnBookmark);  // XML থেকে লোড
            image = itemView.findViewById(R.id.image);
        }
    }
}
