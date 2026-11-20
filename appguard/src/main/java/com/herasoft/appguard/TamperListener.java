package com.herasoft.appguard;

/** ট্যাম্পার ধরা পড়লে কল হয় */
public interface TamperListener {
    /**
     * @return true দিলে AppGuard-এর ডিফল্ট রেসপন্স (self-wipe + exit) চলবে।
     *         false দিলে আপনি নিজে হ্যান্ডল করবেন (যেমন: শুধু একটা ওয়ার্নিং স্ক্রিন দেখানো)।
     */
    boolean onTamperDetected(TamperReason reason);
}
