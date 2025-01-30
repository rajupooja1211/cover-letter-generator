package com.example.dynamic_cover_letter.controller;

import com.example.dynamic_cover_letter.entity.*;
import java.net.*;
import java.lang.StringBuilder;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.poi.xwpf.usermodel.*;
import com.example.dynamic_cover_letter.entity.CoverLetterRequest;
import com.example.dynamic_cover_letter.entity.CoverLetter;
import com.example.dynamic_cover_letter.entity.User;
import com.example.dynamic_cover_letter.repository.*;
import com.example.dynamic_cover_letter.service.CoverLetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import java.math.BigInteger;
import java.util.*;


@RestController
@RequestMapping("/api/cover-letters")
public class CoverLetterController {

    @Autowired
    private CoverLetterService coverLetterService;

    @Autowired
    private CoverLetterRepository coverLetterRepository;
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/user/{email}")
    public ResponseEntity<List<CoverLetter>> getCoverLettersByEmail(@PathVariable String email) {
        List<CoverLetter> coverLetters = coverLetterRepository.findByEmail(email);
        return ResponseEntity.ok(coverLetters);
    }
    

    @PostMapping
    public CoverLetter createCoverLetter(@RequestBody CoverLetter coverLetter) {
        return coverLetterService.createCoverLetter(coverLetter);
    }

    @DeleteMapping("/{id}")
    public void deleteCoverLetter(@PathVariable Long id) {
        coverLetterService.deleteCoverLetter(id);
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadCoverLetter(@PathVariable Long id) {
        Optional<CoverLetter> coverLetterOptional = coverLetterRepository.findById(id);

        if (coverLetterOptional.isPresent()) {
            CoverLetter coverLetter = coverLetterOptional.get();
            byte[] fileBytes = coverLetter.getFileData();

            // Return the file as a downloadable response
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=cover_letter_" + id + ".docx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                    .body(fileBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateCoverLetter(@RequestBody CoverLetterRequest request) {
    String str = convertToCoverLetterString(request);
    System.out.println("\nGenerated String:\n" + str + "\n");

    try {
        String coverLetter = generateCoverLetterFromInput(str);

        // Create the .docx file
        byte[] fileBytes = createDocxFile(coverLetter, request.getSelectedTemplate(), request);



          // Save the cover letter in the database
          CoverLetter CL = new CoverLetter();
          CL.setTemplate(request.getSelectedTemplate());
          CL.setCompanyPosition(request.getUser().getCompanyPosition());
          CL.setEmail(request.getUser().getEmail());
          CL.setUser((userRepository.findByEmail(request.getUser().getEmail())));
          CL.setFileData(fileBytes);
  
          coverLetterRepository.save(CL); 

        // Return the file as a downloadable response
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=cover_letter.docx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .body(fileBytes);


    } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // Restore interrupted status
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("The cover letter generation was interrupted. Please try again.");
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while generating the cover letter: " + e.getMessage());
    }
}

    public String generateCoverLetterFromInput(String inputText) throws IOException, InterruptedException {
        // Prepare the request payload for OpenAI GPT-3
        String apiKey = System.getenv("OPENAI_API_KEY");

    // Validate the API key
    if (apiKey == null || apiKey.isEmpty()) {
        throw new RuntimeException("OpenAI API key not found. Please set it as an environment variable.");
    }

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."));
        messages.put(new JSONObject().put("role", "user").put("content", "Generate a cover letter for the following input:\n" + inputText));
    
        JSONObject requestData = new JSONObject();
        requestData.put("model", "gpt-3.5-turbo");  // Specify the GPT model
        requestData.put("messages", messages);
        requestData.put("max_tokens", 1000);  // Limiting to 1000 tokens in the response
        requestData.put("temperature", 0.7);  // Adjusting randomness
    
        // Create an HttpClient instance
        HttpClient client = HttpClient.newHttpClient();
    
        // Create the HttpRequest to OpenAI API, including the Authorization header with your API key
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)  // Use the API key from environment variables
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestData.toString()))  // Send the JSON payload
            .build();
    
        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
        // Check if the response is successful
        if (response.statusCode() != 200) {
            throw new IOException("Error: " + response.statusCode() + " - " + response.body());
        }
    
        // Parse the response (assuming OpenAI returns JSON)
        JSONObject responseJson = new JSONObject(response.body());
        String coverLetterText = responseJson.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim();  // Extract the generated cover letter from the response
    
        return coverLetterText;  // Return the generated cover letter text
    }

