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

export default class MapView extends Component {
	static propTypes = {
		...View.propTypes,
		showAllMarkers: PropTypes.bool,
		zoom: PropTypes.number,
		center: PropTypes.object,
		marker: PropTypes.object,
		markers: PropTypes.array,
		onMapClick: PropTypes.func,
		onMapLoaded: PropTypes.func,
		onMarkerClick: PropTypes.func
	};

	static defaultProps = {
		showAllMarkers: false,
		marker: null,
		markers: [],
		center: null,
	};

	constructor() {
		super();
	}

	_onChange(event) {
		if (typeof this.props[event.nativeEvent.type] === 'function') {
			this.props[event.nativeEvent.type](event.nativeEvent.params);
		}
	}

	render() {
		return <AMapView {...this.props} onChange={this._onChange.bind(this)}/>;
	}
}

const AMapView = requireNativeComponent('RCTMAMapView', MapView, {
	nativeOnly: {
		onChange: true
	}
});