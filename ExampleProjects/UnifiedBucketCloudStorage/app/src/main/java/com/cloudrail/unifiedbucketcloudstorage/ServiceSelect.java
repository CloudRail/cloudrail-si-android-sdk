package com.cloudrail.unifiedbucketcloudstorage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ServiceSelect extends Fragment {
    private OnServiceSelectedListener mListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button pressed = (Button) v;
            String service = null;

            switch (pressed.getId()) {
                case R.id.amazonButton: service = "amazon"; break;
                case R.id.backblazeButton: service = "backblaze"; break;
                case R.id.googleButton: service = "google"; break;
                case R.id.microsoftButton: service = "microsoft"; break;
                case R.id.rackspaceButton: service = "rackspace"; break;
            }

            mListener.onServiceSelected(service);
        }
    };

    public ServiceSelect() {
        // Required empty public constructor
    }

    public static ServiceSelect newInstance() {
        ServiceSelect fragment = new ServiceSelect();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_select, container, false);

        Button amazon = v.findViewById(R.id.amazonButton);
        amazon.setOnClickListener(mOnClickListener);
        Button backplace = v.findViewById(R.id.backblazeButton);
        backplace.setOnClickListener(mOnClickListener);
        Button google = v.findViewById(R.id.googleButton);
        google.setOnClickListener(mOnClickListener);
        Button microsoft = v.findViewById(R.id.microsoftButton);
        microsoft.setOnClickListener(mOnClickListener);
        Button rackspace = v.findViewById(R.id.rackspaceButton);
        rackspace.setOnClickListener(mOnClickListener);

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
