import React from 'react';
import BookList from '~/src/components/BookList';
import { useStore } from '~/src/contexts/StoreContext';
import { useUser } from '~/src/contexts/UserContext';

export default () => {
	const [{
		favourites,
	}, userDispatch] = useUser();

	const list = Object.values(favourites)
		.filter((fav) => fav.book_id)
		.map((fav) => fav.book_id)

	return (
		<section className="container">
			<BookList list={ list } />
		</section>
	);
};
