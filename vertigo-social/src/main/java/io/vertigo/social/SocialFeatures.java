/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2019, vertigo-io, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiere - BP 159 - 92357 Le Plessis Robinson Cedex - France
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.social;

import io.vertigo.app.config.Feature;
import io.vertigo.app.config.Features;
import io.vertigo.core.param.Param;
import io.vertigo.social.impl.comment.CommentServicesImpl;
import io.vertigo.social.impl.mail.MailManagerImpl;
import io.vertigo.social.impl.notification.NotificationServicesImpl;
import io.vertigo.social.plugins.comment.memory.MemoryCommentPlugin;
import io.vertigo.social.plugins.comment.redis.RedisCommentPlugin;
import io.vertigo.social.plugins.mail.javax.JavaxSendMailPlugin;
import io.vertigo.social.plugins.notification.memory.MemoryNotificationPlugin;
import io.vertigo.social.plugins.notification.redis.RedisNotificationPlugin;
import io.vertigo.social.services.comment.CommentServices;
import io.vertigo.social.services.mail.MailManager;
import io.vertigo.social.services.notification.NotificationServices;
import io.vertigo.social.webservices.account.AccountWebServices;
import io.vertigo.social.webservices.comment.CommentWebServices;
import io.vertigo.social.webservices.notification.NotificationWebServices;

/**
 * Defines the 'social' extension
 * @author pchretien
 */
public final class SocialFeatures extends Features<SocialFeatures> {

	private boolean commentsEnabled;
	private boolean notificationsEnabled;
	private boolean webapiEnabled;
	private boolean mailEnabled;

	/**
	 * Constructor;
	 */
	public SocialFeatures() {
		super("vertigo-social");
	}

	/**
	 * Activates notifications
	 * @return the features
	 */
	@Feature("notifications")
	public SocialFeatures withNotifications() {
		notificationsEnabled = true;
		return this;
	}

	@Feature("notifications.redis")
	public SocialFeatures withRedisNotifications() {
		getModuleConfigBuilder().addPlugin(RedisNotificationPlugin.class);
		return this;
	}

	@Feature("notifications.memory")
	public SocialFeatures withMemoryNotifications() {
		getModuleConfigBuilder().addPlugin(MemoryNotificationPlugin.class);
		return this;
	}

	/**
	 * Activates comments
	 * @return the features
	 */
	@Feature("comments")
	public SocialFeatures withComments() {
		commentsEnabled = true;
		return this;
	}

	@Feature("comments.redis")
	public SocialFeatures withRedisComments() {
		getModuleConfigBuilder().addPlugin(RedisCommentPlugin.class);
		return this;
	}

	@Feature("comments.memory")
	public SocialFeatures withMemoryComments() {
		getModuleConfigBuilder().addPlugin(MemoryCommentPlugin.class);
		return this;
	}

	/**
	 * Activates mail
	 * @return the features
	 */
	@Feature("mail")
	public SocialFeatures withMails() {
		mailEnabled = true;
		return this;
	}

	@Feature("mail.javax")
	public SocialFeatures withJavaxMail(final Param... params) {
		getModuleConfigBuilder()
				.addPlugin(JavaxSendMailPlugin.class, params);

		return this;

	}

	/**
	 * Activates comments
	 * @return the features
	 */
	@Feature("webapi")
	public SocialFeatures withWebApi() {
		webapiEnabled = true;
		return this;
	}

	@Override
	protected void buildFeatures() {
		if (notificationsEnabled) {
			getModuleConfigBuilder()
					.addComponent(NotificationServices.class, NotificationServicesImpl.class);
			if (webapiEnabled) {
				getModuleConfigBuilder()
						.addComponent(AccountWebServices.class)
						.addComponent(NotificationWebServices.class);
			}
		}
		if (commentsEnabled) {
			getModuleConfigBuilder()
					.addComponent(CommentServices.class, CommentServicesImpl.class);
			if (webapiEnabled) {
				getModuleConfigBuilder()
						.addComponent(CommentWebServices.class);
			}
		}
		if (mailEnabled) {
			getModuleConfigBuilder().addComponent(MailManager.class, MailManagerImpl.class);
		}

	}
}
