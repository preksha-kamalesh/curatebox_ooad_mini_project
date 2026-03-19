package com.curatebox.model.state;

import com.curatebox.model.Subscription;

/**
 * State Design Pattern Interface for Managing Subscription Lifecycles.
 */
public interface SubscriptionState {
    void pause(Subscription context);
    void resume(Subscription context);
    void cancel(Subscription context);
}
