# Unified Bucket Cloud Storage Example

This demo application lets you browse and download files saved with a bucket cloud service (Amazon Web Services S3, Backblaze, Google Cloud Platform, Microsoft Azure Storage or Rackspace). It is written for Android and uses the [CloudRail Unified Bucket Cloud Storage API](https://cloudrail.com/integrations/interfaces/BusinessCloudStorage). Want to build something similar? [Get started with CloudRail](https://cloudrail.com/signup) today, it's free.

## Prerequisites
You need to have Android Studio ([installation manual](https://developer.android.com/studio/install.html)) installed and need developer credentials for the services you want to use. Instructions on how they can be acquired can be found on our [Unified Cloud Storage API site](https://cloudrail.com/integrations/interfaces/CloudStorage;serviceIds=Box%2CDropbox%2CEgnyte%2CGoogleDrive%2COneDrive%2COneDriveBusiness). You also need a CloudRail API key that you can [get fro free here](https://cloudrail.com/signup).


Find the following piece of code in your *MainActivity.java* file and fill in your credentials:

````java
public final static String CLOUDRAIL_APP_KEY = "5947b1545f3a46262b370388";

public final static String AMAZON_ACCESS_KEY = "AKIAJI3FPLEUVE7BNUGQ";
public final static String AMAZON_SECRET_ACCESS_KEY = "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";
public final static String AMAZON_REGION = "eu-central-1";

ublic final static String AMAZON_ACCESS_KEY = "";
public final static String AMAZON_SECRET_ACCESS_KEY = "";
public final static String AMAZON_REGION = "";


public final static String BACKBLAZE_ACCOUNT_ID = "";
public final static String BACKBLACE_APP_KEY = "";

public final static String GOOGLE_CLIENT_EMAIL = "";
public final static String GOOGLE_PRIVATE_KEY = "";
public final static String GOOGLE_PROJECT_ID = "";

public final static String AZURE_ACCOUNT_NAME = "";
public final static String AZURE_ACCESS_KEY = "";

public final static String RACKSPACE_USER_NAME = "";
public final static String RACKSPACE_API_KEY = "";
````

## Using the app

After choosing a service, the user sees a list of buckets. They can add new buckets by clicking the "new folder" icon in the top-right, refresh by clicking the "refresh" icon next to it and delete buckets by long-pressing them. When clicking on a bucket, the app will show the list of this bucket's files. The user can download or delete them from the bucket by long-pressing them. They can also refresh by clicking the "refresh" icon in the top-right.

![screenshots 1](https://github.com/CloudRail/cloudrail.github.io/raw/master/img/android_demo_bucketCloud.png)