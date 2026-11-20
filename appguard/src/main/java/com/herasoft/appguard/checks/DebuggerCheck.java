package com.herasoft.appguard.checks;

import android.os.Debug;

public final class DebuggerCheck {

    public static boolean isDebuggerAttached() {
        if (Debug.isDebuggerConnected()) return true;

        // টাইমিং-ভিত্তিক সূক্ষ্ম চেক: ডিবাগার/ব্রেকপয়েন্ট থাকলে সাধারণ লুপেও অস্বাভাবিক দেরি হয়
        long start = System.nanoTime();
        int dummy = 0;
        for (int i = 0; i < 1000; i++) dummy += i;
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        return elapsedMs > 100;
    }

    private DebuggerCheck() {}
}
