import React from 'react';
import { useStore } from '~/src/contexts/StoreContext';
import { useUser } from '~/src/contexts/UserContext';

export default () => {
	const [{
		books,
		authors,
		genres,
		reviews,
	}, storeDispatch] = useStore();
	const [{
	}, userDispatch] = useUser();

	const addToBasket = (id) => {
		userDispatch({ type: 'addToBasket', book_id: id });
	};

	return (<ul>
		{
			Object.values(books).map((book) => (
				<li key={ book.id }>
					<q>
						{ book.title }
					</q> by <i>{
						authors?.[book.author_id]?.name || <>author #{book.author_id}</>
					}</i> - {
						genres?.[book.genre_id]?.name || <i>genre #{book.genre_id}</i>
					} <a
						onClick={ () => addToBasket(book.id) }
					>
						Add to basket
					</a>
					{
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
