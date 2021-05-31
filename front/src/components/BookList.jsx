import React from 'react';
import { useStore } from '~/src/contexts/StoreContext';

export default () => {
	const [store, storeDispatch] = useStore();
	const { books, authors, genres } = store;

	return (<ul>
		{
			Object.values(books).map((book) => (
				<li key={ book.id }>
					{
						book.id
					}. {
						book.title
					} by {
						authors?.[book.author_id]?.name || <i>author #{book.author_id}</i>
					} - {
						genres?.[book.genre_id]?.name || <i>genre #{book.genre_id}</i>
					}
				</li>
			))
		}
	</ul>);
};
