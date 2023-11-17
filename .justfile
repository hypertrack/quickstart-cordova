alias ra := run-android
alias ag := add-plugin-from-github
alias al := update-plugin-local
alias ui := re-add-ios
alias oi := open-ios

add-plugin-from-github:
    echo "Use local dependency instead (just al)"

run-android:
    cordova run android

run-ios:
    cordova run ios

build-android:
    cordova build android

update-plugin-local:
    cordova plugin remove cordova-plugin-hypertrack-v3
    cordova plugin add file:../cordova-plugin-hypertrack

# you will need to copy google-services.json manually
re-add-android:
    cordova platform remove android
    cordova platform add android

re-add-ios:
    cordova platform remove ios
    cordova platform add ios

open-ios:
    open platforms/ios/QuickstartCordova.xcworkspace
