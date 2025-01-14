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
package io.vertigo.ui.impl.thymeleaf.components;

import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IStandaloneElementTag;

import io.vertigo.lang.Assertion;

public final class NamedComponentContentComponent {

	private final IModel innerModel;
	private final String name;
	private final boolean elementTag;
	private final boolean standaloneTag;
	private final boolean openTag;
	private final boolean closeTag;

	public NamedComponentContentComponent(final IModel innerModel) {
		Assertion.checkNotNull(innerModel);
		//-----
		this.innerModel = innerModel;
		final IElementTag firstLevelTag = (IElementTag) innerModel.get(0);
		name = firstLevelTag.getElementDefinition().getElementName().getElementName();
		elementTag = firstLevelTag instanceof IElementTag;
		standaloneTag = firstLevelTag instanceof IStandaloneElementTag;
		openTag = firstLevelTag instanceof IOpenElementTag;
		closeTag = firstLevelTag instanceof ICloseElementTag;
	}

	/**
	 * Returns the name of the first component
	 *
	 * @return Component name
	 */
	public String getName() {
		return name;
	}

	public boolean isTag(final String testedName) {
		return name.equals(testedName);
	}

	public boolean isElement() {
		return elementTag;
	}

	public boolean isStandalone() {
		return standaloneTag;
	}

	public boolean isOpenTag() {
		return openTag;
	}

	public boolean isCloseTag() {
		return closeTag;
	}

	/**
	 * Returns the thymeleaf Model.
	 * @return IModel inner model
	 */
	public IModel getModel() {
		return innerModel;
	}

	@Override
	public String toString() {
		return innerModel.toString();
	}

}
