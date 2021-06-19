import React from 'react';
import { Redirect } from 'react-router';
import BookList from '~/src/components/BookList';
import { useUser } from '~/src/contexts/UserContext';
import { useStore } from '~/src/contexts/StoreContext';

export default () => {
	const [{
		authors
	},] = useStore();
	const [{
		user,
		favourites,
	},] = useUser();

	if (!user?.email)
		return (<Redirect to="/"/>);

	const fav_books = Object.values(favourites)
		.flatMap((fav) => fav.book_id || [])
	const fav_authors = Object.values(favourites)
		.flatMap((fav) => fav.author_id || [])

	return (
		<section className="container">
			<h1>Favourites</h1>
			{ fav_books.length > 0 && <h3>Books</h3> }
			<BookList list={ fav_books } />
			{ fav_authors.length > 0 && <>
				<h3>Authors</h3>
				<ul>
					{ Object.values(fav_authors).map((author) => (
						<li key={author}>{authors?.[author]?.name || <i>author #{author}</i> }</li>
					))}
				</ul>
			</>}
		</section>
	);
};
