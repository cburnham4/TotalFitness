# Google Play Data Safety Responses

**App:** Complete Fitness (`letshangllc.allfitness`)  
**Version:** 3.0.0 (versionCode 100)  
**Last Updated:** November 2026  
**Target SDK:** 36

This document provides responses for the Google Play Console Data Safety section.

---

## Overview

Complete Fitness is a local workout tracker. Exercise, lift, cardio, and body-weight workout history are stored in an on-device SQLite database. The app does not use Firebase or cloud sync. Google AdMob (with UMP consent) is used for advertisements.

---

## Health / Fitness apps declaration (Play Console)

**Required:** Because this app stores health and fitness related data (workout sets, reps, weights, cardio distance/duration, body-weight exercise history), complete Google Play’s **Health apps** declaration in Play Console (Policy → App content / Health). Declare that the app provides fitness tracking features and that health/fitness data is processed **on-device only** (not shared with the developer’s servers). Revisit this declaration if you later add cloud sync, Health Connect, or wearable integrations.

---

## Data Collection Summary

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| Health and fitness (workout history) | Yes (on device) | No | App functionality |
| App interactions | Yes | Yes (AdMob) | Advertising |
| Advertising ID | Yes | Yes (AdMob) | Advertising |
| Crash logs | No | No | — |
| Email / account | No | No | — |

---

## Detailed Data Types

### 1. Health and fitness

#### Fitness activity / workout data
- **Collected:** Yes (locally in SQLite: exercises, routines, muscle groups, lift sets, cardio sets, body-weight sets, dates)
- **Shared:** No (not uploaded by the app)
- **Purpose:** App functionality — track workouts for the user
- **Optional:** Core feature; user enters data voluntarily
- **Encrypted in transit:** N/A (local only)
- **Deletion available:** User can delete individual items in-app; uninstalling removes the local database

### 2. App Activity

#### App Interactions
- **Collected:** Yes (via Google AdMob / advertising SDK)
- **Shared:** Yes (with Google AdMob for ad personalization, subject to consent)
- **Purpose:** Advertising
- **Optional:** Subject to GDPR/UMP consent
- **User Control:** UMP consent dialog allows opt-out of personalized ads where required

### 3. Device or Other IDs

#### Advertising ID
- **Collected:** Yes (via Google AdMob)
- **Shared:** Yes (with Google for advertising)
- **Purpose:** Advertising
- **Optional:** Users can reset or opt out via device settings
- **User Control:** UMP consent dialog in EU/EEA regions

---

## Third-Party SDKs

### Google AdMob (`play-services-ads`)
- **Data collected:** Advertising ID, app interactions, device info
- **Purpose:** Display advertisements (adaptive banners)
- **Shared:** Yes, with Google advertising partners
- **User Control:** UMP consent flow for GDPR compliance
- **Privacy Policy:** https://policies.google.com/privacy
- **Traffic:** HTTPS only (Amazon Mobile Ads removed)

### Google User Messaging Platform (UMP)
- **Purpose:** GDPR consent management before requesting ads
- **Data collected:** Consent preferences
- **Privacy Policy:** https://policies.google.com/privacy

---

## Permissions

| Permission | Why |
|------------|-----|
| `INTERNET` | Load ads |
| `ACCESS_NETWORK_STATE` | AdMob connectivity checks |
| `ACCESS_WIFI_STATE` | Legacy ad SDK connectivity (retained for AdMob) |

No location, camera, contacts, or notification permissions are requested by the app code. If Play Console flags AdMob-merged permissions, declare them as used by the advertising SDK.

---

## Data Security

### Encryption
- **In transit:** Yes for ad/consent network traffic (TLS/HTTPS)
- **At rest:** Workout data is stored in the app’s private SQLite database

### Data Deletion
Users can delete workout entries in-app, or clear all data by clearing app storage / uninstalling.

---

## Children's Privacy

This app is **not** directed at children under 13 (COPPA) or under 16 (GDPR).

- `setTagForUnderAgeOfConsent(false)` is configured in UMP
- No child-directed ad serving is enabled
- Confirm Families policy / Designed for Families status in Play Console if applicable

---

## Regional Compliance

### GDPR (EU/EEA)
- UMP consent dialog is shown before serving personalized ads when required
- Non-personalized ads may be served when consent is not given

### CCPA (California)
- Users can opt out of personalized advertising via device-level advertising settings

---

## Play Console Form Responses

### Does your app collect or share any of the required user data types?
**Yes**

### Is all of the user data collected by your app encrypted in transit?
**Yes** (for any networked collection via AdMob/UMP; workout data is local-only)

### Do you provide a way for users to request that their data is deleted?
**Yes** (in-app delete + uninstall / clear storage)

### Data Types to Declare

1. **Health and fitness** (fitness info / workout history)
   - Collected: Yes
   - Shared: No
   - Ephemeral: No
   - Required: Yes (core feature)
   - Purpose: App functionality

2. **Device or other IDs**
   - Collected: Yes
   - Shared: Yes (Google/AdMob)
   - Ephemeral: No
   - Required: No (user can opt out)
   - Purpose: Advertising

3. **App interactions** (if listed separately)
   - Collected: Yes (AdMob)
   - Shared: Yes
   - Purpose: Advertising

---

## Contact

For privacy inquiries, contact the developer via the email address listed on the Google Play Store listing.
