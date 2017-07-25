package com.example.luwu.lwmap;

import android.app.Activity;
import android.graphics.Color;
import android.os.StrictMode;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.maps2d.MapsInitializer;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.MapsInitializer;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;


import com.amap.api.maps2d.model.Marker;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactContext;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import android.content.pm.PackageManager;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;
/**
 * Created by luwu on 16/11/14.
 */
public class AMapViewManager extends ViewGroupManager<AMapView> {
    private  static final  String REACT_CLASS = "RCTMAMapView";
    private static AMapView mMapView;
    private  ThemedReactContext mReactContent;
    private ReadableArray childrenPoint;
    private Marker mMarker;
    private List<Marker> mMarkers = new ArrayList<>();
    private List<Marker>infoMarkers = new ArrayList<>();
    private List<ReadableMap>markerOptions = new ArrayList<>();
    private TextView mMarkerText;
    private boolean showAllMarkers = false;
    @Override
    public String getName(){
        return REACT_CLASS;
    }

    public  void initSDK(Context context){
        try {
            MapsInitializer.initialize(context);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public AMapView createViewInstance(ThemedReactContext context){
        mReactContent = context;
        if (mMapView != null){
            mMapView.onDestroy();
        }
        mMapView = new AMapView(context,this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setListeners(mMapView);
        try {
            MapsInitializer.initialize(context);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return mMapView;
    }

    @ReactProp(name = "showAllMarkers")
    public void setShowAllMarkers(AMapView mapView,boolean showAllMarkers){
        this.showAllMarkers = showAllMarkers;
    }

    @ReactProp(name="center")
    public void setCenter(AMapView mapView,ReadableMap option){
        if (option != null){
            double latitude = option.getDouble("latitude");
            double longitude = option.getDouble("longitude");
            LatLng point = new LatLng(latitude,longitude);
            float zoom =  mapView.getMap().getCameraPosition().zoom;
            mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(point,zoom));
        }
    }

    @ReactProp(name="marker")
    public void setMarker(AMapView mapView,ReadableMap option){
        if (mMarker != null){
            mMarker.remove();
            mMarker = null;
        }
        if (option != null){
            mMarker = MarkerUtil.addMarker(mapView,option);
//            mMarker.showInfoWindow();
        }
    }

    @ReactProp(name="markers")
    public void setMarkers(AMapView mapView, ReadableArray options) {
        mMarkers.clear();
        markerOptions.clear();
        mapView.getMap().clear();
        for (int i = 0; i < options.size(); i++) {
            ReadableMap option = options.getMap(i);
            if (mMarkers.size() > i + 1 && mMarkers.get(i) != null) {
                MarkerUtil.updateMaker(mMarkers.get(i), option);
            } else {
                mMarkers.add(i, MarkerUtil.addMarker(mapView, option));
                markerOptions.add(i,option);
            }
        }
        if (options.size() < mMarkers.size()) {
            int end = options.size();
            int markerSize = mMarkers.size();
            for (int i = markerSize; i > options.size(); i--) {
                mMarkers.get(i - 1).remove();
                mMarkers.remove(i - 1);
            }
        }
    }



    private void emitMapError(String message, String type) {
        WritableMap error = Arguments.createMap();
        error.putString("message", message);
        error.putString("type", type);

        mReactContent
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("onError", error);
    }


    private void setListeners(final AMapView mapView){
        final AMap map = mapView.getMap();
        if (mMarkerText == null){
            mMarkerText = new TextView(mapView.getContext());
            mMarkerText.setBackgroundResource(R.drawable.popup);
            mMarkerText.setPadding(32,32,32,32);
        }

        map.setOnMapLoadedListener(new AMap.OnMapLoadedListener(){
            @Override
            public void onMapLoaded(){

                sendEvent("onMapLoaded",null);
            }
        });

        map.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                WritableMap writableMap = Arguments.createMap();
                writableMap.putDouble("latitude", latLng.latitude);
                writableMap.putDouble("longitude", latLng.longitude);
                sendEvent("onMapClick", writableMap);
            }
        });

        map.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                WritableMap writableMap = Arguments.createMap();
                WritableMap position = Arguments.createMap();
                position.putDouble("latitude", marker.getPosition().latitude);
                position.putDouble("longitude", marker.getPosition().longitude);
                writableMap.putMap("position", position);
                writableMap.putString("title", marker.getTitle());
                sendEvent("onMarkerClick", writableMap);
                if (infoMarkers.contains(marker)){
                    return true;
                }

                if(marker.getTitle().length() > 0) {
//                    mMarkerText.setText(marker.getTitle());
//
//                    mMarkerText.setVisibility(View.GONE);
                    Boolean show = marker.isInfoWindowShown();
                    if (show){
//                        marker.hideInfoWindow();
                        return true;
                    }else {
                        return false;
                    }
                }
                else {
                    return true;
                }

            }
        });

        map.setOnCameraChangeListener(new AMap.OnCameraChangeListener(){
            @Override
            public void onCameraChange(CameraPosition position){

            }

            @Override
            public void onCameraChangeFinish(CameraPosition position){
                if (showAllMarkers == false){
                    return;
                }
                float zoom =  mapView.getMap().getCameraPosition().zoom;
                if (zoom > 11 && infoMarkers.size() == 0){
                    mapView.getMap().clear();
                    infoMarkers.clear();
                    mMarkers.clear();
                    for (int i = 0; i < markerOptions.size(); i++) {
                        ReadableMap option = markerOptions.get(i);
                        infoMarkers.add(i, MarkerUtil.addMarker(mapView,option ,mReactContent));
                    }
                    return;
                }

                if (zoom < 11  && infoMarkers.size() > 0){
                    mapView.getMap().clear();
                    infoMarkers.clear();
                    mMarkers.clear();
                    for (int i = 0;i < markerOptions.size();i++){
                        ReadableMap option = markerOptions.get(i);
                        mMarkers.add(i, MarkerUtil.addMarker(mapView,option));
                    }
                }
            }
        });

