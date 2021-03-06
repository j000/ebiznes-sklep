import React, { Fragment, useState, useEffect } from "react";
import { useRouteMatch, useParams, useHistory } from "react-router";
import { Switch, Route, Link } from 'react-router-dom';
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { getRequest } from '~/src/utils';

////////////////////////////////////////

const DeleteForm = (props) => {
	const history = useHistory();

	return (
		<Formik
			initialValues={{}}
			onSubmit={ (values, { setSubmitting }) => {
				if (!confirm('This action cannot be undone.\nAre you sure?')) {
					setSubmitting(false);
					return;
				}

				(async (setIsSubmitting) => {
					await getRequest(`${props.baseUrl}/${props.id}`, undefined, 'DELETE');
					if (props.return) history.push(props.return);
					if (props.callback) props.callback();
					setIsSubmitting(false);
				})(setSubmitting);
			}}
		>
			{({ isSubmitting }) => (
				<Form
					className="inline float-right"
				>
					<input
						className="danger"
						type="submit"
						value="Delete"
						disabled={isSubmitting}
					/>
				</Form>
			)}
		</Formik>
	);
};

const EditForm = (props) => {
	const { id } = useParams();
	const [element, setElement] = useState(props.propertiesState || {});
	const [error, setErrors] = useState(false);
	const history = useHistory();
	const withId = id ? '/' + id : '';

	const loadData = () => {
		let ignore = false;

		const fetchData = async () => {
			try {
				const data = await getRequest(props.baseUrl + withId);
				if (!ignore) setElement(data);
			} catch (e) {
				if (!ignore) setErrors(true);
			}
		};

		fetchData();
		return () => {
			ignore = true;
		};
	};

	useEffect(() => {
		if (id) {
			loadData();
		}
	}, []);

	return (
		<>
			{ error && <p>Something went wrong...</p> }
			<Formik
				initialValues={element}
				enableReinitialize={true}
				onSubmit={async (values, { resetForm, setSubmitting }) => {
					const ans = await getRequest(props.baseUrl + withId, values, id ? 'PUT' : 'POST')
					setSubmitting(false);
					if (ans.error) {
						console.log(ans.error);
						return;
					}
					if (props.return)
						history.push(props.return);
					if (props.callback) {
						props.callback();
						resetForm();
					}
				}}
			>
				{({ isSubmitting }) => (
					<Form>
						<fieldset disabled={isSubmitting}>
							{ Object.entries(props.properties).map(([key, value]) => (
								<Fragment key={key}>
									<label htmlFor={key}>{value}</label>
									<Field
										id={key}
										name={key}
										{...props.propertiesAttributes[key]}
									/>
									<ErrorMessage name={key} />
								</Fragment>
							))}

							<div className="right">
								<button type="submit">Submit</button>
								<button type="reset" className="button-outline">Reset</button>
							</div>
						</fieldset>
					</Form>
				)}
			</Formik>
		</>
	)
}

const TableRow = (props) => {
	const { url } = useRouteMatch();
	const data = props.data;

	return (
		<tr key={ data.id }>
			<td>
				{data.id}
			</td>
			{ Object.entries(props.properties).map(([key, value]) => (
				<td key={key}>
					{data[key]}
				</td>
			))}
			<td>
				<Link
					to={`${url}/${data.id}`}
					className="button"
				>
					Edit
				</Link>
			</td>
			<td>
				<DeleteForm
					id={data.id}
					baseUrl={props.baseUrl}
					callback={props.callback}
				/>
			</td>
		</tr>
	);
};

const ElementTable = (props) => {
	const [elements, setElements] = useState([]);
	const [, setErrors] = useState([]);
	let ignore = false;

	const fetchData = async () => {
		try {
			const data = await getRequest(props.baseUrl);
			if (!ignore) setElements(data);
		} catch (error) {
			if (!ignore) setErrors([error]);
		}
	};

	useEffect(() => {
		fetchData();

		return () => {
			ignore = true
		};
	}, []);

	return (
		<>
			<section className="container">
				<h1>{props.title}</h1>
				<table className="with-buttons">
					<caption>{props.title}</caption>
					<thead>
						<tr>
							<th scope="col">ID</th>
							{ Object.entries(props.properties).map(([key, value]) => (
								<th
									key={key}
									scope="col"
								>
									{value}
								</th>
							))}
							<th scope="col"></th>
							<th scope="col"></th>
						</tr>
					</thead>
					<tbody>
						{ elements.map((i) => (
							<TableRow
								key={i.id}
								data={i}
								callback={fetchData}
								properties={props.properties}
								baseUrl={props.baseUrl}
							/>
						)) }
					</tbody>
				</table>
			</section>
			<section className="container">
				<h1>Create new</h1>
				<EditForm
					baseUrl={props.baseUrl}
					properties={props.properties}
					propertiesAttributes={props.propertiesAttributes}
					propertiesState={props.propertiesState}
					callback={fetchData}
				/>
			</section>
		</>
	);
};

const Edit = (props) => {
	const { id } = useParams();

	return (
		<section className="container">
			<h1>{props.title} {id}</h1>
			<EditForm
				baseUrl={props.baseUrl}
				properties={props.properties}
				propertiesAttributes={props.propertiesAttributes}
				propertiesState={props.propertiesState}
				return={props.path}
			/>
			<Link
				to={props.path}
				className="button"
			>
				Back
			</Link>
			<DeleteForm
				id={id}
				return={props.path}
			/>
		</section>
	);
};

export default (props) => {
	const { path } = useRouteMatch();

	return (
		<Switch>
			<Route path={`${path}/:id`}>
				<Edit
					title={props.editTitle}
					baseUrl={props.baseUrl}
					path={path}
					properties={props.properties}
					propertiesState={props.propertiesState}
					propertiesAttributes={props.propertiesAttributes}
				/>
			</Route>
			<Route>
				<ElementTable
					title={props.baseTitle}
					baseUrl={props.baseUrl}
					properties={props.properties}
					propertiesState={props.propertiesState}
					propertiesAttributes={props.propertiesAttributes}
				/>
			</Route>
		</Switch>
	);
};
