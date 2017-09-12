package com.cloudrail.unifiedvideo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cloudrail.si.CloudRail;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String EXTRA_SERVICE_STRING = "com.cloudrail.unifiedvideo.service";
    String[] services = {"Twitch", "Vimeo", "YouTube"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CloudRail.setAppKey("");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, services));

        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String serviceString = services[i];

        Intent intent = new Intent(this, VideoServiceActivity.class);
        intent.putExtra(EXTRA_SERVICE_STRING, serviceString);
        startActivity(intent);
    }
}
