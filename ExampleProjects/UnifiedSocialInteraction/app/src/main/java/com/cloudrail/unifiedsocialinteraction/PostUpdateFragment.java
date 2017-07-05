package com.cloudrail.unifiedsocialinteraction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cloudrail.si.interfaces.Social;

import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostUpdateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostUpdateFragment extends Fragment {
    public static final int PICK_IMAGE = 1;
    public static final int PICK_VIDEO = 2;

    private Context mContext;
    private Social mService;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PostUpdateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PostUpdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostUpdateFragment newInstance(Social service) {
        PostUpdateFragment fragment = new PostUpdateFragment();
        Bundle args = new Bundle();
        ((PostUpdateFragment) fragment).setService(service);
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_post_update, container, false);

        Button postText = (Button) v.findViewById(R.id.postText);
        postText.setOnClickListener(mPostListener);
        Button postPic = (Button) v.findViewById(R.id.postPicture);
        postPic.setOnClickListener(mPostListener);
        Button postVid = (Button) v.findViewById(R.id.postVideo);
        postVid.setOnClickListener(mPostListener);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        void onFragmentInteraction(Uri uri);
    }

    private void setService(Social service) {
        mService = service;
    }

    private View.OnClickListener mPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText postText = ((Activity) mContext).findViewById(R.id.postEditText);
            final String text = postText.getText().toString();
            switch (v.getId()) {
                case R.id.postText: {
                    if (text == null || text.trim() == "") {
                        Toast.makeText(mContext, "please enter text", Toast.LENGTH_SHORT);
                        break;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            mService.postUpdate(text);

                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "update posted", Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }).start();
                    break;
                }
                case R.id.postPicture: {
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                    startActivityForResult(chooserIntent, PICK_IMAGE);
                    break;
                }
                case R.id.postVideo: {
                    Toast.makeText(mContext, "not implemented", Toast.LENGTH_SHORT);
                    break;
                }
                default:
                    throw new RuntimeException("Unknown Button ID!!");
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("MainActivity::onActivityResult");
        EditText editText = (EditText) ((Activity) mContext).findViewById(R.id.postEditText);
        final String text = editText.getText().toString();
        final Uri uri = data.getData();

        if (requestCode == PostUpdateFragment.PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("uri = " + uri);
                            InputStream  stream = mContext.getContentResolver().openInputStream(uri);
                            mService.postImage(text, stream);
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "posted with image", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                }).start();
            }
        }
    }
}
