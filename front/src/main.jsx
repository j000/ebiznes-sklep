import React, { lazy, Suspense } from 'react';
import ReactDOM from 'react-dom';
import { HashRouter as Router, Switch, Route, useLocation } from 'react-router-dom';

import LayoutMain from '~/src/LayoutMain';
import HomePage from '~/src/pages/HomePage';

const pages = {
	'/author': lazy(() => import('~/src/pages/Author')),
	'/genre': lazy(() => import('~/src/pages/Genre')),
	'/book': lazy(() => import('~/src/pages/Book')),
	'/user': lazy(() => import('~/src/pages/User')),
	'/review': lazy(() => import('~/src/pages/Review')),
	'/basket': lazy(() => import('~/src/pages/Basket')),
	'/collection': lazy(() => import('~/src/pages/Collection')),
	'/collectionhelper': lazy(() => import('~/src/pages/CollectionHelper')),
};

const Loading = () => {
	return (
		<section className="container">
			<h1>Loading...</h1>
		</section>
	);
};

const NotFound = () => (
	<section className="container">
		<h1>Not Found</h1>
		<p>
			No page exists at <code>{useLocation().pathname}</code>
		</p>
	</section>
);

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
						<Route path="*">
							<NotFound />
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
