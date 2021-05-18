import React, { Fragment } from 'react';
import { Route, Link } from 'react-router-dom';

const links = {
	'/author': 'Authors',
	'/genre': 'Genres',
	'/book': 'Books',
};

export default (props) => {
	return (
		<Fragment>
			<header
				className="container"
				role="navigation"
				aria-label="main navigation"
			>
				<Link
					className="title"
					to="/"
				>
					Yup.
				</Link>
				<ul>
					{ Object.entries(links).map(([key, value]) => (
						<li key={value}>
							<Link
								className="button button-outline"
								to={key}
							>
								{value}
							</Link>
						</li>
					))}
				</ul>
			</header>
			{props.children}
			<footer className="container">
				<p>
					Footer here
				</p>
			</footer>
		</Fragment>
	)
}
