package com.curatebox.service.command;

import com.curatebox.model.Subscription;
import com.curatebox.model.SubscriptionStatus;

public class ResumeSubscriptionCommand implements SubscriptionCommand {

    @Override
    public void execute(Subscription subscription) {
        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Subscription is already ACTIVE. Cannot resume.");
        }
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Subscription is CANCELLED. Lifecycle has ended, cannot resume.");
        }
        subscription.setStatus(SubscriptionStatus.ACTIVE);
    }
}
