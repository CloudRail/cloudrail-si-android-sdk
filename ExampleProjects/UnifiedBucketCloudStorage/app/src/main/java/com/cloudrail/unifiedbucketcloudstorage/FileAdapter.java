package com.cloudrail.unifiedbucketcloudstorage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cloudrail.si.interfaces.BusinessCloudStorage;
import com.cloudrail.si.types.Bucket;
import com.cloudrail.si.types.BusinessFileMetaData;

import java.util.List;

/**
 * Created by cloudrail on 07/07/17.
 */

public class FileAdapter extends ArrayAdapter<BusinessFileMetaData> {
    private List<BusinessFileMetaData> data;

    public FileAdapter(Context context, int resource, List<BusinessFileMetaData> objects) {
        super(context, resource, objects);
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }

        final BusinessFileMetaData file = this.data.get(position);

        if(file != null) {
            TextView tv = (TextView) v.findViewById(R.id.list_item);
            tv.setText(file.getFileName());
        }
        return v;
    }
}
