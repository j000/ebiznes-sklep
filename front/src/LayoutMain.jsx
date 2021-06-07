import React from 'react';
import { Link, NavLink } from 'react-router-dom';

const links = {
	'/books': 'Books',
};
const adminLinkBase = '/admin';
const adminLinks = {
	'/author': 'Authors',
	'/genre': 'Genres',
	'/book': 'Books',
	'/user': 'Users',
	'/review': 'Reviews',
	'/basket': 'Baskets',
	'/collection': 'Collections',
	'/collectionhelper': 'Collection Helpers',
	'/order': 'Orders',
};

export default (props) => {
	return (
		<>
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
					{ Object.entries(links).map(([link, name]) => (
						<li key={name}>
							<NavLink
								className="button button-outline"
								to={ link }
							>
								{ name }
							</NavLink>
						</li>
					))}
					<li>
						<a
							className="button button-outline"
							tabIndex="0"
						>
							Admin &#x25BC;
						</a>
						<ul className="dropdown">
							{ Object.entries(adminLinks).map(([link, name]) => (
								<li key={name}>
									<NavLink
										className="button button-outline"
										exact
										to={adminLinkBase + link}
									>
										{name}
									</NavLink>
								</li>
							))}
						</ul>
					</li>
				</ul>
			</header>
			{props.children}
			<footer className="container">
				<p>
					Footer here
				</p>
			</footer>
		</>
	)
}
