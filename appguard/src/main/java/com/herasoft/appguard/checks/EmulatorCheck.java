package com.herasoft.appguard.checks;

import android.os.Build;

/** ঐচ্ছিক — সতর্কতার সাথে ব্যবহার করুন, কারণ অনেক বৈধ টেস্টারও emulator ব্যবহার করে */
public final class EmulatorCheck {

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.equals("sdk") || Build.PRODUCT.equals("sdk_x86")
                || Build.PRODUCT.equals("vbox86p");
    }

    private EmulatorCheck() {}
}
