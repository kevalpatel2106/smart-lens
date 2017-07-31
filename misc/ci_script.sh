#!/usr/bin/env bash

set -e

if [ "$COMPONENT" == "text-max" ]; then
    ./gradlew connectedAndroidTest test jacocoTestReport mergeAndroidReports --continue --stacktrace
elif [ "$COMPONENT" == "text-min" ]; then
    ./gradlew connectedAndroidTest test --stacktrace
elif [ "$COMPONENT" == "build" ]; then
    ./gradlew assembleRelease --stacktrace
else
    echo "That module doesn't exist, now does it? ;)"
    exit 1
fi