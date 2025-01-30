import React, { useEffect, useState } from 'react';
import './ViewCoverLetters.css';
import back from './../images/back.png';



const backStyle = {
    backgroundImage: `url(${back})`
}

function ViewCoverLetters() {
    const [coverLetters, setCoverLetters] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchCoverLetters = async () => {
            try {
                const userEmail = localStorage.getItem("userEmail");
                console.log(userEmail);
                const response = await fetch(`/api/cover-letters/user/${userEmail}`);
                if (response.ok) {
                    const data = await response.json();
                    setCoverLetters(data);
                } else {
                    setError('Failed to fetch cover letters');
                }
            } catch (err) {
                setError('An error occurred while fetching cover letters');
            } finally {
                setLoading(false);
            }
        };

        fetchCoverLetters();
    }, []);

    const downloadCoverLetter = async (id) => {
        try {
            const response = await fetch(`/api/cover-letters/download/${id}`);
            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `cover_letter_${id}.docx`; // Set the filename
                document.body.appendChild(a);
                a.click();
                a.remove();
            } else {
                console.error('Failed to download cover letter');
            }
        } catch (err) {
            console.error('Error:', err);
        }
    };
    

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>{error}</div>;
    }

    if (coverLetters.length === 0) {
        return <div className="empty">No cover letters found..</div>;
    }

    return (
        <div className="background" style = {backStyle}>
        <div className="view-cover-letters-container" >
            <h1 className="View">Your Cover Letters</h1>
            <table className="cover-letters-table">
                <thead>
                    <tr>
                        {/* <th>ID</th> */}
                        <th>Company/Position</th>
                        <th>Template</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {coverLetters.map((letter) => (
                        <tr key={letter.id}>
                            {/* <td>{letter.id}</td> */}
                            <td>{letter.companyPosition || 'No Position Specified'}</td>
                            <td>{<td>
  {letter.template === "/template1.png" && <img src="/template1.png" alt="Template 1" width="100" height="100" />}
  {letter.template === "/template2.png" && <img src="/template2.png" alt="Template 2" width="100" height="100" />}
  {letter.template === "/template3.png" && <img src="/template3.png" alt="Template 3" width="100" height="100" />}
</td>
}</td>
                            <td>
                            <button
                                    className="download-btn"
                                    onClick={() => downloadCoverLetter(letter.id)}
                                >
                                    Download
                                </button>

                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
        </div>
    );
}

export default ViewCoverLetters;
