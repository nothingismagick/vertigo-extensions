package io.vertigo.x.workflow.dao.instance;

import javax.inject.Inject;
import io.vertigo.app.Home;
import io.vertigo.dynamo.task.metamodel.TaskDefinition;
import io.vertigo.dynamo.task.model.Task;
import io.vertigo.dynamo.task.model.TaskBuilder;
import io.vertigo.dynamo.impl.store.util.DAO;
import io.vertigo.dynamo.store.StoreManager;
import io.vertigo.dynamo.store.StoreServices;
import io.vertigo.dynamo.task.TaskManager;
import io.vertigo.x.workflow.domain.instance.WfWorkflow;

/**
 * DAO : Accès à un object (DTO, DTC). 
 * WfWorkflowDAO
 */
public final class WfWorkflowDAO extends DAO<WfWorkflow, java.lang.Long> implements StoreServices {
	 
	/**
	 * Contructeur.
	 * @param storeManager Manager de persistance
	 * @param taskManager Manager de Task
	 */
	@Inject
	public WfWorkflowDAO(final StoreManager storeManager, final TaskManager taskManager) {
		super(WfWorkflow.class, storeManager, taskManager);
	}
	

	/**
	 * Creates a taskBuilder.
	 * @param name  the name of the task
	 * @return the builder 
	 */
	private static TaskBuilder createTaskBuilder(final String name) {
		final TaskDefinition taskDefinition = Home.getApp().getDefinitionSpace().resolve(name, TaskDefinition.class);
		return new TaskBuilder(taskDefinition);
	}

	/**
	 * Execute la tache TK_FIND_ACTIVE_WORKFLOWS_FOR_UPDATES.
	 * @param wfwdId Long 
	 * @return io.vertigo.dynamo.domain.model.DtList<io.vertigo.x.workflow.domain.instance.WfWorkflow> dtWfWorkflow
	*/
	public io.vertigo.dynamo.domain.model.DtList<io.vertigo.x.workflow.domain.instance.WfWorkflow> findActiveWorkflowsForUpdates(final Long wfwdId) {
		final Task task = createTaskBuilder("TK_FIND_ACTIVE_WORKFLOWS_FOR_UPDATES")
				.addValue("WFWD_ID", wfwdId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}

	/**
	 * Execute la tache TK_READ_WORKFLOW_FOR_UPDATE.
	 * @param wfwId Long 
	 * @return io.vertigo.x.workflow.domain.instance.WfWorkflow workflow
	*/
	public io.vertigo.x.workflow.domain.instance.WfWorkflow readWorkflowForUpdate(final Long wfwId) {
		final Task task = createTaskBuilder("TK_READ_WORKFLOW_FOR_UPDATE")
				.addValue("WFW_ID", wfwId)
				.build();
		return getTaskManager()
				.execute(task)
				.getResult();
	}


}
