package com.herasoft.appguard;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * ট্যাম্পার ধরা পড়লে নিজের অ্যাপের sandbox-এর মধ্যে ডেটা মুছে ফেলে।
 * এটা অন্য কোনো অ্যাপ, ফাইল বা সিস্টেম ডেটাতে হাত দেয় না —
 * শুধু কলিং অ্যাপের নিজস্ব dataDir-এর মধ্যে কাজ করে।
 */
public final class TamperResponse {

    private static final String TAG = "AppGuard.Response";

    public static void wipeAndExit(Context ctx) {
        try {
            // ১) এনক্রিপশন key ধ্বংস → encrypted string/asset চিরতরে অপাঠ্য
            SecretKeyStore.destroyKey(ctx);

            // ২) SharedPreferences wipe
            File prefsDir = new File(ctx.getApplicationInfo().dataDir, "shared_prefs");
            deleteRecursive(prefsDir);

            // ৩) internal ফাইল/ক্যাশ wipe
            deleteRecursive(ctx.getFilesDir());
            deleteRecursive(ctx.getCacheDir());
        } catch (Exception e) {
            Log.e(TAG, "Wipe failed", e);
        } finally {
            if (ctx instanceof Activity) {
                ((Activity) ctx).finishAffinity();
            }
            System.exit(0);
        }
    }

    private static void deleteRecursive(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) for (File c : children) deleteRecursive(c);
        }
        //noinspection ResultOfMethodCallIgnored
        f.delete();
    }

    private TamperResponse() {}
}
