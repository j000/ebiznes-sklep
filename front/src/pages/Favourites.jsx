import React from 'react';
import BookList from '~/src/components/BookList';
import { useUser } from '~/src/contexts/UserContext';

export default () => {
	const [{
		favourites,
	},] = useUser();

	const list = Object.values(favourites)
		.flatMap((fav) => fav.book_id || [])

	return (
		<section className="container">
			<BookList list={ list } />
		</section>
	);
};
