import React, { useState } from "react";

export default () => {
	const [count, setCount] = useState(0);

	return (
		<section className="container">
			<h1>Hello, World!</h1>
			<button
				onClick={() => setCount(count + 1)}
			>
				Clicked {count} times
			</button>
		</section>
	);
}
