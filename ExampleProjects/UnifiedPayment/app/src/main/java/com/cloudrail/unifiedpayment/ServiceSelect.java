package com.cloudrail.unifiedpayment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServiceSelect.OnServiceSelectedListener} interface
 * to handle interaction events.
 * Use the {@link ServiceSelect#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceSelect extends Fragment {
    private OnServiceSelectedListener mListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button pressed = (Button) v;
            String service = null;

            switch (pressed.getId()) {
                case R.id.paypalButton: service = "paypal"; break;
                case R.id.stripeButton: service = "stripe"; break;
            }

            mListener.onServiceSelected(service);
        }
    };

    public ServiceSelect() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ServiceSelect.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceSelect newInstance() {
        ServiceSelect fragment = new ServiceSelect();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_service_select, container, false);

        Button paypal = v.findViewById(R.id.paypalButton);
        paypal.setOnClickListener(mOnClickListener);
        Button stripe = v.findViewById(R.id.stripeButton);
        stripe.setOnClickListener(mOnClickListener);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnServiceSelectedListener) {
            mListener = (OnServiceSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnServiceSelectedListener");
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
    public interface OnServiceSelectedListener {
        void onServiceSelected(String service);
    }
}
