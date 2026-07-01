# Elegant Galaxy TV Launcher

Custom Android TV launcher for Elegant Galaxy Pvt. Ltd.'s in-house LED TVs, replacing the third-party Dolphin launcher currently shipped on non-webOS models.

## Stack

- Kotlin + Jetpack Compose (Material 3 + `androidx.tv` TV-Material for D-Pad-aware focus behavior)
- Single Activity (`MainActivity`), Compose Navigation between screens (no extra Activities — keeps memory overhead low on 1-2GB RAM sets)
- Min SDK 28 (Android 9), target SDK 33
- Gradle Kotlin DSL (`build.gradle.kts`)

## Project layout

```
app/src/main/java/com/elegantgalaxy/tvlauncher/
  MainActivity.kt              single entry point, hosts the NavHost
  TvLauncherApplication.kt     app-wide init hook (empty scaffold)
  model/                       AppInfo, AppCategory data classes
  navigation/                  Screen routes + NavGraph
  ui/theme/                    Color.kt, Type.kt, Theme.kt — brand palette lives here
  ui/home/                     HomeScreen, AppGrid (adaptive grid), AppCarousel (per-category row)
  ui/settings/                 SettingsScreen
  ui/components/               AppTile (shared focusable tile)
  utils/                       FocusUtils (D-Pad focus visuals), AppLauncherUtils (PackageManager), MockData
```

## Why these decisions (for the React/Next.js-familiar)

- **Composable ≈ React component.** `@Composable fun HomeScreen(...)` is like a function component; recomposition ≈ re-render, triggered by state reads (via `remember`/`mutableStateOf`), same idea as React state triggering a re-render.
- **`Modifier` chain ≈ combined `className`/inline styles.** `Modifier.padding(8.dp).background(Color.Black)` reads like chained utility classes — order matters, same as CSS cascade.
- **No CSS/DOM.** Layout is `Row`/`Column`/`Box` (flexbox-like), sizing is `dp` (density-independent px, roughly like `rem`).
- **`remember { }`** is the Compose equivalent of `useMemo`/`useState` combined — it survives recomposition but not process death (use `rememberSaveable` or a ViewModel for that later).
- **Navigation** (`androidx.navigation.compose`) works like React Router: a `NavHost` maps string routes to composables, `navController.navigate("settings")` ≈ `router.push("/settings")`.

## D-Pad focus system

TVs have no mouse/touch — every interactive element must be reachable and clearly indicate "focus" via remote. `utils/FocusUtils.kt` centralizes this:

- `rememberTvFocusVisuals()` gives back an `interactionSource` (wire into `clickable`/`Card`) and a `modifier` (scale + border) that reacts to D-Pad focus.
- Scale is used instead of shadow/elevation for the focus ring because shadow recomputation is expensive on low-RAM chipsets like the MT9255.
- `AppTile` and `SettingsRow` both consume this so every focusable surface in the app looks and behaves identically.

Compose's default focus traversal (up/down/left/right) works automatically for anything laid out in a `Row`/`Column`/`LazyRow`/`LazyVerticalGrid` — you generally don't need to hand-wire D-Pad key handling unless you want to override default traversal order.

## Responsive layout (24"–65")

`AppGrid` uses `GridCells.Adaptive(minSize = 160.dp)` instead of a fixed column count. Since TVs at a given density just have more physical width on a bigger panel, Compose fits more 160dp columns automatically — no per-screen-size branching required. Same principle applies to any future grid: prefer `Adaptive`/`weight()`-based sizing over fixed pixel/dp column counts.

## Branding

Brand colors and the dark/OLED palette live in one place: [`ui/theme/Color.kt`](app/src/main/java/com/elegantgalaxy/tvlauncher/ui/theme/Color.kt). `colors.xml` mirrors the same hex values for the few things Compose can't own (window background, before Compose has drawn a frame).

Placeholder assets to replace once real logo files are supplied:

| File | Purpose | Replace with |
|---|---|---|
| `res/drawable/company_logo.xml` | Home screen header logo | Vector trace or PNG export of the real logo |
| `res/drawable/ic_launcher_foreground.xml` / `ic_launcher_background.xml` | App/launcher icon (adaptive icon, API 26+) | Real logo monogram, background color |
| `res/drawable/tv_banner.xml` | Android TV home-screen banner (min 320×180dp, 16:9) | Real logo lockup banner artwork |

