package com.herasoft.appguard;

/** কোন কারণে ট্যাম্পার/সিকিউরিটি ভায়োলেশন ধরা পড়লো */
public enum TamperReason {
    SIGNATURE_MISMATCH,
    DEBUGGER_ATTACHED,
    ROOT_DETECTED,
    HOOK_FRAMEWORK_DETECTED,
    EMULATOR_DETECTED
}
