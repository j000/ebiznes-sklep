import React, { Fragment } from 'react';
import { Link, NavLink } from 'react-router-dom';

const linkBase = '/admin';
const links = {
	'/author': 'Authors',
	'/genre': 'Genres',
	'/book': 'Books',
	'/user': 'Users',
	'/review': 'Reviews',
	'/basket': 'Baskets',
	'/collection': 'Collections',
	'/collectionhelper': 'Collection Helpers',
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
					<h1>
						Yup.
					</h1>
				</Link>
				<ul>
					{ Object.entries(links).map(([key, value]) => (
						<li key={value}>
							<NavLink
								className="button button-outline"
								exact
								to={linkBase + key}
							>
								{value}
							</NavLink>
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
