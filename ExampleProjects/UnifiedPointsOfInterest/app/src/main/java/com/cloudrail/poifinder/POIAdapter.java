package com.cloudrail.poifinder;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cloudrail.si.types.POI;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The adapter containing the list of POIs. The incoming list will be sorted by distance.
 *
 * @author patrick
 */
public class POIAdapter extends ArrayAdapter<Pair<POI, Long>> {

    private List<Pair<POI, Long>> data;

    public POIAdapter(Context context, int resource, List<Pair<POI, Long>> objects) {
        super(context, resource, objects);
        data = objects;
        Collections.sort(data, new POIComparator());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_poi, null);
        }

        Pair<POI, Long> poi = data.get(position);

        TextView name = (TextView) v.findViewById(R.id.poi_name);
        name.setText(poi.first.getName());

        TextView distance = (TextView) v.findViewById(R.id.poi_distance);
        distance.setText(poi.second.toString() + "m");

        return v;
    }

    @Override
    public Pair<POI, Long> getItem(int position) {
        return data.get(position);
    }

    private class POIComparator implements Comparator<Pair<POI, Long>> {
        @Override
        public int compare(Pair<POI, Long> lhs, Pair<POI, Long> rhs) {
            return lhs.second.compareTo(rhs.second);
        }
    }
}
