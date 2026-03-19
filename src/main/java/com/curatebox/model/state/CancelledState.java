package com.curatebox.model.state;

import com.curatebox.model.Subscription;

public class CancelledState implements SubscriptionState {

    @Override
    public void pause(Subscription context) {
        throw new IllegalStateException("Subscription is CANCELLED. Lifecycle has ended, cannot pause.");
    }

    @Override
    public void resume(Subscription context) {
        throw new IllegalStateException("Subscription is CANCELLED. Lifecycle has ended, cannot resume.");
    }

    @Override
    public void cancel(Subscription context) {
        throw new IllegalStateException("Subscription is already CANCELLED.");
    }
}
