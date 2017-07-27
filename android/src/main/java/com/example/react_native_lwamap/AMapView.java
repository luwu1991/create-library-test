package com.example.react_native_lwamap;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapFragment;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.Polygon;
import com.amap.api.maps2d.model.Polyline;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.NoSuchKeyException;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;
/**
 * Created by luwu on 16/11/15.
 */
public class AMapView extends MapView implements AMap.OnMapLoadedListener{
    public AMap map;
    private static final String[] PERMISSIONS = new String[] {
            "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    private final AMapViewManager manager;
    private LifecycleEventListener lifecycleListener;
    private boolean paused = false;
    public AMapView(ThemedReactContext context, AMapViewManager manager){
        super(context);
        this.manager = manager;
        super.onCreate(null);
        super.onResume();
        if (this.getMap() != null) {
            map = this.getMap();
            map.setOnMapLoadedListener(this);
        }
        final AMapView view = this;
    }


    @Override
    public void onMapLoaded(){
        Log.i("AMap", "mapLoaded");

        lifecycleListener = new LifecycleEventListener() {
            @Override
            public void onHostResume() {
                if (hasPermissions()) {
                    //noinspection MissingPermission
                    map.setMyLocationEnabled(false);
                }
                synchronized (AMapView.this) {
                    AMapView.this.onResume();
                    paused = false;
                }
            }

            @Override
            public void onHostPause() {
                if (hasPermissions()) {
                    //noinspection MissingPermission
                    map.setMyLocationEnabled(false);
                }
                synchronized (AMapView.this) {
                    AMapView.this.onPause();
                    paused = true;
                }
            }

            @Override
            public void onHostDestroy() {
                AMapView.this.doDestroy();
            }
        };

        ((ThemedReactContext) getContext()).addLifecycleEventListener(lifecycleListener);
        manager.pushEvent(this, "onMapReady", new WritableNativeMap());
        final AMapView view = this;


    }

    private boolean hasPermissions() {
        return checkSelfPermission(getContext(), PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(getContext(), PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED;
    }

    public synchronized void doDestroy() {
        if (lifecycleListener != null) {
            ((ThemedReactContext) getContext()).removeLifecycleEventListener(lifecycleListener);
            lifecycleListener = null;
        }
        if (!paused) {
            onPause();
        }
        onDestroy();
    }
}
