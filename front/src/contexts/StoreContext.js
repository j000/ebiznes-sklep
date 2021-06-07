import React from 'react';
import { loadToMap, loadToMapAlter, loadToMapArray } from '~/src/utils';

const StoreContext = React.createContext();

const getAuthors = () => loadToMap('author');
const getBooks = () => loadToMap('book');
const getGenres = () => loadToMap('genre');
const getCollections = () => loadToMapArray(
	'collection',
	'collectionhelper',
	'collection_id',
	'book_id',
);
const getReviews = () => loadToMapAlter('review', 'book_id');

const storeState = {
	books: {},
	authors: {},
	genres: {},
	collections: {},
	reviews: {},
};

let loading = false;

const storeReducer = (state, action) => {
	switch (action.type) {
		case 'setBooks': {
			console.log('setBooks to ', action.data);
			return {...state, books: action.data || {}};
		}
		case 'setAuthors': {
			console.log('setAuthors to ', action.data);
			return {...state, authors: action.data || {}};
		}
		case 'setGenres': {
			console.log('setGenres to ', action.data);
			return {...state, genres: action.data || {}};
		}
		case 'setCollections': {
			console.log('setCollections to ', action.data);
			return {...state, collections: action.data || {}};
		}
		case 'setReviews': {
			console.log('setReviews to ', action.data);
			return {...state, reviews: action.data || {}};
		}
		default: {
			throw new Error(`Unhandled action type: ${action.type}`);
		}
	}
};

export const StoreProvider = ({children}) => {
	const [store, storeDispatch] = React.useReducer(storeReducer, storeState);

	const customDispatch = async (action) => {
		switch(action.type) {
			case "loadAll": {
				customDispatch({type: 'loadBooks'});
				customDispatch({type: 'loadAuthors'});
				customDispatch({type: 'loadGenres'});
				customDispatch({type: 'loadCollections'});
				customDispatch({type: 'loadReviews'});
				return;
			}
			case "loadBooks": {
				getBooks().then((data) => storeDispatch({type: 'setBooks', data}));
				return;
			}
			case "loadAuthors": {
				getAuthors().then((data) => storeDispatch({type: 'setAuthors', data}));
				return;
			}
			case "loadGenres": {
				getGenres().then((data) => storeDispatch({type: 'setGenres', data}));
				return;
			}
			case "loadCollections": {
				getCollections().then((data) => storeDispatch({type: 'setCollections', data}));
				return;
			}
			case "loadReviews": {
				getReviews().then((data) => storeDispatch({type: 'setReviews', data}));
				return;
			}
			default: {
				storeDispatch(action);
			}
		}
	};

	return (
		<StoreContext.Provider value={[store, customDispatch]}>
			{ children }
		</StoreContext.Provider>
	);
};

export const useStore = () => {
	const context = React.useContext(StoreContext);
	if (context === undefined) {
		throw new Error('useStore must be used within a StoreContext');
	}
	if (!loading && context[0] == storeState) {
		loading = true;
		context[1]({type: 'loadAll'});
	}
	return context;
};
