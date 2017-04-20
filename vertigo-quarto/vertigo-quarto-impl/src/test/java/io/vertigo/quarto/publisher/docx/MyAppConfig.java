/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2017, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
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
package io.vertigo.quarto.publisher.docx;

import io.vertigo.app.config.AppConfig;
import io.vertigo.app.config.AppConfigBuilder;
import io.vertigo.app.config.DefinitionProviderConfigBuilder;
import io.vertigo.app.config.ModuleConfigBuilder;
import io.vertigo.commons.impl.CommonsFeatures;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.dynamo.impl.DynamoFeatures;
import io.vertigo.dynamo.plugins.environment.DynamoDefinitionProvider;
import io.vertigo.quarto.plugins.publisher.docx.DOCXMergerPlugin;
import io.vertigo.quarto.publisher.PublisherManager;
import io.vertigo.quarto.publisher.TestPublisherDefinitionProvider;
import io.vertigo.quarto.publisher.impl.PublisherManagerImpl;

class MyAppConfig {

	public static AppConfig config() {
		return new AppConfigBuilder().beginBoot()
				.withLocales("fr_FR")
				.addPlugin(ClassPathResourceResolverPlugin.class)
				.endBoot()
				.addModule(new CommonsFeatures()
						.withScript()
						.build())
				.addModule(new DynamoFeatures()
						.build())
				.addModule(new ModuleConfigBuilder("myApp")
						.addComponent(PublisherManager.class, PublisherManagerImpl.class)
						.addPlugin(DOCXMergerPlugin.class)
						.addDefinitionProvider(new DefinitionProviderConfigBuilder(DynamoDefinitionProvider.class)
								.addDefinitionResource("kpr", "io/vertigo/quarto/publisher/data/execution.kpr")
								.build())
						.addDefinitionProvider(new DefinitionProviderConfigBuilder(TestPublisherDefinitionProvider.class).build())
						.build())
				.build();

	}

}