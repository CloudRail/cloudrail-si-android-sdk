package com.cloudrail.unifiedsocialinteraction;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.cloudrail.si.interfaces.Social;
import com.cloudrail.si.services.Facebook;
import com.cloudrail.si.services.FacebookPage;
import com.cloudrail.si.services.Twitter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChooseServiceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChooseServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseServiceFragment extends Fragment {

    private Context mContext;

    private View.OnClickListener mServiceSelectedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Social service;

            switch (v.getId()) {
                case R.id.Facebook: {
                    service = new Facebook(mContext, "[Client ID]", "[Client Secret]");
                    break;
                }
                case R.id.FacebookPages: {
                    service = new FacebookPage(mContext, "[PageName]", "[Client ID]", "[Client Secret]");
                    break;
                }
                case R.id.Twitter: {
                    service = new Twitter(mContext, "[Client ID]", "[Client Secret]");
                    break;
                }
                default:
                    throw new RuntimeException("Unknown Button ID!!");
            }
            switch (v.getId()) {
                case R.id.Facebook: {
                    service = new Facebook(mContext, "439557219752767", "7265db555fbf26606870451605e1ae37");
                    break;
                }
                case R.id.FacebookPages: {
                    service = new FacebookPage(mContext, "[PageName]", "[Client ID]", "[Client Secret]");
                    break;
                }
                case R.id.Twitter: {
                    service = new Twitter(mContext, "FW6M3WmjhyiT2AYsohDZHGDTw", "1Ts5gOmPzXUgeckMDPV0dSEuY51L77cJCYBfftQFu6kI9kv2dp");
                    break;
                }
                default:
                    throw new RuntimeException("Unknown Button ID!!");
            }

            login(service);
//            new PerformLogin().execute(profile);
        }
    };

    private void login(final Social service) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                service.login();
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onFragmentInteraction(service);
                    }
                });
            }
        }).start();
    }

    private OnFragmentInteractionListener mListener;

    public ChooseServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChooseServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseServiceFragment newInstance() {
        return new ChooseServiceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View v = inflater.inflate(R.layout.fragment_choose, container, false);

        Button facebook = (Button) v.findViewById(R.id.Facebook);
        facebook.setOnClickListener(mServiceSelectedListener);
        Button twitter = (Button) v.findViewById(R.id.Twitter);
        twitter.setOnClickListener(mServiceSelectedListener);


        return v;
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
        mContext = context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mContext = null;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Social service);
    }
}
