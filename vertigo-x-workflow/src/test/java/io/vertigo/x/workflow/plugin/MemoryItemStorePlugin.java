


/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2016, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
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

package io.vertigo.x.workflow.plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.vertigo.dynamo.domain.model.DtObject;
import io.vertigo.x.impl.workflow.ItemStorePlugin;

/**
 *
 * @author xdurand
 *
 */
public class MemoryItemStorePlugin implements ItemStorePlugin {

	private final Map<Long, DtObject> inMemoryItemStore = new ConcurrentHashMap<>();

	@Override
	public void addItem(Long itemId, DtObject item) {
		inMemoryItemStore.put(itemId, item);
	}
	
	@Override
	public DtObject readItem(final Long itemId) {
		return inMemoryItemStore.get(itemId);
	}

}