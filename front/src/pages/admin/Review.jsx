import React from "react";
import PagesGenerator from '~/src/PagesGenerator.jsx';

////////////////////////////////////////

const baseUrl = 'review';

const baseTitle = 'Reviews';
const editTitle = 'Edit review';

const properties = {
	content: 'Review',
	user_id: 'User ID',
	book_id: 'Book ID',
};

const propertiesAttributes = {
	user_id: {
		type: 'number',
	},
	book_id: {
		type: 'number',
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
