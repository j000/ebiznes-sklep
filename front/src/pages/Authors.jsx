import React from 'react';
import BookList from '~/src/components/BookList';
import { useStore } from '~/src/contexts/StoreContext';
import { useUser } from '~/src/contexts/UserContext';

export default () => {
	const [{
		authors,
		books,
	}] = useStore();
	const [{ user, favourites }, userDispatch] = useUser();

	const addAuthorToFavourites = (author_id) => {
		userDispatch({ type: 'addAuthorToFavourites', author_id });
	};

	const removeAuthorFromFavourites = (author_id) => {
		userDispatch({ type: 'removeAuthorFromFavourites', author_id });
	};

	const isInFavourites = (id) => favourites && favourites.some((elem) => elem.author_id == id);

	const Favourite = ({id}) => {
		if (!user?.email)
			return null;
		if (isInFavourites(id))
			return (
				<a
					onClick={ () => removeAuthorFromFavourites(id) }
				>
					&#9733;
				</a>
			);
		return (
			<a
				onClick={ () => addAuthorToFavourites(id) }
			>
				&#9734;
			</a>
		);
	};

	return (
		<section className="container">
			<h1>
				Authors
			</h1>
			{ Object.values(authors).map((author) => {
				const list = Object.values(books).filter((book) => book.author_id === author.id);
				return (
				<React.Fragment key={author.id}>
					<h3>
						{ author.name } <Favourite id={author.id} />
					</h3>
					<BookList list={ list.slice(0, 4).map((book) => book.id) } />
					<p>
						{ list.length > 4 && 'and more...' }
					</p>
				</React.Fragment>
			)
			})}
		</section>
	);
};
