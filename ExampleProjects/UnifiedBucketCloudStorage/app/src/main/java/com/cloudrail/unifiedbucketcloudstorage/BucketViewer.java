package com.cloudrail.unifiedbucketcloudstorage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudrail.si.interfaces.BusinessCloudStorage;
import com.cloudrail.si.services.AmazonS3;
import com.cloudrail.si.services.Backblaze;
import com.cloudrail.si.services.GoogleCloudPlatform;
import com.cloudrail.si.services.MicrosoftAzure;
import com.cloudrail.si.services.Rackspace;
import com.cloudrail.si.types.Bucket;

import java.util.List;


// * {@link BucketViewer.OnFragmentInteractionListener} interface
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BucketViewer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BucketViewer extends Fragment {
    private OnBucketSelectedListener mListener;

    // the fragment initialization parameters
    private static final String ARG_SERVICE = "service";

    private Context context;
    private ListView listView;
    private ProgressBar spinner;
    private View selectedItem;
    private int selectedItemPosition;

    private String mServiceString;
    private BusinessCloudStorage service;

    private String new_bucket_name;

    public BucketViewer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serviceStr the name of the bucket cloud storage service
     * @return A new instance of fragment BucketViewer.
     */
    public static BucketViewer newInstance(String serviceStr) {
        BucketViewer fragment = new BucketViewer();
        Bundle args = new Bundle();
        args.putString(ARG_SERVICE, serviceStr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mServiceString = getArguments().getString(ARG_SERVICE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bucket_viewer, container, false);
        this.listView = (ListView) v.findViewById(R.id.bucketListView);
        this.spinner = (ProgressBar) v.findViewById(R.id.spinner);

        switch (mServiceString) {
            case "amazon": {
                service = new AmazonS3(context, MainActivity.AMAZON_ACCESS_KEY, MainActivity.AMAZON_SECRET_ACCESS_KEY, MainActivity.AMAZON_REGION);
                break;
            }
            case "backblaze": {
                service = new Backblaze(context, MainActivity.BACKBLAZE_ACCOUNT_ID, MainActivity.BACKBLACE_APP_KEY);
                break;
            }
            case "google": {
                service = new GoogleCloudPlatform(context, MainActivity.GOOGLE_CLIENT_EMAIL, MainActivity.GOOGLE_PRIVATE_KEY, MainActivity.GOOGLE_PROJECT_ID);
                break;
            }
            case "microsoft": {
                service = new MicrosoftAzure(context, MainActivity.AZURE_ACCOUNT_NAME, MainActivity.AZURE_ACCESS_KEY);
                break;
            }
            case "rackspace": {
                service = new Rackspace(context, MainActivity.RACKSPACE_USER_NAME, MainActivity.RACKSPACE_API_KEY, MainActivity.RACKSPACE_REGION);
                break;
            }
        }
        refreshList();

        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = view;
                selectedItemPosition = position;
                PopupMenu popupMenu = new PopupMenu(context, view);
                MenuInflater menuInflater = ((Activity)context).getMenuInflater();
                menuInflater.inflate(R.menu.selected_bucket_bar, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete_bucket: {
                                removeItem();
                                return true;
                            }
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();

                return true;
            }
        });

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = view;
                selectedItemPosition = position;
                ArrayAdapter<Bucket> adapter = (ArrayAdapter<Bucket>) listView.getAdapter();
                Bucket bucket = adapter.getItem(position);
                mListener.onBucketSelected(mServiceString, bucket.getName(), bucket.getIdentifier());
            }
        });

        ((TextView)v.findViewById(R.id.text2)).setText(mServiceString);
        return v;
    }

    private void removeItem() {
        this.startSpinner();
        ArrayAdapter<Bucket> adapter = (ArrayAdapter<Bucket>) listView.getAdapter();
        final Bucket bucket = adapter.getItem(selectedItemPosition);
        new Thread(new Runnable() {
            @Override
            public void run() {
                service.deleteBucket(bucket);
                refreshList();
            }
        }).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_buckets, menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnBucketSelectedListener) {
            mListener = (OnBucketSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnServiceSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        context = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                refreshList();
                break;
            }
            case R.id.action_create_bucket: {
                createBucket();
                break;
            }
        }
        return true;
    }

    private void createBucket() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("create new bucket");
        final EditText input = new EditText(context);
        input.setHint("enter name");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new_bucket_name = input.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bucket newBucket = service.createBucket(new_bucket_name);
                            if (newBucket != null) {
                                refreshList();

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Bucket " + new_bucket_name + " created", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch(Exception e) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Bucket " + new_bucket_name + " could not be created\nMaybe there was a naming conflict.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void refreshList() {
        startSpinner();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Bucket> items = service.listBuckets();

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BucketAdapter listAdapter = new BucketAdapter(context, R.layout.list_item, items);
                        listView.setAdapter(listAdapter);
                        stopSpinner();
                    }
                });
            }
        }).start();
    }

    private void startSpinner() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.VISIBLE);
            }
        });
    }

    private void stopSpinner() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
            }
        });
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnBucketSelectedListener {
        void onBucketSelected(String service, String bucketName, String bucketId);
    }
}
