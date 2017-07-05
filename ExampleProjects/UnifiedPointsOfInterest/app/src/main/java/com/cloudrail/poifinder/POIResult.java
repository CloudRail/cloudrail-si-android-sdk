package com.cloudrail.poifinder;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cloudrail.si.interfaces.PointsOfInterest;
import com.cloudrail.si.services.Foursquare;
import com.cloudrail.si.services.GooglePlaces;
import com.cloudrail.si.services.Yelp;
import com.cloudrail.si.types.POI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link POIResult#newInstance} factory method to
 * create an instance of this fragment.
 */
public class POIResult extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String ARG_CATEGORY = "category";

    private String mCategory;
    private PointsOfInterest poi;
    private GoogleApiClient mGoogleApiClient;
    private Context context;
    private ListView listView;


    public POIResult() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category The category of POIs to search for.
     * @return A new instance of fragment POIResult.
     */
    public static POIResult newInstance(String category) {
        POIResult fragment = new POIResult();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_poiresult, container, false);

        listView = (ListView) v.findViewById(R.id.poiList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                POIAdapter poiAdapter = (POIAdapter) listView.getAdapter();
                Pair<POI, Long> item = poiAdapter.getItem(position);
                com.cloudrail.si.types.Location location = item.first.getLocation();

                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + location.getLatitude() + "," +
                        location.getLongitude() + "(" + Uri.encode(item.first.getName()) + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        initServices(context);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        initServices(activity);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        context = activity;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            new GetPOIsTask().execute(location);
        } catch (SecurityException se) {
            throw new RuntimeException("Missing permission to access location data.");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void initServices(Context context) {
        poi = new GooglePlaces(context, "AIzaSyBO1nbR0ZaDct5po9vwXapteN7gsQkCEGQ");
//        poi = new Yelp(context, "iz0cItpo8zES7apeuK_aBQ", "ZGSKN2XmEV8j0melw_PvZX3pZPo",
//                "zP9c2NmgfYAIclIj6_6J74KJJklRda08", "GQTmYLfx441vsYQphKUb5ctVEc4");
//        poi = new Foursquare(context, "CHP45LRN1001UWCO2TLU0USWMBXY2OFMAJSPRP5AJ0IBKNSC",
//                "5VCXTROQNDHUP1TVUYIWNQZOINMMQDVBN2X5GPH5OSDXZSL4");
    }

    private static long distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return Math.round(dist * 1000);
    }

    private class GetPOIsTask extends AsyncTask<Location, Void, List<Pair<POI, Long>>> {

        @Override
        protected List<Pair<POI, Long>> doInBackground(Location... params) {
            double lat = params[0].getLatitude();
            double lng = params[0].getLongitude();
            List<String> categories = new ArrayList<>();
            categories.add(mCategory);

            List<POI> list = poi.getNearbyPOIs(lat, lng, 5000L, null, categories);

            List<Pair<POI, Long>> res = new ArrayList<>();

            for (POI poi : list) {
                com.cloudrail.si.types.Location location = poi.getLocation();
                Pair<POI, Long> elem = new Pair<>(poi, distFrom(lat, lng, location.getLatitude(), location.getLongitude()));
                res.add(elem);
            }

            return res;
        }

        @Override
        protected void onPostExecute(List<Pair<POI, Long>> pois) {
            POIAdapter poiAdapter = new POIAdapter(context, R.layout.list_poi, pois);
            listView.setAdapter(poiAdapter);
        }
    }
}
