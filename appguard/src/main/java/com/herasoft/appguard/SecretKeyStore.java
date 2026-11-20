package com.herasoft.appguard;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * সংবেদনশীল স্ট্রিং (API key, সার্ভার URL, লাইসেন্স লজিক) প্লেইন টেক্সটে না রেখে
 * এটা দিয়ে এনক্রিপ্ট করে রাখুন। key টা per-install র‍্যান্ডম ভ্যালু থেকে ডেরাইভ হয়;
 * AppGuard ট্যাম্পার ধরলে এই key ফাইল মুছে ফেলে, ফলে পুরনো encrypted ডেটা
 * চিরতরে ডিক্রিপ্ট-অযোগ্য হয়ে যায়।
 */
public final class SecretKeyStore {

    private static final String PREF_NAME = "agk_store";
    private static final String KEY_ALIAS = "agk_v1";

    public static SecretKeySpec getOrCreateKey(Context ctx) throws Exception {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String b64 = sp.getString(KEY_ALIAS, null);
        byte[] keyBytes;
        if (b64 == null) {
            keyBytes = new byte[32]; // AES-256
            new SecureRandom().nextBytes(keyBytes);
            sp.edit().putString(KEY_ALIAS, Base64.encodeToString(keyBytes, Base64.NO_WRAP)).apply();
        } else {
            keyBytes = Base64.decode(b64, Base64.NO_WRAP);
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    /** ট্যাম্পার ধরা পড়লে AppGuard এটা কল করে — key স্থায়ীভাবে মুছে যায় */
    public static void destroyKey(Context ctx) {
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().commit();
    }

    public static String encrypt(Context ctx, String plainText) throws Exception {
        SecretKeySpec key = getOrCreateKey(ctx);
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        return Base64.encodeToString(combined, Base64.NO_WRAP);
    }

    public static String decrypt(Context ctx, String cipherTextB64) throws Exception {
        SecretKeySpec key = getOrCreateKey(ctx);
        byte[] combined = Base64.decode(cipherTextB64, Base64.NO_WRAP);
        byte[] iv = Arrays.copyOfRange(combined, 0, 16);
        byte[] encrypted = Arrays.copyOfRange(combined, 16, combined.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, "UTF-8");
    }

    private SecretKeyStore() {}
}
