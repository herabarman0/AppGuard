package com.herasoft.appguard;

import android.content.Context;
import android.util.Log;

import com.herasoft.appguard.checks.DebuggerCheck;
import com.herasoft.appguard.checks.EmulatorCheck;
import com.herasoft.appguard.checks.HookCheck;
import com.herasoft.appguard.checks.RootCheck;
import com.herasoft.appguard.checks.SignatureCheck;

/**
 * ============================================================================
 *  AppGuard — HeraSoft অ্যাপ-সিকিউরিটি লাইব্রেরি
 * ============================================================================
 *
 * ব্যবহার (Application.onCreate() বা প্রথম Activity-এর onCreate()):
 *
 *   AppGuardConfig config = new AppGuardConfig.Builder()
 *           .expectedSignatureSha256("আপনার-SHA256-হ্যাশ")
 *           .build();
 *
 *   AppGuard.init(this, config, reason -> {
 *       Log.w("MyApp", "Tamper detected: " + reason);
 *       return true; // ডিফল্ট wipe+exit রেসপন্স চালানোর জন্য true
 *   });
 *
 * প্রতিটা অ্যাপের জন্য শুধু AppGuardConfig বদলালেই যথেষ্ট — এই লাইব্রেরির
 * ভেতরের কোনো কোড স্পর্শ করার দরকার নেই।
 * ============================================================================
 */
public final class AppGuard {

    private static final String TAG = "AppGuard";

    public static void init(Context context, AppGuardConfig config, TamperListener listener) {
        Context app = context.getApplicationContext();
        TamperReason reason = runChecks(app, config);

        if (reason != null) {
            Log.w(TAG, "Security check failed: " + reason);
            boolean runDefaultResponse = (listener == null) || listener.onTamperDetected(reason);
            if (runDefaultResponse && config.wipeOnTamper) {
                TamperResponse.wipeAndExit(app);
            }
        }
    }

    /** রেসপন্স ছাড়া শুধু স্ট্যাটাস জানতে চাইলে (যেমন সেটিংস স্ক্রিনে দেখানোর জন্য) */
    public static TamperReason checkOnly(Context context, AppGuardConfig config) {
        return runChecks(context.getApplicationContext(), config);
    }

    private static TamperReason runChecks(Context ctx, AppGuardConfig config) {
        if (config.expectedSignatureSha256 != null
                && !SignatureCheck.isValid(ctx, config.expectedSignatureSha256)) {
            return TamperReason.SIGNATURE_MISMATCH;
        }
        if (config.blockDebugger && DebuggerCheck.isDebuggerAttached()) {
            return TamperReason.DEBUGGER_ATTACHED;
        }
        if (config.blockRoot && RootCheck.isDeviceRooted(ctx)) {
            return TamperReason.ROOT_DETECTED;
        }
        if (config.blockHookFrameworks && HookCheck.isHookFrameworkPresent()) {
            return TamperReason.HOOK_FRAMEWORK_DETECTED;
        }
        if (config.blockEmulator && EmulatorCheck.isEmulator()) {
            return TamperReason.EMULATOR_DETECTED;
        }
        return null;
    }

    private AppGuard() {}
}
