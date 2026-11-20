package com.herasoft.appguard.checks;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public final class RootCheck {

    private static final String[] ROOT_PATHS = {
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su",
            "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su",
            "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su",
            "/su/bin/su", "/system/bin/.ext/.su"
    };

    private static final String[] ROOT_PACKAGES = {
            "com.noshufou.android.su", "com.koushikdutta.superuser",
            "eu.chainfire.supersu", "com.topjohnwu.magisk"
    };

    public static boolean isDeviceRooted(Context ctx) {
        return checkRootPaths() || checkRootPackages(ctx) || checkBuildTags() || checkSuBinary();
    }

    private static boolean checkRootPaths() {
        for (String path : ROOT_PATHS) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootPackages(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        for (String pkg : ROOT_PACKAGES) {
            try {
                pm.getPackageInfo(pkg, 0);
                return true; // পাওয়া গেলে মানে অ্যাপটা ইনস্টল আছে
            } catch (PackageManager.NameNotFoundException ignored) {
                // চালিয়ে যান, পরের প্যাকেজ চেক করুন
            }
        }
        return false;
    }

    private static boolean checkBuildTags() {
        String tags = Build.TAGS;
        return tags != null && tags.contains("test-keys");
    }

    private static boolean checkSuBinary() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private RootCheck() {}
}
