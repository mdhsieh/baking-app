package com.michaelhsieh.bakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michaelhsieh.bakingapp.model.Recipe;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private List<Recipe> recipeList;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    // data is passed into the constructor
    RecipeAdapter(Context context, List<Recipe> data) {
        this.inflater = LayoutInflater.from(context);
        this.recipeList = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.recipeTitle.setText(recipe.getName());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView recipeTitle;

        ViewHolder(View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.tv_recipe_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Recipe getItem(int id) {
        return recipeList.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
