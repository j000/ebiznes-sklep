import React from 'react';
import { loadToMap } from '~/src/utils';

const UserContext = React.createContext();

const getFavourites = () => loadToMap('favourite');
const getBasket = () => loadToMap('basket');

const userState = {
	favourites: {},
	basket: {},
};

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
	if (context[0] == userState) {
		context[1]({type: 'loadAll'});
	}
	return context;
};
