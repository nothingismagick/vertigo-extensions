package io.vertigo.x.impl.notification;

import io.vertigo.dynamo.domain.model.URI;
import io.vertigo.lang.Assertion;
import io.vertigo.x.account.Account;
import io.vertigo.x.notification.Notification;

import java.util.Set;

/**
 * @author pchretien
 */
public final class NotificationEvent {
	private final Notification notification;
	private final Set<URI<Account>> accountURIs;

	//private final List<VUserGroup> toUserGroups;

	NotificationEvent(final Notification notification, final Set<URI<Account>> accountURIs) {
		Assertion.checkNotNull(notification);
		Assertion.checkNotNull(accountURIs);
		//-----
		this.notification = notification;
		this.accountURIs = accountURIs;
	}

	public Notification getNotification() {
		return notification;
	}

	public Set<URI<Account>> getToAccountURIs() {
		return accountURIs;
	}
}