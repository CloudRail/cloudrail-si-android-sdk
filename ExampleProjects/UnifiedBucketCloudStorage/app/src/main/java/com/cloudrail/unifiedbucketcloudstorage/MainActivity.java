package com.cloudrail.unifiedbucketcloudstorage;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cloudrail.si.CloudRail;

public class MainActivity extends AppCompatActivity implements ServiceSelect.OnServiceSelectedListener, BucketViewer.OnBucketSelectedListener {
    private String mServiceString;

    public final static String CLOUDRAIL_APP_KEY = "";

    public final static String AMAZON_ACCESS_KEY = "";
    public final static String AMAZON_SECRET_ACCESS_KEY = "";
    public final static String AMAZON_REGION = "";


    public final static String BACKBLAZE_ACCOUNT_ID = "";
    public final static String BACKBLACE_APP_KEY = "";

    public final static String GOOGLE_CLIENT_EMAIL = "";
    public final static String GOOGLE_PRIVATE_KEY = "";
    public final static String GOOGLE_PROJECT_ID = "";

    public final static String AZURE_ACCOUNT_NAME = "";
    public final static String AZURE_ACCESS_KEY = "";

    public final static String RACKSPACE_USER_NAME = "";
    public final static String RACKSPACE_API_KEY = "";
    public final static String RACKSPACE_REGION = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CloudRail.setAppKey(CLOUDRAIL_APP_KEY);

        browseToServiceSelection();
    }

    @Override
    public void onBackPressed() {
        BucketViewer bucketViewer = (BucketViewer)getFragmentManager().findFragmentByTag("bucketViewer");
        Fragment fileViewer = getFragmentManager().findFragmentByTag("fileViewer");

        if (fileViewer != null) {
            browseToService(mServiceString);
        } else if (bucketViewer != null) {
            browseToServiceSelection();
        } else {
            super.onBackPressed();
        }
    }

    private void browseToServiceSelection() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = ServiceSelect.newInstance();
        fragmentTransaction.replace(R.id.content, content, "serviceSelect");
        fragmentTransaction.commit();
    }

    private void browseToService(String service) {
        mServiceString = service;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = BucketViewer.newInstance(service);
        fragmentTransaction.replace(R.id.content, content, "bucketViewer");
        fragmentTransaction.commit();
    }

    private void browseToBucket(String service, String bucketName, String bucketId) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = FileViewer.newInstance(service, bucketName, bucketId);
        fragmentTransaction.replace(R.id.content, content, "fileViewer");
        fragmentTransaction.commit();
    }

    @Override
    public void onServiceSelected(String service) {
        browseToService(service);
    }

    @Override
    public void onBucketSelected(String service, String bucketName, String bucketId) { browseToBucket(service, bucketName, bucketId); }
}
