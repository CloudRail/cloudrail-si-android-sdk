package com.cloudrail.poifinder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategorySelect.OnCategorySelectedListener} interface
 * to handle interaction events.
 * Use the {@link CategorySelect#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategorySelect extends Fragment {

    private OnCategorySelectedListener mListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button pressed = (Button) v;
            String category = null;

            switch (pressed.getId()) {
                case R.id.foodButton: category = "food"; break;
                case R.id.bankButton: category = "bank"; break;
                case R.id.mallButton: category = "shopping_mall"; break;
                case R.id.taxiButton: category = "taxi_stand"; break;
                case R.id.parkingButton: category = "parking"; break;
                case R.id.pharmacyButton: category = "pharmacy"; break;
                case R.id.gasStationButton: category = "gas_station"; break;
                case R.id.trainStationButton: category = "train_station"; break;
            }

            mListener.onCategorySelected(category);
        }
    };

    public CategorySelect() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CategorySelect.
     */
    public static CategorySelect newInstance() {
        return new CategorySelect();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category_select, container, false);

        Button food = (Button) v.findViewById(R.id.foodButton);
        food.setOnClickListener(mOnClickListener);
        Button bank = (Button) v.findViewById(R.id.bankButton);
        bank.setOnClickListener(mOnClickListener);
        Button mall = (Button) v.findViewById(R.id.mallButton);
        mall.setOnClickListener(mOnClickListener);
        Button taxi = (Button) v.findViewById(R.id.taxiButton);
        taxi.setOnClickListener(mOnClickListener);
        Button parking = (Button) v.findViewById(R.id.parkingButton);
        parking.setOnClickListener(mOnClickListener);
        Button pharmacy = (Button) v.findViewById(R.id.pharmacyButton);
        pharmacy.setOnClickListener(mOnClickListener);
        Button train = (Button) v.findViewById(R.id.trainStationButton);
        train.setOnClickListener(mOnClickListener);
        Button gas = (Button) v.findViewById(R.id.gasStationButton);
        gas.setOnClickListener(mOnClickListener);


        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategorySelectedListener) {
            mListener = (OnCategorySelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnCategorySelectedListener) {
            mListener = (OnCategorySelectedListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnCategorySelectedListener {
        void onCategorySelected(String category);
    }
}
