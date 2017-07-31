#!/usr/bin/env bash
echo no | android create avd --force --name test --target android-21 --abi armeabi-v7a  -d "Nexus 6"
sed -i -e 's/hw.ramSize=512/hw.ramSize=1024/g' ${HOME}/.android/avd/test.avd/config.ini
sed -i -e 's/vm.heapSize=48/vm.heapSize=192/g' ${HOME}/.android/avd/test.avd/config.ini
cat ${HOME}/.android/avd/test.avd/config.ini