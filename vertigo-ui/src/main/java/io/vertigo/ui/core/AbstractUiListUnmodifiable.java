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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.definition.DefinitionReference;
import io.vertigo.dynamo.domain.metamodel.DtDefinition;
import io.vertigo.dynamo.domain.metamodel.DtField;
import io.vertigo.dynamo.domain.metamodel.DtFieldName;
import io.vertigo.dynamo.domain.metamodel.FormatterException;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtObject;
import io.vertigo.dynamo.domain.model.Entity;
import io.vertigo.dynamo.domain.model.UID;
import io.vertigo.dynamo.store.StoreManager;
import io.vertigo.lang.Assertion;
import io.vertigo.util.ClassUtil;
import io.vertigo.vega.engines.webservice.json.VegaUiObject;
import io.vertigo.vega.webservice.model.UiList;
import io.vertigo.vega.webservice.model.UiObject;

/**
 * Wrapper d'affichage des listes d'objets métier.
 * @author npiedeloup
 * @param <O> the type of entity
 */
public abstract class AbstractUiListUnmodifiable<O extends DtObject> extends AbstractList<UiObject<O>> implements UiList<O>, Serializable {
	private static final long serialVersionUID = 5475819598230056558L;

	private static final int NB_MAX_ELEMENTS = 1000; //Max nb elements in list. Must be kept under 1000 to ensure good performances.

	/**
	 * Accès au storeManager.
	 */
	protected final ComponentRef<StoreManager> storeManager = ComponentRef.makeLazyRef(StoreManager.class);
	/**
	 * Accès au transactionManager.
	 */
	protected final ComponentRef<VTransactionManager> transactionManager = ComponentRef.makeLazyRef(VTransactionManager.class);

	private final Map<Integer, UiObject<O>> uiObjectByIndex = new HashMap<>();
	private final Map<String, Map<Serializable, UiObject<O>>> uiObjectByFieldValue = new HashMap<>();

	//==========================================================================
	private final DefinitionReference<DtDefinition> dtDefinitionRef;
	private final String camelIdFieldName; //nullable (Option n'est pas serializable)

	/**
	 * Constructeur.
	 * @param dtDefinition DtDefinition
	 */
	AbstractUiListUnmodifiable(final DtDefinition dtDefinition, final Optional<DtFieldName<O>> keyFieldNameOpt) {
		Assertion.checkNotNull(dtDefinition);
		//-----
		dtDefinitionRef = new DefinitionReference<>(dtDefinition);
		final Optional<DtField> idFieldOption = getDtDefinition().getIdField();
		if (idFieldOption.isPresent()) {
			camelIdFieldName = idFieldOption.get().getName();
		} else {
			Assertion.checkState(keyFieldNameOpt.isPresent(), "DtDefinition : {0} is not an entity, you must provide a keyFieldName", dtDefinition.getName());
			camelIdFieldName = keyFieldNameOpt.get().name();
		}
	}

	/** {@inheritDoc} */
	@Override
	public Class<O> getObjectType() {
		return (Class<O>) ClassUtil.classForName(getDtDefinition().getClassCanonicalName());
	}

	public String getIdFieldName() {
		return camelIdFieldName;
	}

	/**
	 * Initialize l'index des UiObjects par Id.
	 * Attention : nécessite la DtList (appel obtainDtList).
	 */
	protected final void initUiObjectByIdIndex() {
		if (camelIdFieldName != null) {
			initUiObjectByKeyIndex(camelIdFieldName);
		}
	}

	/**
	 * Initialize l'index des UiObjects par Clé.
	 * Attention : nécessite la DtList (appel obtainDtList).
	 * @param keyFieldName Nom du champs à indexer
	 */
	public final void initUiObjectByKeyIndex(final String keyFieldName) {
		final Map<Serializable, UiObject<O>> uiObjectById = obtainUiObjectByIdMap(keyFieldName);
		for (final UiObject<O> uiObject : this) {
			uiObjectById.put(((VegaUiObject<O>) uiObject).getTypedValue(keyFieldName, Serializable.class), uiObject);
		}
	}

	/**
	 * Récupère la liste des elements.
	 * Peut-être appelé souvant : doit assurer un cache local (transient au besoin) si chargement.
	 * @return Liste des éléments
	 */
	protected abstract DtList<O> obtainDtList();

	/**
	 * @return DtDefinition de l'objet métier
	 */
	@Override
	public final DtDefinition getDtDefinition() {
		return dtDefinitionRef.get();
	}

