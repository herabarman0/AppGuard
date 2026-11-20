package com.herasoft.appguard.checks;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.security.MessageDigest;

public final class SignatureCheck {

    private static final String TAG = "AppGuard.Signature";

    public static boolean isValid(Context ctx, String expectedSha256) {
        if (expectedSha256 == null || expectedSha256.trim().isEmpty()) {
            // config-এ হ্যাশ না দিলে এই চেক স্কিপ হয়ে যায়
            return true;
        }
        String actual = getCurrentSignatureHash(ctx);
        return actual != null && actual.equalsIgnoreCase(expectedSha256.trim());
    }

    /** সেটআপের সময় এটা কল করে Logcat থেকে হ্যাশটা কপি করে config-এ বসান */
    @SuppressWarnings("deprecation")
    public static String getCurrentSignatureHash(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo info = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] sigs = info.signatures;
            if (sigs == null || sigs.length == 0) return null;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(sigs[0].toByteArray());
            byte[] digest = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02X", b));
            String hash = sb.toString();
            Log.i(TAG, "Current signature SHA-256 = " + hash);
            return hash;
        } catch (Exception e) {
            Log.e(TAG, "Signature check failed", e);
            return null;
        }
    }

    private SignatureCheck() {}
}
