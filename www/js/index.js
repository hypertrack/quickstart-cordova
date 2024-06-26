var app = {
  // Application Constructor
  initialize: function () {
    document.addEventListener(
      "deviceready",
      this.onDeviceReady.bind(this),
      false
    );
  },

  // deviceready Event Handler
  // Bind any cordova events here. Common events are:
  // 'pause', 'resume', etc.
  onDeviceReady: function () {
    this.receivedEvent("deviceready");
  },

  // Update DOM on a Received Event
  receivedEvent: async function (id) {
    try {
      const deviceId = await HyperTrack.getDeviceId();
      console.log("getDeviceId", deviceId);
      document.getElementById("deviceId").textContent = deviceId;

      const name = "Quickstart Cordova";
      HyperTrack.setName(name);
      console.log("setName", name);

      let platformName = "";
      if (device.platform === "Android") {
        platformName = "android";
      } else if (device.platform === "iOS") {
        platformName = "ios";
      }
      let metadata = {
        /**
         * `driver_handle` is used to link the device and the driver.
         * You can use any unique user identifier here.
         * The recommended way is to set it on app login in set it to null on logout
         * (to remove the link between the device and the driver)
         **/
        driver_handle: `test_driver_quickstart_cordova_${platformName}`,
        /**
         * You can also add any custom data to the metadata.
         */
        source: name,
        employee_id: Math.round(Math.random() * 10000),
      };
      HyperTrack.setMetadata(metadata);
      console.log("setMetadata", JSON.stringify(metadata));

      HyperTrack.subscribeToErrors(function (errors) {
        console.log(`listener errors ${JSON.stringify(errors)}`);
        document.getElementById("errorsState").textContent =
          getErrorsText(errors);
      });

      HyperTrack.subscribeToIsAvailable(function (isAvailable) {
        console.log(`listener isAvailable ${isAvailable}`);
        document.getElementById("isAvailableState").textContent =
          JSON.stringify(isAvailable);
      });

      HyperTrack.subscribeToIsTracking(function (isTracking) {
        console.log(`listener isTracking ${isTracking}`);
        document.getElementById("isTrackingState").textContent =
          JSON.stringify(isTracking);
      });

      HyperTrack.subscribeToLocation(function (location) {
        console.log(`listener location ${JSON.stringify(location)}`);
        document.getElementById("locationState").textContent =
          getLocationResultText(location);
      });

      document
        .getElementById("addGeotag")
        .addEventListener("click", async () => {
          const orderHandle = "test_order";
          const orderStatus = {
            type: "orderStatusCustom",
            value: "test_status",
          };
          const data = {
            payload: "Quickstart Cordova",
            value: Math.random(),
          };
          const result = await HyperTrack.addGeotag(
            orderHandle,
            orderStatus,
            data
          );
          logAndAlert(`addGeotag ${getLocationResultText(result)}`);
        });

      document
        .getElementById("addGeotagWithExpectedLocation")
        .addEventListener("click", async () => {
          const orderHandle = "test_order";
          const orderStatus = {
            type: "orderStatusCustom",
            value: "test_status",
          };
          const data = {
            payload: "Quickstart Cordova",
            value: Math.random(),
          };
          const expectedLocation = {
            latitude: 37.787359,
            longitude: -122.408227,
          };
          const result = await HyperTrack.addGeotagWithExpectedLocation(
            orderHandle,
            orderStatus,
            data,
            expectedLocation
          );
          logAndAlert(
            `addGeotagWithExpectedLocation ${getLocationWithDeviationResultText(
              result
            )}`
          );
        });

      document
        .getElementById("getErrors")
        .addEventListener("click", async () => {
          const errors = await HyperTrack.getErrors();
          logAndAlert(`getErrors ${getErrorsText(errors)}`);
        });

      document
        .getElementById("getIsAvailable")
        .addEventListener("click", async () => {
          const isAvailable = await HyperTrack.getIsAvailable();
          logAndAlert(`getIsAvailable ${isAvailable}`);
        });

      document
        .getElementById("getIsTracking")
        .addEventListener("click", async () => {
          const isTracking = await HyperTrack.getIsTracking();
          logAndAlert(`getIsTracking ${isTracking}`);
        });

      document
        .getElementById("getLocation")
        .addEventListener("click", async () => {
          const result = await HyperTrack.getLocation();
          logAndAlert(`getLocation ${getLocationResultText(result)}`);
        });

      document
        .getElementById("getMetadata")
        .addEventListener("click", async () => {
          const metadata = await HyperTrack.getMetadata();
          logAndAlert(`getMetadata ${JSON.stringify(metadata)}`);
        });

      document.getElementById("getName").addEventListener("click", async () => {
        const name = await HyperTrack.getName();
        logAndAlert(`getName ${name}`);
      });

      document.getElementById("locate").addEventListener("click", async () => {
        HyperTrack.locate(function (result) {
          logAndAlert(`locate ${getLocateResultText(result)}`);
        });
      });

      document
        .getElementById("setIsAvailableTrue")
        .addEventListener("click", async () => {
          const isAvailable = true;
          HyperTrack.setIsAvailable(isAvailable);
          console.log(`setIsAvailable ${isAvailable}`);
        });

      document
        .getElementById("setIsAvailableFalse")
        .addEventListener("click", async () => {
          const isAvailable = false;
          HyperTrack.setIsAvailable(isAvailable);
          console.log(`setIsAvailable ${isAvailable}`);
        });

      document
        .getElementById("setIsTrackingTrue")
        .addEventListener("click", async () => {
          const isTracking = true;
          HyperTrack.setIsTracking(isTracking);
          console.log(`setIsTracking ${isTracking}`);
        });

      document
        .getElementById("setIsTrackingFalse")
        .addEventListener("click", async () => {
          const isTracking = false;
          HyperTrack.setIsTracking(isTracking);
          console.log(`setIsTracking ${isTracking}`);
        });

      document.getElementById("unsubscribe").addEventListener("click", () => {
        HyperTrack.unsubscribeFromErrors();
        HyperTrack.unsubscribeFromIsAvailable();
        HyperTrack.unsubscribeFromIsTracking();
        HyperTrack.unsubscribeFromLocate();
        HyperTrack.unsubscribeFromLocation();
        console.log("unsubscribe");
      });
    } catch (e) {
      console.log("Error", e);
    }
  },
};