	/** {@inheritDoc} */
	@Override
	public final UiObject<O> get(final int index) {
		UiObject<O> element = uiObjectByIndex.get(index);
		if (element == null) {
			element = new MapUiObject<>(obtainDtList().get(index));
			uiObjectByIndex.put(index, element);
			Assertion.checkState(uiObjectByIndex.size() < 1000, "Trop d'élément dans le buffer uiObjectByIndex de la liste de {0}", getDtDefinition().getName());
		}
		return element;
	}

	/** {@inheritDoc} */
	@Override
	public final int size() {
		return obtainDtList().size();
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(final Object o) {
		if (o instanceof DtObject) {
			return indexOfDtObject((DtObject) o);
		} else if (o instanceof UiObject) {
			return indexOfUiObject((UiObject<O>) o);
		}
		return super.indexOf(o);
	}

	/**
	 * @param uiObject UiObject recherché
	 * @return index de l'objet dans la liste
	 */
	private int indexOfUiObject(final UiObject<O> uiObject) {
		Assertion.checkNotNull(uiObject);
		//-----
		return obtainDtList().indexOf(uiObject.getServerSideObject());
	}

	/**
	 * @param dtObject DtObject recherché
	 * @return index de l'objet dans la liste
	 */
	private int indexOfDtObject(final DtObject dtObject) {
		Assertion.checkNotNull(dtObject);
		//-----
		return obtainDtList().indexOf(dtObject);
	}

	/**
	 * Récupère un objet par la valeur de son identifiant.
	 * Utilisé par les select, radio et autocomplete en mode ReadOnly.
	 * @param keyFieldName Nom du champ identifiant
	 * @param keyValue Valeur de l'identifiant
	 * @return UiObject
	 * @throws FormatterException Format error
	 */
	public UiObject<O> getById(final String keyFieldName, final Serializable keyValue) {
		final Map<Serializable, UiObject<O>> uiObjectById = obtainUiObjectByIdMap(keyFieldName);
		UiObject<O> uiObject = uiObjectById.get(keyValue);
		if (uiObject == null) {
			uiObject = loadMissingEntity(keyFieldName, keyValue, uiObjectById);
		}
		return uiObject;
	}

	private UiObject<O> loadMissingEntity(final String keyFieldName, final Serializable keyValue, final Map<Serializable, UiObject<O>> uiObjectById) {
		final DtDefinition dtDefinition = getDtDefinition();
		// ---
		Assertion.checkState(dtDefinition.getIdField().isPresent(), "The definition : {0} must have an id to retrieve elements by Id", dtDefinition);
		// ---
		UiObject<O> uiObject;
		final DtField dtField = dtDefinition.getField(keyFieldName);
		Assertion.checkArgument(dtField.getType().isId(), "La clé {0} de la liste doit être la PK", keyFieldName);

		final O entity = (O) loadDto(keyValue);
		uiObject = new MapUiObject<>(entity);
		uiObjectById.put(keyValue, uiObject);
		Assertion.checkState(uiObjectById.size() < NB_MAX_ELEMENTS, "Trop d'élément dans le buffer uiObjectById de la liste de {0}", getDtDefinition().getName());
		return uiObject;
	}

	private Entity loadDto(final Object key) {
		//-- Transaction BEGIN
		try (final VTransactionWritable transaction = transactionManager.get().createCurrentTransaction()) {
			return storeManager.get().getDataStore().<Entity> readOne(UID.of(getDtDefinition(), key));
		}
	}

	/**
	 * Récupère l'index des UiObjects par Id.
	 * @param keyFieldName Nom du champ identifiant
	 * @return Index des UiObjects par Id
	 */
	protected final Map<Serializable, UiObject<O>> obtainUiObjectByIdMap(final String keyFieldName) {
		Map<Serializable, UiObject<O>> uiObjectById = uiObjectByFieldValue.get(keyFieldName);
		if (uiObjectById == null) {
			uiObjectById = new HashMap<>();
			uiObjectByFieldValue.put(keyFieldName, uiObjectById);
		}
		return uiObjectById;
	}

	/**
	 * @return Liste des uiObjects bufferisés (potentiellement modifiés).
	 */
	protected final Collection<UiObject<O>> getUiObjectBuffer() {
		return uiObjectByIndex.values();
	}

	/**
	 * Vide le buffer des UiObjects (potentiellement modifiés).
	 */
	protected final void clearUiObjectBuffer() {
		uiObjectByIndex.clear();
	}

	public ArrayList<HashMap<String, Serializable>> listForClient(final Set<String> fieldsForClient, final Map<String, Function<Serializable, String>> valueTransformers) {
		final ArrayList<HashMap<String, Serializable>> listForClient = new ArrayList<>();
		for (final UiObject uiObject : this) {
			listForClient.add(((MapUiObject) uiObject).mapForClient(fieldsForClient, valueTransformers));
		}
		return listForClient;
	}

}
