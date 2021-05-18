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

const propertiesTypes = {
	author_id: 'number',
	genre_id: 'number',
	price: 'number',
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
			propertiesTypes={propertiesTypes}
			propertiesState={propertiesState}
		/>
	);
};
