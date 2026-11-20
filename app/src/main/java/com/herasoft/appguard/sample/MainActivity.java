package com.herasoft.appguard.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.herasoft.appguard.AppGuard;
import com.herasoft.appguard.AppGuardConfig;
import com.herasoft.appguard.TamperReason;
import com.herasoft.appguard.checks.SignatureCheck;

/**
 * এই স্যাম্পল দেখায় কীভাবে যেকোনো HeraSoft অ্যাপে AppGuard ইন্টিগ্রেট করতে হয়।
 *
 * প্রথমবার চালানোর ধাপ:
 *  ১) নিচের "PUT_YOUR_APP_SIGNATURE_SHA256_HERE" এখনকার মতো রেখেই অ্যাপ রান করুন
 *  ২) Logcat-এ ট্যাগ "AppGuard.Signature" খুঁজে বের করা হ্যাশটা কপি করুন
 *  ৩) সেটা নিচের EXPECTED_SIGNATURE-এ বসিয়ে আবার বিল্ড করুন
 */
public class MainActivity extends Activity {

    private static final String TAG = "AppGuardSample";
    private static final String EXPECTED_SIGNATURE = "PUT_YOUR_APP_SIGNATURE_SHA256_HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ডিবাগিং-এর জন্য বর্তমান সিগনেচার হ্যাশ প্রিন্ট করুন (প্রথমবার সেটআপের সময়)
        SignatureCheck.getCurrentSignatureHash(this);

        AppGuardConfig config = new AppGuardConfig.Builder()
                .expectedSignatureSha256(EXPECTED_SIGNATURE)
                .blockDebugger(true)
                .blockRoot(true)
                .blockHookFrameworks(true)
                .blockEmulator(false) // টেস্টিং সহজ করতে emulator ব্লক বন্ধ রাখা হলো
                .wipeOnTamper(true)
                .build();

        AppGuard.init(this, config, (TamperReason reason) -> {
            Log.w(TAG, "নিরাপত্তা লঙ্ঘন ধরা পড়েছে: " + reason);
            return true; // ডিফল্ট wipe+exit চালানোর জন্য true
        });

        TextView tv = new TextView(this);
        tv.setText("AppGuard সক্রিয় আছে ✅");
        tv.setTextSize(20);
        tv.setPadding(40, 100, 40, 40);
        setContentView(tv);
    }
}
