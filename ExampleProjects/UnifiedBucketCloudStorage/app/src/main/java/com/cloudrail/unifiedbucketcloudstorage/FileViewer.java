package com.cloudrail.unifiedbucketcloudstorage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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
import com.cloudrail.si.types.BusinessFileMetaData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


// * {@link BucketViewer.OnFragmentInteractionListener} interface

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FileViewer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileViewer extends Fragment {
    // the fragment initialization parameters
    private static final String ARG_SERVICE = "service";
    private static final String ARG_BUCKET_NAME = "bucketName";
    private static final String ARG_BUCKET_ID = "bucketId";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Context context;
    private ListView listView;
    private ProgressBar spinner;
    private View selectedItem;
    private int selectedItemPosition;

    private String mServiceString;
    private String mBucketName;
    private String mBucketId;
    private Bucket mBucket;
    private BusinessCloudStorage service;

    private String new_bucket_name;

    public FileViewer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param service the bucket cloud storage
     * @param bucketName the name of the bucket to view
     * @param bucketId the id of the bucket to view
     * @return A new instance of fragment BucketViewer.
     */
    public static FileViewer newInstance(String service, String bucketName, String bucketId) {
        FileViewer fragment = new FileViewer();
        Bundle args = new Bundle();
        args.putString(ARG_SERVICE, service);
        args.putString(ARG_BUCKET_NAME, bucketName);
        args.putString(ARG_BUCKET_ID, bucketId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mServiceString = getArguments().getString(ARG_SERVICE);
            mBucketName = getArguments().getString(ARG_BUCKET_NAME);
            mBucketId = getArguments().getString(ARG_BUCKET_ID);

            mBucket = new Bucket();
            mBucket.setName(mBucketName);
            mBucket.setIdentifier(mBucketId);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);
        this.listView = (ListView) v.findViewById(R.id.fileListView);
        this.spinner = (ProgressBar) v.findViewById(R.id.spinner);

        switch (mServiceString) {
            case "amazon": {
//                service = new AmazonS3(context, "[Your S3 Access Key ID]", "[Your S3 Secret Access Key]", "[Your AWS region]");
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
                menuInflater.inflate(R.menu.selected_file_bar, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete_file: {
                                removeItem();
                                return true;
                            }
                            case R.id.action_download_file: {
                                downloadItem();
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

        ((TextView)v.findViewById(R.id.text2)).setText("Bucket \"" + mBucketName + "\"");
        return v;
    }

    private void removeItem() {
        this.startSpinner();
        ArrayAdapter<BusinessFileMetaData> adapter = (ArrayAdapter<BusinessFileMetaData>) listView.getAdapter();
        final BusinessFileMetaData file = adapter.getItem(selectedItemPosition);
        new Thread(new Runnable() {
            @Override
            public void run() {
                service.deleteFile(file.getFileName(), mBucket);
                refreshList();
            }
        }).start();
    }

    private void downloadItem() {
        this.startSpinner();
        ArrayAdapter<BusinessFileMetaData> adapter = (ArrayAdapter<BusinessFileMetaData>) listView.getAdapter();
        final BusinessFileMetaData file = adapter.getItem(selectedItemPosition);
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = service.downloadFile(file.getFileName(), mBucket);
                try {
                    verifyStoragePermissions((Activity) context);
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File targetFile = new File(dir, file.getFileName());
//                    File targetFile = new File(Environment.DIRECTORY_DOWNLOADS + "/" + file.getFileName());
                    dir.mkdirs();
                    targetFile.createNewFile();
                    OutputStream outStream = new FileOutputStream(targetFile);

                    byte[] buffer = new byte[8 * 1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }

                    is.close();
                    outStream.close();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    stopSpinner();
                }
            }
        }).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_files, menu);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                refreshList();
                break;
            }
/*            case R.id.action_upload_file: {
                uploadFlie();
                break;
            }*/
        }
        return true;
    }

/*    private void uploadFlie() {
    }*/

    private void refreshList() {
        startSpinner();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<BusinessFileMetaData> files = service.listFiles(mBucket);
//                final List<Bucket> items = service.listBuckets();

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FileAdapter listAdapter = new FileAdapter(context, R.layout.list_item, files);
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

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
