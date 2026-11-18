# Collect for Android

Android companion for [collect.tpgofighting.top](https://collect.tpgofighting.top) — a
native WebView shell. The app opens the Collect site, so new articles appear
without app updates.

- Package: `top.tpgofighting.collect`
- minSdk 26, targetSdk 35
- WebView with JavaScript + DOM storage; back button walks WebView history;
  links outside `collect.tpgofighting.top` open in the system browser.

## Build

Pushes to `main` build a debug APK with GitHub Actions
(`./gradlew :app:assembleDebug`) and publish it to a GitHub release.
