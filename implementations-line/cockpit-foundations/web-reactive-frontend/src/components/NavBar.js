import { Link } from 'react-router-dom';
import { Navbar, Container, Nav } from 'react-bootstrap';
import logo from './../media/cybnity-gorilla-light.svg';

export default function NavBar() {
	return (
		<Navbar bg="secondary" variant="dark" expand="lg">
			<Container fluid>
				<Navbar.Brand href="/">
					<img
						alt=""
						src={logo}
						width="30"
						height="30"
						className="d-inline-block align-top"
					/>{' '}
					CYBNITY Defense Platform
				</Navbar.Brand>
				<Nav className="me-auto">
					<Nav.Link href="/">HOME</Nav.Link>
					<Nav.Link href="/organization_signup">SIGN-UP ORGANIZATION</Nav.Link>
					<Nav.Link href="/useraccount_signup">SIGN-UP ACCOUNT</Nav.Link>
					<Nav.Link href="/resource">SIGN-IN</Nav.Link>
				</Nav>
			</Container>
		</Navbar>
	);
};
