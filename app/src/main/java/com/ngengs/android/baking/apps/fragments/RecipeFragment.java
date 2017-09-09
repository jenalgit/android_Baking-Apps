/*******************************************************************************
 * Copyright (c) 2017 Rizky Kharisma (@ngengs)
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

package com.ngengs.android.baking.apps.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ngengs.android.baking.apps.R;
import com.ngengs.android.baking.apps.adapters.IngredientAdapter;
import com.ngengs.android.baking.apps.adapters.StepAdapter;
import com.ngengs.android.baking.apps.data.Ingredient;
import com.ngengs.android.baking.apps.data.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeFragment extends Fragment {
    private static final String ARG_PARAM_INGREDIENT = "INGREDIENT";
    private static final String ARG_PARAM_STEP = "STEP";
    @BindView(R.id.recyclerIngredient)
    RecyclerView mRecyclerIngredient;
    @BindView(R.id.recycleStep)
    RecyclerView mRecyclerStep;
    private Unbinder unbinder;

    private List<Ingredient> mIngredientList;
    private List<Step> mStepList;
    private StepAdapter mStepAdapter;


    private OnFragmentInteractionListener mListener;

    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Call or build the Recipe fragment with given params.
     *
     * @param ingredients
     *         List of ingredient
     * @param steps
     *         List of steps
     *
     * @return The instance fragment
     */
    public static RecipeFragment newInstance(List<Ingredient> ingredients, List<Step> steps) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM_INGREDIENT, new ArrayList<Parcelable>(ingredients));
        args.putParcelableArrayList(ARG_PARAM_STEP, new ArrayList<Parcelable>(steps));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                       + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIngredientList = new ArrayList<>();
        mStepList = new ArrayList<>();
        if (getArguments() != null) {
            List<Ingredient> tempIngredients = getArguments().getParcelableArrayList(
                    ARG_PARAM_INGREDIENT);
            List<Step> tempSteps = getArguments().getParcelableArrayList(ARG_PARAM_STEP);
            if (tempIngredients != null) {
                mIngredientList.addAll(tempIngredients);
            }
            if (tempSteps != null) {
                mStepList.addAll(tempSteps);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            mIngredientList = savedInstanceState.getParcelableArrayList(ARG_PARAM_INGREDIENT);
            mStepList = savedInstanceState.getParcelableArrayList(ARG_PARAM_STEP);
        }
        mRecyclerIngredient.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerIngredient.setHasFixedSize(true);
        mRecyclerIngredient.setAdapter(new IngredientAdapter(mIngredientList));
        mRecyclerStep.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerStep.setHasFixedSize(true);
        mStepAdapter = new StepAdapter(getContext(), mStepList, new StepAdapter.OnClickListener() {
            @Override
            public void onClickStep(int position) {
                if (mListener != null) {
                    mListener.onRecipeStepClick(position);
                }
            }
        });
        mRecyclerStep.setAdapter(mStepAdapter);
        ViewCompat.setNestedScrollingEnabled(mRecyclerIngredient, false);
        ViewCompat.setNestedScrollingEnabled(mRecyclerStep, false);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ARG_PARAM_INGREDIENT, new ArrayList<>(mIngredientList));
        outState.putParcelableArrayList(ARG_PARAM_STEP, new ArrayList<>(mStepList));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Give indicator to the selected step.
     *
     * @param position
     *         Position of the indicator
     */
    public void changeSelectedStep(int position) {
        if (mStepAdapter != null && mStepAdapter.getItemCount() >= position) {
            mStepAdapter.indicatorSelected(position);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>* See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.</p>
     */
    public interface OnFragmentInteractionListener {
        void onRecipeStepClick(int position);
    }
}