//        map.setInfoWindowAdapter(new AMap.InfoWindowAdapter(){
//            @Override
//            public View getInfoContents(Marker marker) {
//
//                View infoContent = mapView.get(marker);
//                render(marker, infoContent);
//                return infoContent;
//            }
//
//            /**
//             * 监听自定义infowindow窗口的infowindow事件回调
//             */
//            @Override
//            public View getInfoWindow(Marker marker) {
//
//                AMapMarker markerView = (AMapMarker)marker;
//
//                return markerView.getCalloutView();
//            }
//
//            /**
//             * 自定义infowinfow窗口
//             */
//            public void render(Marker marker, View view) {
//
//                String title = marker.getTitle();
//                TextView titleUi = ((TextView) view.findViewById(R.id.title));
//                if (title != null) {
//                    SpannableString titleText = new SpannableString(title);
//                    titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
//                            titleText.length(), 0);
//                    titleUi.setTextSize(15);
//                    titleUi.setText(titleText);
//
//                } else {
//                    titleUi.setText("");
//                }
//                String snippet = marker.getSnippet();
//                TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
//                if (snippet != null) {
//                    SpannableString snippetText = new SpannableString(snippet);
//                    snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
//                            snippetText.length(), 0);
//                    snippetUi.setTextSize(20);
//                    snippetUi.setText(snippetText);
//                } else {
//                    snippetUi.setText("");
//                }
//            }
//        });

    }


    public void pushEvent(View view, String name, WritableMap data) {
        ReactContext reactContext = (ReactContext) view.getContext();
        reactContext.getJSModule(RCTEventEmitter.class)
                .receiveEvent(view.getId(), name, data);
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        WritableMap event = Arguments.createMap();
        event.putMap("params", params);
        event.putString("type", eventName);
        mReactContent
                .getJSModule(RCTEventEmitter.class)
                .receiveEvent(mMapView.getId(),
                        "topChange",
                        event);
    }


    /**
     *
     * @return
     */
    public static MapView getMapView() {
        return mMapView;
    }

}
