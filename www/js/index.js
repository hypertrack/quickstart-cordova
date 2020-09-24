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
  initialize: function() {
    document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
  },

  // deviceready Event Handler
  //
  // Bind any cordova events here. Common events are:
  // 'pause', 'resume', etc.
  onDeviceReady: function() {
    this.receivedEvent('deviceready');
  },

  // Update DOM on a Received Event
  receivedEvent: function(id) {
    var parentElement = document.getElementById(id);
    var listeningElement = parentElement.querySelector('.listening');
    var receivedElement = parentElement.querySelector('.received');

    listeningElement.setAttribute('style', 'display:none;');
    receivedElement.setAttribute('style', 'display:block;');

    console.log('Received Event: ' + id);

    hypertrack.enableDebugLogging();
    hypertrack.initialize(
        'uvIAA8xJANxUxDgINOX62-LINLuLeymS6JbGieJ9PegAPITcr9fgUpROpfSMdL9kv-qFjl17NeAuBHse8Qu9sw', onHyperTrackReady, onHyperTrackInitFailed
      );
    window.addEventListener('onHyperTrackStatusChanged', onHyperTrackStatusChanged);
  }
};

function onHyperTrackReady(sdkInstance) {
  console.log("HyperTrack succesfully initialized");
  // Device ID
  sdkInstance.getDeviceId(
    function(deviceId) {console.log("HyperTrack device id is " + deviceId);},
    function(err) {console.log("HyperTrack: can't get device id due to err " + err);}
  );
  // Set device name
  sdkInstance.setDeviceName('Elvis');
  // Set device metadata
  sdkInstance.setDeviceMetadata({"platform": "cordova"});
  // is running
  sdkInstance.isRunning(
    function(isRunning) {console.log("HyperTrack isRunning: " + isRunning);},
    function(err) {console.log("HyperTrack: can't get is running status due to err " + err);}
  );
  // Sync Settings
  sdkInstance.syncDeviceSettings(
   function(isRunning) {console.log("HyperTrack sync device settings:");},
   function(err) {console.log("HyperTrack: can't get sync device settings due to err " + err);}
 );
}

function onHyperTrackInitFailed(error) {
  console.log("HyperTrack init failed with error " + error);
}

/**
 * Callback, that fires when tracking status changes.
 *
 * @param {String} newStatus 'start', 'stop' or error description.
 *
 */
function onHyperTrackStatusChanged(newStatus) {
  console.log("Received HyperTrack status change: " + newStatus);
}

app.initialize();
