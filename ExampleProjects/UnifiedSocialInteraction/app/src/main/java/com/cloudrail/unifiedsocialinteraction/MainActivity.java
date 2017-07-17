package com.cloudrail.unifiedsocialinteraction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.cloudrail.si.CloudRail;
import com.cloudrail.si.interfaces.Social;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ChooseServiceFragment.OnFragmentInteractionListener {

    private String mCurrentFragment;
    private Social mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CloudRail.setAppKey("[CloudRail API key]");

        browseToServiceSelection();
    }

    private void browseToServiceSelection() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = ChooseServiceFragment.newInstance();
        fragmentTransaction.replace(R.id.content, content);
        fragmentTransaction.commit();
        mCurrentFragment = "ChooseService";
    }

    @Override
    public void onFragmentInteraction(Social service) {
        mService = service;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = PostUpdateFragment.newInstance(service);
        fragmentTransaction.replace(R.id.content, content);
        fragmentTransaction.commit();
        mCurrentFragment = "EditStatus";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EditText editText = (EditText) findViewById(R.id.postEditText);
        final String text = editText.getText().toString();
        final Uri uri = data.getData();
        final Context context = this;

        if (requestCode == PostUpdateFragment.PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(uri.toString()); //get URL from your uri object
                            InputStream stream = url.openStream();
                            mService.postImage(text, stream);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "posted with image", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                }).start();
            }
        }
    }
}
