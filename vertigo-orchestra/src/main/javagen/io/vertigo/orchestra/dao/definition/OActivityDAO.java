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
package io.vertigo.orchestra.dao.definition;

import javax.inject.Inject;

import java.util.Optional;
import io.vertigo.app.Home;
import io.vertigo.dynamo.task.metamodel.TaskDefinition;
import io.vertigo.dynamo.task.model.Task;
import io.vertigo.dynamo.task.model.TaskBuilder;
import io.vertigo.dynamo.impl.store.util.DAO;
import io.vertigo.dynamo.store.StoreManager;
import io.vertigo.dynamo.store.StoreServices;
import io.vertigo.dynamo.task.TaskManager;
import io.vertigo.orchestra.domain.definition.OActivity;
import io.vertigo.lang.Generated;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class OActivityDAO extends DAO<OActivity, java.lang.Long> implements StoreServices {

	/**
	 * Contructeur.
	 * @param storeManager Manager de persistance
	 * @param taskManager Manager de Task
	 */
	@Inject
	public OActivityDAO(final StoreManager storeManager, final TaskManager taskManager) {
		super(OActivity.class, storeManager, taskManager);
	}


	/**
	 * Creates a taskBuilder.
	 * @param name  the name of the task
	 * @return the builder 
	 */
	private static TaskBuilder createTaskBuilder(final String name) {
		final TaskDefinition taskDefinition = Home.getApp().getDefinitionSpace().resolve(name, TaskDefinition.class);
		return Task.builder(taskDefinition);
	}

	/**
	 * Execute la tache TkGetActivitiesByProId.
	 * @param proId Long 
	 * @return io.vertigo.dynamo.domain.model.DtList<io.vertigo.orchestra.domain.definition.OActivity> dtOActivities
	*/
	public io.vertigo.dynamo.domain.model.DtList<io.vertigo.orchestra.domain.definition.OActivity> getActivitiesByProId(final Long proId) {
		final Task task = createTaskBuilder("TkGetActivitiesByProId")
				.addValue("proId", proId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkGetAllActivitiesInActiveProcesses.
	 * @return io.vertigo.dynamo.domain.model.DtList<io.vertigo.orchestra.domain.definition.OActivity> dtOActivities
	*/
	public io.vertigo.dynamo.domain.model.DtList<io.vertigo.orchestra.domain.definition.OActivity> getAllActivitiesInActiveProcesses() {
		final Task task = createTaskBuilder("TkGetAllActivitiesInActiveProcesses")
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkGetFirstActivityByProcess.
	 * @param proId Long 
	 * @return io.vertigo.orchestra.domain.definition.OActivity dtOActivity
	*/
	public io.vertigo.orchestra.domain.definition.OActivity getFirstActivityByProcess(final Long proId) {
		final Task task = createTaskBuilder("TkGetFirstActivityByProcess")
				.addValue("proId", proId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TkGetNextActivityByActId.
	 * @param actId Long 
	 * @return Option de io.vertigo.orchestra.domain.definition.OActivity dtOActivity
	*/
	public Optional<io.vertigo.orchestra.domain.definition.OActivity> getNextActivityByActId(final Long actId) {
		final Task task = createTaskBuilder("TkGetNextActivityByActId")
				.addValue("actId", actId)
				.build();
		return Optional.ofNullable((io.vertigo.orchestra.domain.definition.OActivity) getTaskManager()
				.execute(task)
				.getResult());
	}

}
