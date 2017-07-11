package com.cloudrail.unifiedpayment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cloudrail.si.CloudRail;

public class MainActivity extends AppCompatActivity implements ServiceSelect.OnServiceSelectedListener {

    //TODO REMOVE KEYS

    public final static String CLOUDRAIL_APP_KEY = "5947b1545f3a46262b370388";

    public final static String PAYPAL_CLIENT_IDENTIFIER = "AWW6zTBEkFz4ridW1QgMIg2-KqpR0Ww04d3nf-rooW97B_c9b09Nt3iynL7GKHB1_7rN39oWMB4Sui0c";
    public final static String PAYPAL_CLIENT_SECRET = "EIJQze3Ykgu56ajtf8mq8q0dUNqC9-kRCUPvHqakO0r-UkTdRy91hs_kOaj06ZYLYU-WmkO4n0qqQbUB";

    public final static String STRIPE_SECRET_KEY = "sk_test_AavUPoLur3i2XwEMZjRYnpmb";

/*    public final static String CLOUDRAIL_APP_KEY = "";

    public final static String PAYPAL_CLIENT_IDENTIFIER = "";
    public final static String PAYPAL_CLIENT_SECRET = "";

    public final static String STRIPE_SECRET_KEY = "";
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CloudRail.setAppKey(CLOUDRAIL_APP_KEY);

        browseToServiceSelection();
    }

    @Override
    public void onBackPressed() {
        Fragment chargeViewer = getFragmentManager().findFragmentByTag("chargeViewer");
        if (chargeViewer != null) {
            browseToServiceSelection();
        } else {
            super.onBackPressed();
        }
    }

    private void browseToServiceSelection() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = ServiceSelect.newInstance();
        fragmentTransaction.replace(R.id.content, content, "serviceSelect");
        fragmentTransaction.commit();
    }

    private void browseToChargeView(String service) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment content = ChargeViewer.newInstance(service);
        fragmentTransaction.replace(R.id.content, content, "chargeViewer");
        fragmentTransaction.commit();
    }

    @Override
    public void onServiceSelected(String service) {
        browseToChargeView(service);
    }
}
