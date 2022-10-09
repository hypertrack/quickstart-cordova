/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
  receivedEvent: function (id) {
    console.log("Received Event: " + id +" "+window.cordova.platformId);
    if (window.cordova.platformId.toLowerCase() === 'android') {
      getBlockers();
    }

    hypertrack.enableDebugLogging();
    hypertrack.initialize(
      "YOUR-PUBLISHABLE-KEY-HERE",
      onHyperTrackReady,
      onHyperTrackInitFailed
    );
  },
};

function onHyperTrackReady(sdkInstance) {
  console.log("HyperTrack succesfully initialized");

  //Device ID
  sdkInstance.getDeviceId(
    function (deviceId) {
      var content = document.getElementById("deviceId");
      content.textContent = deviceId;
    },
    function (err) {
      console.log("HyperTrack: can't get device id due to err " + err);
    }
  );
  // Set device name
  sdkInstance.setDeviceName("Elvis");
  // Set device metadata
  sdkInstance.setDeviceMetadata({ platform: "cordova" });

  // Sync Settings
  sdkInstance.syncDeviceSettings(
    function (isRunning) {
      console.log("HyperTrack sync device settings:");
    },
    function (err) {
      console.log(
        "HyperTrack: can't get sync device settings due to err " + err
      );
    }
  );

  // Collect bread crumbs
  sdkInstance.addGeoTag(
    { action: "login" },
    { latitude: 26.922070, longitude: 75.778885 },
    function (deviceLocation) {
      console.log("Created geotag at " + deviceLocation);
    },
    function (err) {
      console.log("Can't determine current location. Error code " + err);
    }
  );

  sdkInstance.isTracking(
    function (isTracking) {
      if (isTracking) {
        var x = document.getElementById("deviceready");
        x.style.display = "flex";
      } else {
        var x = document.getElementById("deviceready");
        x.style.display = "none";
      }
    },
    function (err) {
      console.log("HyperTrack: can't able to get isTracking due to err " + err);
    }
  );

  document.getElementById("startTracking").addEventListener("click", () => {
    if (window.cordova.platformId.toLowerCase() === 'android') {
      getBlockers();
    }
    sdkInstance.start(
      function () {
        console.log("HyperTrack start Tracking:");
        var x = document.getElementById("deviceready");
        x.style.display = "flex";
      },
      function (err) {
        console.log(
          "HyperTrack: can't able to start tracking due to err " + err
        );
      }
    );
  });

  document.getElementById("getAvailability").addEventListener("click", () => {
    sdkInstance.getAvailability(
      function (status) {
        alert("HyperTrack getAvailability:" + JSON.stringify(status));
      },
      function (err) {
        alert("HyperTrack: can't able to get availability due to err " + err);
      }
    );
  });

  document.getElementById("isRunning").addEventListener("click", () => {
    sdkInstance.isRunning(
      function (isRunning) {
        alert("HyperTrack isRunning:" + isRunning);
      },
      function (err) {
        alert("HyperTrack: can't able to get isRunning due to err " + err);
      }
    );
  });

  document.getElementById("isTracking").addEventListener("click", () => {
    sdkInstance.isTracking(
      function (isTracking) {
        alert("HyperTrack isTracking:" + isTracking);
      },
      function (err) {
        alert("HyperTrack: can't able to get isTracking due to err " + err);
      }
    );
  });

  document
    .getElementById("setAvailabilityTrue")
    .addEventListener("click", () => {
      sdkInstance.setAvailability(true);
    });

  document
    .getElementById("setAvailabilityFalse")
    .addEventListener("click", () => {
      sdkInstance.setAvailability(false);
    });

  document.getElementById("stopTracking").addEventListener("click", () => {
    sdkInstance.stop(
      function () {
        var x = document.getElementById("deviceready");
        x.style.display = "none";
        console.log("HyperTrack stop Tracking:");
      },
      function (err) {
        console.log(
          "HyperTrack: can't able to stop tracking due to err " + err
        );
      }
    );
  });
  //activateTrackingListener
  document
    .getElementById("activateTrackingListener")
    .addEventListener("click", () => {
      sdkInstance.trackingStateChange(
        function (val) {
          var content = document.getElementById("trackingListener");
          content.textContent = JSON.stringify(val);
        },
        function (err) {
          console.log(
            "HyperTrack: can't get is trackingStateChange due to err " + err
          );
        }
      );
    });

  document
    .getElementById("disposeTrackingListener")
    .addEventListener("click", () => {
      sdkInstance.disposeTrackingState(
        function () {
          var content = document.getElementById("trackingListener");
          content.textContent = 'NA';
          console.log("HyperTrack disposeTrackingState:");
        },
        function (err) {
          console.log(
            "HyperTrack: can't get is disposeTrackingState due to err " + err
          );
        }
      );
    });

  document
    .getElementById("activateAvailabilityListener")
    .addEventListener("click", () => {
      sdkInstance.availabilityStateChange(
        function (val) {
          var content = document.getElementById("availabilityListener");
          content.textContent = JSON.stringify(val);
        },
        function (err) {
          console.log(
            "HyperTrack: can't get is availabilityStateChange due to err " + err
          );
        }
      );
    });

  document
    .getElementById("disposeAvailabilityListener")
    .addEventListener("click", () => {
      sdkInstance.disposeAvailabilityState(
        function () {
          console.log("HyperTrack disposeAvailabilityState:");
          var content = document.getElementById("availabilityListener");
          content.textContent = 'NA';
        },
        function (err) {
          console.log(
            "HyperTrack: can't get is disposeAvailabilityState due to err " +
              err
          );
        }
      );
    });
}

function onHyperTrackInitFailed(error) {
  console.log("HyperTrack init failed with error " + error);
}

function getBlockers() {
  hypertrack.getBlockers((val)=>{
    val.forEach(element => {
      element.resolve();
    });
  },(err)=>{
    console.log("HyperTrack:getBlockers err " + err);
  });
}

app.initialize();
