import React from "react";
import PagesGenerator from '~/src/PagesGenerator.jsx';

////////////////////////////////////////

const baseUrl = 'http://localhost:9000/api/review';

const baseTitle = 'Reviews';
const editTitle = 'Edit review';

const properties = {
	content: 'Review',
	user_id: 'User ID',
	book_id: 'Book ID',
};

const propertiesTypes = {
	user_id: 'number',
	book_id: 'number',
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
