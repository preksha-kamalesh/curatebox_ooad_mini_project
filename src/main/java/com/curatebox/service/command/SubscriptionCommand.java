package com.curatebox.service.command;

import com.curatebox.model.Subscription;

public interface SubscriptionCommand {
    void execute(Subscription subscription);
}
