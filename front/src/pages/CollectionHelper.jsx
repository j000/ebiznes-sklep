import React from "react";
import PagesGenerator from '~/src/PagesGenerator.jsx';

////////////////////////////////////////

const baseUrl = 'http://localhost:9000/api/collectionhelper';

const baseTitle = 'Collection Helpers';
const editTitle = 'Edit collection helper';

const properties = {
	collection_id: 'Collection ID',
	book_id: 'Book ID',
};

const propertiesAttributes = {
	collection_id: {
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
