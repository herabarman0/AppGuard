package com.herasoft.appguard;

/**
 * প্রতিটা অ্যাপের জন্য আলাদা config — সোর্স কোড এডিট না করে Builder দিয়ে বসানো যায়।
 *
 * ব্যবহার:
 *   AppGuardConfig config = new AppGuardConfig.Builder()
 *           .expectedSignatureSha256("ABCDEF0123...")
 *           .blockEmulator(false)
 *           .blockDebugger(true)
 *           .blockRoot(true)
 *           .blockHookFrameworks(true)
 *           .wipeOnTamper(true)
 *           .build();
 */
public final class AppGuardConfig {

    final String expectedSignatureSha256;
    final boolean blockEmulator;
    final boolean blockDebugger;
    final boolean blockRoot;
    final boolean blockHookFrameworks;
    final boolean wipeOnTamper;

    private AppGuardConfig(Builder b) {
        this.expectedSignatureSha256 = b.expectedSignatureSha256;
        this.blockEmulator = b.blockEmulator;
        this.blockDebugger = b.blockDebugger;
        this.blockRoot = b.blockRoot;
        this.blockHookFrameworks = b.blockHookFrameworks;
        this.wipeOnTamper = b.wipeOnTamper;
    }

    public static final class Builder {
        private String expectedSignatureSha256 = null;
        private boolean blockEmulator = false;
        private boolean blockDebugger = true;
        private boolean blockRoot = true;
        private boolean blockHookFrameworks = true;
        private boolean wipeOnTamper = true;

        /** আপনার signed APK-এর SHA-256 সিগনেচার হ্যাশ (null রাখলে সিগনেচার চেক স্কিপ হবে) */
        public Builder expectedSignatureSha256(String hash) {
            this.expectedSignatureSha256 = hash;
            return this;
        }

        public Builder blockEmulator(boolean value) {
            this.blockEmulator = value;
            return this;
        }

        public Builder blockDebugger(boolean value) {
            this.blockDebugger = value;
            return this;
        }

        public Builder blockRoot(boolean value) {
            this.blockRoot = value;
            return this;
        }

        public Builder blockHookFrameworks(boolean value) {
            this.blockHookFrameworks = value;
            return this;
        }

        /** false দিলে ট্যাম্পার ধরা পড়লেও ডেটা wipe হবে না, শুধু listener-কে জানানো হবে */
        public Builder wipeOnTamper(boolean value) {
            this.wipeOnTamper = value;
            return this;
        }

        public AppGuardConfig build() {
            return new AppGuardConfig(this);
        }
    }
}
