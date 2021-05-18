import React from "react";
import PagesGenerator from '~/src/PagesGenerator.jsx';

////////////////////////////////////////

const baseUrl = 'http://localhost:9000/api/collection';

const baseTitle = 'Collections';
const editTitle = 'Edit collection';

const properties = {
	name: 'Name',
};

const propertiesTypes = {};

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
