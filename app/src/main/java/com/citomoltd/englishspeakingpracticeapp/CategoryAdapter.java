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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> categories;
    private OnItemClickListener listener;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(CategoryModel category);
    }

    public CategoryAdapter(List<CategoryModel> categories, Context context, OnItemClickListener listener) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryModel category = categories.get(position);
        holder.textCategory.setText(category.getName());

        int img = context.getResources().getIdentifier(category.getImg(), "drawable", context.getPackageName());
        int img2 = context.getResources().getIdentifier(category.getImg2(), "drawable", context.getPackageName());
        int img3 = context.getResources().getIdentifier(category.getImg3(), "drawable", context.getPackageName());
        int img4 = context.getResources().getIdentifier(category.getImg4(), "drawable", context.getPackageName());

        if (img != 0) {
            holder.imgOne.setImageResource(img);
        } else {
            holder.imgOne.setImageResource(R.drawable.beginner); // fallback image
        }
        if (img2 != 0) {
            holder.imgTwo.setImageResource(img2);
        } else {
            holder.imgTwo.setImageResource(R.drawable.beginner); // fallback image
        }
        if (img3 != 0) {
            holder.imgThree.setImageResource(img3);
        } else {
            holder.imgThree.setImageResource(R.drawable.beginner); // fallback image
        }
        if (img4 != 0) {
            holder.imgFour.setImageResource(img4);
        } else {
            holder.imgFour.setImageResource(R.drawable.beginner); // fallback image
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(category));
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCategory;
        ImageView imgOne, imgTwo, imgThree, imgFour;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.textCategory);
            imgOne = itemView.findViewById(R.id.imgOne);
            imgTwo = itemView.findViewById(R.id.imgTwo);
            imgThree = itemView.findViewById(R.id.imgThree);
            imgFour = itemView.findViewById(R.id.imgFour);

        }
    }
}

