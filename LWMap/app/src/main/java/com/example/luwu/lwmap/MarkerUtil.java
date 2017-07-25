package com.example.luwu.lwmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.LatLng;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;

/**
 * Created by luwu on 16/11/14.
 */
public class MarkerUtil {

    public static void updateMaker(Marker maker, ReadableMap option) {
        LatLng position = getLatLngFromOption(option);
        maker.setPosition(position);
        maker.setTitle(option.getString("title"));
    }


    public static Marker addMarker(AMapView mapView,ReadableMap option){
        LatLng position = getLatLngFromOption(option);

        Marker marker = mapView.getMap().addMarker(new MarkerOptions().
        position(position)
        .title(option.getString("title"))
        );
        return marker;
    }

    public static Marker addMarker(AMapView mapView, ReadableMap option, ThemedReactContext content){
        LatLng position = getLatLngFromOption(option);
        String title = option.getString("title");
        LayoutInflater inflater = (LayoutInflater)content.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view4 = inflater.inflate(R.layout.custom_info,null);
        TextView textView=(TextView) view4.findViewById(R.id.info_win_txt);
        textView.setText(title);
        Marker marker = mapView.getMap().addMarker(new MarkerOptions().
                position(position)
                .icon(BitmapDescriptorFactory.fromView(view4))
                .title(title)
        );
        return marker;
    }

    private static LatLng getLatLngFromOption(ReadableMap option) {
        double latitude = option.getDouble("latitude");
        double longitude = option.getDouble("longitude");
        return new LatLng(latitude, longitude);

    }


}
