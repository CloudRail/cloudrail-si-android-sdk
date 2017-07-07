package com.cloudrail.unifiedsmssender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.cloudrail.si.CloudRail;
import com.cloudrail.si.interfaces.SMS;
import com.cloudrail.si.services.Nexmo;
import com.cloudrail.si.services.Twilio;
import com.cloudrail.si.services.Twizo;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Twilio twilio;
    Twizo twizo;
    Nexmo nexmo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CloudRail.setAppKey("[Your CloudRail API key]");
        twilio = new Twilio(this, "[Twilio API ID]", "[Twilio API Secret]");
        twizo = new Twizo(this, "[SendGrid API Key]");
        nexmo = new Nexmo(this, "[Nexmo API ID]", "[Nexmo API Secret]");


        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        final SMS service;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.twilioRadioButton: {
                service = twilio;
                break;
            }
            case R.id.twizoRadioButton: {
                service = twizo;
                break;
            }
            case R.id.nexmoRadioButton: {
                service = nexmo;
                break;
            }
            default:
                throw new RuntimeException("Unknown Button ID!!");
        }

        final String fromName = ((EditText) findViewById(R.id.senderEditText)).getText().toString().trim();
        final String toNumber = ((EditText) findViewById(R.id.receiverEditText)).getText().toString().trim();
        final String message = ((EditText) findViewById(R.id.messageEditText)).getText().toString().trim();

        String serviceStr = "twilio";
        if (service == twizo) serviceStr = "twizo";
        if (service == nexmo) serviceStr = "nexmo";
        System.out.println("from: " + fromName + "  to: " + toNumber + "  with: " + serviceStr + "  message: " + message);

        new Thread(new Runnable() {
            @Override
            public void run() {
                service.sendSMS(fromName, toNumber, message);
            }
        }).start();
    }
}
