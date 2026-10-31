# Shadow Wing JARVIS — Android starter source

This is a Java-based Android Studio project (no Kotlin). It is a **starter build**, not a finished autonomous social-media agent.

## Included
- Android app shell and JARVIS-style command screen
- Local task queue with approve/reject actions
- App-launch allowlist for Fiverr, Instagram, Gmail, Facebook and LinkedIn
- WhatsApp intentionally excluded
- WorkManager periodic background briefing notification
- No password, OTP, cookie, or session-token collection
- Clear status messages: approvals are recorded locally but do not execute external actions

## Important limitations
This source does **not** connect to private Fiverr, Instagram, Gmail, Facebook or LinkedIn account APIs. It opens the official sites after you explicitly allow an app. Real posting, inbox monitoring, replying, negotiation, or order actions require platform-approved OAuth/API integrations and a secure backend. Each platform has its own access scopes, app review, account-type restrictions, and terms. Do not use browser scraping, stored passwords, session cookies, or automated spam.

Instagram publishing, for example, is generally for eligible Professional (Business/Creator) accounts using Meta's official API and permissions. Fiverr API access must be verified for your account/use case before building that adapter. The backend must store OAuth secrets server-side, encrypt tokens, use least-privilege scopes, and expose a per-action approval endpoint.

Android background execution is controlled by the OS. WorkManager schedules deferrable periodic work (minimum interval 15 minutes); it cannot guarantee continuous 24/7 execution. Battery saver, Doze, OEM restrictions, network availability, and notification permission affect timing.

## Build APK
1. Install Android Studio on a computer.
2. Open this `ShadowWingJARVIS` folder in Android Studio.
3. Let Gradle sync. Use JDK 17 and Android SDK Platform 35.
4. Build > Build Bundle(s) / APK(s) > Build APK(s).
5. The debug APK will be under `app/build/outputs/apk/debug/app-debug.apk`.

Command line (after installing Android SDK and Gradle):
```bash
gradle assembleDebug
```

This environment did not have the Android SDK or Gradle installed, so an APK could not be compiled here. The source project is provided for building in Android Studio.

## Next integration phase
- Create a backend service and OAuth callback URLs.
- Register your own developer apps with each platform.
- Request only required scopes and complete app review where needed.
- Add adapters one platform at a time.
- Route every publish/send/order/price commitment through a preview + explicit approval screen.
- Add audit log, token revocation, encrypted token storage, rate limits, retry handling and data deletion controls.

## Privacy
The current Android starter stores task text and app allowlist locally in SharedPreferences. Avoid entering client secrets, passwords, OTPs, or sensitive buyer information. For production, migrate task data to encrypted storage and implement a privacy policy and secure backend.
