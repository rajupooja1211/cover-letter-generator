import React, { useState, useEffect } from 'react';
import './ProfileForm.css';
import { useNavigate } from 'react-router-dom';
import back from './../images/back.png';



const backStyle = {
    backgroundImage: `url(${back})`
}




function ProfileForm() {
    const [profile, setProfile] = useState({
        education: [{ school: '', specialization: '', years: '', gpa: '' }],
        workExperience: [{ company: '', role: '', yearsWorked: '', description: '' }],
        skills: [],
        certifications: []
    });
    const [message, setMessage] = useState('');
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const userEmail = localStorage.getItem('userEmail'); // Assuming the user's email is stored in localStorage


    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const response = await fetch(`/api/profiles/profile?email=${userEmail}`);
                if (response.ok) {
                    const data = await response.json();
                    if (data) {
                        window.location.href = '/generate-cover-letter'; // Redirect if profile exists
                    }
                } else if (response.status === 404) {
                    console.log('No profile found, allowing user to create one.');
                } else {
                    throw new Error('Failed to fetch profile');
                }
            } catch (error) {
                console.error('Error fetching profile:', error);
                // setMessage('An error occurred while checking your profile.');
            } finally {
                setLoading(false);
            }
        };

        fetchProfile();
    }, [userEmail]);

    const handleSubmit = async (e) => {
        e.preventDefault();

        const profileData = {
            ...profile,
            user: {
                email: userEmail,
            },
        };
        try {
            const response = await fetch('/api/profiles', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(profileData)
            });


            if (response.ok) {
                alert('Profile saved successfully!');
                navigate('/generate-cover-letter');
            } else {
                const errorMessage = await response.text();
                alert(`Failed to save profile: ${errorMessage}`);
            }
        } catch (error) {
            console.error('Error saving profile:', error);
            setMessage('An error occurred. Please try again.');
        }
    };

    const handleChange = (section, index, field, value) => {
        const updatedSection = [...profile[section]];
        updatedSection[index][field] = value;
        setProfile({ ...profile, [section]: updatedSection });
    };

    const addSection = (section) => {
        setProfile({
            ...profile,
            [section]: [...profile[section], {}]
        });
    };

    const removeSection = (section, index) => {
        if (window.confirm('Are you sure you want to delete this item?')) {
            const updatedSection = [...profile[section]];
            updatedSection.splice(index, 1);
            setProfile({ ...profile, [section]: updatedSection });
        }
    };

    if (loading) {
        return <div className="loading">Loading...</div>;
    }

    return (
        
        <div className="form" style = {backStyle}>
        <h2 className ="profile">Complete Your Profile</h2>
        <div className="profile-form-container">
            <form className="profile-form" onSubmit={handleSubmit}>
             

                {/* Education Section */}
                <h3>Education</h3>
                {profile.education.map((edu, index) => (
                    <div key={index} className="form-section">
                        <button
                            type="button"
                            className="delete-btn"
                            onClick={() => removeSection('education', index)}
                        >
                            &#x2716;
                        </button>
                        <input
                            type="text"
                            placeholder="School/University"
                            value={edu.school || ''}
                            onChange={(e) => handleChange('education', index, 'school', e.target.value)}
                            required
                        />
                        <input
                            type="text"
                            placeholder="Specialization"
                            value={edu.specialization || ''}
                            onChange={(e) => handleChange('education', index, 'specialization', e.target.value)}
                        />
                        <input
                            type="text"
                            placeholder="Years Attended"
                            value={edu.years || ''}
                            onChange={(e) => handleChange('education', index, 'years', e.target.value)}
                        />
                        <input
                            type="text"
                            placeholder="GPA/Percentage"
                            value={edu.gpa || ''}
                            onChange={(e) => handleChange('education', index, 'gpa', e.target.value)}
                        />
                    </div>
                ))}
                <button type="button" className="add-btn" onClick={() => addSection('education')}>
                    Add Education
                </button>

                {/* Work Experience Section */}
                <h3>Work Experience</h3>
                {profile.workExperience.map((work, index) => (
                    <div key={index} className="form-section">
                        <button
                            type="button"
                            className="delete-btn"
                            onClick={() => removeSection('workExperience', index)}
                        >
                            &#x2716;
                        </button>
                        <input
                            type="text"
                            placeholder="Company"
                            value={work.company || ''}
                            onChange={(e) => handleChange('workExperience', index, 'company', e.target.value)}
                            required
                        />
                        <input
                            type="text"
                            placeholder="Role/Position"
                            value={work.role || ''}
                            onChange={(e) => handleChange('workExperience', index, 'role', e.target.value)}
                            required
                        />
                        <input
                            type="text"
                            placeholder="Years Worked"
                            value={work.yearsWorked || ''}
                            onChange={(e) => handleChange('workExperience', index, 'yearsWorked', e.target.value)}
                            required
                        />
                        <textarea
                            placeholder="Description"
                            value={work.description || ''}
                            onChange={(e) => handleChange('workExperience', index, 'description', e.target.value)}
                        />
                    </div>
                ))}
                <button type="button" className="add-btn" onClick={() => addSection('workExperience')}>
                    Add Work Experience
                </button>

                <h3>Skills</h3>
                <div className="form-section">
                    {profile.skills.map((skill, index) => (
                        <div key={index} className="inline-item">
                            <span>{skill}</span>
                            <button
                                type="button"
                                className="delete-btn"
                                onClick={() => {
                                    if (window.confirm(`Are you sure you want to delete "${skill}"?`)) {
                                        const updatedSkills = [...profile.skills];
                                        updatedSkills.splice(index, 1);
                                        setProfile({ ...profile, skills: updatedSkills });
                                    }
                                }}
                            >
                                &#x2716;
                            </button>
                        </div>
                    ))}
                    <div className="inline-item">
                        <input
                            type="text"
                            placeholder="Add a skill"
                            onKeyDown={(e) => {
                                if (e.key === 'Enter' && e.target.value.trim()) {
                                    e.preventDefault();
                                    setProfile({
                                        ...profile,
                                        skills: [...profile.skills, e.target.value.trim()],
                                    });
                                    e.target.value = '';
                                }
                            }}
                        />
                        <button
                            type="button"
                            className="add-btn"
                            onClick={(e) => {
                                const input = e.target.previousSibling;
                                if (input.value.trim()) {
                                    setProfile({
                                        ...profile,
                                        skills: [...profile.skills, input.value.trim()],
                                    });
                                    input.value = '';
                                }
                            }}
                        >
                            Add Skill
                        </button>
                    </div>
                </div>


                <h3>Certifications</h3>
                <div className="form-section">
                    {profile.certifications.map((cert, index) => (
                        <div key={index} className="inline-item">
                            <span>{cert}</span>
                            <button
                                type="button"
                                className="delete-btn"
                                onClick={() => {
                                    if (window.confirm(`Are you sure you want to delete "${cert}"?`)) {
                                        const updatedCertifications = [...profile.certifications];
                                        updatedCertifications.splice(index, 1);
                                        setProfile({ ...profile, certifications: updatedCertifications });
                                    }
                                }}
                            >
                                &#x2716;
                            </button>
                        </div>
                    ))}
                    <div className="inline-item">
                        <input
                            type="text"
                            placeholder="Add a certification"
                            onKeyDown={(e) => {
                                if (e.key === 'Enter' && e.target.value.trim()) {
                                    e.preventDefault();
                                    setProfile({
                                        ...profile,
                                        certifications: [...profile.certifications, e.target.value.trim()],
                                    });
                                    e.target.value = '';
                                }
                            }}
                        />
                        <button
                            type="button"
                            className="add-btn"
                            onClick={(e) => {
                                const input = e.target.previousSibling;
                                if (input.value.trim()) {
                                    setProfile({
                                        ...profile,
                                        certifications: [...profile.certifications, input.value.trim()],
                                    });
                                    input.value = '';
                                }
                            }}
                        >
                            Add Certification
                        </button>
                    </div>
                </div>

                <button type="submit" className="btn-primary" >
                    Save Profile
                </button>
            </form>
           
            {/* {message && <p className="error-message">{message}</p>} */}
        </div>
        </div>
        // </div>
        
        
    );
}

export default ProfileForm;
