package com.example.luwu.lwmap;

import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;


/**
 * Created by luwu on 16/11/14.
 */
public class AMapModule extends AMapBaseModule {
    private static final String REACT_CLASS = "AMapModule";

    private Marker marker;
    public AMapModule(ReactApplicationContext reactContext){
        super(reactContext);
        context = reactContext;
    }

    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void setMarker(double latitude, double longitude) {
        if(marker != null) {
            marker.remove();
        }
        LatLng point = new LatLng(latitude, longitude);
        MarkerOptions option = new MarkerOptions()
                .position(point);
        marker = (Marker)getMap().addMarker(option);
    }

    protected MapView getMapView(){
        return AMapViewManager.getMapView();
    }

    protected AMap getMap(){
        return AMapViewManager.getMapView().getMap();
    }
}
