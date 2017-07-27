import {
	requireNativeComponent,
	View,
	NativeModules,
	Platform,
	DeviceEventEmitter
} from 'react-native';

import React, {
	Component,
	PropTypes
} from 'react';

import _LWMap from './js/LWMapView.js'
import _LWGeolocation from './js/LWGeolocation.js'


export const LWMapView = _LWMap

export const LWGeolocation = _LWGeolocation