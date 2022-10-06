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

alias u := update-plugin-local
alias a := run-android
