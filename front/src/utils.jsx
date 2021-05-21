import { useState, Component } from "react";

export const getRequest = async (url, data, method='GET') => {
	const result = await fetch(url, {
		method,
		headers: {
			'Accept': 'application/json',
			'Content-type': 'application/json',
		},
		body: data ? JSON.stringify(data) : undefined,
	});
	return result.json();
};

// no hooks here...
export class ErrorBoundary extends Component {
	constructor(props) {
		super(props);
		this.state = { error: null, errorInfo: null };
	};

	componentDidCatch(error, errorInfo) {
		// Catch errors in any components below and re-render with error message
		this.setState({
			error: error,
			errorInfo: errorInfo
		});
		// You can also log error messages to an error reporting service here
	};

	render() {
		if (this.state.errorInfo) {
			// Error path
			return (
				<>
					<h2>Something went wrong.</h2>
					<details style={{ whiteSpace: 'pre-wrap' }}>
						{this.state.error && this.state.error.toString()}
						<br />
						{this.state.errorInfo.componentStack}
					</details>
				</>
			);
		}
		// Normally, just render children
		return this.props.children;
	};
};

export const priceInput = (initialValue) => {
	const [value, setValue] = useState(initialValue);

	return {
		value,
		setValue,
		reset: () => setValue(initialValue),
		bind: {
			value: () => { value / 100 },
			onChange: (event) => {
				setValue(Math.ceil(event.target.value * 100));
			},
		},
	};
};

export default getRequest;
