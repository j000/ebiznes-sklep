import React from "react";
import PagesGenerator from '~/src/PagesGenerator.jsx';

////////////////////////////////////////

const baseUrl = 'http://localhost:9000/api/user';

const baseTitle = 'Users';
const editTitle = 'Edit user';

const properties = {
	login: 'Login',
	password: 'Password',
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
