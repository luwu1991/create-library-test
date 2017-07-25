import {
	requireNativeComponent,
	NativeModules,
	Platform,
	DeviceEventEmitter
} from 'react-native';

import React, {
	Component,
	PropTypes
} from 'react';


const _module = NativeModules.AMapGeolocationModule;

export default {
	reverseGeoCode(lat, lng) {
			return new Promise((resolve, reject) => {
				try {
					_module.reverseGeoCode(lat, lng);
				} catch (e) {
					reject(e);
					return;
				}
				DeviceEventEmitter.once('onGetReverseGeoCodeResult', resp => {
					resolve(resp);
				});
			});
		},

		getCurrentPosition() {
			if (Platform.OS == 'ios') {
				return new Promise((resolve, reject) => {
					navigator.geolocation.getCurrentPosition((data) => {
						_module.getAmapCoorFromGPSCoor(data.coords.latitude, data.coords.longitude)
							.then((position) => {
								resolve(position);
							})
					}, (error) => {
						reject(error);
					}, {
						enableHighAccuracy: true,
						timeout: 20000,
						maximumAge: 1000
					});
				});
			}
			return new Promise((resolve, reject) => {
				try {
					_module.getCurrentPosition();
				} catch (e) {
					reject(e);
					return;
				}
				DeviceEventEmitter.once('onGetCurrentLocationPosition', resp => {
					resolve(resp);
				});
			});
		},
		POIKeywordsSearch(city, addr) {
			return new Promise((resolve, reject) => {
				try {
					_module.POIKeywordsSearch(city, addr);
				} catch (e) {
					reject(e);
					return;
				}
				DeviceEventEmitter.once('onGetSearchPOIResult', resp => {
					resolve(resp);
				});
			});
		}
}