package com.michaelhsieh.bakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michaelhsieh.bakingapp.model.Step;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RecipeStepsListAdapter extends RecyclerView.Adapter<RecipeStepsListAdapter.ViewHolder> {

    private List<Step> steps;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    // data is passed into the constructor
    RecipeStepsListAdapter(Context context, List<Step> steps) {
        this.inflater = LayoutInflater.from(context);
        this.steps = steps;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recipe_step_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Step step = steps.get(position);
        holder.stepDisplay.setText(step.getShortDescription());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return steps.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView stepDisplay;

        ViewHolder(View itemView) {
            super(itemView);
            stepDisplay = itemView.findViewById(R.id.tv_step_short_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onRecipeStepItemClick(view, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    Step getItem(int id) {
        return steps.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onRecipeStepItemClick(View view, int position);
    }
}

