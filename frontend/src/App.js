import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './components/HomePage';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import ProfileForm from './components/ProfileForm';
import GenerateCoverLetter from './components/GenerateCoverLetter';
import ViewCoverLetters from './components/ViewCoverLetters';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/profile-form" element={<ProfileForm />} />
                <Route path="/generate-cover-letter" element={<GenerateCoverLetter />} />
                <Route path="/view-cover-letters" element={<ViewCoverLetters />} />
            </Routes>
        </Router>
    );
}

export default App;
