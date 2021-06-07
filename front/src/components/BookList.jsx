import React from 'react';
import { useStore } from '~/src/contexts/StoreContext';

export default () => {
	const [{
		books,
		authors,
		genres,
		reviews,
	}, storeDispatch] = useStore();

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
					}
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
