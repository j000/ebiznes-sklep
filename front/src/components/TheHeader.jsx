import React from 'react';
import { Link, NavLink } from 'react-router-dom';
import { TheBasket } from '~/src/components/TheBasket';
import { useUser } from '~/src/contexts/UserContext';

const links = {
	'/authors': 'Authors',
};
const userLinks = {
	'/favourites': 'Favourites',
}
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

export const TheHeader = () => {
	const [{ user }, dispatchUser] = useUser();

	const Buttons = () => {
		if (user?.email)
			return (
				<>
					<li>
						<span className="button button-clear">
							{ user.nick || user.email }
						</span>
					</li>
					<li>
						<a
							className="button button-outline"
							onClick={signout}
						>
							Sign Out
						</a>
					</li>
				</>
			);
		return (
			<>
				<li>
					<NavLink
						className="button button-outline"
						to={ '/signin' }
					>
						Sign in
					</NavLink>
				</li>
				<li>
					<NavLink
						className="button"
						to={ '/register' }
					>
						Register
					</NavLink>
				</li>
			</>
		);
	};

	const signout = () => dispatchUser({type: 'signOut'});

	return (
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
				{ user?.email && <>{
					Object.entries(userLinks).map(([link, name]) => (
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
						<TheBasket />
					</li>
				</>}
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
				<Buttons />
			</ul>
		</header>
	)
}
