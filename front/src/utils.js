import React, { useState, Component } from 'react';
import Cookie from 'js-cookie';

export const endpoint = (
	window.location.hostname === "localhost" || window.location.hostname.startsWith('127.')
	? 'http://localhost:9000/api'
	: 'https://ebiznes.azurewebsites.net/api'
);

export const getRequest = async (url, data, method='GET') => {
	if (!url.startsWith('http')) {
		url = `${endpoint}/${url}`;
	}
	const result = await fetch(url, {
		method,
		headers: {
			'Accept': 'application/json',
			'Content-type': 'application/json',
			'Csrf-Token': Cookie.get('csrfToken'),
		},
		mode: 'cors',
		credentials: 'include',
		body: data ? JSON.stringify(data) : undefined,
	});
	return result.json();
};

export const loadToMap = async (url) => {
	const all = await getRequest(url);
	return all.reduce((map, elem) => {
		map[elem.id] = elem;
		return map;
	}, {});
};

export const loadToMapAlter = async (url, key) => {
	const all = await getRequest(url);
	return all.reduce((map, elem) => {
		if (map[elem[key]]) {
			map[elem[key]].push(elem);
		} else {
			map[elem[key]] = [elem];
		}
		return map;
	}, {});
};

export const loadToMapArray = async (url, url2, key, key2) => {
	const [data, helper] = await Promise.all([
		loadToMap(url),
		getRequest(url2),
	]);
	return helper.reduce((parent, elem) => {
		if (elem[key] && parent[elem[key]]) {
			if (parent[elem[key]][key2]) {
				parent[elem[key]][key2].push(elem[key2]);
			} else {
				parent[elem[key]][key2] = [elem[key2]];
			}
		}
		return parent;
	}, data);
};


// no hooks here...
export class ErrorBoundary extends Component {
	constructor(props) {
		super(props);
		this.state = { error: null, errorInfo: null };
	}

	componentDidCatch(error, errorInfo) {
		// Catch errors in any components below and re-render with error message
		this.setState({
			error: error,
			errorInfo: errorInfo
		});
		// You can also log error messages to an error reporting service here
	}

	render() {
		if (this.state.errorInfo) {
			// Error path
			return (
				<section {...this.props}>
					<h2>Something went wrong.</h2>
					<details style={{ whiteSpace: 'pre-wrap' }}>
						{this.state.error && this.state.error.toString()}
						<br />
						{this.state.errorInfo.componentStack}
					</details>
				</section>
			);
		}
		// Normally, just render children
		return this.props.children;
	}
}

export const priceInput = (initialValue) => {
	const [value, setValue] = useState(initialValue);

	return {
		value,
		setValue,
		reset: () => setValue(initialValue),
		bind: {
			value: () => ( value / 100 ),
			onChange: (event) => {
				setValue(Math.ceil(event.target.value * 100));
			},
		},
	};
};

export default getRequest;
