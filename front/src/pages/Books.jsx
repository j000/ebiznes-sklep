import React from 'react';
import BookList from '~/src/components/BookList';
import { useStore } from '~/src/contexts/StoreContext';

export default () => {
	const [{
		books,
	},] = useStore();

	return (
		<section className="container">
			<BookList list={ Object.values(books).map((book) => book.id) } />
		</section>
	);
};
