package com.cloudrail.loginwithsample;

import com.cloudrail.si.CloudRail;
import com.cloudrail.si.interfaces.Profile;
import com.cloudrail.si.servicecode.commands.json.jsonsimple.JSONObject;
import com.cloudrail.si.servicecode.commands.json.jsonsimple.parser.JSONParser;
import com.cloudrail.si.services.Facebook;
import com.cloudrail.si.services.GooglePlus;
import com.cloudrail.si.services.LinkedIn;
import com.cloudrail.si.services.Twitter;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Communication {

    private static final String BASE = "http://10.0.2.2:5000";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient mClient = new OkHttpClient();

    public String registerUserSync(Profile profile) {
        JSONObject body = new JSONObject();
        body.put("state", profile.saveAsString());
        body.put("name", getServiceName(profile));

        RequestBody requestBody = RequestBody.create(JSON, body.toJSONString());
        Request request = new Request.Builder().url(BASE + "/user/authenticate")
//                .addHeader("Authorization", CloudRail.getAppKey())
//                .addHeader("Authorization", "token " + CloudRail.getAppKey())
                .post(requestBody).build();

        Response response = null;
        try {
            response = mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.header("Token");
    }

    public String getStatus(String token) {
        Request request = new Request.Builder().url(BASE + "/user/status")
//                .addHeader("Authorization " + CloudRail.getAppKey(), "token " + token).get().build();
                .addHeader("Authorization", "token " + token).get().build();
        Response response = null;
        String status = null;

        try {
            response = mClient.newCall(request).execute();
            System.out.println(response.body().string());
            JSONObject resp = (JSONObject) new JSONParser().parse(response.body().string());
            System.out.println("Communication::hello");
            System.out.println(resp.toString());
            status = (String) resp.get("status");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    public void updateStatus(String status, String token) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toJSONString());
System.out.println("Communication::appKey= " + CloudRail.getAppKey());
        Request request = new Request.Builder().url(BASE + "/user/status")
                .addHeader("Authorization", "token " + token)
//                .addHeader("Token", token)
                .post(requestBody).build();

        Response response = null;
        try {
            response = mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(response.code() != 200) {
            throw new RuntimeException("Updating status failed with code " + response.code() + "!");
        }
    }

    private String getServiceName(Profile profile) {
        if (profile instanceof Facebook) return "Facebook";
        if (profile instanceof GooglePlus) return "GooglePlus";
        if (profile instanceof Twitter) return "Twitter";
        if (profile instanceof LinkedIn) return "LinkedIn";

        return null;
    }
}
