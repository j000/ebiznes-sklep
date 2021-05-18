const getRequest = async (url, data, method='GET') => {
	const result = await fetch(url, {
		method,
		headers: {
			'Accept': 'application/json',
			'Content-type': 'application/json',
		},
		body: data ? JSON.stringify(data) : undefined,
	});
	const json = await result.json();
	return json;
};

export {
	getRequest,
};
