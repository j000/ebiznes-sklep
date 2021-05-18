import React, { Fragment } from 'react';
import { Route, Link } from 'react-router-dom';

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
					<li>
						<Link
							className="button button-outline"
							to="/author"
						>
							Authors
						</Link>
					</li>
					<li>
						<Link
							className="button button-outline"
							to="/fail"
						>
							404
						</Link>
					</li>
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
