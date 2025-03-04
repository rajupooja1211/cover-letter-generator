**Cover Letter Generator**

This project is a **Cover Letter Generator** built with **React (Frontend)** and **Spring Boot (Backend)**. It allows users to log in, input details, and generate dynamic cover letters.

## **Technologies Used**
- **Frontend:** React, JavaScript, CSS
- **Backend:** Spring Boot, Java, Maven
- **Database:** MySQL (or H2 for development)
- **Build Tools:** Maven, npm

## **Prerequisites**
Make sure you have the following installed:

1. **Java 17+**  
   Check installation:
   ```sh
   java -version
   ```

2. **Maven** (for backend)  
   Verify installation:
   ```sh
   mvn -version
   ```

3. **Node.js and npm** (for frontend)  
   Verify installation:
   ```sh
   node -v
   npm -v
   ```

4. **MySQL (Optional for Production)**
   - If using MySQL, create a database:
     ```sql
     CREATE DATABASE cover_letter;
     ```

## **Installation**

### **1. Clone the Repository**
```sh
git clone https://github.com/your-repo/cover-letter-generator.git
cd cover-letter-generator
```

### **2. Set Up the Backend**
```sh
cd backend
cp src/main/resources/application-example.properties src/main/resources/application.properties
```
Modify `application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cover_letter
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### **3. Install Frontend Dependencies**
```sh
cd ../frontend
npm install
```


## **Running the Project**

### **1. Start the Backend**
```sh
cd backend
./mvnw spring-boot:run
```
If successful, you should see:
```
Tomcat started on port(s): 8080
```

### **2. Build and Serve the Frontend**
```sh
cd frontend
npm run build
```
Then, copy the built files to the backend's static folder:
```sh
cp -r frontend/build/* backend/src/main/resources/static/
```

### **3. Open the Application**
Go to **[http://localhost:8080](http://localhost:8080)** in your browser.

---

## **API Endpoints**
| Method | Endpoint            | Description               |
|--------|---------------------|---------------------------|
| POST   | `/api/auth/login`   | User authentication       |
| POST   | `/api/auth/register` | User registration        |
| GET    | `/api/letters`      | Fetch cover letters       |
| POST   | `/api/letters`      | Generate a new cover letter |



## **Contributing**
1. Fork the repository
2. Create a new branch (`feature-branch`)
3. Commit changes and push
4. Open a pull request


