cordova.define('cordova/plugin_list', function(require, exports, module) {
  module.exports = [
    {
      "id": "cordova-plugin-hypertrack-v3.hypertrack",
      "file": "plugins/cordova-plugin-hypertrack-v3/www/HyperTrackPlugin.js",
      "pluginId": "cordova-plugin-hypertrack-v3",
      "clobbers": [
        "hypertrack"
      ]
    }
  ];
  module.exports.metadata = {
    "cordova-plugin-hypertrack-v3": "0.3.0",
    "cordova-plugin-whitelist": "1.3.4"
  };
});