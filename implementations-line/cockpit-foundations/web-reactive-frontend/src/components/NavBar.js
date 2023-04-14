import { Link } from 'react-router-dom';

export default function NavBar() {
	return (
		<nav>
			<div className='flex justify-around items-center py-5 bg-[#234] text-white'>
				<h1 className='font-semibold font-2xl'>KeyCloak App</h1>
				<ul className='flex'>
					<li className='mx-1'>
						<Link to='/'>Home</Link>
					</li>
					<li className='mx-1'>
						<Link to='/resource'>Login</Link>
					</li>
				</ul>
			</div>
		</nav>
	);
};
