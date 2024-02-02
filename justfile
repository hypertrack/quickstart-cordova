
alias ag := add-plugin-from-github
alias al := add-plugin-local
alias ap := add-plugin
alias oi := open-ios
alias ra := run-android
alias ui := re-add-ios

add-plugin version:
    cordova plugin add cordova-plugin-hypertrack-v3
    npm i cordova-plugin-hypertrack-v3@{{version}}

add-plugin-from-github version:
    echo "Use local dependency instead (just al), adding from github doesn't work well with Cordova"
    exit 1

add-plugin-local:
    cordova plugin remove cordova-plugin-hypertrack-v3
    cordova plugin add file:../cordova-plugin-hypertrack

build-android:
    cordova build android

open-ios:
    open platforms/ios/QuickstartCordova.xcworkspace

# you need to copy google-services.json manually
re-add-android:
    cordova platform remove android
    cordova platform add android

re-add-ios:
    cordova platform remove ios
    cordova platform add ios

run-android:
    cordova run android

run-ios:
    cordova run ios

