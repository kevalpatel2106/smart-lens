#!/usr/bin/env bash

set -e

if [ "$COMPONENT" == "unit" ]; then
    ./gradlew test
elif [ "$COMPONENT" == "instrumentation" ]; then
    ./gradlew connectedAndroidTest jacocoTestReport mergeAndroidReports --continue --stacktrace
elif [ "$COMPONENT" == "build" ]; then
    ./gradlew assembleRelease --stacktrace
else
    echo "That module doesn't exist, now does it? ;)"
    exit 1
fi