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

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.Arrays;
import java.util.Collections;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vertigo.app.AutoCloseableApp;
import io.vertigo.core.component.di.injector.Injector;
import io.vertigo.dynamo.domain.model.URI;
import io.vertigo.dynamo.domain.util.DtObjectUtil;
import io.vertigo.x.account.Account;
import io.vertigo.x.account.AccountBuilder;
import io.vertigo.x.account.AccountGroup;
import io.vertigo.x.account.AccountManager;
import io.vertigo.x.impl.rules.RuleConditionDefinition;
import io.vertigo.x.impl.rules.RuleDefinition;
import io.vertigo.x.impl.rules.SelectorDefinition;
import io.vertigo.x.impl.workflow.ItemStorePlugin;
import io.vertigo.x.workflow.data.MyDummyDtObject;
import io.vertigo.x.workflow.domain.instance.WfWorkflow;
import io.vertigo.x.workflow.domain.model.WfActivityDefinition;
import io.vertigo.x.workflow.domain.model.WfWorkflowDefinition;

/**
 * Tests unitaires pour le Workflow Manager
 * @author xdurand
 *
 */
public class WorkflowManagerTest {

	private AutoCloseableApp app;

	@Inject
	private WorkflowManager workflowManager;

	@Inject
	private AccountManager accountManager;
	
	@Inject
	private ItemStorePlugin itemStorePlugin;

	@Before
	public void setUp() {
		app = new AutoCloseableApp(MyAppConfig.config());
		Injector.injectMembers(this, app.getComponentSpace());
	}

	@After
	public void tearDown() {
		if (app != null) {
			app.close();
		}
	}
	
	private MyDummyDtObject createDummyDtObject() {
		MyDummyDtObject myDummyDtObject = new MyDummyDtObject();
		myDummyDtObject.setId(1L);
		myDummyDtObject.setDivision("DIV");
		myDummyDtObject.setEntity("ENT");
		itemStorePlugin.addItem(myDummyDtObject.getId(), myDummyDtObject);
		return myDummyDtObject;
	}
	
