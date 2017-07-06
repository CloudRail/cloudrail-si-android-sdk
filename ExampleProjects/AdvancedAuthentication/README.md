# Advanced Authentication Sample

This sample should give you a good starting point for implementing the advanced authentication method. The main difference to the standard authentication is the fact that we now open the website where the users need to login to their account in the user's standard browser instead of a WebView. By doing this, users can verify that the website that was opened actually belongs to the provider they selected. The WebView in the standard authentication does not show any details about which site was opened or if the certificate belongs to the provider.

The default method using a WebView is okay if your users trust you and you trust us not to maliciously open a fake website.
If this cannot be asserted, we recommend advanced authentication which requires less trust.

## How to enable it
First, you have to find the following line of code and enter your credentials:

```java
private final static String LICENSE_KEY = "<Your License Key>";
private final static String DB_CLIENT_ID = "<Dropbox Client ID>";
private final static String DB_CLIENT_SECRET = "<Dropbox Client Secret>";
```
Information on how to get developer credentials for Dropbox can be [found here](https://cloudrail.com/integrations/interfaces/CloudStorage;serviceIds=Dropbox).

In order for the SDK to know that you want to use the advanced method you need to set the following flag:

```java
CloudRail.setAdvancedAuthenticationMode(true);
```

This can for instance be done right after you set your license key. By adding this line, whenever a login is necessary, the SDK will open the users browser and display the login page. When the user correctly enters his credentials, he will be redirected to the standard redirect URL: https://www.cloudrailauth.com/auth which will result in a 'page not found' error. To avoid this, we have to declare, that the app wants to handle this URL by adding an intent filter to the manifest file:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:host="www.cloudrailauth.com" android:scheme="https" android:path="/auth" />
</intent-filter>
```

This will give the user the choice to open the website with your app. When the user does so, you need to pass the intent that the browser sent to your activity to our SDK like this:

```java
CloudRail.setAuthenticationResponse(getIntent());
```

After doing so, you just trigger the login process again and the SDK will continue the same way as if it would return from the WebView.

There are some things you need to keep in mind when using this method. For instance, when the browser redirects to your app, the app is restarted so you need to make sure that you save any state you need before triggering the authentication process. A way this could be done is shown in this sample, so go ahead and check it out.


## Using the app
Upon clicking the Dropbox Login button, the user will be redirected to an external browser. Here they can check that they are actually using the correct service.

![screenshot1](https://github.com/CloudRail/cloudrail.github.io/raw/master/img/android_demo_advancedAuthentication.png)