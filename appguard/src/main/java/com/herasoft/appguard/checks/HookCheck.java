package com.herasoft.appguard.checks;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/** Frida / Xposed / Substrate-এর মতো ডাইনামিক ইনস্ট্রুমেন্টেশন টুল ডিটেকশন */
public final class HookCheck {

    private static final List<String> SUSPICIOUS_MAP_KEYWORDS = Arrays.asList("frida", "xposed", "substrate");

    public static boolean isHookFrameworkPresent() {
        return checkXposed() || checkFridaDefaultPort() || checkSuspiciousMemoryMaps();
    }

    private static boolean checkXposed() {
        try {
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                if (ste.getClassName().contains("de.robv.android.xposed")) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static boolean checkFridaDefaultPort() {
        // frida-server ডিফল্টে 27042 পোর্টে চলে
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", 27042), 150);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkSuspiciousMemoryMaps() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream("/proc/self/maps")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String lower = line.toLowerCase();
                for (String keyword : SUSPICIOUS_MAP_KEYWORDS) {
                    if (lower.contains(keyword)) return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private HookCheck() {}
}