    public String convertToCoverLetterString(CoverLetterRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
    
        // Append the prompt and the selected template
        stringBuilder.append("Generate a 350 word cover letter based on the details and data given below. Only generate the body. I don't want header, footer and the signature part. I also don't want the The Complimentary Close.").append("\n");
        System.out.println(request.getSelectedTemplate());
    
        // Append certifications
        stringBuilder.append("Certifications: ").append(request.getCertifications()).append("\n");
    
        // Append education details
        stringBuilder.append("Education: ");
        for (Education edu : request.getEducation()) {
            stringBuilder.append("[School: ").append(edu.getSchool())
                         .append(", Specialization: ").append(edu.getSpecialization())
                         .append(", GPA: ").append(edu.getGpa())
                         .append(", Years: ").append(edu.getYears())
                         .append("]\n");
        }
    
        // Append skills
        stringBuilder.append("Skills: ").append(request.getSkills()).append("\n");
    
        // Append user information
        User user = request.getUser();
        stringBuilder.append("User Info: ")
                     .append("[First Name: ").append(user.firstName())
                     .append(", Last Name: ").append(user.getlastName())
                     .append(", Email: ").append(user.getEmail())
                     .append(", Company/Position Applying for: ").append(user.getCompanyPosition())
                     .append("]\n");
    
        // Append work experience
        stringBuilder.append("Work Experience: ");
        for (Experience exp : request.getExperience()) {
            stringBuilder.append("[Company: ").append(exp.getCompany())
                         .append(", Role: ").append(exp.getRole())
                         .append(", Description: ").append(exp.getDescription()).append(exp.getYearsWorked())
                         .append("]\n");
        }
    
        // Return the constructed string
        return stringBuilder.toString();
    }
    public byte[] createDocxFile(String coverLetter, String selectedTemplate, CoverLetterRequest request) throws IOException {
        // Create a new document
        XWPFDocument document = new XWPFDocument();
        setDocumentMargins(document, 720, 720, 720, 720);
    
        switch (selectedTemplate) {
            case "/template1.png":
                // Template 1
                addHeader(document, request.getUser().firstName() + " " + request.getUser().getlastName(), "Calibri", 24, true, ParagraphAlignment.LEFT);
                addRightAlignedText(document, request.getUser().getEmail() + "\n" + "[Phone Number]" + "\n" + "[Address]", "Garamond", 12);
                addHorizontalLine(document);
                addRightAlignedText(document, LocalDate.now().toString(), "Garamond", 12);
                addRecipientDetails(document, "Hiring Manager\nCompany Name\n123 Company Address\nCity, State, Zip", "Century Gothics", 12);
                addSingleLineGap(document);
                addSubject(document, "Job Reference: " + "Position", "Century Gothic", 14, true, ParagraphAlignment.LEFT);
                 addSingleLineGap(document);
                 addContent(document, coverLetter, "Garamond", 12);
                addSignature(document, request.getUser().firstName() + " " + request.getUser().getlastName(), "Garamond", 12, true);
                break;
    
            case "/template2.png":
                // Template 2
                addHeader(document, request.getUser().firstName() + " " + request.getUser().getlastName(), "Futura", 20, true, ParagraphAlignment.LEFT);
                // addContactDetails(document, request.getUser().getEmail() + " | " + "[Phone Number]" + " | " + "[Address]", "Futura", 11, ParagraphAlignment.LEFT);
                addHorizontalLine(document);
                //addContent(document, coverLetter, "Futura", 12);
                addLeftRightData(document, request.getUser().getEmail() + " \n " + "[Phone Number]" + " \n " + "[Address]", coverLetter);
                addSignature(document, request.getUser().firstName() + " " + request.getUser().getlastName(), "Futura", 12, true);
                break;
    
            case "/template3.png":
                // Template 3
                addHeader(document, request.getUser().firstName() + " " + request.getUser().getlastName(), "Times New Roman", 16, true, ParagraphAlignment.LEFT);
                addContactDetails(document, request.getUser().getEmail() + " \n " + "[Phone Number]" + " \n " + "[Address]", "Times New Roman", 12, ParagraphAlignment.LEFT);
                addHorizontalLine(document);
                addSectionTitle(document, "[Hiring Manager’s Name]\nCompany Address\nCity, State, Zip", "Times New Roman", 12, false);
                addSingleLineGap(document);
                addSingleLineGap(document);
                addContent(document, coverLetter, "Times New Roman", 12);
                addSignature(document, request.getUser().firstName() + " " + request.getUser().getlastName(), "Times New Roman", 12, true);
            
                break;
            default:
                throw new IllegalArgumentException("Invalid template selection");
        }
    
        // Write the document to a ByteArrayOutputStream
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            document.write(out);
            document.close();
            return out.toByteArray();
        }
    }
    
    // Utility methods for reusable formatting components
    private void addHeader(XWPFDocument document, String text, String fontFamily, int fontSize, boolean bold, ParagraphAlignment alignment) {
        XWPFParagraph header = document.createParagraph();
        header.setAlignment(alignment);
        XWPFRun run = header.createRun();
        //run.setText(text);
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        run.setBold(bold);
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            run.setText(lines[i]);
            if (i < lines.length - 1) { // Add a break if it's not the last line
                run.addBreak();
            }
        }
    }
    
    private void addRightAlignedText(XWPFDocument document, String text, String fontFamily, int fontSize) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun run = paragraph.createRun();
        //run.setText(text);
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            run.setText(lines[i]);
            if (i < lines.length - 1) { // Add a break if it's not the last line
                run.addBreak();
            }
        }
    }
    
    private void addHorizontalLine(XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak();
        run.setText
        ("__________________________________________________________________________________________");
        run.addBreak();
    }

    private void addSingleLineGap(XWPFDocument document) {
        // Create a new paragraph
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setSpacingBefore(200); // Add spacing before (adjust as needed)
        paragraph.setSpacingAfter(200);  // Add spacing after (optional for consistent gaps)
        
        // Add a blank run to create the gap
        XWPFRun run = paragraph.createRun();
        run.setText(""); // Empty run for a blank line
    }
    
    private void addRecipientDetails(XWPFDocument document, String text, String fontFamily, int fontSize) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        //run.setText(text);
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            run.setText(lines[i]);
            if (i < lines.length - 1) { // Add a break if it's not the last line
                run.addBreak();
            }
        }
    }
    
    private void addSubject(XWPFDocument document, String text, String fontFamily, int fontSize, boolean bold, ParagraphAlignment alignment) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(alignment);
        XWPFRun run = paragraph.createRun();
        // run.setText(text);
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        run.setBold(bold);
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            run.setText(lines[i]);
            if (i < lines.length - 1) { // Add a break if it's not the last line
                run.addBreak();
            }
        }
    }
    
    private void addContent(XWPFDocument document, String text, String fontFamily, int fontSize) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun run = paragraph.createRun();
        //run.setText(text);
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            run.setText(lines[i]);
            if (i < lines.length - 1) { // Add a break if it's not the last line
                run.addBreak();
            }
        }
    }
    
    private void addSignature(XWPFDocument document, String name, String fontFamily, int fontSize, boolean bold) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        run.addBreak();
        run.addBreak();
        run.setText("Sincerely,");
        run.addBreak();
        run.setText(name);
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        run.setBold(bold);
        
        
    }
    
    private void addContactDetails(XWPFDocument document, String text, String fontFamily, int fontSize, ParagraphAlignment alignment) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(alignment);
        XWPFRun run = paragraph.createRun();
       // run.setText(text);
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            run.setText(lines[i]);
            if (i < lines.length - 1) { // Add a break if it's not the last line
                run.addBreak();
            }
        }
    }
    
    private void addSectionTitle(XWPFDocument document, String text, String fontFamily, int fontSize, boolean bold) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        //run.setText(text);
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        run.setBold(bold);
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            run.setText(lines[i]);
            if (i < lines.length - 1) { // Add a break if it's not the last line
                run.addBreak();
            }
        }
    }

    private void setDocumentMargins(XWPFDocument document, int top, int bottom, int left, int right) {
        // Access the document's section properties
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
    
        // Add or access the page margin properties
        CTPageMar pageMar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
    
        // Set margins (values are in twentieths of a point, so 1440 = 1 inch)
        pageMar.setTop(BigInteger.valueOf(top));       // Top margin
        pageMar.setBottom(BigInteger.valueOf(bottom)); // Bottom margin
        pageMar.setLeft(BigInteger.valueOf(left));     // Left margin
        pageMar.setRight(BigInteger.valueOf(right));   // Right margin
    }
    private void addLeftRightData(XWPFDocument document, String leftData, String rightData) {
        // Create a table with 1 row and 2 columns
        XWPFTable table = document.createTable(1, 2);
        //table.getCTTbl().addNewTblPr().addNewJc().setVal(STJc.CENTER); // Center the table in the document
    // Set table width to 100% of the page width
    table.setWidth("10800");

    // Set the width of the left column (30%)
    XWPFTableCell leftCell = table.getRow(0).getCell(0);
    leftCell.setWidth("3240");
    //leftCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER); // Align vertically
    XWPFParagraph leftParagraph = leftCell.addParagraph();
    leftParagraph.setAlignment(ParagraphAlignment.LEFT); // Align left data
    XWPFRun leftRun = leftParagraph.createRun();
    //leftRun.setText(leftData);
    leftRun.setFontSize(12);
    String[] lines1 = leftData.split("\n");
    for (int i = 0; i < lines1.length; i++) {
        leftRun.setText(lines1[i]);
        if (i < lines1.length - 1) { // Add a break if it's not the last line
        leftRun.addBreak();
        }
    }

    // Set the width of the right column (70%)
    XWPFTableCell rightCell = table.getRow(0).getCell(1);
    rightCell.setWidth("7560");
    //rightCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER); // Align vertically
    XWPFParagraph rightParagraph = rightCell.addParagraph();
    rightParagraph.setAlignment(ParagraphAlignment.BOTH); // Align right data
    XWPFRun rightRun = rightParagraph.createRun();
    //rightRun.setText(rightData);
    rightRun.setFontSize(12);
    String[] lines = rightData.split("\n");
    for (int i = 0; i < lines.length; i++) {
        rightRun.setText(lines[i]);
        if (i < lines.length - 1) { // Add a break if it's not the last line
        rightRun.addBreak();
        }
    }

    // Remove default empty paragraphs in the table cells
    leftCell.removeParagraph(0);
    rightCell.removeParagraph(0);
    }
    

    
}