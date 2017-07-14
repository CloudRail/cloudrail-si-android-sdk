package com.cloudrail.poifinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * The main activity of this sample application. The only purpose is to load the correct fragments.
 *
 * @author patrick
 */
public class MainActivity extends AppCompatActivity implements CategorySelect.OnCategorySelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        browseToCategorySelect();
    }

    @Override
    public void onBackPressed() {
        Fragment results = getFragmentManager().findFragmentByTag("results");

        if(results == null) {
            super.onBackPressed();
        } else {
            browseToCategorySelect();
        }
    }

    @Override
    public void onCategorySelected(String category) {
        browseToResults(category);
    }

    private void browseToCategorySelect() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = CategorySelect.newInstance();
        fragmentTransaction.replace(R.id.content, content);
        fragmentTransaction.commit();
    }

    private void browseToResults(String category) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = POIResult.newInstance(category);
        fragmentTransaction.replace(R.id.content, content, "results");
        fragmentTransaction.commit();
    }
}
