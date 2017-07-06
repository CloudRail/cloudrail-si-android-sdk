# Unified Points Of Interest Example

This project demonstrates how CloudRail SI's Points of Interest interface can be used to easily integrate a search for nearby points of interest.

## Prerequisites
You need to have a CloudRail API key that you can [get fro free here](https://cloudrail.com/signup). You also need developer credentials for the services you want to use. You can find out how to get them [here](https://cloudrail.com/integrations/interfaces/PointsOfInterest;serviceIds=Foursquare%2CGooglePlaces%2CYelp).

If you have the necessary keys, find the following piece of code in your *POIResult.java* file:

````java
private void initServices(Context context) {
    poi = new GooglePlaces(context, "Google Places API Key");
//  poi = new Yelp(context, "[Yelp Consumer Key]", "[Yelp Consumer Secret]", "[Yelp Token]", "[Yelp Token Secret]");
//  poi = new Foursquare(context, "[Foursquare Client Identifier]", "[Foursquare Client Secret]");
    }
````
To change the service, you have to add / delete comments.

To run the application, you have to have the location service of your device activated.

## Using the app

When opening the app, the user can choose for which category they want to see Points of Interest. Depending on the used service, varying POIs will appear, as seen below (left: Google Places, middle: Yelp, right: Foursquare). When clicking on an entry, the user will be redirected to the device's maps app.

![screenshot1](https://github.com/CloudRail/cloudrail.github.io/raw/master/img/android_demo_poiFinder.png)
