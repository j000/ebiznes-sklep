import React from 'react';
import { loadToMap, getRequest } from '~/src/utils';
import Cookie from 'js-cookie';

const UserContext = React.createContext();

const getFavourites = () => getRequest('favourite');
const getBasket = () => loadToMap('basket');
const getOrders = () => loadToMap('order');
const getPayment = () => loadToMap('payment');

const userState = {
	favourites: [],
	basket: {},
	orders: {},
	payments: {},
	user: {},
};

let loading = false;

const userReducer = (state, action) => {
	switch (action.type) {
		case 'setFavourites': {
			console.log('setFavourites to ', action.data);
			return {...state, favourites: action.data || {}};
		}
		case 'setBasket': {
			console.log('setBasket to ', action.data);
			return {...state, basket: action.data || {}};
		}
		case 'setOrder': {
			console.log('setOrder to ', action.data);
			return {...state, order: action.data || {}};
		}
		case 'setPayment': {
			console.log('setPayment to ', action.data);
			return {...state, payments: action.data || {}};
		}
		case 'setUser': {
			console.log('setUser to ', action.data);
			return {...state, user: action.data || {}};
		}
		case 'addToBasket': {
			console.log('addToBasket ', action.book_id);
			const newState = {...state};
			const { book_id } = action;
			if (!newState.basket[book_id]) {
				newState.basket[book_id] = 1;
			} else {
				newState.basket[book_id] += 1;
			}
			return newState;
		}
		case 'removeFromBasket': {
			console.log('removeFromBasket ', action.book_id);
			const newState = {...state};
			const { book_id } = action;

			if (newState.basket[book_id] > 1) {
				newState.basket[book_id] -= 1;
			} else {
				delete newState.basket[book_id];
			}
			return newState;
		}
		case 'addBookToFavourites': {
			console.log('addBookToFavourites ', action.book_id);
			const newState = {...state};
			const { book_id } = action;
			if (!newState.favourites.some((elem) => elem.book_id === book_id)) {
				newState.favourites.push({book_id});
				// send to backend
			}
			return newState;
		}
		case 'removeBookFromFavourites': {
			console.log('removeBookFromFavourites ', action.book_id);
			const newState = {...state};
			const { book_id } = action;
			newState.favourites = newState.favourites.filter((x) => !(x.book_id && x.book_id === book_id));
			// send to backend
			return newState;
		}
		case 'addAuthorToFavourites': {
			console.log('addAuthorToFavourites ', action.author_id);
			const newState = {...state};
			const { author_id } = action;
			if (!newState.favourites.some((elem) => elem.author_id === author_id)) {
				newState.favourites.push({author_id});
				// send to backend
			}
			return newState;
		}
		case 'removeAuthorFromFavourites': {
			console.log('removeAuthorFromFavourites ', action.author_id);
			const newState = {...state};
			const { author_id } = action;
			newState.favourites = newState.favourites.filter((x) => !(x.author_id && x.author_id === author_id));
			// send to backend
			return newState;
		}
		case 'cleanAll': {
			console.log('cleanAll');
			return {...userState};
		}
		default: {
			throw new Error(`Unhandled action type: ${action.type}`);
		}
	}
};

export const UserProvider = ({children}) => {
	const [user, userDispatch] = React.useReducer(userReducer, userState);

	const customDispatch = async (action) => {
		switch(action.type) {
			case "loadAll": {
				customDispatch({type: 'loadFavourites'});
				// customDispatch({type: 'loadBasket'}); // TODO: sync with basket on the server
				return;
			}
			case "loadFavourites": {
				return getFavourites().then((data) => userDispatch({type: 'setFavourites', data}));
			}
			case "loadBasket": {
				return getBasket().then((data) => userDispatch({type: 'setBasket', data}));
			}
			case "loadOrders": {
				return getOrders().then((data) => userDispatch({type: 'setOrder', data}));
			}
			case "loadPayments": {
				return getPayment().then((data) => userDispatch({type: 'setPayment', data}));
			}
			case "register": {
				return getRequest('register', action.user, 'POST')
				.then((data) => {
					if (data && data.error)
						throw data;
					if (data && data.success === false)
						throw new Error(data.message || 'Register failed')
					// register does not log in
					// userDispatch({type: 'setUser', data});
				});
			}
			case "signIn": {
				return getRequest('signIn', action.user, 'POST')
				.then((data) => {
					if (data && data.error)
						throw data;
					if (data && data.success === false)
						throw new Error(data.message || 'Sign in failed')
					userDispatch({type: 'setUser', data});
				});
			}
			case "signOut": {
				return getRequest('signOut')
				.then((data) => userDispatch({type: 'cleanAll'}));
			}
			default: {
				userDispatch(action);
			}
		}
	};

	React.useEffect(() => {
		const profile = Cookie.get('profile');
		if (!profile)
			return;
		Cookie.remove('profile');
		userDispatch({type: 'setUser', data: {
			email: profile,
		}});
	}, []);

	React.useEffect(() => {
		getRequest('check')
			.then((data) => {
				console.log(data);
				if (data.email !== 'Guest')
					userDispatch({type: 'setUser', data});
			});
	}, []);

	return (
		<UserContext.Provider value={[user, customDispatch]}>
			{ children }
		</UserContext.Provider>
	);
};

export const useUser = () => {
	const context = React.useContext(UserContext);
	if (context === undefined) {
		throw new Error('useUser must be used within a UserContext');
	}
	if (!loading && context[0] === userState) {
		loading = true;
		context[1]({type: 'loadAll'});
	}
	return context;
};
