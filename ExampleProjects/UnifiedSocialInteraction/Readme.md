# Unified Social Interaction Sample
This sample shows how you can use the CloudRail SI library in order to post status updates to different social networks.

## Prerequisites
You need to have developer credentials for the services you want to use. [Instructions on how to get them can be found here](https://cloudrail.com/integrations/interfaces/Social;interfaceId=Social;interfaceFunctionId=Social-postUpdate;platformId=Android;serviceIds=Facebook%2CFacebookPage%2CTwitter).

## Necessary Code Changes
Locate the following piece of code within your *MainActivity.java* class and replace the placeholder with your CloudRail API key:

````java
CloudRail.setAppKey("[CloudRail API key]");
````

Then, locate this part of code within *ChooseServiceFragment.java* and change the placeholders with your developer credentials.

````java
switch (v.getId()) {
    case R.id.Facebook: {
        service = new Facebook(mContext, "[Client ID]", "[Client Secret]");
        break;
    }
    case R.id.FacebookPages: {
        service = new FacebookPage(mContext, "[PageName]", "[Client ID]", "[Client Secret]");
        break;
    }
    case R.id.Twitter: {
        service = new Twitter(mContext, "[Client ID]", "[Client Secret]");
        break;
    }
    default:
        throw new RuntimeException("Unknown Button ID!!");
}
````

## Using The app
![Screenshot 1](https://cloudrail.github.io/img/android_demo_socialInteraction.png)
At the beginning, the user chooses which service they want to use. They will then be forwarded to the service's login page. After logging in, the user can post updates and optionally add an image or video.
