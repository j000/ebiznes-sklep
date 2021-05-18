import React, { lazy, Suspense } from "react";
import ReactDOM from "react-dom";
import { HashRouter as Router, Switch, Route } from "react-router-dom";

import LayoutMain from '~/src/LayoutMain';
import HomePage from '~/src/pages/HomePage';

const pages = {
	'/author': lazy(() => import('~/src/pages/Author')),
	'/genre': lazy(() => import('~/src/pages/Genre')),
	'/book': lazy(() => import('~/src/pages/Book')),
	'/user': lazy(() => import('~/src/pages/User')),
};

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
						<Route path="/" exact>
							<HomePage />
						</Route>
						{ Object.entries(pages).map(([key, value]) => (
								<Route key={key} path={key} component={value} />
						))}
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
