/*******************************************************************************
 * Copyright (c) 2017 Rizky Kharisma (@ngengs)
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            if (tempIngredients != null) mIngredientList.addAll(tempIngredients);
            if (tempSteps != null) mStepList.addAll(tempSteps);
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
        mStepAdapter = new StepAdapter(getContext(), mStepList,
                                       new StepAdapter.OnClickListener() {
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onRecipeStepClick(int position);
    }
}
