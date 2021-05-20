import React from "react";
import PagesGenerator from '~/src/PagesGenerator.jsx';

////////////////////////////////////////

const baseUrl = 'http://localhost:9000/api/book';

const baseTitle = 'Books';
const editTitle = 'Edit book';

const properties = {
	title: 'Title',
	author_id: 'Author ID',
	genre_id: 'Genre ID',
	price: 'Price',
};

const propertiesAttributes = {
	author_id: {
		type: 'number',
	},
	genre_id: {
		type: 'number',
	},
	price: {
		type: 'number',
		min: 0,
		step: 0.01,
	},
};

const propertiesState = {};
Object.keys(properties).forEach((key) => {
	propertiesState[key] = '';
});

////////////////////////////////////////

export default () => {
	return (
		<PagesGenerator
			baseUrl={baseUrl}
			baseTitle={baseTitle}
			editTitle={editTitle}
			properties={properties}
			propertiesAttributes={propertiesAttributes}
			propertiesState={propertiesState}
		/>
	);
};
