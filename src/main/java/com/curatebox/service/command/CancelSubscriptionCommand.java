package com.curatebox.service.command;

import com.curatebox.model.Subscription;
import com.curatebox.model.SubscriptionStatus;

public class CancelSubscriptionCommand implements SubscriptionCommand {

    @Override
    public void execute(Subscription subscription) {
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Subscription is already CANCELLED.");
        }
        subscription.setStatus(SubscriptionStatus.CANCELLED);
    }
}
