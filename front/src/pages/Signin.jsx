import React from "react";
import { useHistory } from "react-router";
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { endpoint, getRequest } from '~/src/utils';
import { useUser } from '~/src/contexts/UserContext';

////////////////////////////////////////

export default (props) => {
	const fields = {
		email: "E-Mail",
		password: "Password",
	};

	const [error, setError] = React.useState();

	const [user, setUser] = React.useState({
		email: '',
		password: '',
	});

	const history = useHistory();

	const [, userDispatch] = useUser();

	return (
		<section className="container">
			<h1>Sign in</h1>
			<p>{ error }</p>
			<Formik
				initialValues={user}
				enableReinitialize={true}
				onSubmit={async (user, { setSubmitting }) => {
					setError();
					userDispatch({type: 'signIn', user})
					.then(() => {
						history.push('/');
					})
					.catch((e) => {
						setError(e.error);
					})
					.finally(() => {
						setSubmitting(false);
					});
				}}
			>
				{({ isSubmitting }) => (
					<Form>
						<fieldset disabled={isSubmitting}>
							<label htmlFor="email">E-Mail</label>
							<Field
								id="email"
								name="email"
							/>
							<ErrorMessage name="email" />
							<label htmlFor="Password">Password</label>
							<Field
								id="password"
								name="password"
								type="password"
							/>
							<ErrorMessage name="password" />

							<div className="right">
								<button type="submit">Sign In</button>
								<button type="reset" className="button-outline">Reset</button>
							</div>
						</fieldset>
					</Form>
				)}
			</Formik>
			<a
				className="button"
				href={`${endpoint}/authenticate/google`}
			>
				Sign in with Google
			</a>
			<a
				className="button"
				href={`${endpoint}/authenticate/github`}
			>
				Sign in with GitHub
			</a>
		</section>
	);
};
