alias a := add-plugin
alias ag := add-plugin-from-github
alias al := add-plugin-local
alias ap := add-plugin
alias oi := open-ios
alias ra := run-android
alias ui := re-add-ios

add-plugin version:
    cordova plugin add cordova-plugin-hypertrack-v3
    npm i cordova-plugin-hypertrack-v3@{{version}}
    just pod-install

add-plugin-from-github version:
    echo "Use local dependency instead (just al), adding from github doesn't work well with Cordova"
    exit 1

add-plugin-local:
    cordova plugin remove cordova-plugin-hypertrack-v3
    cordova plugin add file:../cordova-plugin-hypertrack
    just pod-install

build-android:
    cordova build android

open-ios:
    open platforms/ios/QuickstartCordova.xcworkspace

pod-install:
    #!/usr/bin/env sh
    set -euo pipefail

    cd platforms/ios
    rm Podfile.lock
    pod install --repo-update
    cd ../..

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

