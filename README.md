# Cordova Quickstart for HyperTrack SDK

[![GitHub](https://img.shields.io/github/license/hypertrack/quickstart-cordova?color=orange)](./LICENSE)
[![cordova-plugin-hypertrack-v3](https://img.shields.io/badge/cordova_plugin_hypertrack_v3-1.0.0-brightgreen.svg)](https://github.com/hypertrack/cordova-plugin-hypertrack)

[HyperTrack](https://www.hypertrack.com/) lets you add live location tracking to your mobile app. Live location is made available along with ongoing activity, tracking controls and tracking outage with reasons.

This repo contains an example Cordova app that has everything you need to get started.

For information about how to get started with Cordova SDK, please check this [Guide](https://hypertrack.com/docs/install-sdk-cordova).

## How to get started?

### Create HyperTrack Account

[Sign up](https://dashboard.hypertrack.com/signup) for HyperTrack and get your publishable key from the [Setup page](https://dashboard.hypertrack.com/setup).

### Set up the environment

- [Install Cordova CLI](https://cordova.apache.org/docs/en/latest/guide/cli/index.html#installing-the-cordova-cli)
- [Set up environment](https://cordova.apache.org/docs/en/latest/guide/cli/index.html#install-pre-requisites-for-building)

### Clone Quickstart app

### Install Dependencies

#### General Dependencies

Run `npm install`

#### iOS dependencies

Quickstart app uses [CocoaPods](https://cocoapods.org/) dependency manager to install the latest version of the iOS SDK. Using the latest version of CocoaPods is advised.

If you don't have CocoaPods, [install it first](https://guides.cocoapods.org/using/getting-started.html#installation).

```sh
cd ios
pod install
```

### Add platforms

[Add platforms](https://cordova.apache.org/docs/en/latest/guide/cli/index.html#add-platforms)

#### Configure Android platform

Add build params to `quickstart-cordova/platforms/android/gradle.properties`:

```
cdvSdkVersion=31
```

### Update the publishable key

Follow the instructions for setting the publishable key for [iOS](https://hypertrack.com/docs/install-sdk-ios/#set-the-publishable-key) and [Android](https://hypertrack.com/docs/install-sdk-android/#set-the-publishable-key).

### [Set up silent push notifications](https://hypertrack.com/docs/install-sdk-cordova/#set-up-silent-push-notifications)

HyperTrack SDK needs Firebase Cloud Messaging to manage on-device tracking as well as enable using HyperTrack cloud APIs from your server to control the tracking.

### Run the app

#### Build the app

Run `cordova build`

#### Android

Run `cordova run android`

#### iOS

Open the app's workspace file (`/ios/QuickstartCordova.xcworkspace`) with Xcode.

Select your device (SDK requires real device, it won't work using simulator) and hit Run.

##### If you have "Unsupported Swift Version" error

Set `Build Settings > Swift Compiler - Language > Swift version` to the latest version

### Grant permissions

Grant location and activity permissions (choose "Always Allow" for location).

### Start tracking

Press `Start tracking` button.

To see the device on a map, open the [HyperTrack dashboard](https://dashboard.hypertrack.com/).

## Support

Join our [Slack community](https://join.slack.com/t/hypertracksupport/shared_invite/enQtNDA0MDYxMzY1MDMxLTdmNDQ1ZDA1MTQxOTU2NTgwZTNiMzUyZDk0OThlMmJkNmE0ZGI2NGY2ZGRhYjY0Yzc0NTJlZWY2ZmE5ZTA2NjI) for instant responses. You can also email us at help@hypertrack.com
