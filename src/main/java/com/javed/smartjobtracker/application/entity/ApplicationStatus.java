package com.javed.smartjobtracker.application.entity;

import com.javed.smartjobtracker.common.exception.InvalidStatusTransitionException;

public enum ApplicationStatus {

    APPLIED,
    SHORTLISTED,
    ASSESSMENT,
    INTERVIEW,
    OFFER,
    ACCEPTED,
    REJECTED;

    public boolean canTransitionTo(ApplicationStatus target) {
        return switch (this) {

            case APPLIED ->
                    target == SHORTLISTED || target == REJECTED;

            case SHORTLISTED ->
                    target == ASSESSMENT || target == INTERVIEW || target == REJECTED;

            case ASSESSMENT ->
                    target == INTERVIEW || target == REJECTED;

            case INTERVIEW ->
                    target == OFFER || target == REJECTED;

            case OFFER ->
                    target == ACCEPTED || target == REJECTED;

            case REJECTED ->
                    target == APPLIED;

            case ACCEPTED ->
                    false; // terminal state
        };
    }

    public void validateTransition(ApplicationStatus target) {
        if (!canTransitionTo(target)) {
            throw new InvalidStatusTransitionException(
                    "Invalid status transition: " + this + " → " + target
            );
        }
    }
}