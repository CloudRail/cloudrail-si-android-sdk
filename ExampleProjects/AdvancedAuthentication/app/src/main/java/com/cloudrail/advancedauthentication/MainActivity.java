package com.cloudrail.advancedauthentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cloudrail.si.CloudRail;
import com.cloudrail.si.exceptions.ParseException;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Dropbox;

public class MainActivity extends AppCompatActivity {

    private final static String LOGIN_PENDING = "LOGIN_PENDING";
    private final static String AUTH_DATA = "AUTH_DATA";

    // TODO: Fill in your credentials here
    private final static String LICENSE_KEY = "[Your CloudRail Key]";
    private final static String DB_CLIENT_ID = "[Dropbox Client Identifier]";
    private final static String DB_CLIENT_SECRET = "[Dropbox Client Secret]";

    private CloudStorage cs = new Dropbox(this, DB_CLIENT_ID, DB_CLIENT_SECRET);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure CloudRail
        CloudRail.setAppKey(LICENSE_KEY);
        CloudRail.setAdvancedAuthenticationMode(true);

        // Register click listener
        Button dbLogin = (Button) findViewById(R.id.dropbox);
        dbLogin.setOnClickListener(new StartLogin());

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        if (sharedPreferences.getString(AUTH_DATA, null) != null) {
            /* If there is already a logged in user, load data */

            try {
                cs.loadAsString(sharedPreferences.getString(AUTH_DATA, null));
                new GetUserName().execute();
            } catch (ParseException e) {
                // If parsing fails, remove user data -> new login required
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(AUTH_DATA);
                editor.apply();
            }
        } else if (sharedPreferences.getBoolean(LOGIN_PENDING, false)) {
            /* If a login is pending, continue it */

            // Set pending login to false
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(LOGIN_PENDING, false);
            editor.apply();

            // Set result and continue login
            CloudRail.setAuthenticationResponse(getIntent());
            new Thread() {
                @Override
                public void run() {
                    cs.login();

                    editor.putString(AUTH_DATA, cs.saveAsString());
                    editor.apply();

                    new GetUserName().execute();
                }
            }.start();
        } else {
            TextView tv = (TextView) findViewById(R.id.username);
            tv.setText("not logged in");
        }
    }

    private class StartLogin implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Set the login to pending so that we now that a login process has been started
            editor.putBoolean(LOGIN_PENDING, true);
            editor.apply();

            // Start the login process
            new Thread() {
                @Override
                public void run() {
                    cs.login();
                }
            }.start();
        }
    }

    private class GetUserName extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return cs.getUserName();
        }

        @Override
        protected void onPostExecute(String s) {
            TextView tv = (TextView) findViewById(R.id.username);
            tv.setText("logged in as " + s);
        }
    }
}
