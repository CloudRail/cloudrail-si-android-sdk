package com.cloudrail.unifiedvideo;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.cloudrail.si.interfaces.Video;
import com.cloudrail.si.services.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class VideoServiceActivity extends AppCompatActivity {
    private int PICK_VIDEO_REQUEST = 1;
    Video service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_service);

        Intent intent = getIntent();
        String serviceString = intent.getStringExtra(MainActivity.EXTRA_SERVICE_STRING);

        service = initService(serviceString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_upload_video:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_VIDEO_REQUEST);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Toast.makeText(getApplicationContext(), "starting upload", Toast.LENGTH_SHORT).show();
            final Uri uri = data.getData();


            new Thread(new Runnable() {
                @Override
                public void run() {
                    // run in background
                    try {
                        File videoFile = new File(uri.getPath());
                        InputStream stream = new FileInputStream(videoFile);
                        Long size = videoFile.length();
                        String channelId = service.getOwnChannel().getId();
                        String type = getContentResolver().getType(uri);
                        service.uploadVideo("some title", "some description", stream, size, channelId, type);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "finished upload", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private Video initService(String serviceString) {
        switch (serviceString) {
            case "Twitch":
                return new Twitch(
                        this,
                        "6ozfh1e33lk0q6gne9wp9ynvq4fo49",
                        "3pmumzzork9rws6ftu2cwov5v03yka"
                );
            case "Vimeo":
                return new Vimeo(
                        this,
                        "660e0bc0ea56d413e1c932ad28b046405008865d",
                        "v82zukoR8Fzhmr47Ia5yrLieClJSUnyhTNqI7VCUMx57LRpfwfF7X3T3Jmut1R3KKqFGsCs7yZbIgx50Cevg/eyHTrES8JTzB1bOhTgRDQjzqy+qslksfwvAEVUxnTm3"
                );
        }
        return new YouTube(
                this,
                "141870010879-1b96e5c9gc89t6920bbm23emi3jnlgbc.apps.googleusercontent.com",
                "",
                "com.cloudrail.unifiedvideo:/oauth2redirect",
                "someState"
        );
    }
}
