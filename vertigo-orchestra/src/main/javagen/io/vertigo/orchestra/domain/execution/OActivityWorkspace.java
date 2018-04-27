/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2018, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
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
package io.vertigo.orchestra.domain.execution;

import io.vertigo.dynamo.domain.model.Entity;
import io.vertigo.dynamo.domain.model.URI;
import io.vertigo.dynamo.domain.model.VAccessor;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;
import io.vertigo.lang.Generated;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
@io.vertigo.dynamo.domain.stereotype.DataSpace("orchestra")
public final class OActivityWorkspace implements Entity {
	private static final long serialVersionUID = 1L;

	private Long acwId;
	private Boolean isIn;
	private String workspace;

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "A_TKW_TKE",
			fkFieldName = "ACE_ID",
			primaryDtDefinitionName = "DT_O_ACTIVITY_EXECUTION",
			primaryIsNavigable = true,
			primaryRole = "ActivityExecution",
			primaryLabel = "ActivityExecution",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DT_O_ACTIVITY_WORKSPACE",
			foreignIsNavigable = false,
			foreignRole = "ActivityWorkspace",
			foreignLabel = "ActivityWorkspace",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.vertigo.orchestra.domain.execution.OActivityExecution> aceIdAccessor = new VAccessor<>(io.vertigo.orchestra.domain.execution.OActivityExecution.class, "ActivityExecution");

	/** {@inheritDoc} */
	@Override
	public URI<OActivityWorkspace> getURI() {
		return DtObjectUtil.createURI(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id de l'execution d'un processus'.
	 * @return Long acwId <b>Obligatoire</b>
	 */
	@Field(domain = "DO_O_IDENTIFIANT", type = "ID", required = true, label = "Id de l'execution d'un processus")
	public Long getAcwId() {
		return acwId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id de l'execution d'un processus'.
	 * @param acwId Long <b>Obligatoire</b>
	 */
	public void setAcwId(final Long acwId) {
		this.acwId = acwId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Workspace in/out'.
	 * @return Boolean isIn <b>Obligatoire</b>
	 */
	@Field(domain = "DO_O_BOOLEEN", required = true, label = "Workspace in/out")
	public Boolean getIsIn() {
		return isIn;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Workspace in/out'.
	 * @param isIn Boolean <b>Obligatoire</b>
	 */
	public void setIsIn(final Boolean isIn) {
		this.isIn = isIn;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Contenu du workspace'.
	 * @return String workspace
	 */
	@Field(domain = "DO_O_JSON_TEXT", label = "Contenu du workspace")
	public String getWorkspace() {
		return workspace;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Contenu du workspace'.
	 * @param workspace String
	 */
	public void setWorkspace(final String workspace) {
		this.workspace = workspace;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'ActivityExecution'.
	 * @return Long aceId <b>Obligatoire</b>
	 */
	@Field(domain = "DO_O_IDENTIFIANT", type = "FOREIGN_KEY", required = true, label = "ActivityExecution")
	public Long getAceId() {
		return (Long)  aceIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'ActivityExecution'.
	 * @param aceId Long <b>Obligatoire</b>
	 */
	public void setAceId(final Long aceId) {
		aceIdAccessor.setId(aceId);
	}

 	/**
	 * Association : ActivityExecution.
	 * @return l'accesseur vers la propriété 'ActivityExecution'
	 */
	public VAccessor<io.vertigo.orchestra.domain.execution.OActivityExecution> activityExecution() {
		return aceIdAccessor;
	}
	
	@Deprecated
	public io.vertigo.orchestra.domain.execution.OActivityExecution getActivityExecution() {
		// we keep the lazyness
		if (!aceIdAccessor.isLoaded()) {
			aceIdAccessor.load();
		}
		return aceIdAccessor.get();
	}

	/**
	 * Retourne l'URI: ActivityExecution.
	 * @return URI de l'association
	 */
	@Deprecated
	public io.vertigo.dynamo.domain.model.URI<io.vertigo.orchestra.domain.execution.OActivityExecution> getActivityExecutionURI() {
		return aceIdAccessor.getURI();
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}