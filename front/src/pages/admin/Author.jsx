import React from "react";
import PagesGenerator from '~/src/PagesGenerator.jsx';

////////////////////////////////////////

const baseUrl = 'author';

const baseTitle = 'Author';
const editTitle = 'Edit author';

const properties = {
	name: 'Name',
};

const propertiesAttributes = {};

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
