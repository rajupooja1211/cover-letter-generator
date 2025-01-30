import React from 'react';
import { Link } from 'react-router-dom';
import "./HomePage.css";

function HomePage() {
    return (
        <div>
            <video autoPlay muted loop className="background-video">
                <source src="/2.mp4" type="video/mp4" />
                Your browser does not support the video tag.
            </video>
            
                <h2>Welcome to the Dynamic Cover Letter Generator</h2>
                <div className="hero-section">
                <div className="action-buttons">
                <Link to="/login" className="btn-primary">Login</Link>
                <Link to="/register" className="btn-secondary">Register</Link>
                </div>
                </div>
            </div>
        
    );
}
export default HomePage;
