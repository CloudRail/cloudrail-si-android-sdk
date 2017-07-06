# Unified Cloud Storage Example

This demo application let's you manage all of your cloud storage accounts (Dropbox, Google Drive, OneDrive and Box) in a single place. 
It is written for Android and uses the [CloudRail Unified Cloud Storage API](https://cloudrail.com/integrations/interfaces/CloudStorage;platformId=Android;serviceIds=Box%2CDropbox%2CGoogleDrive%2COneDrive). Want to build something similar? [Get started with CloudRail](https://cloudrail.com/signup) today, it's free.

## Prerequisites
You need to have Android Studio ([installation manual](https://developer.android.com/studio/install.html)) installed and need developer credentials for the services you want to use. Instructions on how they can be acquired can be found on our [Unified Cloud Storage API site](https://cloudrail.com/integrations/interfaces/CloudStorage;serviceIds=Box%2CDropbox%2CEgnyte%2CGoogleDrive%2COneDrive%2COneDriveBusiness). You also need a CloudRail API key that you can [get fro free here](https://cloudrail.com/signup).

If you have the necessary keys, find the following piece of code in your *Services.java* file and replace the placeholders with your credentials:

````java
private void initDropbox() {
    dropbox.set(new Dropbox(context, "[Client ID]", "[Client Secret]"));
}
private void initBox() {
    box.set(new Box(context, "[Client ID]", "[Client Secret]"));
}
private void initGoogleDrive() {
    googledrive.set(new GoogleDrive(context, "[Client ID]", "", "com.cloudrail.fileviewer:/oauth2redirect", ""));
    ((GoogleDrive) googledrive.get()).useAdvancedAuthentication();
}
private void initOneDrive() {
    onedrive.set(new OneDrive(context, "[Client ID]", "[Client Secret]"));
}
private void initOneDriveBusiness() {
    onedrivebusiness.set(new OneDriveBusiness(context, "[Client ID]", "[Client Secret]"));
}

private void initEgnyte() {
    egnyte.set(new Egnyte(context, "[Domain]", "[Client ID]", "[Client Secret]"));
}
`````

## Using the app

![screenshot1](https://github.com/CloudRail/cloudrail.github.io/raw/master/img/android_demo_fileViewer_1.png)

![screenshot1](https://github.com/CloudRail/cloudrail.github.io/raw/master/img/android_demo_fileViewer_2.png)
