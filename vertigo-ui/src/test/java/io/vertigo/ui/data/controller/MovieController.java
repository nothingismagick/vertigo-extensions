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
package io.vertigo.ui.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.vertigo.dynamo.domain.model.DtListState;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.data.domain.movies.Movie;
import io.vertigo.ui.data.domain.reference.OuiNonChoice;
import io.vertigo.ui.data.services.movies.MovieServices;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/movie")
public class MovieController extends AbstractVSpringMvcController {

	private final ViewContextKey<Movie> movieKey = ViewContextKey.of("movie");
	private final ViewContextKey<OuiNonChoice> ouiKey = ViewContextKey.of("oui");
	private final ViewContextKey<OuiNonChoice> nonKey = ViewContextKey.of("non");
	private final ViewContextKey<Movie> moviesKey = ViewContextKey.of("movies");

	@Autowired
	private MovieServices movieServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @RequestParam("movId") final Long movId) {
		final Movie movie = movieServices.get(movId);
		viewContext.publishDto(movieKey, movie);
		final OuiNonChoice oui = new OuiNonChoice();
		oui.setKey(Boolean.TRUE);
		final OuiNonChoice non = new OuiNonChoice();
		non.setKey(Boolean.FALSE);
		viewContext.publishDto(ouiKey, oui);
		viewContext.publishDto(nonKey, non);
		viewContext.publishDtList(moviesKey, movieServices.getMovies(DtListState.defaultOf(Movie.class)));
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	public String doSave(
			@ViewAttribute("movie") final Movie movie, final RedirectAttributes redirectAttributes) {
		movieServices.save(movie);
		redirectAttributes.addAttribute("movId", movie.getMovId());
		return "redirect:/movie/";
	}

}
