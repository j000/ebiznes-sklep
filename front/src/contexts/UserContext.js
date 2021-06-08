import React from 'react';
import { loadToMap } from '~/src/utils';

const UserContext = React.createContext();

const getFavourites = () => loadToMap('favourite');
const getBasket = () => loadToMap('basket');
const getOrders = () => loadToMap('order');
const getPayment = () => loadToMap('payment');

const userState = {
	favourites: {},
	basket: {},
	orders: {},
	payments: {},
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
				getFavourites().then((data) => userDispatch({type: 'setFavourites', data}));
				return;
			}
			case "loadBasket": {
				getBasket().then((data) => userDispatch({type: 'setBasket', data}));
				return;
			}
			case "loadOrders": {
				getOrders().then((data) => userDispatch({type: 'setOrder', data}));
				return;
			}
			case "loadPayments": {
				getPayment().then((data) => userDispatch({type: 'setPayment', data}));
				return;
			}
			default: {
				userDispatch(action);
			}
		}
	};

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
	if (!loading && context[0] == userState) {
		loading = true;
		context[1]({type: 'loadAll'});
	}
	return context;
};
