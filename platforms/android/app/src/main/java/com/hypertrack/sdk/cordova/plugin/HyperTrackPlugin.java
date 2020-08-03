package com.hypertrack.sdk.cordova.plugin;

import android.location.Location;
import android.util.Log;


import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hypertrack.sdk.HyperTrack;
import com.hypertrack.sdk.TrackingError;
import com.hypertrack.sdk.TrackingStateObserver;
import com.hypertrack.sdk.utils.StaticUtilsAdapter;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import java.util.Map;

public class HyperTrackPlugin extends CordovaPlugin implements TrackingStateObserver.OnTrackingStateChangeListener {
 
	private static final String TAG = "HyperTrackPlugin";
	
	private HyperTrack sdkInstance;
	private CallbackContext statusUpdateCallback;
	 
	@Override	 
	public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) {

		Log.d(TAG,"HyperTrackPlugin execute: " + action);
		
		try {
			switch (action) {
				case "initialize":
					if (sdkInstance == null) {
						String publishableKey = args.getString(0);
						sdkInstance = HyperTrack.getInstance(publishableKey);
					}
					callbackContext.success();
					return true;
				case "enableDebugLogging":
					HyperTrack.enableDebugLogging();
					callbackContext.success();
					return true;
				case "getDeviceId":
					throwIfNotInitialized();
					String deviceId = sdkInstance.getDeviceID();
					callbackContext.success(deviceId);
					return true;
				case "setDeviceName":
					throwIfNotInitialized();
					String deviceName = args.getString(0);
					sdkInstance.setDeviceName(deviceName);
					callbackContext.success();
					return true;
				case "setDeviceMetadata":
					throwIfNotInitialized();
					String deviceMetaJson = args.getString(0);
					Map<String, Object> meta = StaticUtilsAdapter.getGson().fromJson(deviceMetaJson, new TypeToken<Map<String, Object>>() {}.getType());
					Location expectedLocation = getExpectedLocation(args);
					sdkInstance.addGeotag(meta, expectedLocation);
					callbackContext.success();
					return true;
				case "addGeoTag":
					throwIfNotInitialized();
					String tagMetaJson = args.getString(0);
					Map<String, Object> payload = StaticUtilsAdapter.getGson().fromJson(tagMetaJson, new TypeToken<Map<String, Object>>() {}.getType());
					sdkInstance.addGeotag(payload);
					callbackContext.success();
					return true;
				case "requestPermissionsIfNecessary":
					throwIfNotInitialized();
					sdkInstance.requestPermissionsIfNecessary();
					callbackContext.success();
					return true;
				case "allowMockLocations":
					throwIfNotInitialized();
					sdkInstance.allowMockLocations();
					callbackContext.success();
					return true;
				case "syncDeviceSettings":
					throwIfNotInitialized();
					sdkInstance.syncDeviceSettings();
					callbackContext.success();
					return true;
				case "isRunning":
					throwIfNotInitialized();
					callbackContext.success(sdkInstance.isRunning()?1:0);
					return true;
				case "subscribe":
					throwIfNotInitialized();
					createTrackingStateChannel(callbackContext);
					PluginResult subscriptionResult = new PluginResult(PluginResult.Status.NO_RESULT);
					subscriptionResult.setKeepCallback(true);
					callbackContext.sendPluginResult(subscriptionResult);
					return true;
				case "unsubscribe":
					throwIfNotInitialized();
					disposeTrackingStateChannel();
					PluginResult unsubscribeResult = new PluginResult(PluginResult.Status.NO_RESULT);
					unsubscribeResult.setKeepCallback(true);
					callbackContext.sendPluginResult(unsubscribeResult);
					return true;

				default:
					callbackContext.error("Method not found");
					return false;
			}
		} catch(Throwable e) {
			Log.d(TAG, "onPluginAction: " + action + ", ERROR: " + e.getMessage());
			callbackContext.error(e.getMessage());
			return false;
		}

	}

	private void disposeTrackingStateChannel() {
		sdkInstance.removeTrackingListener(this);
		if (statusUpdateCallback != null) {
			PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT, "");
			result.setKeepCallback(false);
			statusUpdateCallback.sendPluginResult(result);
		}
	}

	private void createTrackingStateChannel(CallbackContext callbackContext) {
		statusUpdateCallback = callbackContext;
		sdkInstance.addTrackingListener(this);
	}

	private Location getExpectedLocation(JSONArray args) {
		if (args.length() < 2) return null;
		SerializedLocation payload = StaticUtilsAdapter.getGson()
				.fromJson(args.optString(1), SerializedLocation.class);
		return payload != null ? payload.asLocation() : null;
	}

	private void throwIfNotInitialized() throws IllegalStateException {
		if (sdkInstance == null) throw new IllegalStateException("Sdk wasn't initialized");
	}

	private void sendUpdate(String update) {
		if (statusUpdateCallback!= null) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, update);
			result.setKeepCallback(true);
			statusUpdateCallback.sendPluginResult(result);

		}
	}

	@Override public void onError(TrackingError trackingError) { sendUpdate(trackingError.message); }

	@Override public void onTrackingStart() { sendUpdate("start"); }

	@Override public void onTrackingStop() { sendUpdate("stop"); }

	static class SerializedLocation{
		@SerializedName("latitude") public double latitude = Double.NaN;
		@SerializedName("longitude") public double lognitude = Double.NaN;

		Location asLocation() {
			Location result = new Location("any");
			result.setLatitude(latitude);
			result.setLongitude(lognitude);
			return result;
		}
	}

}
