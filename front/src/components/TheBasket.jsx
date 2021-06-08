import React from 'react';
import { Link, NavLink } from 'react-router-dom';
import { useUser } from '~/src/contexts/UserContext';
import { useStore } from '~/src/contexts/StoreContext';

export const TheBasket = (props) => {
	const [{
		basket,
	}, userDispatch] = useUser();
	const [{
		books,
	},] = useStore();

	return (
		<>
			<a
				className="button button-outline"
				tabIndex="0"
			>
				Basket {Object.entries(basket).reduce((sum, [k, v]) => sum + v, 0) || ''}
			</a>
			<ul className="dropdown">
				{ Object.entries(basket).map(([book_id, count]) => (
					<li key={book_id}>
						{ count }x { books?.[book_id]?.title || book_id }
						<a
							onClick={ () => userDispatch({type: 'removeFromBasket', book_id }) }
						>
							Remove
						</a>
					</li>
				))}
			</ul>
		</>
	)
}
