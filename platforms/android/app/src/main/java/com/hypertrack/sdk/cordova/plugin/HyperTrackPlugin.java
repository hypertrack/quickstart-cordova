package com.hypertrack.sdk.cordova.plugin;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hypertrack.sdk.Blocker;
import com.hypertrack.sdk.GeotagResult;
import com.hypertrack.sdk.HyperTrack;
import com.hypertrack.sdk.ServiceNotificationConfig;
import com.hypertrack.sdk.TrackingError;
import com.hypertrack.sdk.TrackingStateObserver;
import com.hypertrack.sdk.logger.HTLogger;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class HyperTrackPlugin extends CordovaPlugin implements TrackingStateObserver.OnTrackingStateChangeListener {
 
	private static final String TAG = "HyperTrackPlugin";
	
	private HyperTrack sdkInstance;
	private CallbackContext statusUpdateCallback;

	private final Gson mGson = new Gson();
	 
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
				case "getBlockers":
					Set<Blocker> blockers = HyperTrack.getBlockers();
					callbackContext.success(serializeBlockers(blockers));
					return true;
				case "resolveBlocker":
					String blockerCode = args.getString(0);
					resolveBlocker(callbackContext, blockerCode);
					return true;
				case "getDeviceId":
					throwIfNotInitialized();
					String deviceId = sdkInstance.getDeviceID();
					callbackContext.success(deviceId);
					return true;
				case "start":
					throwIfNotInitialized();
					sdkInstance.start();
					callbackContext.success();
					return true;
				case "stop":
					throwIfNotInitialized();
					sdkInstance.stop();
					callbackContext.success();
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
					Map<String, Object> meta = mGson.fromJson(deviceMetaJson, new TypeToken<Map<String, Object>>() {}.getType());
					sdkInstance.setDeviceMetadata(meta);
					callbackContext.success();
					return true;
				case "setTrackingNotificationProperties":
					throwIfNotInitialized();
					String title = args.getString(0);
					String body = args.getString(1);
					sdkInstance.setTrackingNotificationConfig(
						new ServiceNotificationConfig.Builder()
								.setContentTitle(title)
								.setContentText(body)
								.build()
				    );
					callbackContext.success();
					return true;
				case "addGeoTag":
					throwIfNotInitialized();
					String tagMetaJson = args.getString(0);
					Location expectedLocation = getExpectedLocation(args);
					Map<String, Object> payload = mGson.fromJson(tagMetaJson, new TypeToken<Map<String, Object>>() {}.getType());
					GeotagResult result = sdkInstance.addGeotag(payload, expectedLocation);
					if (result instanceof  GeotagResult.Success) {
						HTLogger.d(TAG, "Geotag created successfully " + result);
						callbackContext.success(getLocationJson(result));
					} else {
						HTLogger.w(TAG, "Geotag error:" + result);
						GeotagResult.Error error = (GeotagResult.Error) result;
						callbackContext.error(error.getReason().ordinal());
					}
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

	private void resolveBlocker(CallbackContext callbackContext, String blockerCode) {
		switch (blockerCode) {
			case "OL1": Blocker.LOCATION_PERMISSION_DENIED.resolve();
						callbackContext.success();
						break;
			case "OS1": Blocker.LOCATION_SERVICE_DISABLED.resolve();
						callbackContext.success();
						break;
			case "OA1": Blocker.ACTIVITY_PERMISSION_DENIED.resolve();
						callbackContext.success();
						break;
			case "OL2": Blocker.BACKGROUND_LOCATION_DENIED.resolve();
						callbackContext.success();
						break;
			default:
				callbackContext.error("Unknown blocker code " + blockerCode);
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
		Log.i(TAG, "expected location argument " + args.optString(1));
		SerializedLocation serializedLocation = mGson.fromJson(args.optString(1), SerializedLocation.class);
		Log.i(TAG, "Serializedlocation " + serializedLocation);
		return serializedLocation.asLocation();
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

	@NonNull
	private JSONArray serializeBlockers(Set<Blocker> blockers) {
		JSONArray result = new JSONArray();
		for (Blocker blocker: blockers) {
			try {
				JSONObject serializedBlocker = new JSONObject();
				serializedBlocker.put("userActionTitle", blocker.userActionTitle);
				serializedBlocker.put("userActionExplanation", blocker.userActionExplanation);
				serializedBlocker.put("userActionCTA", blocker.userActionCTA);
				serializedBlocker.put("code", blocker.code);
				result.put(serializedBlocker);
			} catch (JSONException e) {
				Log.w(TAG, "Got exception serializing blocker.", e);
			}
		}
		return result;
	}

	private JSONObject getLocationJson(GeotagResult result) {
		assert result instanceof GeotagResult.Success;
		GeotagResult.Success success = (GeotagResult.Success) result;
		JSONObject json = new JSONObject();
		Location location = success.getDeviceLocation();
		try {
			json.put("latitude", location.getLatitude());
			json.put("longitude", location.getLongitude());
			if (result instanceof GeotagResult.SuccessWithDeviation) {
				GeotagResult.SuccessWithDeviation successWithDeviation = (GeotagResult.SuccessWithDeviation) success;
				json.put("deviation", successWithDeviation.getDeviationDistance());
			}
		} catch (JSONException e) {
			HTLogger.w(TAG, "Can't serialize Json", e);
		}
		return json;
	}

	@Override public void onError(TrackingError trackingError) { sendUpdate(trackingError.message); }

	@Override public void onTrackingStart() { sendUpdate("start"); }

	@Override public void onTrackingStop() { sendUpdate("stop"); }

	static class SerializedLocation{
		@SerializedName("latitude") public double latitude = Double.NaN;
		@SerializedName("longitude") public double lognitude = Double.NaN;
		@SerializedName("deviation") public Integer deviation = null;
		@SerializedName("isRestricted") public Boolean isRestricted = false;

		Location asLocation() {
			Location result = new Location("any");
			result.setLatitude(latitude);
			result.setLongitude(lognitude);
			return result;
		}
	}

}