If you hand me the actual logo JPGs, I can convert them to the vector/adaptive-icon assets above (Android Studio's Image Asset Studio does this via `File > New > Image Asset` if you'd rather do it locally).

Boot animation (`bootanimation.zip`) is a **device/firmware-level** asset, not something this APK controls — it lives outside Android and is flashed separately by whoever owns the ROM/firmware build for the MT9255 boards. Let me know if that's a separate deliverable you need help with.

## Running against real installed apps

`HomeScreen` currently renders `utils/MockData.sampleApps` so the UI can be built/reviewed without a device. To switch to real installed apps:

```kotlin
val apps = remember { AppLauncherUtils.queryLaunchableApps(context) }
```

`AppLauncherUtils.queryLaunchableApps` filters `PackageManager` for `CATEGORY_LEANBACK_LAUNCHER` activities (the same category real TV apps declare to appear in *any* TV launcher's app list).

## Building

Requires Android Studio (Iguana+) or a Gradle 8.7 / JDK 17 toolchain.

**First-time setup:** this repo doesn't include the `gradlew`/`gradlew.bat` wrapper scripts or `gradle-wrapper.jar` (they're binary/generated and there was no local Gradle install to generate them from). Opening the project in Android Studio will offer to generate the wrapper automatically — accept that prompt, or run `gradle wrapper --gradle-version 8.7` once if you have Gradle installed some other way.

```bash
# Debug build (installs as com.elegantgalaxy.tvlauncher.debug, side-by-side with release)
./gradlew assembleDebug

# Release build (requires keystore.properties, see below)
./gradlew assembleRelease
```

Output APKs land in `app/build/outputs/apk/{debug,release}/`.

## Signing (for release APKs)

1. Generate a keystore once (keep it **outside** version control, e.g. one directory above the repo):
   ```bash
   keytool -genkey -v -keystore ../release-keystore.jks -alias elegantgalaxy-tv-launcher \
     -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Copy `keystore.properties.example` to `keystore.properties` (already git-ignored) and fill in the real path/passwords.
3. `./gradlew assembleRelease` will now produce a signed APK automatically. Without `keystore.properties` present, release builds still compile but are unsigned.

**Keep `release-keystore.jks` and its passwords backed up somewhere safe outside git.** If you lose it, you cannot publish updates that Android will treat as "the same app" — you'd need to uninstall/reinstall on every TV instead of updating in place.

## Deploying to a TV (USB / ADB sideload)

1. Enable Developer Options + USB debugging on the TV (usually: Settings > About > click "Build" 7×, then Settings > Developer Options > USB debugging).
2. Connect via USB (or ADB over network if the chipset supports it) and confirm the device is visible:
   ```bash
   adb devices
   ```
3. Install (or reinstall) the APK:
   ```bash
   adb install -r app/build/outputs/apk/release/app-release.apk
   ```
4. To make it the active home launcher without a manual picker prompt each boot:
   ```bash
   adb shell cmd package set-home-activity com.elegantgalaxy.tvlauncher/.MainActivity
   ```
   (Falls back to the launcher picker dialog on Android versions where `set-home-activity` isn't available — the user then just needs to select "Elegant Galaxy TV" once and optionally set it as default.)
5. For a factory-line "USB firmware update" flow instead of per-unit `adb install`, this APK can be dropped into whatever OTA/update package your MT9255 firmware build already uses to push `/system` or `/data` app updates — that packaging step lives outside this repo, in your firmware build pipeline.

## Performance guardrails

- Single Activity, no fragments — avoids duplicate view-hierarchy memory overhead.
- `LazyRow`/`LazyVerticalGrid` for all app lists — only visible tiles are composed/measured, so large app catalogues don't hurt scroll performance on 1-2GB RAM.
- Focus affordance uses `scale`, not `elevation`/shadow, to avoid expensive shadow recomputation per frame.
- `isMinifyEnabled` + `isShrinkResources` are on for release builds (R8 shrinking + resource stripping) to keep APK size and RAM footprint down.

## Roadmap (not yet implemented)

- Wire real volume/brightness/Wi-Fi controls into `SettingsScreen` (currently placeholder rows)
- Persist last-focused tile across app restarts
- Streaming service deep-link integration, voice control, smart-home panel (mentioned as future work — no scaffolding added yet, intentionally, to avoid speculative abstractions)
