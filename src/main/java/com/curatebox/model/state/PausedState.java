package com.curatebox.model.state;

import com.curatebox.model.Subscription;
import com.curatebox.model.SubscriptionStatus;

public class PausedState implements SubscriptionState {

    @Override
    public void pause(Subscription context) {
        throw new IllegalStateException("Subscription is already PAUSED. Cannot pause again.");
    }

    @Override
    public void resume(Subscription context) {
        context.setStatus(SubscriptionStatus.ACTIVE);
        context.setState(new ActiveState());
    }

    @Override
    public void cancel(Subscription context) {
        context.setStatus(SubscriptionStatus.CANCELLED);
        context.setState(new CancelledState());
    }
}
