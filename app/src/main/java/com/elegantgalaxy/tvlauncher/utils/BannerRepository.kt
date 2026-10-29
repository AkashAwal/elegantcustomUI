package com.elegantgalaxy.tvlauncher.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.elegantgalaxy.tvlauncher.model.Banner
import com.elegantgalaxy.tvlauncher.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Hero-banner feed, kept deliberately light for 1-2GB RAM boxes:
 *  - [load] only reads local files (cached feed, else the bundled assets/banners.json),
 *    so the home screen never waits on the network.
 *  - [refreshIfStale] hits the network at most once per [REFRESH_INTERVAL_MS], fetches a
 *    few-KB JSON, downloads at most [MAX_BANNERS] images, and re-encodes each to
 *    [IMAGE_WIDTH] px wide JPEG so the UI only ever decodes small files.
 *  - No networking/image library: HttpURLConnection + org.json + BitmapFactory.
 *
 * Feed format (JSON array, see assets/banners.json): id, title, subtitle, accent (#RRGGBB),
 * package, and optional image (https URL), deepLink, expires (epoch seconds).
 */
object BannerRepository {

    /**
     * Hosted feed URL (https only). Empty = bundled banners only (assets/banners.json), and any
     * previously cached feed is ignored. Currently empty so the hero shows just the bundled
     * Succession banner; restore "https://akashawal.com/launcher/banners.json" to go back to
     * the hosted feed (which can supply `image`, `meta` and `asset` fields per banner).
     */
    private const val FEED_URL = ""

    /** TMDB read-access token (local.properties -> BuildConfig). When set, it replaces the feed. */
    private val TMDB_TOKEN: String = BuildConfig.TMDB_TOKEN
    private val useRemote get() = FEED_URL.isNotEmpty() || TMDB_TOKEN.isNotEmpty()

    private const val MAX_BANNERS = 4
    private const val REFRESH_INTERVAL_MS = 24L * 60 * 60 * 1000
    private const val IMAGE_WIDTH = 1280
    private const val MAX_IMAGE_BYTES = 3 * 1024 * 1024
    private const val TIMEOUT_MS = 10_000

    private fun dir(context: Context) = File(context.filesDir, "banners").apply { mkdirs() }
    private fun feedFile(context: Context) = File(dir(context), "banners.json")

    fun load(context: Context): List<Banner> {
        val cached = if (FEED_URL.isEmpty()) null else feedFile(context).takeIf { it.exists() }?.readText()
        val json = cached ?: context.assets.open("banners.json").bufferedReader().use { it.readText() }
        val banners = runCatching { parse(context, json) }.getOrNull().orEmpty()
        if (banners.isNotEmpty() || cached == null) return banners
        // Cached feed was corrupt or fully expired (e.g. the daily job stopped): use the bundled set.
        return runCatching {
            parse(context, context.assets.open("banners.json").bufferedReader().use { it.readText() })
        }.getOrDefault(emptyList())
    }

    /** Returns true if the cache was replaced and the caller should [load] again. */
    fun refreshIfStale(context: Context): Boolean {
        if (FEED_URL.isEmpty()) return false
        val feed = feedFile(context)
        if (feed.exists() && System.currentTimeMillis() - feed.lastModified() < REFRESH_INTERVAL_MS) return false

        return runCatching {
            val json = download(FEED_URL, 256 * 1024).toString(Charsets.UTF_8)
            val array = JSONArray(json)
            val keep = HashSet<String>()
            for (i in 0 until minOf(array.length(), MAX_BANNERS)) {
                val item = array.getJSONObject(i)
                val image = item.optString("image")
                if (image.startsWith("https://")) {
                    val name = imageName(item.getString("id"), image)
                    keep += name
                    val target = File(dir(context), name)
                    if (!target.exists()) fetchImage(image, target)
                }
            }
            // Write the feed last so a failed image download never leaves a half-updated cache.
            val tmp = File(dir(context), "banners.json.tmp")
            tmp.writeText(json)
            tmp.renameTo(feed)
            feed.setLastModified(System.currentTimeMillis())
            dir(context).listFiles()?.forEach {
                if (it.name.endsWith(".jpg") && it.name !in keep) it.delete()
            }
            true
        }.getOrDefault(false)
    }

    /** Image file name keyed on id + url so a changed image URL gets re-downloaded. */
    private fun imageName(id: String, url: String) = "img_${id.filter { it.isLetterOrDigit() }}_${url.hashCode()}.jpg"

    private fun parse(context: Context, json: String): List<Banner> {
        val array = JSONArray(json)
        val nowSeconds = System.currentTimeMillis() / 1000
        val result = ArrayList<Banner>()
        for (i in 0 until array.length()) {
            if (result.size == MAX_BANNERS) break
            val item = array.getJSONObject(i)
            val expires = item.optLong("expires", 0L)
            if (expires != 0L && expires < nowSeconds) continue

            val id = item.getString("id")
            val image = item.optString("image")
            val bundled = item.optString("asset")
            val imageFile = when {
                // Artwork shipped inside the APK (assets/<asset>), copied once to the banner dir so
                // [decode] only ever deals with plain files.
                bundled.isNotEmpty() -> copyBundledImage(context, id, bundled)
                image.isNotEmpty() -> File(dir(context), imageName(id, image))
                else -> null
            }
            result += Banner(
                id = id,
                title = item.getString("title"),
                subtitle = item.optString("subtitle"),
                accentArgb = runCatching { Color.parseColor(item.optString("accent")) }.getOrDefault(0xFF1F5C99.toInt()),
                imagePath = imageFile?.takeIf { it.exists() }?.absolutePath,
                packageName = item.getString("package"),
                deepLink = item.optString("deepLink").ifEmpty { null },
                meta = item.optString("meta"),
            )
        }
        return result
    }

    private fun copyBundledImage(context: Context, id: String, assetPath: String): File? {
        val target = File(dir(context), "asset_${id.filter { it.isLetterOrDigit() }}.jpg")
        if (!target.exists()) {
            runCatching {
                context.assets.open(assetPath).use { input -> target.outputStream().use { input.copyTo(it) } }
            }.onFailure { return null }
        }
        return target
    }

    private fun download(url: String, maxBytes: Int): ByteArray {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            check(conn.responseCode == 200) { "HTTP ${conn.responseCode}" }
            val out = java.io.ByteArrayOutputStream()
            conn.inputStream.use { input ->
                val buf = ByteArray(8 * 1024)
                while (true) {
                    val n = input.read(buf)
                    if (n < 0) break
                    out.write(buf, 0, n)
                    check(out.size() <= maxBytes) { "response too large" }
                }
            }
            return out.toByteArray()
        } finally {
            conn.disconnect()
        }
    }

    /** Downloads, downsamples to ~[IMAGE_WIDTH] px wide, and stores as JPEG. */
    private fun fetchImage(url: String, target: File) {
        val bytes = download(url, MAX_IMAGE_BYTES)
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        val bitmap = BitmapFactory.decodeByteArray(
            bytes, 0, bytes.size,
            BitmapFactory.Options().apply { inSampleSize = sampleSizeFor(bounds.outWidth, IMAGE_WIDTH) },
        ) ?: error("undecodable image")
        try {
            val tmp = File(target.parentFile, target.name + ".tmp")
            tmp.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it) }
            tmp.renameTo(target)
        } finally {
            bitmap.recycle()
        }
    }

    /** Largest power-of-two sample size that keeps the width >= [targetWidth]. */
    fun sampleSizeFor(width: Int, targetWidth: Int): Int {
        var sample = 1
        while (width / (sample * 2) >= targetWidth) sample *= 2
        return sample
    }

    /**
     * Decodes a cached banner image as ARGB_8888. RGB_565 would halve the RAM, but at <=1280px wide
     * that's only ~2MB saved and its 5/6/5-bit channels visibly band the dark navy gradients and
     * skin tones — the hero is the largest picture on screen, so it gets full colour depth.
     */
    fun decode(path: String): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        return BitmapFactory.decodeFile(
            path,
            BitmapFactory.Options().apply {
                inSampleSize = sampleSizeFor(bounds.outWidth, IMAGE_WIDTH)
                inPreferredConfig = Bitmap.Config.ARGB_8888
            },
        )
    }
}
