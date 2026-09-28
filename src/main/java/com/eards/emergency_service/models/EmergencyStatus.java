package com.eards.emergency_service.models;

import java.util.EnumSet;
import java.util.Set;

public enum EmergencyStatus {
    REPORTED,
    WAITING_FOR_DISPATCH,
    DISPATCHED,
    DISPATCHING,
    AMBULANCE_EN_ROUTE,
    ON_SCENE,
    COMPLETED,
    CANCELLED,
    FAILED;

    public boolean canTransitionTo(EmergencyStatus next) {
        return allowedNext().contains(next);
    }

    public Set<EmergencyStatus> allowedNext() {
        return switch (this) {
            case REPORTED -> EnumSet.of(WAITING_FOR_DISPATCH, DISPATCHING, CANCELLED);
            case WAITING_FOR_DISPATCH -> EnumSet.of(DISPATCHING, CANCELLED, FAILED);
            case DISPATCHING -> EnumSet.of(DISPATCHED, WAITING_FOR_DISPATCH, CANCELLED, FAILED);
            case DISPATCHED -> EnumSet.of(AMBULANCE_EN_ROUTE, CANCELLED, FAILED);
            case AMBULANCE_EN_ROUTE -> EnumSet.of(ON_SCENE, CANCELLED, FAILED);
            case ON_SCENE -> EnumSet.of(COMPLETED, FAILED);
            case COMPLETED, CANCELLED, FAILED -> EnumSet.noneOf(EmergencyStatus.class);
        };
    }
}