function getLocationResultText(result) {
  if (result.type === "success") {
    let latitude = result.value.latitude;
    let longitude = result.value.longitude;
    return `Location: ${latitude}, ${longitude}`;
  } else {
    return getLocationErrorText(result.value);
  }
}

function getLocateResultText(result) {
  if (result.type === "success") {
    let location = result.value;
    let latitude = location.latitude;
    let longitude = location.longitude;
    return `Location: ${latitude}, ${longitude}`;
  } else {
    return getErrorsText(result.value);
  }
}

function getLocationWithDeviationResultText(result) {
  if (result.type === "success") {
    let locationWithDeviation = result.value;
    let deviation = locationWithDeviation.deviation;
    let location = locationWithDeviation.location;
    let latitude = location.latitude;
    let longitude = location.longitude;
    return `Location: ${latitude}, ${longitude}, deviation: ${deviation}`;
  } else {
    return getLocationErrorText(result.value);
  }
}

function getLocationErrorText(locationError) {
  if (locationError.type === "errors") {
    let errors = locationError.value;
    return getErrorsText(errors);
  } else {
    return `LocationError: ${locationError.type}`;
  }
}

function getErrorsText(errors) {
  console.log(errors);
  return `Errors: ${errors.join("\n")}`;
}

function logAndAlert(text) {
  console.log(text);
  alert(text);
}

app.initialize();
