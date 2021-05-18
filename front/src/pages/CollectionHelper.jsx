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

const propertiesTypes = {
	collection_id: 'number',
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
