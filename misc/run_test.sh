./gradlew connectedAndroidTest test jacocoTestReport mergeAndroidReports --continue --stacktrace
bash <(curl -s https://codecov.io/bash)
