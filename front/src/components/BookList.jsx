import React from 'react';
import { useStore } from '~/src/contexts/StoreContext';
import { useUser } from '~/src/contexts/UserContext';

export default ({ list }) => {
	const [{
		books,
		authors,
		genres,
		reviews,
	}, ] = useStore();
	const [{ user, basket, favourites }, userDispatch] = useUser();

	const addToBasket = (id) => {
		userDispatch({ type: 'addToBasket', book_id: id });
	};

	const removeFromBasket = (id) => {
		userDispatch({ type: 'removeFromBasket', book_id: id });
	};

	const addBookToFavourites = (id) => {
		userDispatch({ type: 'addBookToFavourites', book_id: id });
	};

	const removeBookFromFavourites = (id) => {
		userDispatch({ type: 'removeBookFromFavourites', book_id: id });
	};

	const isInFavourites = (id) => favourites && favourites.some((elem) => elem.book_id === id);

	return (<ul>
		{
			Object.values(list).flatMap((id) => books[id] || []).map((book) => (
				<li key={ book.id }>
					<q>
						{ book.title }
					</q> by <i>{
						authors?.[book.author_id]?.name || <>author #{book.author_id}</>
						}</i> - {
							genres?.[book.genre_id]?.name || <i>genre #{book.genre_id}</i>
					} { user?.email && <>
						<a
							onClick={ () => addToBasket(book.id) }
						>
							Add to basket
						</a> { basket && basket[book.id] && (
								<a
									onClick={ () => removeFromBasket(book.id) }
								>
									Remove from basket
								</a>
							)
						} {
							isInFavourites(book.id) ? (
								<a
									onClick={ () => removeBookFromFavourites(book.id) }
								>
									&#9733;
								</a>
							) : (
								<a
									onClick={ () => addBookToFavourites(book.id) }
								>
									&#9734;
								</a>
							)
						}</>
					} {
						reviews?.[book.id]?.length && (
							<ul>
								{reviews?.[book.id]?.map((review) => (
									<li key={ review.id }>
										{ review.content }
									</li>
								))}
							</ul>
						)
					}
				</li>
			))
		}
	</ul>);
};
