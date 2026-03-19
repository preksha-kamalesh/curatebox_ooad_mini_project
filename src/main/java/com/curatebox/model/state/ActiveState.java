package com.curatebox.model.state;

import com.curatebox.model.Subscription;
import com.curatebox.model.SubscriptionStatus;

public class ActiveState implements SubscriptionState {

    @Override
    public void pause(Subscription context) {
        context.setStatus(SubscriptionStatus.PAUSED);
        context.setState(new PausedState());
    }

    @Override
    public void resume(Subscription context) {
        throw new IllegalStateException("Subscription is already ACTIVE. Cannot resume.");
    }

    @Override
    public void cancel(Subscription context) {
        context.setStatus(SubscriptionStatus.CANCELLED);
        context.setState(new CancelledState());
    }
}
