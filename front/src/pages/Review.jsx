import React, { useState, useEffect } from "react";
import { useRouteMatch, useParams, useHistory } from "react-router";
import { Switch, Route, Link } from 'react-router-dom';
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { getRequest } from '~/src/utils.jsx';

////////////////////////////////////////

const baseUrl = 'http://localhost:9000/api/review';

const properties = {
	content: 'Review',
	user_id: 'User ID',
	book_id: 'BookId',
};

const propertiesState = {
	content: '',
	user_id: '',
	book_id: '',
};

const baseTitle = 'Reviews';
const editTitle = 'Edit review';

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

				(async (setSubmitting) => {
				const x = await getRequest(`${baseUrl}/${props.id}`, undefined, 'DELETE');
				if (props.return)
					history.push(props.return);
				if (props.callback)
					props.callback();
				setSubmitting(false);
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
	const [element, setElement] = useState(propertiesState);
	const [error, setErrors] = useState(false);
	const [query, setQuery] = useState(0);
	const history = useHistory();
	const withId = id ? '/' + id : '';

	const loadData = () => {
		let ignore = false;

		const fetchData = async () => {
			try {
				const data = await getRequest(baseUrl + withId);
				if (!ignore) setElement(data);
			} catch (error) {
				if (!ignore) setErrors(true);
			}
		};

		fetchData();
		return () => {
			ignore = true;
		};
	};

	if (id) {
		useEffect(loadData, []);
	}

	return (
		<>
			{ error && <p>Something went wrong...</p> }
			<Formik
				initialValues={element}
				enableReinitialize={true}
				onSubmit={async (values, { resetForm, setSubmitting }) => {
					await getRequest(baseUrl + withId, values, id ? 'PUT' : 'POST')
					if (props.return)
						history.push(props.return);
					if (props.callback) {
						props.callback();
						resetForm();
					}
					setSubmitting(false);
				}}
			>
				{({ isSubmitting }) => (
					<Form>
						<fieldset disabled={isSubmitting}>
							<label htmlFor="content">Review</label>
							<Field id="content" name="content" type="text" />
							<ErrorMessage name="content" />

							<label htmlFor="user_id">User ID</label>
							<Field id="user_id" name="user_id" type="number" />
							<ErrorMessage name="user_id" />

							<label htmlFor="book_id">Book ID</label>
							<Field id="book_id" name="book_id" type="number" />
							<ErrorMessage name="book_id" />

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
	const { path, url } = useRouteMatch();
	const data = props.data;

	return (
		<tr key={ data.id }>
			<td>
				{data.id}
			</td>
			{ Object.entries(properties).map(([key, value]) => (
				<td key={key}>{data[key]}</td>
			))}
			<td>
				<Link to={`${url}/${data.id}`} className="button">
					Edit
				</Link>
			</td>
			<td>
				<DeleteForm id={data.id} callback={props.callback} />
			</td>
		</tr>
	);
};

const ElementTable = () => {
	const [elements, setElements] = useState([]);
	const [errors, setErrors] = useState([]);
	let ignore = false;

	const fetchData = async () => {
		try {
			const data = await getRequest(baseUrl);
			if (!ignore) setElements(data);
		} catch (error) {
			if (!ignore) setErrors([error]);
		}
	};

	useEffect(() => {
		fetchData();

		return () => { ignore = true };
	}, []);

	return (
		<>
			<section className="container">
				<h1>{baseTitle}</h1>
				<table className="with-buttons">
					<thead>
						<tr>
							<th>ID</th>
							{ Object.entries(properties).map(([key, value]) => (
								<th key={key}>{value}</th>
							))}
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						{ elements.map((i) => (
							<TableRow
								key={i.id}
								data={i}
								callback={fetchData}
							/>
						)) }
					</tbody>
				</table>
			</section>
			<section className="container">
				<h1>Create new</h1>
				<EditForm callback={fetchData} />
			</section>
		</>
	);
};

const Edit = (props) => {
	const { id } = useParams();

	return (
		<section className="container">
			<h1>{editTitle} {id}</h1>
			<EditForm return={props.path}/>
			<Link to={props.path} className="button">
				Back
			</Link>
			<DeleteForm id={id} return={props.path} />
		</section>
	);
};

export default () => {
	let { path, url } = useRouteMatch();

	return (
		<Switch>
			<Route path={`${path}/:id`}>
				<Edit path={path} />
			</Route>
			<Route>
				<ElementTable />
			</Route>
		</Switch>
	);
};
