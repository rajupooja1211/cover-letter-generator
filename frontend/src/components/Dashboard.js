import React from "react";
import { useNavigate } from 'react-router-dom';
import "./Dashboard.css";

function Dashboard() {
    const navigate = useNavigate();

    const handleCreateCoverLetter = async () => {
        try {
            const email = localStorage.getItem('userEmail'); // Retrieve user email from localStorage
            if (!email) {
                alert('No user logged in!');
                navigate('/login');
                return;
            }

            // Fetch profile existence from the backend
            const response = await fetch(`/api/profiles/profile?email=${encodeURIComponent(email)}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' },
            });

            if (response.status === 200) {
                const profile = await response.json();
                if (profile) {
                    // If the profile exists, navigate to cover letter generation page
                    navigate('/generate-cover-letter');
                }
            } else if (response.status === 204) {
                // If the profile does not exist, navigate to the profile form
                navigate('/profile-form');
            } else {
                throw new Error('Unexpected response from server');
            }
        } catch (error) {
            console.error('Error checking profile existence:', error);
            alert('An error occurred while checking the profile. Please try again.');
        }
    };

    const handleViewExistingCoverLetters = () => {
        navigate('/view-cover-letters');
    };

    return (
        <div className="dashboard">
            <video autoPlay muted loop className="background-video">
                <source src="/1.mp4" type="video/mp4" />
                Your browser does not support the video tag.
            </video>
            <div>
                <h1>Welcome to Your Dashboard</h1>
                
                <div className="actions">
                    <button className="btn" onClick={handleCreateCoverLetter}>Create New Cover Letter</button>
                    <button className="btn" onClick={handleViewExistingCoverLetters}>View Existing Cover Letters</button>
                </div>
            </div>
        </div>
    );
}

export default Dashboard;
