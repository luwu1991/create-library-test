package com.example.luwu.lwmap;


import android.location.Geocoder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeQuery;


import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
/**
 * Created by luwu on 16/11/14.
 */
public class AMapGeolocationModule extends AMapBaseModule implements OnGeocodeSearchListener,AMapLocationListener,OnPoiSearchListener {

    private AMapLocationClient locationClient;
    Boolean isLocation;
    private static Geocoder geocoder;
    private static PoiSearch.Query poiquery;
    public AMapGeolocationModule(ReactApplicationContext reactContext){
        super(reactContext);
        context = reactContext;
    }

    public String getName() {
        return "AMapGeolocationModule";
    }

    private void initLocationClient(){
        isLocation = true;
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        locationClient = new AMapLocationClient(context.getApplicationContext());

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setOnceLocation(true);
        locationClient.setLocationListener(this);
        locationClient.setLocationOption(mLocationOption);
    }

    @ReactMethod
    public void getCurrentPosition(){
        initLocationClient();
        isLocation = true;
        locationClient.startLocation();
    }

    @ReactMethod
    public void reverseGeoCode(double lat, double lng) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(context);
        LatLonPoint point = new LatLonPoint(lat,lng);
        RegeocodeQuery query = new RegeocodeQuery(point,200,GeocodeSearch.AMAP);
        geocoderSearch.setOnGeocodeSearchListener(this);
        geocoderSearch.getFromLocationAsyn(query);

//        RegeocodeAddress address = null;
//        try {
//            address = geocoderSearch
//                    .getFromLocation(query);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (null == address) {
//            WritableMap params = Arguments.createMap();
//            params.putString("title","");
//            sendEvent("onGetReverseGeoCodeResult",params);
//            return;
//        }
//        String addressFormat = address.getFormatAddress();
//        WritableMap params = Arguments.createMap();
//        params.putString("title",addressFormat);
//        sendEvent("onGetReverseGeoCodeResult",params);
    }

    @ReactMethod
    public void POIKeywordsSearch(String city,String addr){
        PoiSearch.Query query = new PoiSearch.Query(addr,"",city);
        query.setPageSize(20);
        query.setPageNum(0);
        poiquery = query;
        PoiSearch poiSearch = new PoiSearch(context,query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiItemSearched(PoiItem item,int index){
        List<WritableMap> markers = new ArrayList<>();
    }

    @Override
    public void onPoiSearched(PoiResult result,int rCode){
//        List<Dictionary> markers = new ArrayList<>();
        WritableArray markers = Arguments.createArray();

        if (rCode == 1000){
            if (result != null && result.getQuery() != null){
                if (result.getQuery().equals(poiquery)){
                    List<PoiItem> poiItems = result.getPois();

                    if (poiItems != null && poiItems.size() >0){
//                        AMapViewManager.getMapView().getMap().clear();

                        for (int i =0;i<poiItems.size();i++){
                            PoiItem item = poiItems.get(i);
                            WritableMap marker = Arguments.createMap();
                            marker.putDouble("latitude",item.getLatLonPoint().getLatitude());
                            marker.putDouble("longitude",item.getLatLonPoint().getLongitude());
                            marker.putString("title",item.getTitle());
                            markers.pushMap(marker);
                        }

                        sendEvent("onGetSearchPOIResult",markers);
                    }
                }
            }else {
                WritableArray params = Arguments.fromArray(markers);
                sendEvent("onGetSearchPOIResult",params);
            }
        }else {
            WritableArray params = Arguments.fromArray(markers);
                sendEvent("onGetSearchPOIResult",params);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation location){
        if (location.getLocationType() == AMapLocation.LOCATION_TYPE_GPS){
            DPoint point = new DPoint(location.getLatitude(),location.getLongitude());
            CoordinateConverter converter = new CoordinateConverter(context);
            converter.from(CoordinateConverter.CoordType.GPS);
            try {
                converter.coord(point);
                DPoint desLatLng = converter.convert();
                WritableMap params = Arguments.createMap();
                params.putDouble("latitude",desLatLng.getLatitude());
                params.putDouble("longitude",desLatLng.getLongitude());
                if (location.getPoiName().length() > 0 ){
                    params.putString("title",location.getPoiName());
                }else {
                    params.putString("title",location.getAddress());
                }

                sendEvent("onGetCurrentLocationPosition",params);
//                locationClient.stopLocation();
            }catch(Exception e){
//                locationClient.stopLocation();
            }

            return;
        }
        WritableMap params = Arguments.createMap();
        params.putDouble("latitude",location.getLatitude());
        params.putDouble("longitude",location.getLongitude());
        if (location.getPoiName().length() > 0 ){
            params.putString("title",location.getPoiName());
        }else {
            params.putString("title",location.getAddress());
        }

        sendEvent("onGetCurrentLocationPosition",params);
//        locationClient.stopLocation();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode){
        if (rCode == 1000){
        WritableMap params = Arguments.createMap();
        params.putString("title",result.getRegeocodeAddress().getFormatAddress());
        sendEvent("onGetReverseGeoCodeResult",params);
        }else {
            WritableMap params = Arguments.createMap();
            params.putString("title","");
            sendEvent("onGetReverseGeoCodeResult",params);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
        // TODO Auto-generated method stub

    }



}
