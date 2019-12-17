package com.badrul.awla;

import android.app.Application;

import net.gotev.uploadservice.BuildConfig;
import net.gotev.uploadservice.UploadService;

public class Initializer extends Application {


    public void onCreate() {
        super.onCreate();
        // setup the broadcast action namespace string which will
        // be used to notify upload status.
        // Gradle automatically generates proper variable as below.
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // Or, you can define it manually.
        UploadService.NAMESPACE = "com.badrul.awla";

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/sofiapromedium.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/sofiapromedium.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/sofiapromedium.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/sofiapromedium.ttf");
    }
}