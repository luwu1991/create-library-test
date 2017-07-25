package com.example.luwu.lwmap;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.List;
/**
 * Created by luwu on 16/11/14.
 */
abstract public class AMapBaseModule extends ReactContextBaseJavaModule{
    protected ReactApplicationContext context;
    public AMapBaseModule(ReactApplicationContext reactContext){
        super(reactContext);
        context = reactContext;
    }


    /**
     *
     * @param eventName
     * @param params
     */
    protected void sendEvent(String eventName,@Nullable WritableMap params) {
        context
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
    protected  void  sendEvent(String eventName, @Nullable WritableArray params){
        context
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
    protected  void  sendEvent(String eventName, @Nullable List params){
        context
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
