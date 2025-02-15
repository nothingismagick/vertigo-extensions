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
package io.vertigo.orchestra.dao.execution;

import javax.inject.Inject;

import io.vertigo.app.Home;
import io.vertigo.dynamo.task.TaskManager;
import io.vertigo.dynamo.task.metamodel.TaskDefinition;
import io.vertigo.dynamo.task.model.Task;
import io.vertigo.dynamo.task.model.TaskBuilder;
import io.vertigo.dynamo.store.StoreServices;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.Generated;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
 @Generated
public final class ExecutionPAO implements StoreServices {
	private final TaskManager taskManager;

	/**
	 * Constructeur.
	 * @param taskManager Manager des Task
	 */
	@Inject
	public ExecutionPAO(final TaskManager taskManager) {
		Assertion.checkNotNull(taskManager);
		//-----
		this.taskManager = taskManager;
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
	 * Execute la tache TkHandleDeadProcessesOfNode.
	 * @param nodId Long 
	*/
	public void handleDeadProcessesOfNode(final Long nodId) {
		final Task task = createTaskBuilder("TkHandleDeadProcessesOfNode")
				.addValue("nodId", nodId)
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkHandleProcessesOfDeadNodes.
	 * @param maxDate java.time.Instant 
	*/
	public void handleProcessesOfDeadNodes(final java.time.Instant maxDate) {
		final Task task = createTaskBuilder("TkHandleProcessesOfDeadNodes")
				.addValue("maxDate", maxDate)
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkReserveActivitiesToLaunch.
	 * @param nodId Long 
	 * @param maxNumber Integer 
	*/
	public void reserveActivitiesToLaunch(final Long nodId, final Integer maxNumber) {
		final Task task = createTaskBuilder("TkReserveActivitiesToLaunch")
				.addValue("nodId", nodId)
				.addValue("maxNumber", maxNumber)
				.build();
		getTaskManager().execute(task);
	}

	/**
	 * Execute la tache TkUpdateProcessExecutionTreatment.
	 * @param preId Long 
	 * @param checked Boolean 
	 * @param checkingDate java.time.Instant 
	 * @param checkingComment String 
	*/
	public void updateProcessExecutionTreatment(final Long preId, final Boolean checked, final java.time.Instant checkingDate, final String checkingComment) {
		final Task task = createTaskBuilder("TkUpdateProcessExecutionTreatment")
				.addValue("preId", preId)
				.addValue("checked", checked)
				.addValue("checkingDate", checkingDate)
				.addValue("checkingComment", checkingComment)
				.build();
		getTaskManager().execute(task);
	}

	private TaskManager getTaskManager() {
		return taskManager;
	}
}
