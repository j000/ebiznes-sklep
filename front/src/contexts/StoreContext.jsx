import React from 'react';
import { getRequest } from '~/src/utils';

export const StoreContext = React.createContext();

const endpoint = 'http://localhost:9000/api';

const getAuthors = () => loadToMap('author');
const getBooks = () => loadToMap('book');
const getGenres = () => loadToMap('genre');

const storeState = {
	books: {},
	authors: {},
	genres: {},
};

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
		default: {
			throw new Error(`Unhandled action type: ${action.type}`);
		}
	}
};

const loadToMap = async (baseUrl, url) => {
	const all = await getRequest(`${baseUrl}/${url}`);
	return all.reduce((map, elem) => {
		map[elem.id] = elem;
		return map;
	}, {});
};

export const StoreProvider = ({children}) => {
	const [store, storeDispatch] = React.useReducer(storeReducer, storeState);

	const customDispatch = async (action) => {
		switch(action.type) {
			case "loadAll": {
				customDispatch({type: 'loadBooks'});
				customDispatch({type: 'loadAuthors'});
				customDispatch({type: 'loadGenres'});
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
	if (context[0] == storeState) {
		context[1]({type: 'loadAll'});
	}
	return context;
};
