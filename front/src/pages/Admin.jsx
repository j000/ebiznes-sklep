import React from 'react';
import { Route } from 'react-router-dom';
import { useRouteMatch } from "react-router";
import { ErrorBoundary } from '~/src/utils';

import Author from './admin/Author';
import Genre from './admin/Genre';
import Book from './admin/Book';
import User from './admin/User';
import Review from './admin/Review';
import Basket from './admin/Basket';
import Collection from './admin/Collection';
import CollectionHelper from './admin/CollectionHelper';
import Order from './admin/Order';

const pages = {
	'author': Author,
	'genre': Genre,
	'book': Book,
	'user': User,
	'review': Review,
	'basket': Basket,
	'collection': Collection,
	'collectionhelper': CollectionHelper,
	'order': Order,
};


export default ({switch: CustomSwitch}) => {
	const { path } = useRouteMatch();

	return (
		<ErrorBoundary className="container">
			<CustomSwitch>
				{ Object.entries(pages).map(([key, value]) => (
					<Route key={key} path={path+key} component={value} />
				))}
			</CustomSwitch>
		</ErrorBoundary>
	);
};

