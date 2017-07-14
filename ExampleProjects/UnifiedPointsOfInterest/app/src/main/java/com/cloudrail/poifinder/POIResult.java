package com.cloudrail.poifinder;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
    private List<PointsOfInterest> poiServices;
    private PointsOfInterest googlePois;
    private List<ListView> listViews;
    private GoogleApiClient mGoogleApiClient;
    private Context context;


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

        LinearLayout layout = (LinearLayout) v.findViewById(R.id.poiFragment);
        listViews = new ArrayList<ListView>();
        for (int i = 0; i < poiServices.size(); i++) {
            final ListView listView = new ListView(context);
            listView.setScrollbarFadingEnabled(false);
            if (i%2 == 1) {
                listView.setBackgroundColor(0xFFBBBBBB);
            }

            TextView header = new TextView(context);
            header.setText(poiServices.get(i).toString());
            header.setTextAppearance(context, android.R.style.TextAppearance_Medium);

            listView.addHeaderView(header);

            listViews.add(listView);
            layout.addView(listView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
            System.out.println("adding listview " + i);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    POIAdapter poiAdapter = (POIAdapter) ((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter();
                    Pair<POI, Long> item = poiAdapter.getItem(position);
                    com.cloudrail.si.types.Location location = item.first.getLocation();

                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + location.getLatitude() + "," +
                            location.getLongitude() + "(" + Uri.encode(item.first.getName()) + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        }

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
            System.out.println("POIResult::lat = " + location.getLatitude());
            for (int i = 0; i < poiServices.size(); i++) {
                getPOIs(i, location);
            }
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
        poiServices = new ArrayList<PointsOfInterest>();
        poiServices.add(new GooglePlaces(context, "[Google Places API Key]"));
        poiServices.add(new Yelp(context, "[Yelp Consumer Key]", "[Yelp Consumer Secret]", "[Yelp Token]", "[Yelp Token Secret]"));
        poiServices.add(new Foursquare(context, "[Foursquare Client Identifier]", "[Foursquare Client Secret]"));
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

    private void getPOIs(final int serviceNumber, final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                List<String> categories = new ArrayList<>();
                categories.add(mCategory);

                List<POI> list = poiServices.get(serviceNumber).getNearbyPOIs(lat, lng, 5000L, null, categories);

                final List<Pair<POI, Long>> poiList = new ArrayList<>();

                for (POI poi : list) {
                    com.cloudrail.si.types.Location location = poi.getLocation();
                    Pair<POI, Long> elem = new Pair<>(poi, distFrom(lat, lng, location.getLatitude(), location.getLongitude()));
                    poiList.add(elem);
                }

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        POIAdapter poiAdapter = new POIAdapter(context, R.layout.list_poi, poiList);

                        listViews.get(serviceNumber).setAdapter(poiAdapter);
                    }
                });

            }
        }).start();
    }
}
