#!/usr/bin/env bash
set -e

# Starting emulators is very costly. We should only start them if we're building a matrix
# component which requires one. We start the travis_create_avd.sh in the background because
# we can get a small performance improvement by continuing the build, and only blocking and
# waiting for the emulator when we absolutely need it.

if [ "$COMPONENT" == "instrumentation" ]; then
    #Creates and starts an emulator.
    echo "Starting AVD for component $COMPONENT"

    #Create AVD
    echo no | android create avd --force --name test --target android-21 --abi armeabi-v7a  -d "Nexus 6"

    #Modify AVD
    sed -i -e 's/hw.ramSize=512/hw.ramSize=1024/g' ${HOME}/.android/avd/test.avd/config.ini #Increase the RAM to 1024
    sed -i -e 's/vm.heapSize=48/vm.heapSize=192/g' ${HOME}/.android/avd/test.avd/config.ini #Increase the heap size to 192MB
    cat ${HOME}/.android/avd/test.avd/config.ini

    #Start AVD
    emulator -avd test -no-skin -no-audio -no-window &
    android-wait-for-emulator

    #Display list of current devices
    adb devices

    #Turn off the animations
    adb shell settings put global window_animation_scale 0.0 
    adb shell settings put global transition_animation_scale 0.0
    adb shell settings put global animator_duration_scale 0.0

    #Unlock the device
    adb shell input keyevent 82 &
    exit 0
fi

exit 0