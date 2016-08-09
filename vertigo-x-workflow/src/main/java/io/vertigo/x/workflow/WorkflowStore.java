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
package io.vertigo.x.workflow;

import java.util.List;

import io.vertigo.x.workflow.domain.instance.WfActivity;
import io.vertigo.x.workflow.domain.instance.WfWorkflow;
import io.vertigo.x.workflow.domain.model.WfActivityDefinition;
import io.vertigo.x.workflow.domain.model.WfTransitionDefinition;
import io.vertigo.x.workflow.domain.model.WfWorkflowDefinition;

/**
 * This class defines the storage of workflow.
 * @author xdurand
 */
public interface WorkflowStore {

	//Instance

	/**
	 * Create a new workflow.
	 * @param workflow
	 */
	void createWorkflowInstance(WfWorkflow workflow);

	/**
	 * Get a workflow instance.
	 * @param wfwId identifinat de l'instance du workflow
	 * @return the correponding workflow id
	 */
	WfWorkflow readWorkflowInstanceById(Long wfwId);

	/**
	 * Get a workflow instance.
	 * @param itemId identifinat de l'instance du workflow
	 * @return the correponding workflow id
	 */
	WfWorkflow readWorkflowInstanceByItemId(Long itemId);

	/**
	 * Update a workflow instance.
	 * /!\ The id must be set
	 * @param workflow the new workflow to update
	 */
	void updateWorkflowInstance(WfWorkflow workflow);

	/**
	 * Fetch an activity by id
	 * @param wfadId
	 * @return the corresponding activity
	 */
	WfActivity readActivity(final Long wfadId);

	/**
	 * Create a new activity
	 * @param wfActivity
	 * @param wfadId
	 */
	void createActivity(WfActivity wfActivity);

	/**
	 * Update an existing activity
	 * @param wfActivity
	 */
	void updateActivity(WfActivity wfActivity);

	/**
	 * Fetch an activity by id
	 * @param wfActivity
	 * @param wfadId
	 */
	void removeActivity(WfActivity wfActivity);

	/**
	 * Does the provided has a next activity
	 * @param activity
	 * @return true if the activity has a default trsnition to another activity. false if the activity is the last activity
	 */
	boolean hasNextActivity(final WfActivity activity);

	/**
	 *
	 * @param activity
	 * @return the next activity definition
	 */
	WfActivityDefinition findNextActivity(WfActivity activity);

	/**
	 * Find the list of all the definitions following the default transitions
	 * @return the list of transitions ordered from start to end.
	 */
	List<WfActivityDefinition> findActivityMatchingRules();

    // Definition
	/**
	 *
	 * @param wfWorkflowDefinition
	 * @return the number of default transition in the workflow
	 */
	public int countDefaultTransitions(final WfWorkflowDefinition wfWorkflowDefinition);

	/**
	 * Create a new workflow definition.
	 * @param workflowDefinition
	 */
	void createWorkflowDefinition(WfWorkflowDefinition workflowDefinition);

	/**
	 * Get an definition of workflow.
	 * @param wfwdId the id of the workflow definition.
	 * @return the corresponding Workflow definition
	 */
	WfWorkflowDefinition readWorkflowDefinition(Long wfwdId);

	/**
	 * Get an definition of workflow.
	 * @param definitionName the name of the workflow definition.
	 * @return the
	 */
	WfWorkflowDefinition readWorkflowDefinition(String definitionName);


	/**
	 *
	 * @param wfWorkflowDefinition
	 */
	void updateWorkflowDefinition(final WfWorkflowDefinition wfWorkflowDefinition);

	/**
	 * Add an activity to the workflow definition.
	 * @param wfWorkflowDefinition the workflow definition.
	 * @param wfActivityDefinition
	 * @param position
	 */
	void createActivityDefinition(WfWorkflowDefinition wfWorkflowDefinition, WfActivityDefinition wfActivityDefinition);

	/**
	 * Remove an activity to the workflow definition.
	 * @param wfWorkflowDefinition the workflow definition.
	 * @param wfActivityDefinition the activity to remove
	 */
	void removeActivityDefinition(WfActivityDefinition wfActivityDefinition);

	/**
	 * Fetch an activity definition by id
	 * @param wfadId
	 * @return the corresponding activityeafinition
	 */
	WfActivityDefinition readActivityDefinition(final Long wfadId);

	/**
	 * Remove an activity to the workflow definition.
	 * @param wfWorkflowDefinition the workflow definition.
	 * @param wfActivityDefinition the activity to remove
	 */
	void updateActivityDefinition(WfActivityDefinition wfActivityDefinition);


	/**
	 * Find an activity by its positon in the default transition chain
	 * @param wfWorkflowDefinition
	 * @param position
	 * @return the matching definition
	 */
	WfActivityDefinition findActivityDefinitionByPosition(final WfWorkflowDefinition wfWorkflowDefinition, final int position);

	/**
	 * Find the list of all the definitions following the default transitions
	 * @param wfWorkflowDefinition
	 * @return the list of transitions ordered from start to end.
	 */
	List<WfActivityDefinition> findAllDefaultActivityDefinitions(WfWorkflowDefinition wfWorkflowDefinition);


	/**
	 * Add a transition
	 * @param transition
	 */
	void addTransition(WfTransitionDefinition transition);

	/**
	 * Remove a transition
	 * @param transition
	 */
	void removeTransition(WfTransitionDefinition transition);


}
