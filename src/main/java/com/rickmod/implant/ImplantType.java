package com.rickmod.implant;

public enum ImplantType {
    RICK_SCANNER_EYE("rick_scanner_eye", "head", 0, 0),
    EVIL_MORTY_GOLD_EYE("evil_morty_gold_eye", "head", 0, 0),
    RICK_FORCE_FIELD("rick_force_field", "body", 12.0f, 240),
    EVIL_MORTY_FORCE_FIELD("evil_morty_force_field", "body", 20.0f, 160),
    RICK_SYNTHETIC_LIVER("rick_synthetic_liver", "body", 0, 0),
    PRIME_NANO_REGENERATOR("prime_nano_regenerator", "body", 0, 0),
    RICK_PLASMA_ARM("rick_plasma_arm", "hands", 0, 0),
    EVIL_MORTY_MAGNET_HAND("evil_morty_magnet_hand", "hands", 0, 0),
    PRIME_QUANTUM_BLADES("prime_quantum_blades", "hands", 0, 0),
    RICK_SPRING_LEGS("rick_spring_legs", "belt", 0, 0),
    PRIME_PHASE_ACCELERATOR("prime_phase_accelerator", "feet", 0, 0);

    public final String id;
    public final String slot;
    public final float maxShield;
    public final int rechargeTicks;

    ImplantType(String id, String slot, float maxShield, int rechargeTicks) {
        this.id = id;
        this.slot = slot;
        this.maxShield = maxShield;
        this.rechargeTicks = rechargeTicks;
    }
}
