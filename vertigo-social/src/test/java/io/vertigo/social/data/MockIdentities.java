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
package io.vertigo.social.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.vertigo.account.account.Account;
import io.vertigo.account.account.AccountGroup;
import io.vertigo.account.plugins.account.store.loader.AccountLoader;
import io.vertigo.account.plugins.account.store.loader.GroupLoader;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.domain.model.URI;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.util.ListBuilder;

public final class MockIdentities implements Component, AccountLoader, GroupLoader {

	private final Map<URI<Account>, Account> accountsMap = new HashMap<>();
	private final Map<String, Account> accountsMapByAuth = new HashMap<>();
	private final Map<URI<AccountGroup>, AccountGroup> groupsMap = new HashMap<>();
	private final Map<URI<Account>, Set<URI<AccountGroup>>> groupsPerAccount = new HashMap<>();
	private final Map<URI<AccountGroup>, Set<URI<Account>>> accountsPerGroup = new HashMap<>();

	/**
	 * @param id account id
	 * @return URI of this account
	 */
	public static URI<Account> createAccountURI(final String id) {
		return URI.of(Account.class, id);
	}

	/**
	 * Init default identities data.
	 */
	public void initData() {
		final Account testAccount0 = Account.builder("0").withAuthToken("0").withDisplayName("John doe").withEmail("john.doe@yopmail.com").build();
		final Account testAccount1 = Account.builder("1").withAuthToken("1").withDisplayName("Palmer Luckey").withEmail("palmer.luckey@yopmail.com").build();
		final Account testAccount2 = Account.builder("2").withAuthToken("2").withDisplayName("Bill Clinton").withEmail("bill.clinton@yopmail.com").build();
		final Account testAccount3 = Account.builder("3").withAuthToken("3").withDisplayName("Phil Mormon").withEmail("phil.mormon@yopmail.com").build();
		saveAccounts(Arrays.asList(testAccount0, testAccount1, testAccount2, testAccount3));

		final URI<Account> accountURI0 = createAccountURI(testAccount0.getId());
		final URI<Account> accountURI1 = createAccountURI(testAccount1.getId());
		final URI<Account> accountURI2 = createAccountURI(testAccount2.getId());

		final AccountGroup testAccountGroup1 = new AccountGroup("100", "TIME's cover");
		final URI<AccountGroup> group1Uri = URI.of(AccountGroup.class, testAccountGroup1.getId());
		saveGroup(testAccountGroup1);

		attach(accountURI1, group1Uri);
		attach(accountURI2, group1Uri);

		final AccountGroup groupAll = new AccountGroup("ALL", "Everyone");
		final URI<AccountGroup> groupAllUri = URI.of(AccountGroup.class, groupAll.getId());
		saveGroup(groupAll);
		attach(accountURI0, groupAllUri);
		attach(accountURI1, groupAllUri);
		attach(accountURI2, groupAllUri);

		//---create 10 noisy data
		final List<Account> accounts = createAccounts();
		saveAccounts(accounts);
		for (final Account account : accounts) {
			final URI<Account> accountUri = createAccountURI(account.getId());
			attach(accountUri, groupAllUri);
		}

	}

	private static int SEQ_ID = 10;

	private static List<Account> createAccounts() {
		return new ListBuilder<Account>()
				.add(createAccount("Jean Meunier", "jmeunier@yopmail.com"))
				.add(createAccount("Emeline Granger", "egranger@yopmail.com"))
				.add(createAccount("Silvia Robert", "sylv.robert@yopmail.com"))
				.add(createAccount("Manuel Long", "manu@yopmail.com"))
				.add(createAccount("David Martin", "david.martin@yopmail.com"))
				.add(createAccount("Véronique LeBourgeois", "vero89@yopmail.com"))
				.add(createAccount("Bernard Dufour", "bdufour@yopmail.com"))
				.add(createAccount("Nicolas Legendre", "nicolas.legendre@yopmail.com"))
				.add(createAccount("Marie Garnier", "marie.garnier@yopmail.com"))
				.add(createAccount("Hugo Bertrand", "hb@yopmail.com"))
				.build();
	}

	private static Account createAccount(final String displayName, final String email) {
		return Account.builder(Integer.toString(SEQ_ID++))
				.withAuthToken(email.substring(0, email.indexOf('@')))
				.withDisplayName(displayName)
				.withEmail(email)
				.build();
	}

	/**
	 * @param accounts accounts to save
	 */
	public void saveAccounts(final List<Account> accounts) {
		accounts.stream().forEach(account -> {
			accountsMap.put(account.getURI(), account);
			accountsMapByAuth.put(account.getAuthToken(), account);
		});
	}

	/**
	 * @param accountGroup group to save
	 */
	public void saveGroup(final AccountGroup accountGroup) {
		groupsMap.put(accountGroup.getURI(), accountGroup);
	}

	/**
	 * Attach account to group
	 * @param accountURI accountURI
	 * @param groupURI groupURI
	 */
	public void attach(final URI<Account> accountURI, final URI<AccountGroup> groupURI) {
		groupsPerAccount.computeIfAbsent(accountURI, key -> new HashSet<>()).add(groupURI);
		accountsPerGroup.computeIfAbsent(groupURI, key -> new HashSet<>()).add(accountURI);
	}

	/** {@inheritDoc} */
	@Override
	public long getAccountsCount() {
		return accountsMap.size();
	}

	/** {@inheritDoc} */
	@Override
	public Account getAccount(final URI<Account> accountURI) {
		return accountsMap.get(accountURI);
	}

	/** {@inheritDoc} */
	@Override
	public Optional<VFile> getPhoto(final URI<Account> accountURI) {
		return Optional.empty();
	}

	/** {@inheritDoc} */
	@Override
	public Optional<Account> getAccountByAuthToken(final String userAuthToken) {
		return Optional.ofNullable(accountsMapByAuth.get(userAuthToken));
	}

	/** {@inheritDoc} */
	@Override
	public long getGroupsCount() {
		return groupsMap.size();
	}

	/** {@inheritDoc} */
	@Override
	public AccountGroup getGroup(final URI<AccountGroup> groupURI) {
		return groupsMap.get(groupURI);
	}

	/** {@inheritDoc} */
	@Override
	public Set<URI<AccountGroup>> getGroupURIs(final URI<Account> accountURI) {
		return groupsPerAccount.computeIfAbsent(accountURI, key -> Collections.emptySet());
	}

	/** {@inheritDoc} */
	@Override
	public Set<URI<Account>> getAccountURIs(final URI<AccountGroup> groupURI) {
		return accountsPerGroup.computeIfAbsent(groupURI, key -> Collections.emptySet());
	}
}
