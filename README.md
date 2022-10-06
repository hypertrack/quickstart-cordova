# Cordova Quickstart for HyperTrack SDK 

![GitHub](https://img.shields.io/github/license/hypertrack/cordova-plugin-hypertrack.svg) 
![npm](https://img.shields.io/npm/v/cordova-plugin-hypertrack-v3.svg) 
![iOS SDK](https://img.shields.io/badge/iOS%20SDK-4.13.0-brightgreen.svg) 
![Android SDK](https://img.shields.io/badge/Android%20SDK-6.3.0-brightgreen.svg)

[HyperTrack](https://www.hypertrack.com/) lets you add live location tracking to your mobile app. Live location is made available along with ongoing activity, tracking controls and tracking outage with reasons. 

This repo contains an example Cordova app that has everything you need to get started.

For information about how to get started with Cordova SDK, please check this [Guide](https://hypertrack.com/docs/install-sdk-cordova).

## How to get started?

### Create HyperTrack Account

[Sign up](https://dashboard.hypertrack.com/signup) for HyperTrack and get your publishable key from the [Setup page](https://dashboard.hypertrack.com/setup).

### Set up the environment

- [Install Cordova CLI](https://cordova.sudo npm install -g cordovaapache.org/docs/en/latest/guide/cli/index.html#installing-the-cordova-cli)
- [Set up environment](httcordova requirementsps://cordova.apache.org/docs/en/latest/guide/cli/index.html#install-pre-requisites-for-building)

### Clone Quickstart app

### Install Dependencies

#### General Dependencies

Run
- `npm install`

#### iOS dependencies**

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
cdvMinSdkVersion=23
```

### Update the publishable key

Insert your HyperTrack publishable key to `const PUBLISHABLE_KEY` in `www/js/index.js`

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

### Enable permissions

Enable location and activity permissions (choose "Always Allow" for location).

### Start tracking

Press `Start tracking` button.

To see the device on a map, open the [HyperTrack dashboard](https://dashboard.hypertrack.com/).

## Support

Join our [Slack community](https://join.slack.com/t/hypertracksupport/shared_invite/enQtNDA0MDYxMzY1MDMxLTdmNDQ1ZDA1MTQxOTU2NTgwZTNiMzUyZDk0OThlMmJkNmE0ZGI2NGY2ZGRhYjY0Yzc0NTJlZWY2ZmE5ZTA2NjI) for instant responses. You can also email us at help@hypertrack.com