	/**
	 * 
	 */
	@Test
	public void testWorkflowRulesManualValidationActivities() {

		final WfWorkflowDefinition wfWorkflowDefinition = new WfWorkflowDefinitionBuilder("WorkflowRules").build();
		workflowManager.createWorkflowDefinition(wfWorkflowDefinition);

		final WfActivityDefinition firstActivity = new WfActivityDefinitionBuilder("Step 1", wfWorkflowDefinition.getWfwdId()).build();
		wfWorkflowDefinition.setWfadId(firstActivity.getWfadId());
		
		AccountGroup accountGroup = new AccountGroup("1", "dummy group");
		Account account = new AccountBuilder("Acc1").build();
		accountManager.getStore().saveGroup(accountGroup);
		accountManager.getStore().saveAccounts(Arrays.asList(account));
		URI<Account> accountUri = DtObjectUtil.createURI(Account.class, account.getId());
		URI<AccountGroup> accountGroupUri = DtObjectUtil.createURI(AccountGroup.class, accountGroup.getId());
		accountManager.getStore().attach(accountUri, accountGroupUri);
		
		//Step 1 : 1 rule, 1 condition
		workflowManager.addActivity(wfWorkflowDefinition, firstActivity, 1);
		RuleDefinition rule1Act1 = new RuleDefinition(null, firstActivity.getWfadId());
		RuleConditionDefinition condition1Rule1Act1 = new RuleConditionDefinition(null, "division", "=", "DIV", null);
		workflowManager.addRule(firstActivity, rule1Act1, Arrays.asList(condition1Rule1Act1));
		//Selector/filter to validate the activity (preventing auto validation when no one is linked to an activity)
		SelectorDefinition selector1 = new SelectorDefinition(null, firstActivity.getWfadId(), accountGroup.getId());
		workflowManager.addSelector(firstActivity, selector1, Collections.emptyList());

		// Step 2 : No rules/condition
		final WfActivityDefinition secondActivity = new WfActivityDefinitionBuilder("Step 2", wfWorkflowDefinition.getWfwdId()).build();
		workflowManager.addActivity(wfWorkflowDefinition, secondActivity, 2);
		// Selector/filter to validate the activity (preventing auto validation when no one is linked to an activity)
		SelectorDefinition selector2 = new SelectorDefinition(null, secondActivity.getWfadId(), accountGroup.getId() );
		workflowManager.addSelector(secondActivity, selector2, Collections.emptyList());
		
		// Step 3 : 1 rule, 2 conditions 
		final WfActivityDefinition thirdActivity = new WfActivityDefinitionBuilder("Step 3", wfWorkflowDefinition.getWfwdId()).build();
		workflowManager.addActivity(wfWorkflowDefinition, thirdActivity, 3);
		RuleDefinition rule1Act3 = new RuleDefinition(null, thirdActivity.getWfadId());
		RuleConditionDefinition condition1Rule1Act3 = new RuleConditionDefinition(null, "division", "=", "DIV", null);
		RuleConditionDefinition condition2Rule1Act3 = new RuleConditionDefinition(null, "entity", "=", "ENT", null);
		workflowManager.addRule(thirdActivity, rule1Act3, Arrays.asList(condition1Rule1Act3, condition2Rule1Act3));
		// Selector/filter to validate the activity (preventing auto validation when no one is linked to an activity)
		SelectorDefinition selector3 = new SelectorDefinition(null, thirdActivity.getWfadId(), accountGroup.getId() );
		workflowManager.addSelector(thirdActivity, selector3, Collections.emptyList());

		// Step 4 : 2 rule, 1 condition 
		final WfActivityDefinition fourthActivity = new WfActivityDefinitionBuilder("Step 4", wfWorkflowDefinition.getWfwdId()).build();
		workflowManager.addActivity(wfWorkflowDefinition, fourthActivity, 4);
		RuleDefinition rule1Act4 = new RuleDefinition(null, fourthActivity.getWfadId());
		RuleConditionDefinition condition1Rule1Act4 = new RuleConditionDefinition(null, "division", "=", "DIV", null);
		RuleDefinition rule2Act4 = new RuleDefinition(null, fourthActivity.getWfadId());
		RuleConditionDefinition condition1Rule2Act4 = new RuleConditionDefinition(null, "division", "=", "ABC", null);
		workflowManager.addRule(fourthActivity, rule1Act4, Arrays.asList(condition1Rule1Act4));
		workflowManager.addRule(fourthActivity, rule2Act4, Arrays.asList(condition1Rule2Act4));
		// Selector/filter to validate the activity (preventing auto validation when no one is linked to an activity)
		SelectorDefinition selector41 = new SelectorDefinition(null, fourthActivity.getWfadId(), accountGroup.getId() );
		workflowManager.addSelector(fourthActivity, selector41, Collections.emptyList());
		
		MyDummyDtObject myDummyDtObject = createDummyDtObject();
		
		WfWorkflow wfWorkflow = workflowManager.createWorkflowInstance("WorkflowRules", "JUnit", false, myDummyDtObject.getId());
		
		// Starting the workflow
		workflowManager.startInstance(wfWorkflow);
		
		// Entry actions should NOT validate all activities.		
		Long currentActivity = wfWorkflow.getWfaId2();
		assertThat(currentActivity, is(firstActivity.getWfadId()));
		
		WfWorkflow wfWorkflowFetched = workflowManager.getWorkflowInstance(wfWorkflow.getWfwId());
		assertThat(wfWorkflowFetched, is(not(nullValue())));
		assertThat(wfWorkflowFetched.getWfaId2(), is(firstActivity.getWfadId()));
		
		// Manually validating activity 1
		WfDecision wfDecisionAct1 = new WfDecisionBuilder(1, account.getId()).build();
		workflowManager.goToNextActivity(wfWorkflow, wfDecisionAct1);
		
		// Activity 1 should now be validated. The current activity is now activity 2
		currentActivity = wfWorkflow.getWfaId2();
		assertThat(currentActivity, is(secondActivity.getWfadId()));
		
		WfWorkflow wfWorkflowFetched2 = workflowManager.getWorkflowInstance(wfWorkflow.getWfwId());
		assertThat(wfWorkflowFetched2, is(not(nullValue())));
		assertThat(wfWorkflowFetched2.getWfaId2(), is(secondActivity.getWfadId()));
		
		//Manually validating activity 2
		WfDecision wfDecisionAct2 = new WfDecisionBuilder(1, account.getId()).build();
		workflowManager.goToNextActivity(wfWorkflow, wfDecisionAct2);
		
		// Activity 2 should now be validated.
		// No rule defined for activity 3. Activity 3 should be autovalidated
		//The current activity should be now activity 4
		currentActivity = wfWorkflow.getWfaId2();
		assertThat(currentActivity, is(fourthActivity.getWfadId()));
		
		WfWorkflow wfWorkflowFetched3 = workflowManager.getWorkflowInstance(wfWorkflow.getWfwId());
		assertThat(wfWorkflowFetched3, is(not(nullValue())));
		assertThat(wfWorkflowFetched3.getWfaId2(), is(fourthActivity.getWfadId()));
		
		//Manually validating activity 4
		WfDecision wfDecisionAct4 = new WfDecisionBuilder(1, account.getId()).build();
		workflowManager.goToNextActivity(wfWorkflow, wfDecisionAct4);
		
		// Activity 4 should now be validated. The current activity is now activity 4
		currentActivity = wfWorkflow.getWfaId2();
		assertThat(currentActivity, is(fourthActivity.getWfadId()));
		assertThat(wfWorkflow.getWfsCode(), is(WfCodeStatusWorkflow.END.name()));
		
		WfWorkflow wfWorkflowFetched5 = workflowManager.getWorkflowInstance(wfWorkflow.getWfwId());
		assertThat(wfWorkflowFetched5.getWfsCode(), is(WfCodeStatusWorkflow.END.name()));
	}
	
