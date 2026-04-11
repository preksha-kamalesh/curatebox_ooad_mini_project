package com.curatebox.service.command;

import com.curatebox.model.Subscription;
import com.curatebox.model.SubscriptionStatus;

public class PauseSubscriptionCommand implements SubscriptionCommand {

    @Override
    public void execute(Subscription subscription) {
        if (subscription.getStatus() == SubscriptionStatus.PAUSED) {
            throw new IllegalStateException("Subscription is already PAUSED. Cannot pause again.");
        }
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Subscription is CANCELLED. Lifecycle has ended, cannot pause.");
        }
        subscription.setStatus(SubscriptionStatus.PAUSED);
    }
}
