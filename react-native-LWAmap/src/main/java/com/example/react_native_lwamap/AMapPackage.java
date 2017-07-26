package com.example.react_native_lwamap;

import  android.app.Activity;

import android.content.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;

import com.facebook.react.uimanager.ViewManager;

/**
 * Created by luwu on 16/11/14.
 */
public class AMapPackage implements  ReactPackage  {
    private  Context mContext;
    AMapViewManager aMapViewManager;

    public AMapPackage (Context content){
        this.mContext = content;
        aMapViewManager = new AMapViewManager();
        aMapViewManager.initSDK(content);
    }
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(
                new AMapModule(reactContext),
                new AMapGeolocationModule(reactContext)
        );
    }

    @Override
    public List<ViewManager> createViewManagers(
            ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList(
                aMapViewManager
        );
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

}
