package com.inventorymate.business.model;

public enum UnitType {
    UNIT(1.0), // piezas
    KG(1000.0),
    G(1.0),
    L(1000.0),
    ML(1.0);

    private final double baseFactor; // factor para convertir a gramos o mililitros

    UnitType(double baseFactor) {
        this.baseFactor = baseFactor;
    }

    public boolean isCompatible(UnitType other) {
        if (this == other) {
            return true;
        }

        // Check if both units are weight or volume
        return (this == G && other == KG) || (this == KG && other == G)
                || (this == ML && other == L) || (this == L && other == ML);
    }

    public double toBase(double value) {
        return switch (this) {
            case KG, L -> value * 1000;
            default -> value;
        };
    }

    public double fromBase(double baseValue) {
        return switch (this) {
            case KG, L -> baseValue / 1000;
            default -> baseValue;
        };
    }

    public boolean isWeight() {
        return this == KG || this == G;
    }

    public boolean isVolume() {
        return this == L || this == ML;
    }
}
