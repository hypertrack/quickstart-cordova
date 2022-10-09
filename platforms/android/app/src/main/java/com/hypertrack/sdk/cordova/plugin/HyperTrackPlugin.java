package com.hypertrack.sdk.cordova.plugin;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hypertrack.sdk.Availability;
import com.hypertrack.sdk.AvailabilityError;
import com.hypertrack.sdk.AvailabilityStateObserver;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HyperTrackPlugin extends CordovaPlugin implements TrackingStateObserver.OnTrackingStateChangeListener, AvailabilityStateObserver.OnAvailabilityStateChangeListener {

    private static final String TAG = "HyperTrackPlugin";

    private HyperTrack sdkInstance;
    private CallbackContext trackingStatusUpdateCallback;
    private CallbackContext availabilityStatusUpdateCallback;

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) {

        Log.d(TAG, "HyperTrackPlugin execute: " + action);

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
                case "getAvailability":
                    throwIfNotInitialized();
                    Availability availability = sdkInstance.getAvailability();
                    callbackContext.success(availability.toString().toLowerCase());
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
                case "setAvailability":
                    throwIfNotInitialized();
                    boolean isAvailable = args.getBoolean(0);
                    sdkInstance.setAvailability(isAvailable ? Availability.AVAILABLE : Availability.UNAVAILABLE);
                    callbackContext.success();
                    return true;
                case "setDeviceMetadata":
                    throwIfNotInitialized();
                    String deviceMetaJson = args.getString(0);
                    JSONObject deviceMetaJsonObject = new JSONObject(deviceMetaJson);
                    Map<String, Object> meta = jsonToMap(deviceMetaJsonObject);
                    sdkInstance.setDeviceMetadata(meta);
                    callbackContext.success();
                    return true;
                case "setTrackingNotificationProperties":
                    throwIfNotInitialized();
                    String title = args.getString(0);
                    String body = args.getString(1);
                    sdkInstance.setTrackingNotificationConfig(new ServiceNotificationConfig.Builder().setContentTitle(title).setContentText(body).build());
                    callbackContext.success();
                    return true;
                case "addGeoTag":
                    throwIfNotInitialized();
                    String tagMetaJson = args.getString(0);
                    Location expectedLocation = getExpectedLocation(args);
                    JSONObject tagMetaJsonObject = new JSONObject(tagMetaJson);
                    Map<String, Object> payload = jsonToMap(tagMetaJsonObject);
                    GeotagResult result = sdkInstance.addGeotag(payload, expectedLocation);
                    if (result instanceof GeotagResult.Success) {
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
                    callbackContext.success(sdkInstance.isRunning() ? 1 : 0);
                    return true;
                case "isTracking":
                    throwIfNotInitialized();
                    callbackContext.success(sdkInstance.isTracking() ? 1 : 0);
                    return true;
                case "trackingStateChange":
                    throwIfNotInitialized();
                    createTrackingStateChannel(callbackContext);
                    PluginResult subscriptionResult = new PluginResult(PluginResult.Status.NO_RESULT);
                    subscriptionResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(subscriptionResult);
                    return true;
                case "disposeTrackingState":
                    throwIfNotInitialized();
                    disposeTrackingStateChannel();
                    callbackContext.success();
                    return true;
                case "availabilityStateChange":
                    throwIfNotInitialized();
                    createAvailabilityStateChannel(callbackContext);
                    PluginResult subscriptionRes = new PluginResult(PluginResult.Status.NO_RESULT);
                    subscriptionRes.setKeepCallback(true);
                    callbackContext.sendPluginResult(subscriptionRes);
                    return true;
                case "disposeAvailabilityState":
                    throwIfNotInitialized();
                    disposeAvailabilityStateChannel();
                    callbackContext.success();
                    return true;
                default:
                    callbackContext.error("Method not found");
                    return false;
            }
        } catch (Throwable e) {
            Log.d(TAG, "onPluginAction: " + action + ", ERROR: " + e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        }

    }

    private void resolveBlocker(CallbackContext callbackContext, String blockerCode) {
        switch (blockerCode) {
            case "OL1":
                Blocker.LOCATION_PERMISSION_DENIED.resolve();
                callbackContext.success();
                break;
            case "OS1":
                Blocker.LOCATION_SERVICE_DISABLED.resolve();
                callbackContext.success();
                break;
            case "OA1":
                Blocker.ACTIVITY_PERMISSION_DENIED.resolve();
                callbackContext.success();
                break;
            case "OL2":
                Blocker.BACKGROUND_LOCATION_DENIED.resolve();
                callbackContext.success();
                break;
            default:
                callbackContext.error("Unknown blocker code " + blockerCode);
        }
    }

    private void disposeTrackingStateChannel() {
        sdkInstance.removeTrackingListener(this);
        if (trackingStatusUpdateCallback != null) {
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT, "");
            result.setKeepCallback(false);
            trackingStatusUpdateCallback.sendPluginResult(result);
        }
    }

    private void disposeAvailabilityStateChannel() {
        sdkInstance.removeAvailabilityListener(this);
        if (availabilityStatusUpdateCallback != null) {
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT, "");
            result.setKeepCallback(false);
            availabilityStatusUpdateCallback.sendPluginResult(result);
        }
    }

    private void createTrackingStateChannel(CallbackContext callbackContext) {
        trackingStatusUpdateCallback = callbackContext;
        sdkInstance.addTrackingListener(this);
    }

    private void createAvailabilityStateChannel(CallbackContext callbackContext) {
        availabilityStatusUpdateCallback = callbackContext;
        sdkInstance.addAvailabilityListener(this);
    }

    private Location getExpectedLocation(JSONArray args) throws JSONException {
        if (args.length() < 2) return null;
        Log.i(TAG, "expected location argument " + args.optString(1));
        String coordinates = args.optString(1);
        JSONObject coordinate = new JSONObject(coordinates);
        Double latitude = coordinate.getDouble("latitude");
        Double longitude = coordinate.getDouble("longitude");
        Location expectedLocation = new Location(LocationManager.GPS_PROVIDER);
        expectedLocation.setLatitude(latitude);
        expectedLocation.setLongitude(longitude);
        return expectedLocation;
    }

    private void throwIfNotInitialized() throws IllegalStateException {
        if (sdkInstance == null) throw new IllegalStateException("Sdk wasn't initialized");
    }

    private void sendTrackingUpdate(String update) {
        if (trackingStatusUpdateCallback != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, update);
            result.setKeepCallback(true);
            trackingStatusUpdateCallback.sendPluginResult(result);
        }
    }

    private void sendAvailabilityUpdate(String update) {
        if (availabilityStatusUpdateCallback != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, update);
            result.setKeepCallback(true);
            availabilityStatusUpdateCallback.sendPluginResult(result);
        }
    }

    @NonNull
    private JSONArray serializeBlockers(Set<Blocker> blockers) {
        JSONArray result = new JSONArray();
        for (Blocker blocker : blockers) {
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

    private Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    private Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    @Override
    public void onError(TrackingError trackingError) {
        sendTrackingUpdate(trackingError.message);
    }

    @Override
    public void onTrackingStart() {
        sendTrackingUpdate("start");
    }

    @Override
    public void onTrackingStop() {
        sendTrackingUpdate("stop");
    }

    @Override
    public void onError(AvailabilityError availabilityError) {
        sendAvailabilityUpdate(availabilityError.toString());
    }

    @Override
    public void onAvailable() {
        sendAvailabilityUpdate("available");
    }

    @Override
    public void onUnavailable() {
        sendAvailabilityUpdate("unavailable");
    }

}
