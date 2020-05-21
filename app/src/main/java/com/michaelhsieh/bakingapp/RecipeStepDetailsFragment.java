package com.michaelhsieh.bakingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michaelhsieh.bakingapp.model.Step;


/** This fragment displays a selected recipe step's video, or
 * a thumbnail image if the video is not available, and also
 * displays a more detailed description of the step.
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeStepDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeStepDetailsFragment extends Fragment {

    // parameter argument with name that matches
    // the fragment initialization parameters
    private static final String ARG_STEP = "Step";

    // parameter of type Step
    private Step step;

    public RecipeStepDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param step The step to get video and description from.
     * @return A new instance of fragment RecipeStepDetailsFragment.
     */
    public static RecipeStepDetailsFragment newInstance(Step step) {
        RecipeStepDetailsFragment fragment = new RecipeStepDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getParcelable(ARG_STEP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_step_details, container, false);

        TextView descriptionDisplay = rootView.findViewById(R.id.tv_step_long_desc);
        descriptionDisplay.setText(step.getDescription());

        return rootView;
    }
}
