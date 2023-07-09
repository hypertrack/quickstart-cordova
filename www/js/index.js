const PUBLISHABLE_KEY = "PasteYourPublishableKeyHere";

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
      console.log("Initializing HyperTrack plugin...");
      var hyperTrack = await HyperTrack.initialize(PUBLISHABLE_KEY, {
        loggingEnabled: true,
        allowMockLocations: true,
      });
      console.log("Initialized HyperTrack", hyperTrack);

      const deviceId = await hyperTrack.getDeviceId();
      console.log("getDeviceId", deviceId);
      document.getElementById("deviceId").textContent = deviceId;

      const name = "Quickstart Cordova";
      hyperTrack.setName(name);
      console.log("setName", name);

      const metadata = {
        app: "Quickstart Cordova",
        value: Math.random(),
      };
      hyperTrack.setMetadata(metadata);
      console.log("setMetadata", JSON.stringify(metadata));

      hyperTrack.subscribeToTracking(function (isTracking) {
        console.log(`listener isTracking ${isTracking}`);
        document.getElementById("trackingListener").textContent =
          JSON.stringify(isTracking);
        document.getElementById("errorsListener").textContent = "";
      });

      hyperTrack.subscribeToAvailability(function (isAvailable) {
        console.log(`listener isAvailable ${isAvailable}`);
        document.getElementById("availabilityListener").textContent =
          JSON.stringify(isAvailable);
      });

      hyperTrack.subscribeToErrors(function (errors) {
        console.log(`listener errors ${JSON.stringify(errors)}`);
        document.getElementById("errorsListener").textContent =
          JSON.stringify(errors);
      });

      document.getElementById("startTracking").addEventListener("click", () => {
        console.log("startTracking");
        hyperTrack.startTracking();
      });

      document.getElementById("stopTracking").addEventListener("click", () => {
        console.log("stopTracking");
        hyperTrack.stopTracking();
      });

      document
        .getElementById("setAvailabilityTrue")
        .addEventListener("click", () => {
          console.log("set isAvailable = true");
          hyperTrack.setAvailability(true);
        });

      document
        .getElementById("setAvailabilityFalse")
        .addEventListener("click", () => {
          console.log("set isAvailable = false");
          hyperTrack.setAvailability(false);
        });

      document
        .getElementById("getAvailability")
        .addEventListener("click", async () => {
          const isAvailable = await hyperTrack.isAvailable();
          logAndAlert(`isAvailable ${isAvailable}`);
        });

      document
        .getElementById("isTracking")
        .addEventListener("click", async () => {
          const isTracking = await hyperTrack.isTracking();
          logAndAlert(`isTracking ${isTracking}`);
        });

      document
        .getElementById("disposeListeners")
        .addEventListener("click", () => {
          hyperTrack.unsubscribeFromTracking();
          hyperTrack.unsubscribeFromAvailability();
          hyperTrack.unsubscribeFromErrors();
          logAndAlert("disposeListeners");
        });

      document
        .getElementById("getLocation")
        .addEventListener("click", async () => {
          const result = await hyperTrack.getLocation();
          logAndAlert(`getLocation ${JSON.stringify(result)}`);
        });

      document
        .getElementById("addGeotag")
        .addEventListener("click", async () => {
          const result = await hyperTrack.addGeotag({
            payload: "Quickstart Cordova",
            value: Math.random(),
          });
          logAndAlert(`addGeotag ${getLocationResultText(result)}`);
        });

      document
        .getElementById("addGeotagWithExpectedLocation")
        .addEventListener("click", async () => {
          const result = await hyperTrack.addGeotagWithExpectedLocation(
            {
              payload: "Quickstart Cordova",
              value: Math.random(),
            },
            {
              latitude: 37.787359,
              longitude: -122.408227,
            }
          );
          logAndAlert(
            `addGeotagWithExpectedLocation ${getLocationWithDeviationResultText(
              result
            )}`
          );
        });

      document.getElementById("sync").addEventListener("click", () => {
        logAndAlert("sync");
      });
    } catch (e) {
      console.log("Error", e);
    }
  },
};

function getLocationResultText(result) {
  if (result.type === "success") {
    let latitude = result.value.value.latitude;
    let longitude = result.value.value.longitude;
    return `Location: ${latitude}, ${longitude}`;
  } else {
    return getLocationErrorText(result.value);
  }
}

function getLocationWithDeviationResultText(result) {
  if (result.type === "success") {
    let locationWithDeviation = result.value;
    let deviation = locationWithDeviation.value.deviation;
    let location = locationWithDeviation.value.location;
    let latitude = location.value.latitude;
    let longitude = location.value.longitude;
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
  return `Errors: ${errors.join("\n")}`;
}

function logAndAlert(text) {
  console.log(text);
  alert(text);
}

app.initialize();
