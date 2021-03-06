import React from 'react';
import ReactDOM from 'react-dom';
import { HashRouter as Router, Switch, Route, useLocation } from 'react-router-dom';

import { StoreProvider } from '~/src/contexts/StoreContext';
import { UserProvider } from '~/src/contexts/UserContext';
import { ErrorBoundary } from '~/src/utils';
import LayoutMain from '~/src/LayoutMain';
import Register from '~/src/pages/Register';
import Signin from '~/src/pages/Signin';

const Admin = React.lazy(() => import('~/src/pages/Admin'));
import Books from '~/src/pages/Books';
const Authors = React.lazy(() => import('~/src/pages/Authors'));
const Favourites = React.lazy(() => import('~/src/pages/Favourites'));

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

const CustomSwitch = ({children}) => (
	<Switch>
		{children}
		<Route>
			<NotFound />
		</Route>
	</Switch>
);

const App = () => {
	return (
		<ErrorBoundary>
			<StoreProvider>
				<UserProvider>
					<Router>
						<LayoutMain>
							<ErrorBoundary className="container">
								<React.Suspense fallback={<Loading />}>
									<CustomSwitch>
										<Route path="/admin/">
											<Admin switch={CustomSwitch} />
										</Route>
										<Route path="/" exact>
											<Books />
										</Route>
										<Route path="/favourites">
											<Favourites />
										</Route>
										<Route path="/Authors">
											<Authors />
										</Route>
										<Route path="/register">
											<Register />
										</Route>
										<Route path="/signin">
											<Signin />
										</Route>
									</CustomSwitch>
								</React.Suspense>
							</ErrorBoundary>
						</LayoutMain>
					</Router>
				</UserProvider>
			</StoreProvider>
		</ErrorBoundary>
	);
}

window.addEventListener('DOMContentLoaded', (event) => {
	ReactDOM.render(
		<App />,
		document.body.firstElementChild
	);
});
