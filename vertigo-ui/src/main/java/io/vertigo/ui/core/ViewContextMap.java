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
package io.vertigo.ui.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import io.vertigo.dynamo.domain.metamodel.DtDefinition;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtListURIForMasterData;
import io.vertigo.dynamo.domain.model.DtObject;
import io.vertigo.lang.Assertion;
import io.vertigo.util.StringUtil;
import io.vertigo.vega.webservice.model.UiList;
import io.vertigo.vega.webservice.model.UiObject;
import io.vertigo.vega.webservice.validation.DefaultDtObjectValidator;
import io.vertigo.vega.webservice.validation.DtObjectValidator;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import io.vertigo.vega.webservice.validation.ValidationUserException;

/**
 * Liste des couples (clé, object) enregistrés.
 * @author npiedeloup
 */
public final class ViewContextMap extends HashMap<String, Serializable> {
	/** Clée de l'id de context dans le context. */
	public static final String CTX = "CTX";
	private static final long serialVersionUID = 2850788652438173312L;
	public static final String INPUT_CTX = "INPUT_CTX";

	private static final String PROTECTED_VALUE_TRANSFORMER = "protected";
	private static final String MAP_VALUE_TRANSFORMER = "map";

	//Index UiObject et DtObject vers clé de context.
	private final Map<Serializable, String> reverseUiObjectIndex = new HashMap<>();
	//Index UiList et DtList vers clé de context. //identity HashMap because two empty list aren't the same
	private final Map<UiList<?>, String> reverseUiListIndex = new IdentityHashMap<>();
	private boolean unmodifiable; //initialisé à false
	private boolean dirty = false;

	private final Map<String, Set<String>> keysForClient = new HashMap<>();
	private final Map<String, Map<String, List<String>>> valueTransformers = new HashMap<>();

	/** {@inheritDoc} */
	@Override
	public Serializable get(final Object key) {
		Assertion.checkNotNull(key);
		//-----
		final Serializable o = super.get(key);
		Assertion.checkNotNull(o, "Objet :{0} non trouvé! Vérifier que l objet est bien enregistré avec la clé. Clés disponibles {1}", key, keySet());
		return o;
	}

	/**
	 * @param key Clé de context
	 * @return UiObject du context
	 */
	public <O extends DtObject> UiObject<O> getUiObject(final String key) {
		return (UiObject<O>) get(key);
	}

	/**
	 * @param key Clé de context
	 * @return UiList du context
	 */
	public <O extends DtObject> UiList<O> getUiList(final String key) {
		return (UiList<O>) get(key);
	}

	/**
	 * @param key Clé de context
	 * @return UiListModifiable du context
	 */
	public <O extends DtObject> BasicUiListModifiable<O> getUiListModifiable(final String key) {
		return (BasicUiListModifiable<O>) get(key);
	}

	/**
	 * @param key Clé de context
	 * @return String du context
	 */
	public String getString(final String key) {
		final Object value = get(key);
		if (value instanceof String[] && ((String[]) value).length > 0) {
			//Struts set des String[] au lieu des String
			//on prend le premier
			return ((String[]) value)[0];
		}
		return (String) get(key);
	}

	/**
	 * @param key Clé de context
	 * @return Long du context
	 */
	public Long getLong(final String key) {
		return (Long) get(key);
	}

	/**
	 * @param key Clé de context
	 * @return Integer du context
	 */
	public Integer getInteger(final String key) {
		return (Integer) get(key);
	}

