import { Link } from 'react-router-dom';

export default function NavBar() {
	return (
		<nav>
			<div className='flex justify-around items-center py-5 bg-[#234]'>
				<h1 className='font-semibold font-2xl'>CYBNITY Defense Platform</h1>
				<ul className='flex'>
					<li className='mx-1'>
						<Link to='/'>Home</Link>
					</li>
					<li className='mx-1'>
						<Link to='/organization_signup'>Sign-up organization</Link>
					</li>
					<li className='mx-1'>
						<Link to='/useraccount_signup'>Sign-up account</Link>
					</li>
					<li className='mx-1'>
						<Link to='/resource'>Sign-in account</Link>
					</li>
				</ul>
			</div>
		</nav>
	);
};
