package com.cloudrail.unifiedemailsender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.cloudrail.si.CloudRail;
import com.cloudrail.si.interfaces.Email;
import com.cloudrail.si.services.MailJet;
import com.cloudrail.si.services.SendGrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MailJet mailJet;
    private SendGrid sendGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CloudRail.setAppKey("[Your CloudRail API key]");
        mailJet = new MailJet(this, "[Public MailJet API Key]", "[Secret MailJet API Key]");
        sendGrid = new SendGrid(this, "[SendGrid API Key]");


        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        final Email service;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.mailJetRadioButton: {
                service = mailJet;
                break;
            }
            case R.id.sendGridRadioButton: {
                service = sendGrid;
                break;
            }
            default:
                throw new RuntimeException("Unknown Button ID!!");
        }

        final String fromAdr = ((EditText) findViewById(R.id.senderEditText)).getText().toString().trim();
        final String fromName = fromAdr;
        String toAdresses = ((EditText) findViewById(R.id.receiverEditText)).getText().toString();
        final List<String> toAdressesList = Arrays.asList(toAdresses.split(","));
        for (String s : toAdressesList) {
            s = s.trim();
        }
        final String subject = ((EditText) findViewById(R.id.subjectEditText)).getText().toString().trim();
        final String textBody = ((EditText) findViewById(R.id.messageEditText)).getText().toString().trim();
        final String htmlBody = textBody;
        final List<String> ccAdresses = null;
        final List<String> bccAdresses = null;

        String serviceStr = "mailJet";
        if (service == sendGrid) serviceStr = "sendGrid";
        System.out.println("from: " + fromAdr + "  to: " + toAdresses + "  with" + serviceStr + "  subject: " + subject);

        new Thread(new Runnable() {
            @Override
            public void run() {
                service.sendEmail(fromAdr, fromName, toAdressesList, subject, textBody, htmlBody, ccAdresses, bccAdresses);
            }
        }).start();
    }
}
