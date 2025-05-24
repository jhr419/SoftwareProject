# EBU6304 SoftwareProject Group 16

This is a Java-based financial management desktop application developed using JavaFX and Maven. This project supports expense tracking, budgeting, category analysis, and intelligent recommendations.

## 📦 Project Structure
```
.
├── src/ # Java source code (main application)
│ ├── main/
│ │ ├── java/com.shelton.ebu6403/ # Main logic: controllers, models
│ │ ├── resources/com.shelton.ebu6403/ # FXML, images, styles
│ ├── test/ # JUnit test files
│ │ ├── java/com.shelton.ebu6403.models/
├── JunitTest/ # Additional controller-level tests
├── data/ # Application data (CSV files)
├── pom.xml # Maven project configuration
├── README.md # Project documentation
```

## 🧰 Technologies
- Java JDK 21
- JavaFX
- Maven
- JUnit 5
- JavaFX Charts

## Before Start (🛠️ Maven Installation)
This project uses [Apache Maven](https://maven.apache.org/) to build and run the application.  
Please ensure that Maven is installed on your system before running the commands below.

### 🔗 How to Install Maven

1. Download Maven from the official site:  
   👉 https://maven.apache.org/download.cgi

2. Follow the step-by-step installation guide:  
   👉 https://maven.apache.org/install.html

3. After installation, open your terminal or command prompt and run:

   ```bash
   mvn -v
   ```

## 🚀 Getting Started

### 1. Clone the repository (Or down download ZIP)
```bash
git clone https://github.com/jhr419/SoftwareProject
cd SoftwareProject
```

### 2. Build the project with Maven

```bash
mvn clean install
```

### 3. Run the application

#### 📌 Option 1: Using Maven on Command Line

Ensure JavaFX is configured and run the application with:

```bash
mvn javafx:run
```

#### 📌 Option 2: Using IntelliJ IDEA 

1. Open the project in **IntelliJ IDEA**.
2. Make sure the JavaFX SDK is configured.
3. Navigate to `src/main/java/com/shelton/ebu6403/LedgerApp.java`.
4. Right-click `LedgerApp` → `Run 'LedgerApp.main()'`.

> ⚠️ Make sure JavaFX is configured in your environment. If using IntelliJ IDEA, enable JavaFX SDK under Project Structure.

## 🧪 Running Tests

To run all unit tests:

```bash
mvn test
```

## 📁 Sample Data Format

Example of an input CSV file in `data/`:
```csv
serialNo,name,date,amount
1,Rent,2025-04-01,1500.00
2,Internet Bill,2025-04-05,60.00
3,Coffee,2025-04-07,15.50
```

## ✨ Features

- Expense and income tracking
- Budget setting and visualization
- Holiday-based spending reminders
- Intelligent categorization using AI
- Clean JavaFX UI with data charts

## 📸 Screenshots
The home page can be seen as follows.

![App Screenshot](src/main/resources/com/shelton/ebu6403/images/preview.png)



## 📝 License

This project is part of BUPT/QMUL EBU6304 Software Engineering Coursework. For academic use only.