	/**
	 * 
	 */
	@Test
	public void testWorkflowRulesAutoValidationNoSelectorAllActivities() {

		final WfWorkflowDefinition wfWorkflowDefinition = new WfWorkflowDefinitionBuilder("WorkflowRules").build();
		workflowManager.createWorkflowDefinition(wfWorkflowDefinition);

		final WfActivityDefinition firstActivity = new WfActivityDefinitionBuilder("Step 1", wfWorkflowDefinition.getWfwdId()).build();
		wfWorkflowDefinition.setWfadId(firstActivity.getWfadId());
		
		//Step 1 : 1 rule, 1 condition (NO Selector)
		workflowManager.addActivity(wfWorkflowDefinition, firstActivity, 1);
		RuleDefinition rule1Act1 = new RuleDefinition(null, firstActivity.getWfadId());
		RuleConditionDefinition condition1Rule1Act1 = new RuleConditionDefinition(null, "division", "=", "DIV", null);
		workflowManager.addRule(firstActivity, rule1Act1, Arrays.asList(condition1Rule1Act1));
		
		// Step 2 : No rules/condition (NO Selector)
		final WfActivityDefinition secondActivity = new WfActivityDefinitionBuilder("Step 2", wfWorkflowDefinition.getWfwdId()).build();
		workflowManager.addActivity(wfWorkflowDefinition, secondActivity, 2);

		// Step 3 : 1 rule, 2 conditions (NO Selector)
		final WfActivityDefinition thirdActivity = new WfActivityDefinitionBuilder("Step 3", wfWorkflowDefinition.getWfwdId()).build();
		workflowManager.addActivity(wfWorkflowDefinition, thirdActivity, 3);
		RuleDefinition rule1Act3 = new RuleDefinition(null, thirdActivity.getWfadId());
		RuleConditionDefinition condition1Rule1Act3 = new RuleConditionDefinition(null, "division", "=", "DIV", null);
		RuleConditionDefinition condition2Rule1Act3 = new RuleConditionDefinition(null, "entity", "=", "ENT", null);
		workflowManager.addRule(thirdActivity, rule1Act3, Arrays.asList(condition1Rule1Act3, condition2Rule1Act3));

		// Step 4 : 2 rule, 1 condition (NO Selector)
		final WfActivityDefinition fourthActivity = new WfActivityDefinitionBuilder("Step 4", wfWorkflowDefinition.getWfwdId()).build();
		workflowManager.addActivity(wfWorkflowDefinition, fourthActivity, 4);
		RuleDefinition rule1Act4 = new RuleDefinition(null, fourthActivity.getWfadId());
		RuleConditionDefinition condition1Rule1Act4 = new RuleConditionDefinition(null, "division", "=", "DIV", null);
		RuleDefinition rule2Act4 = new RuleDefinition(null, fourthActivity.getWfadId());
		RuleConditionDefinition condition1Rule2Act4 = new RuleConditionDefinition(null, "division", "=", "ABC", null);
		workflowManager.addRule(fourthActivity, rule1Act4, Arrays.asList(condition1Rule1Act4));
		workflowManager.addRule(fourthActivity, rule2Act4, Arrays.asList(condition1Rule2Act4));
		
		
		// Creating an object
		MyDummyDtObject myDummyDtObject = createDummyDtObject();
		
		WfWorkflow wfWorkflow = workflowManager.createWorkflowInstance("WorkflowRules", "JUnit", false, myDummyDtObject.getId());
		
		//Starting the workflow
		workflowManager.startInstance(wfWorkflow);
		
		// Entry actions should validate all activities.		
		Long currentActivity = wfWorkflow.getWfaId2();
		assertThat(currentActivity, is(fourthActivity.getWfadId()));
		
		WfWorkflow wfWorkflowFetched = workflowManager.getWorkflowInstance(wfWorkflow.getWfwId());
		assertThat(wfWorkflowFetched, is(not(nullValue())));
		assertThat(currentActivity, is(fourthActivity.getWfadId()));
	}


}