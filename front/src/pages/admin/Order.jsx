import React from "react";
import PagesGenerator from '~/src/PagesGenerator.jsx';

////////////////////////////////////////

const baseUrl = 'http://localhost:9000/api/order';

const baseTitle = 'Orders';
const editTitle = 'Edit order';

const properties = {
	user_id: 'User ID',
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
