import React, { useState } from 'react';
import './Auth.css';

function Register() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [firstName, setfirstName] = useState('');
    const [lastName, setlastName] = useState('');
    const [message, setMessage] = useState('');

    const handleRegister = async (e) => {
        e.preventDefault();
        setMessage('');
        try {
            const response = await fetch('/api/users/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password, firstName, lastName}),
            });

            if (response.ok) {
                const message = await response.text();
                alert("user registration successful"); // Show success message
                window.location.href = '/login'; // Redirect to login page
            } else {
                const message = await response.text();
                setMessage(message);
            }
        } catch (error) {
            console.error('Error registering:', error);
            setMessage('An error occurred. Please try again.');
        }
    };

    return (
        <div className="auth-container">
            <video autoPlay muted loop className="background-video">
                <source src="1.mp4" type="video/mp4" />
                Your browser does not support the video tag.
            </video>
            <form className="auth-form" onSubmit={handleRegister}>
            
                <div className='form-group'>
                    <label>First Name:</label>
                    <input
                        type="text"
                        value={firstName}
                        onChange={(e) => setfirstName(e.target.value)}
                        required
                    />
                </div>
                <div className='form-group'>
                    <label>Last Name:</label>
                    <input
                        type="text"
                        value={lastName}
                        onChange={(e) => setlastName(e.target.value)}
                        required
                    />
                </div>
                <div className='form-group'>
                    <label>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div className='form-group'>
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" className='btn-primary'>Register</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
}

export default Register;
