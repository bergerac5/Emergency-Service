package com.eards.emergency_service.models;

public enum EmergencyStatus {
    REPORTED,
    WAITING_FOR_DISPATCH,
    DISPATCHED,
    DISPATCHING,
    AMBULANCE_EN_ROUTE,
    ON_SCENE,
    COMPLETED,
    CANCELLED,
    FAILED
}
