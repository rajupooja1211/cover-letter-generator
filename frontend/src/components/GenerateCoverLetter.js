import React, { useState, useEffect } from "react";
import "./GenerateCoverLetter.css";
import back from './../images/back.png';




const backStyle = {
    backgroundImage: `url(${back})`
}

function GenerateCoverLetter() {

    const [showModal, setShowModal] = useState(false);

    const [profile, setProfile] = useState(null);
    const [selectedSections, setSelectedSections] = useState({
        education: false,
        workExperience: false,
        skills: false,
        certifications: false,
    });
    const [selectedItems, setSelectedItems] = useState({
        education: [],
        workExperience: [],
        skills: [],
        certifications: [],
    });
    const [userInfo, setUserInfo] = useState({
        firstName: "",
        lastName: "",
        email: "",
        companyPosition: "",
    });
    const [selectedTemplate, setSelectedTemplate] = useState(null);
    const [previewContent, setPreviewContent] = useState(null);
    const [showPreview, setShowPreview] = useState(false);

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const userEmail = localStorage.getItem("userEmail");
                const response = await fetch(`/api/profiles/profile?email=${userEmail}`);
                if (response.ok) {
                    const data = await response.json();
                    setProfile(data);

                    // Autofill user details
                    setUserInfo({
                        firstName: data.firstName || "",
                        lastName: data.lastName || "",
                        email: userEmail,
                        companyPosition: data.companyPosition || "",
                    });
                } else {
                    throw new Error("Failed to fetch profile");
                }
            } catch (error) {
                console.error("Error fetching profile:", error);
            }
        };

        fetchProfile();
    }, []);

    const toggleSection = (section) => {
        const isSelected = !selectedSections[section];
        setSelectedSections({ ...selectedSections, [section]: isSelected });

        if (!isSelected) {
            setSelectedItems({ ...selectedItems, [section]: [] });
        }
    };

    const toggleSubsection = (section, item) => {
        if (!selectedSections[section]) return;

        const updatedItems = [...selectedItems[section]];
        const itemIndex = updatedItems.indexOf(item);

        if (itemIndex > -1) {
            updatedItems.splice(itemIndex, 1);
        } else {
            updatedItems.push(item);
        }

        setSelectedItems({ ...selectedItems, [section]: updatedItems });
    };

    const handleGenerate = () => {
        const selectedData = {
            education: selectedItems.education,
            workExperience: selectedItems.workExperience,
            skills: selectedItems.skills,
            certifications: selectedItems.certifications,
            userInfo,
            selectedTemplate,
        };

        if (
            !selectedSections.education &&
            !selectedSections.workExperience &&
            !selectedSections.skills &&
            !selectedSections.certifications
        ) {
            alert("Please select at least one section to generate the cover letter.");
            return;
        }

        if (!selectedTemplate) {
            alert("Please select a cover letter template.");
            return;
        }
    

        console.log("Selected Data for Cover Letter Generation:", selectedData);
        // You can send this data to the backend or an API for cover letter generation
        const sendJsonToBackend = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/cover-letters/generate", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(selectedData) // Convert the object to a JSON string
                });
        
                if (response.ok) {
                    // Convert the response to a Blob
                    const blob = await response.blob();
        
                    // Create a URL for the Blob
                    const url = window.URL.createObjectURL(blob);
        
                    // Create a temporary <a> element to trigger the download
                    const a = document.createElement("a");
                    a.href = url;
                    a.download = "cover_letter.docx"; // Set the file name
                    document.body.appendChild(a); // Append the <a> element to the DOM
                    a.click(); // Trigger the download
                    a.remove(); // Remove the <a> element from the DOM
                    //setPreviewContent(url); // Set the URL to preview the content
                    //setShowPreview(true);   // Show the modal with the preview
                    setShowModal(true);
                } else {
                    console.error("Failed to send JSON. Status:", response.status);
                }
            } catch (error) {
                console.error("Error while sending JSON to backend:", error);
            }
        };
        
        // Call the function to send JSON
        sendJsonToBackend();
    };

    const handleDownload = (type) => {
        if (!previewContent) return;
        const a = document.createElement("a");
        a.href = previewContent;
        a.download = "cover_letter.docx";
        a.click();
    };

    const closeModal = () => {
        setShowPreview(false);
        setPreviewContent(null);
    };

    if (!profile) {
        return <div>Loading...</div>;
    }

    return (
        <div className="cover" style = {backStyle}>
        <div className="generate-cover-letter-container">
            <h4>Generate Cover Letter</h4>

            {/* User Info Input */}
            <div className="user-info">
                <h3>Your Information</h3>
                <input
                    type="text"
                    placeholder="First Name"
                    value={userInfo.firstName}
                    onChange={(e) => setUserInfo({ ...userInfo, firstName: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Last Name"
                    value={userInfo.lastName}
                    onChange={(e) => setUserInfo({ ...userInfo, lastName: e.target.value })}
                />
                <input
                    type="email"
                    placeholder="Email"
                    value={userInfo.email}
                    onChange={(e) => setUserInfo({ ...userInfo, email: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Company/Position Applying For"
                    value={userInfo.companyPosition}
                    onChange={(e) => setUserInfo({ ...userInfo, companyPosition: e.target.value })}
                />
            </div>

            {/* Profile Sections */}
            <div className="section">
                <label>
                    <input
                        type="checkbox"
                        checked={selectedSections.education}
                        onChange={() => toggleSection("education")}
                    />
                    Education
                </label>
                <div className="subsection">
                    {profile.education.map((edu, index) => (
                        <label key={index} className={!selectedSections.education ? "disabled" : ""}>
                            <input
                                type="checkbox"
                                disabled={!selectedSections.education}
                                checked={selectedItems.education.includes(edu)}
                                onChange={() => toggleSubsection("education", edu)}
                            />
                            {edu.school} - {edu.specialization} ({edu.years})
                        </label>
                    ))}
                </div>
            </div>

            <div className="section">
                <label>
                    <input
                        type="checkbox"
                        checked={selectedSections.workExperience}
                        onChange={() => toggleSection("workExperience")}
                    />
                    Work Experience
                </label>
                <div className="subsection">
                    {profile.workExperience.map((work, index) => (
                        <label key={index} className={!selectedSections.workExperience ? "disabled" : ""}>
                            <input
                                type="checkbox"
                                disabled={!selectedSections.workExperience}
                                checked={selectedItems.workExperience.includes(work)}
                                onChange={() => toggleSubsection("workExperience", work)}
                            />
                            {work.company} - {work.role} ({work.yearsWorked})
                        </label>
                    ))}
                </div>
            </div>

            <div className="section">
                <label>
                    <input
                        type="checkbox"
                        checked={selectedSections.skills}
                        onChange={() => toggleSection("skills")}
                    />
                    Skills
                </label>
                <div className="subsection">
                    {profile.skills.map((skill, index) => (
                        <label key={index} className={!selectedSections.skills ? "disabled" : ""}>
                            <input
                                type="checkbox"
                                disabled={!selectedSections.skills}
                                checked={selectedItems.skills.includes(skill)}
                                onChange={() => toggleSubsection("skills", skill)}
                            />
                            {skill}
                        </label>
                    ))}
                </div>
            </div>

            <div className="section">
                <label>
                    <input
                        type="checkbox"
                        checked={selectedSections.certifications}
                        onChange={() => toggleSection("certifications")}
                    />
                    Certifications
                </label>
                <div className="subsection">
                    {profile.certifications.map((cert, index) => (
                        <label key={index} className={!selectedSections.certifications ? "disabled" : ""}>
                            <input
                                type="checkbox"
                                disabled={!selectedSections.certifications}
                                checked={selectedItems.certifications.includes(cert)}
                                onChange={() => toggleSubsection("certifications", cert)}
                            />
                            {cert}
                        </label>
                    ))}
                </div>
            </div>

            {/* Cover Letter Templates */}
            <div className="cover-letter-templates">
                <h3>Select a Cover Letter Template</h3>
                {["/template1.png", "/template2.png", "/template3.png"].map((template, index) => (
                    <div
                        key={index}
                        className={`template ${selectedTemplate === template ? "selected" : ""}`}
                        onClick={() => setSelectedTemplate(template)}
                    >
                        <img src={template} alt={`Template ${index + 1}`} />
                    </div>
                ))}
            </div>

            <button className="generate-btn" onClick={handleGenerate}>
                Generate Cover Letter
            </button>

           
             
        </div>
        
        {/* Modal for Success Message */}
        {showModal && (
                <div className="modal">
                    <div className="modal-content">
                        
                        <p className="success-message">Your cover letter is downloaded!</p>
                        <button className="close-btn" onClick={() => setShowModal(false)}>Close</button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default GenerateCoverLetter;