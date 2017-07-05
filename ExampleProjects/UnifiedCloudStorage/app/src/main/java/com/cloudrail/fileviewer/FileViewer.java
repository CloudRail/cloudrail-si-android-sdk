package com.cloudrail.fileviewer;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cloudrail.si.CloudRail;
import com.vistrav.ask.Ask;

public class FileViewer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private SharedPreferences sp;
    private SharedPreferences.Editor spe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         *  This is required for Android versions 6.0.0 and above. Android introduced a new permission
         *  police which requires a developer to not only put the required permissions into the manifest
         *  file but also prompt the user to grant the required permission during runtime. For this
         *  purpose we use a library called Ask (https://github.com/00ec454/Ask) since it makes the
         *  process easier than the using the standard Android API.
         */
        Ask.on(this).forPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).go();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);

        Services.getInstance().prepare(this);

        sp = this.getPreferences(Context.MODE_PRIVATE);

        int service = sp.getInt("service", 0);

        this.navigationView.getMenu().getItem(service).setChecked(true);

        if (service == 0) {
            this.navigateToHome();
        } else {
            this.navigateToService(service);
        }
    }

    @Override
    protected void onStop() {
        Services.getInstance().storePersistent();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Files fragment = (Files) getFragmentManager().findFragmentByTag("files");

            if(fragment == null) {
                super.onBackPressed();
                return;
            }

            if(fragment.onBackPressed()) super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.file_viewer, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            this.navigateToHome();
        } if (id == R.id.dropbox) {
            this.navigateToService(1);
        } if (id == R.id.box) {
            this.navigateToService(2);
        } if (id == R.id.google_drive) {
            this.navigateToService(3);
        } if (id == R.id.onedrive) {
            this.navigateToService(4);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToHome() {
        spe = sp.edit();
        spe.putInt("service", 0);
        spe.apply();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = Home.newInstance();
        fragmentTransaction.replace(R.id.content, content);
        fragmentTransaction.commit();
    }

    private void navigateToService(int service) {
        spe = sp.edit();
        spe.putInt("service", service);
        spe.apply();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = Files.newInstance(service);
        fragmentTransaction.replace(R.id.content, content, "files");
        fragmentTransaction.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Files fragment = (Files) getFragmentManager().findFragmentByTag("files");
            if (fragment != null) {
                fragment.search(query);
            }
        } else if(intent.getCategories().contains("android.intent.category.BROWSABLE")) {
            CloudRail.setAuthenticationResponse(intent);
        }
        super.onNewIntent(intent);
    }
}
