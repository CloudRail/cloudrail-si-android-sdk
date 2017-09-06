package com.cloudrail.fileviewer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.cloudrail.si.CloudRail;
import com.cloudrail.si.exceptions.ParseException;
import com.cloudrail.si.services.Box;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Dropbox;
import com.cloudrail.si.services.Egnyte;
import com.cloudrail.si.services.GoogleDrive;
import com.cloudrail.si.services.OneDrive;
import com.cloudrail.si.services.OneDriveBusiness;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This class encapsulates the different services being used by the application. It also initializes
 * them and persists the authentication data.
 *
 * @author patrick
 */
class Services {
    private final static String CLOUDRAIL_LICENSE_KEY = "5947b1545f3a46262b370388";
    private final static Services ourInstance = new Services();

    private final AtomicReference<CloudStorage> dropbox = new AtomicReference<>();
    private final AtomicReference<CloudStorage> box = new AtomicReference<>();
    private final AtomicReference<CloudStorage> googledrive = new AtomicReference<>();
    private final AtomicReference<CloudStorage> onedrive = new AtomicReference<>();
    private final AtomicReference<CloudStorage> onedrivebusiness = new AtomicReference<>();
    private final AtomicReference<CloudStorage> egnyte = new AtomicReference<>();

    private Activity context = null;

    static Services getInstance() {
        return ourInstance;
    }

    private Services() {
    }

    private void initDropbox() {
        Dropbox db = new Dropbox(context, "[Client ID]", "[Client Secret]", "https://auth.cloudrail.com/com.cloudrail.fileviewer", "someState");
        db.useAdvancedAuthentication();
        dropbox.set(db);
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



    // --------- Public Methods -----------
    void prepare(Activity context) {
        this.context = context;

        CloudRail.setAppKey(CLOUDRAIL_LICENSE_KEY);

        this.initDropbox();
        this.initBox();
        this.initGoogleDrive();
        this.initOneDrive();
        this.initOneDriveBusiness();
        this.initEgnyte();

        SharedPreferences sharedPreferences = context.getPreferences(Context.MODE_PRIVATE);

        try {
            String persistent = sharedPreferences.getString("dropboxPersistent", null);
            if (persistent != null) dropbox.get().loadAsString(persistent);
            persistent = sharedPreferences.getString("boxPersistent", null);
            if (persistent != null) box.get().loadAsString(persistent);
            persistent = sharedPreferences.getString("googledrivePersistent", null);
            if (persistent != null) googledrive.get().loadAsString(persistent);
            persistent = sharedPreferences.getString("onedrivePersistent", null);
            if (persistent != null) onedrive.get().loadAsString(persistent);
            persistent = sharedPreferences.getString("onedrivebusinessPersistent", null);
            if (persistent != null) onedrivebusiness.get().loadAsString(persistent);
            persistent = sharedPreferences.getString("egnytePersistent", null);
            if (persistent != null) egnyte.get().loadAsString(persistent);
        } catch (ParseException e) {}
    }

    CloudStorage getService(int service) {
        AtomicReference<CloudStorage> ret = new AtomicReference<>();

        switch (service) {
            case 1:
                ret = this.dropbox;
                break;
            case 2:
                ret = this.box;
                break;
            case 3:
                ret = this.googledrive;
                break;
            case 4:
                ret = this.onedrive;
                break;
            case 5:
                ret = this.onedrivebusiness;
                break;
            case 6:
                ret = this.egnyte;
            default:
                throw new IllegalArgumentException("Unknown service!");
        }

        return ret.get();
    }

    void storePersistent() {
        SharedPreferences sharedPreferences = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("dropboxPersistent", dropbox.get().saveAsString());
        editor.putString("boxPersistent", box.get().saveAsString());
        editor.putString("googledrivePersistent", googledrive.get().saveAsString());
        editor.putString("onedrivePersistent", onedrive.get().saveAsString());
        editor.putString("onedrivebusinessPersistent", googledrive.get().saveAsString());
        editor.putString("egnytePersistent", onedrive.get().saveAsString());
        editor.apply();
    }
}
