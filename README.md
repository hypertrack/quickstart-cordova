# quickstart-cordova
HyperTrack Plugin for Cordova usage sample

[Docs](https://hypertrack.com/docs/install-sdk-cordova)

## Create HyperTrack Account

[Sign up](https://dashboard.hypertrack.com/signup) for HyperTrack and
get your publishable key from the [Setup page](https://dashboard.hypertrack.com/setup).

## Set up Firebase

1. [Set up Firebase Project for quickstart-ionic](https://console.firebase.google.com/u/0/)

## Build the app

Install dependepcies
```npm i```

#### Android

Add platform by running  ```cordova platform add android```

Update Android SDK versions

In ```./quickstart-cordova/platforms/android/app/build.gradle```:

```
minSdkVersion = 24
compileSdkVersion = 31
targetSdkVersion = 31
```

Build the app by running ```cordova build android```

#### IOS

Add platform by running  ```cordova platform add ios```

Build the app by running ```cordova build ios```


