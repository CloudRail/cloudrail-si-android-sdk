# Unified Points Of Interest Example

This project demonstrates how CloudRail SI's Points of Interest interface can be used to easily integrate a search for nearby points of interest using different service providers.

## Prerequisites

You need to have Android Studio ([installation manual](https://developer.android.com/studio/install.html)) installed and need developer credentials for the services you want to use. Instructions on how they can be acquired can be found on our [Unified Points Of Interest API site](https://cloudrail.com/integrations/interfaces/PointsOfInterest;serviceIds=Foursquare%2CGooglePlaces%2CYelp). You also need a CloudRail API key that you can [get fro free here](https://cloudrail.com/signup).

Find the following piece of code in your *POIResult.java* file and enter your credentials:

````java
private void initServices(Context context) {
    poiServices = new ArrayList<PointsOfInterest>();
    poiServices.add(new GooglePlaces(context, "[Google Places API Key]"));
    poiServices.add(new Yelp(context, "[Yelp Consumer Key]", "[Yelp Consumer Secret]", "[Yelp Token]", "[Yelp Token Secret]"));
    poiServices.add(new Foursquare(context, "[Foursquare Client Identifier]", "[Foursquare Client Secret]"));
}
````

If you do not want to use a specific service, you can add comments to it.

To run the application, you have to have the location service of your device activated.

## Using the app

When opening the app, the user can choose for which category they want to see Points of Interest. They will then be presented the POIs found by each of the services in their vicinity. When clicking on an entry, the user will be redirected to the device's maps app.

![screenshot1](https://github.com/CloudRail/cloudrail.github.io/raw/master/img/android_demo_poiFinder.png)