	/**
	 * @param key Clé de context
	 * @return Boolean du context
	 */
	public Boolean getBoolean(final String key) {
		return (Boolean) get(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsKey(final Object key) {
		Assertion.checkNotNull(key);
		//-----
		return super.containsKey(key);
	}

	/**
	 * @param uiObject UiObject recherché
	 * @return Clé de context de l'élément (null si non trouvé)
	 */
	public String findKey(final UiObject<?> uiObject) {
		Assertion.checkNotNull(uiObject);
		//-----
		final String contextKey = reverseUiObjectIndex.get(uiObject);
		if (contextKey != null) {
			return contextKey;
		}
		for (final Map.Entry<UiList<?>, String> entry : reverseUiListIndex.entrySet()) {
			final int index = entry.getKey().indexOf(uiObject);
			if (index >= 0) {
				return entry.getValue() + ".get(" + index + ")";
			}
		}
		return null;
	}

	/**
	 * @param dtObject DtObject recherché
	 * @return Clé de context de l'élément (null si non trouvé)
	 */
	public String findKey(final DtObject dtObject) {
		Assertion.checkNotNull(dtObject);
		//-----
		final String contextKey = reverseUiObjectIndex.get(dtObject);
		if (contextKey != null) {
			return contextKey;
		}
		for (final Map.Entry<UiList<?>, String> entry : reverseUiListIndex.entrySet()) {
			final int index = entry.getKey().indexOf(dtObject);
			if (index >= 0) {
				return entry.getValue() + ".get(" + index + ")";
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Serializable put(final String key, final Serializable value) {
		Assertion.checkState(!unmodifiable, "Ce context ({0}) a été figé et n'est plus modifiable.", super.get(CTX));
		Assertion.checkArgNotEmpty(key);
		Assertion.checkNotNull(value, "la valeur doit être renseignée pour {0}", key);
		Assertion.checkArgument(!(value instanceof DtObject), "Vous devez poser des uiObject dans le context pas des objets métiers ({0})", key);
		Assertion.checkArgument(!(value instanceof DtList), "Vous devez poser des uiList dans le context pas des listes d'objets métiers ({0})", key);
		//-----
		if (value instanceof UiObject) {
			reverseUiObjectIndex.put(value, key);
			reverseUiObjectIndex.put(((UiObject<?>) value).getServerSideObject(), key);
		} else if (value instanceof UiList) {
			reverseUiListIndex.put((UiList<?>) value, key);
		}

		return super.put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public Serializable remove(final Object key) {
		Assertion.checkState(!unmodifiable, "Ce context ({0}) a été figé et n'est plus modifiable.", super.get(CTX));
		Assertion.checkState(key instanceof String, "La clé doit être de type String");
		//---
		final String keyString = (String) key;
		Assertion.checkArgNotEmpty(keyString);
		//---
		// on garde les index en cohérence après un remove
		reverseUiObjectIndex.values().removeIf(val -> keyString.equals(val));
		reverseUiListIndex.values().removeIf(val -> keyString.equals(val));
		// on fait le remove
		return super.remove(key);
	}

	/**
	 * @return Clé de ce context
	 */
	public String getId() {
		return getString(CTX);
	}

	/**
	 * Génère un nouvel Id et passe le context en modifiable.
	 */
	public void makeModifiable() {
		unmodifiable = false;
		super.remove(CTX);
	}

	/**
	 * passe le context en non-modifiable.
	 */
	public void makeUnmodifiable() {
		Assertion.checkState(!dirty, "Can't fixed a dirty context");
		//-----
		unmodifiable = true;
	}

	/**
	 * Mark this context as Dirty : shouldn't be stored and keep old id.
	 */
	public void markDirty() {
		super.put(CTX, super.get(INPUT_CTX));
		unmodifiable = true;
		dirty = true;
	}

	/**
	 * @return if context dirty : shouldn't be stored and keep old id
	 */
	public boolean isDirty() {
		return dirty;
	}

	public ViewContextMap getVContext() {
		return this;
	}

	/**
	 * Ajoute un objet de type form au context.
	 * @param dto Objet à publier
	 */
	public <O extends DtObject> void publish(final String contextKey, final O dto) {
		final UiObject<O> strutsUiObject = new MapUiObject<>(dto);
		strutsUiObject.setInputKey(contextKey);
		put(contextKey, strutsUiObject);
	}

	/**
	 * Vérifie les erreurs de l'objet. Celles-ci sont ajoutées à l'uiMessageStack si nécessaire.
	 */
	public void checkErrors(final String contextKey, final UiMessageStack uiMessageStack) {
		getUiObject(contextKey).checkFormat(uiMessageStack);
		if (uiMessageStack.hasErrors()) {
			throw new ValidationUserException();
		}
	}

	/**
	 * @return objet métier valid�. Lance une exception si erreur.
	 */
	public <O extends DtObject> O readDto(final String contextKey, final UiMessageStack uiMessageStack) {
		return readDto(contextKey, new DefaultDtObjectValidator<>(), uiMessageStack);
	}

	/**
	 * @return objet métier valid�. Lance une exception si erreur.
	 */
	public <O extends DtObject> O readDto(final String contextKey, final DtObjectValidator<O> validator, final UiMessageStack uiMessageStack) {
		checkErrors(contextKey, uiMessageStack);
		// ---
		final O validatedDto = ((UiObject<O>) getUiObject(contextKey)).mergeAndCheckInput(Collections.singletonList(validator), uiMessageStack);
		if (uiMessageStack.hasErrors()) {
			throw new ValidationUserException();
		}
		return validatedDto;
	}

	public void addKeyForClient(final String object, final String fieldName) {
		Assertion.checkState(containsKey(object), "No {0} in context", object);
		//----
		keysForClient.computeIfAbsent(object, k -> new HashSet<>()).add(fieldName);
	}

	public void addKeyForClient(final String object) {
		Assertion.checkState(containsKey(object), "No {0} in context", object);
		//----
		keysForClient.put(object, Collections.emptySet());// notmodifiable because used only for primitives
	}

	public void addProtectedValueTransformer(final String objectKey, final String objectFieldName) {
		Assertion.checkState(containsKey(objectKey), "No {0} in context", objectKey);
		//----
		valueTransformers.computeIfAbsent(objectKey,
				k -> new HashMap<>()).put(objectFieldName,
						Arrays.asList(PROTECTED_VALUE_TRANSFORMER));
	}

	public String obtainFkList(final String objectKey, final String objectFieldName) {
		Assertion.checkState(containsKey(objectKey), "No {0} in context", objectKey);
		Assertion.checkState(objectFieldName.endsWith("_display"), "Can't accept {0}, only '_display' transformer is accepted", objectKey);
		//----
		final String fieldName = objectFieldName.substring(0, objectFieldName.length() - "_display".length());
		final DtDefinition fkDefinition = getUiObject(objectKey).getDtDefinition().getField(fieldName).getFkDtDefinition();
		final String uiMdListContextKey = fkDefinition.getClassSimpleName() + "MdList";
		if (!containsKey(uiMdListContextKey)) {
			unmodifiable = false; //hem :(
			put(uiMdListContextKey, new UiMdList<>(new DtListURIForMasterData(fkDefinition, null)));
			unmodifiable = true;
		}
		return uiMdListContextKey;
	}

	public void addListValueTransformer(final String objectKey, final String objectFieldName, final String listKey, final String listKeyFieldName, final String listDisplayFieldName) {
		valueTransformers.computeIfAbsent(objectKey, k -> new HashMap<>()).put(objectFieldName,
				Arrays.asList(MAP_VALUE_TRANSFORMER, listKey, listKeyFieldName, listDisplayFieldName));
	}

	public ViewContextMap getFilteredViewContext() {
		return getFilteredViewContext(Collections.emptySet());
	}

	ViewContextMap getFilteredViewContext(final Set<String> subFilter) {
		final ViewContextMap viewContextMapForClient = new ViewContextMap();
		viewContextMapForClient.put(CTX, get(CTX));
		for (final Map.Entry<String, Serializable> entry : entrySet()) {
			final String key = entry.getKey();
			if (keysForClient.containsKey(key) && (subFilter.isEmpty() || subFilter.contains(key))) {
				if (entry.getValue() instanceof MapUiObject) {
					viewContextMapForClient.put(entry.getKey(), ((MapUiObject) entry.getValue()).mapForClient(keysForClient.get(key), createTransformers(key)));
				} else if (entry.getValue() instanceof AbstractUiListUnmodifiable) {
					//handle lists
					viewContextMapForClient.put(entry.getKey(), ((AbstractUiListUnmodifiable) entry.getValue()).listForClient(keysForClient.get(key), createTransformers(key)));
				} else if (entry.getValue() instanceof BasicUiListModifiable) {
					//handle lists modifiable
					viewContextMapForClient.put(entry.getKey(), ((BasicUiListModifiable) entry.getValue()).listForClient(keysForClient.get(key), createTransformers(key)));
				} else {
					// just copy it
					viewContextMapForClient.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return viewContextMapForClient;
	}

	private Map<String, Function<Serializable, String>> createTransformers(final String key) {
		if (valueTransformers.containsKey(key)) {
			final Map<String, Function<Serializable, String>> resultMap = new HashMap<>();
			valueTransformers.get(key).entrySet()
					.forEach(entry -> resultMap.put(entry.getKey(), createValueTransformer(entry.getValue())));
			return resultMap;
		}
		return Collections.emptyMap();
	}

	private Function<Serializable, String> createValueTransformer(final List<String> params) {
		Assertion.checkState(params.size() > 0, "ValueTransformer should be typed in first param, provided params {0}", params);
		final String transformerType = params.get(0);

		if (PROTECTED_VALUE_TRANSFORMER.equals(transformerType)) {
			return ProtectedValueUtil::generateProtectedValue;
		} else if (MAP_VALUE_TRANSFORMER.equals(transformerType)) {
			Assertion.checkState(params.size() == 3 + 1, "ListValueTransformer requires 3 params, provided params {0}", params);
			// ---
			final String listKey = params.get(1);
			final String listKeyFieldName = params.get(2);
			final String listDisplayFieldName = params.get(3);

			// if value is null the transformer return null
			return value -> value != null ? ((AbstractUiListUnmodifiable) getUiList(listKey)).getById(listKeyFieldName, value).getString(listDisplayFieldName) : null;
		}
		throw new IllegalStateException(StringUtil.format("Unsupported ValueTransformer type {0}", transformerType));
	}

}
