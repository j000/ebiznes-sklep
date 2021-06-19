import React from 'react';
import { TheHeader } from '~/src/components/TheHeader';
import { TheFooter } from '~/src/components/TheFooter';

export default (props) => {
	return (
		<>
			<TheHeader />
			{props.children}
			<TheFooter />
		</>
	)
}
