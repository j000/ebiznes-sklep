import React, { lazy, Suspense } from "react";
import ReactDOM from "react-dom";
import { HashRouter as Router, Switch, Route } from "react-router-dom";

import LayoutMain from '~/src/LayoutMain';
import HomePage from '~/src/pages/HomePage';

const Author = lazy(() => import('~/src/pages/Author'));

const Loading = () => {
	return (
		<section className="container">
			<h1>Loading...</h1>
		</section>
	);
};

const App = () => {
	return (
		<Router>
			<LayoutMain>
				<Suspense fallback={<Loading />}>
					<Switch>
						<Route path="/author">
							<Author />
						</Route>
						<Route path="/" exact>
							<HomePage />
						</Route>
						<Route>
							<section className="container">
								<h1>Not Found</h1>
							</section>
						</Route>
					</Switch>
				</Suspense>
			</LayoutMain>
		</Router>
	);
}

window.addEventListener('DOMContentLoaded', (event) => {
	ReactDOM.render(
		<App />,
		document.body.firstElementChild
	);
});
