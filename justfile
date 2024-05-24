alias a := add-plugin
alias ag := add-plugin-github
alias al := add-plugin-local
alias ap := add-plugin
alias c := clean
alias ogp := open-github-prs
alias oi := open-ios
alias ra := run-android
alias s := setup
alias us := update-sdk
alias v := version
alias va := version-android

SDK_NAME := "HyperTrack SDK Cordova"
SDK_REPOSITORY_NAME := "cordova-plugin-hypertrack"
QUICKSTART_REPOSITORY_NAME := "quickstart-cordova"

# Source: https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string
# \ are escaped
SEMVER_REGEX := "(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?"

add-plugin version: hooks
    cordova plugin add cordova-plugin-hypertrack-v3
    npm i --save-exact cordova-plugin-hypertrack-v3@{{version}}
    just pod-install

add-plugin-local: hooks
    cordova plugin remove cordova-plugin-hypertrack-v3
    cordova plugin add file:../cordova-plugin-hypertrack
    just pod-install

add-plugin-github branch: hooks
    echo "Use local dependency instead (just al), adding from github doesn't work well with Cordova"
    exit 1

clean: hooks
    rm -rd node_modules
    rm package-lock.json

get-dependencies: hooks
    npm install

hooks:
    # no hooks for Quickstart Cordova, the project is generated and gitignored

open-github-prs:
    open "https://github.com/hypertrack/{{QUICKSTART_REPOSITORY_NAME}}/pulls"

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

setup: get-dependencies hooks

update-sdk version: hooks
    git checkout -b update-sdk-{{version}}
    just add-plugin {{version}}
    git commit -am "Update {{SDK_NAME}} to {{version}}"
    just open-github-prs

version:
    @cat package.json | grep cordova-plugin-hypertrack-v3 | head -n 1 | grep -o -E '{{SEMVER_REGEX}}'

version-android:
    #!/usr/bin/env sh
    set -euo pipefail
    cd platforms/android
    ./gradlew app:dependencies | grep "com.hypertrack:sdk-android" | head -n 1 | grep -o -E '{{SEMVER_REGEX}}'
    cd ../..
