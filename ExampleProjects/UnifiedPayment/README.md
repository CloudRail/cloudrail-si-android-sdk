# Unified Payment Example

This application demonstrates how you can use the [CloudRail SI Unified Payment API](https://cloudrail.com/integrations/interfaces/Payment) to easily integrate different payment services (PayPal and Stripe). Want to build something similar? [Get started with CloudRail](https://cloudrail.com/signup) today.

## Prerequisites
You need to have Android Studio ([installation manual](https://developer.android.com/studio/install.html)) installed and need developer credentials for the services you want to use. Ito get them can be found on our [Unified Payment API site](https://cloudrail.com/integrations/interfaces/Payment). You also need a CloudRail API key that you can [get here](https://cloudrail.com/signup).

Find the following piece of code in your *MainActivity.java* file and fill in your credentials:

````java
public final static String CLOUDRAIL_APP_KEY = "";

public final static String PAYPAL_CLIENT_IDENTIFIER = "";
public final static String PAYPAL_CLIENT_SECRET = "";

public final static String STRIPE_SECRET_KEY = "";
````

## Using the app
After choosing a service, the user sees a list of all transactions performed with the service within the last year. By clicking on Add Charge (the pencil button) in the ActionBar menu, the user can add a new charge. Be aware that the services may restrict the possible inputs when you are in testing mode. For example, Stripe accepts [these credit cards](https://stripe.com/docs/testing). When long-clicking on a charge, the user can refund a charge, either fully or only partially. When clicking on a charge, an overlay pops up that shows the charge information and all the refunds connected to it.

![screenshots 1](https://github.com/CloudRail/cloudrail.github.io/raw/master/img/android_demo_payment.png)
