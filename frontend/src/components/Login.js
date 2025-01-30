import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Auth.css';

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(''); // Clear any previous errors
    
        try {
            const response = await fetch('/api/users/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password }),
            });
    
            // Check if the response status is OK
            if (response.ok) {
                const result = await response.text();
                localStorage.setItem('userEmail', email);
    
                if (result === "Login successful!") {
                    navigate('/dashboard'); // Redirect only if login is successful
                } else {
                    setError(result); // Display the error message from the backend
                }
            } else {
                // Handle cases where the backend response is not OK
                alert("Login failed!! Try again");
            }
        } catch (err) {
            setError('An error occurred. Please try again later.');
        }
    };
    

    return (
        <div className="auth-container">
            <video autoPlay muted loop className="background-video">
                <source src="/1.mp4" type="video/mp4" />
                Your browser does not support the video tag.
            </video>
            <form className="auth-form" onSubmit={handleSubmit}>
                
                <div className="form-group">
                    <label>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" className="btn-primary">Login</button>
            </form>
        </div>
    );
}

export default Login;
