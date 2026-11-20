# AppGuard

HeraSoft-এর Android অ্যাপগুলোর জন্য একটা ছোট, dependency-ফ্রি সিকিউরিটি লাইব্রেরি —
সিগনেচার ট্যাম্পারিং, ডিবাগার, root, এবং Frida/Xposed-এর মতো হুক ফ্রেমওয়ার্ক ডিটেক্ট করে,
আর ধরা পড়লে অ্যাপের নিজস্ব ডেটা/এনক্রিপশন-কী মুছে ফেলে।

## কী কী করে

| চেক | বর্ণনা |
|---|---|
| সিগনেচার ভেরিফিকেশন | APK re-sign করে মডিফাই করা হয়েছে কিনা |
| Debugger ডিটেকশন | কেউ live ডিবাগার দিয়ে inspect করছে কিনা |
| Root ডিটেকশন | su বাইনারি, root ম্যানেজার অ্যাপ, test-keys বিল্ড |
| Hook framework ডিটেকশন | Frida সার্ভার পোর্ট, Xposed, /proc/self/maps-এ সন্দেহজনক এন্ট্রি |
| Emulator ডিটেকশন (ঐচ্ছিক) | বিল্ড ফিঙ্গারপ্রিন্ট দিয়ে emulator শনাক্ত |
| Tamper response | SharedPreferences + internal files wipe, এনক্রিপশন key ধ্বংস, অ্যাপ বন্ধ |

## ইনস্টল (JitPack দিয়ে)

এই রিপো GitHub-এ পাবলিশ করার পর, আপনার অন্য যেকোনো HeraSoft অ্যাপের
`build.gradle`-এ যোগ করুন:

```gradle
// root build.gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

// app/build.gradle
dependencies {
    implementation 'com.github.<আপনার-গিটহাব-ইউজারনেম>:AppGuard:1.0.0'
}
```

(ট্যাগ/রিলিজ ছাড়া সরাসরি commit hash দিয়েও ব্যবহার করা যায় — JitPack ডকুমেন্টেশন দেখুন।)

## ব্যবহার

```java
AppGuardConfig config = new AppGuardConfig.Builder()
        .expectedSignatureSha256("আপনার-SHA256-হ্যাশ")
        .blockDebugger(true)
        .blockRoot(true)
        .blockHookFrameworks(true)
        .blockEmulator(false)
        .wipeOnTamper(true)
        .build();

AppGuard.init(this, config, reason -> {
    Log.w("MyApp", "Tamper detected: " + reason);
    return true; // ডিফল্ট wipe+exit রেসপন্স
});
```

### সিগনেচার হ্যাশ বের করা

প্রথমবার, `expectedSignatureSha256` খালি/ডামি রেখে অ্যাপ চালান, তারপর:

```java
SignatureCheck.getCurrentSignatureHash(context);
```

কল করলে Logcat-এ ট্যাগ `AppGuard.Signature`-এ হ্যাশটা প্রিন্ট হবে — সেটা কপি করে
config-এ বসান।

## প্রজেক্ট গঠন

```
AppGuard/
├── appguard/        ← লাইব্রেরি মডিউল (এটাই GitHub/JitPack-এর মূল প্রোডাক্ট)
│   └── src/main/java/com/herasoft/appguard/
│       ├── AppGuard.java            ← মূল entry point
│       ├── AppGuardConfig.java      ← প্রতি-অ্যাপ কনফিগারেশন (Builder)
│       ├── TamperListener.java
│       ├── TamperReason.java
│       ├── TamperResponse.java      ← wipe/self-destruct লজিক
│       ├── SecretKeyStore.java      ← string/asset এনক্রিপশন হেলপার
│       └── checks/
│           ├── SignatureCheck.java
│           ├── DebuggerCheck.java
│           ├── RootCheck.java
│           ├── HookCheck.java
│           └── EmulatorCheck.java
└── app/             ← ব্যবহারের উদাহরণ (ডেমো অ্যাপ)
```

## সীমাবদ্ধতা (সততার সাথে)

- এটা অ্যাপকে "ডিকম্পাইল-প্রুফ" করে না — কোনো টুলই তা পারে না। এটা decompile/tamper
  করাকে কষ্টসাধ্য ও অনর্থক করে তোলে, একেবারে অসম্ভব না।
- একজন দক্ষ আক্রমণকারী root+Frida দিয়ে এই চেকগুলোই runtime-এ patch করে বাইপাস
  করতে পারে। আসল সুরক্ষা আসে এই লাইব্রেরি + R8/ProGuard obfuscation + string
  encryption — তিনটা একসাথে ব্যবহার করলে।
- `TamperResponse` শুধু কলিং অ্যাপের নিজস্ব sandbox (`dataDir`)-এর মধ্যে ফাইল মুছে —
  অন্য কোনো অ্যাপ, ফাইল বা সিস্টেম ডেটায় হাত দেয় না।

## লাইসেন্স

MIT — নিজের প্রয়োজন অনুযায়ী পরিবর্তন করে ব্যবহার করুন।
