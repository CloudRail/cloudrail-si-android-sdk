package com.cloudrail.loginwithsample;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cloudrail.si.CloudRail;

public class MainActivity extends AppCompatActivity implements ChooseService.OnTokenListener {

    private String mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CloudRail.setAppKey("[Your CloudRail Key]");
        setContentView(R.layout.activity_main);

        browseToServiceSelection();
    }

    @Override
    public void onBackPressed() {
        if(mCurrentFragment.equals("EditStatus")) {
            browseToServiceSelection();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onToken(String[] token) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = EditStatus.newInstance(token);
        fragmentTransaction.replace(R.id.content, content);
        fragmentTransaction.commit();
        mCurrentFragment = "EditStatus";
    }

    private void browseToServiceSelection() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = ChooseService.newInstance();
        fragmentTransaction.replace(R.id.content, content);
        fragmentTransaction.commit();
        mCurrentFragment = "ChooseService";
    }
}
