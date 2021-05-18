import React from "react";
import PagesGenerator from '~/src/PagesGenerator.jsx';

////////////////////////////////////////

const baseUrl = 'http://localhost:9000/api/basket';

const baseTitle = 'Baskets';
const editTitle = 'Edit basket';

const properties = {
	user_id: 'User ID',
	book_id: 'Book ID',
	count: 'Count',
};

const propertiesTypes = {
	user_id: 'number',
	book_id: 'number',
	count: 'number',
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
