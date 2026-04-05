# 🎓 Student Management System
### *Java + Swing GUI + MySQL + JDBC*

A desktop application to manage student records — add, display, sort, search, modify, and delete students through a clean graphical interface backed by a MySQL database.

---

## ✨ Features

| Feature | Details |
|---|---|
| **Add Student** | Form with validation — required fields & GPA range check |
| **Display All** | Styled scrollable table with alternating row colors |
| **Sort** | By First Name, Last Name, Major, GPA, or Student ID |
| **Search** | By Student ID, First/Last Name, or Major (partial match) |
| **Modify** | Update any field of a student by their ID |
| **Delete** | Remove a student record (with confirmation prompt) |
| **Clear Fields** | Reset the form in one click |
| **Status Bar** | Live feedback at the bottom of the window |

### Security
- All database queries use **PreparedStatement** — protected against SQL injection
- Connections are opened and closed per operation (try-with-resources)

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| GUI | Java Swing (JFrame, JPanel, JTable, GridBagLayout) |
| Database | MySQL 8.x |
| DB Driver | JDBC — `mysql-connector-j-9.5.0.jar` |
| IDE | VS Code / IntelliJ IDEA / Eclipse |

---

## 📁 Project Structure

```
Student-Management-System-Java/
│
├── Student-Management-System-in-Java/
│   ├── src/
│   │   ├── Main.java          ← Entry point — launches on Swing EDT
│   │   ├── AppGUI.java        ← Full GUI + all button logic (PreparedStatements)
│   │   ├── dbConnect.java     ← MySQL connection setup
│   │   └── Table.java         ← Converts ResultSet → JTable model
│   │
│   ├── lib/
│   │   └── mysql-connector-j-9.5.0.jar   ← MySQL JDBC driver (add this manually)
│   │
│   ├── bin/                   ← Compiled .class files go here
│   └── student_data.sql       ← DB + table creation + sample data
│
├── .gitignore
└── README.md
```

---

## ⚙️ Setup & Run

### Step 1 — Prerequisites

- Java JDK 17 or later → [Download](https://adoptium.net/)
- MySQL 8.x → [Download](https://dev.mysql.com/downloads/)
- MySQL Connector/J JAR → [Download](https://dev.mysql.com/downloads/connector/j/)
  - Select **Platform Independent** → download the ZIP → extract the `.jar`
  - Place `mysql-connector-j-9.5.0.jar` inside the `lib/` folder

### Step 2 — Database Setup

```sql
-- Option A: Terminal
mysql -u root -p < Student-Management-System-in-Java/student_data.sql

-- Option B: MySQL Workbench
-- File > Run SQL Script > select student_data.sql
```

This creates the `studentdb` database with the `sdata` table and 10 sample students.

### Step 3 — Configure Database Credentials

Open `src/dbConnect.java` and update:

```java
private static final String DB_NAME = "studentdb";   // your database name
private static final String DB_USER = "root";         // your MySQL username
private static final String DB_PASS = "your_password_here"; // ← change this
```

### Step 4 — Compile

```bash
cd Student-Management-System-in-Java

# Windows
javac -d bin -cp "lib/mysql-connector-j-9.5.0.jar" src/*.java

# macOS / Linux
javac -d bin -cp "lib/mysql-connector-j-9.5.0.jar" src/*.java
```

### Step 5 — Run

```bash
# Windows
java -cp "bin;lib/mysql-connector-j-9.5.0.jar" Main

# macOS / Linux
java -cp "bin:lib/mysql-connector-j-9.5.0.jar" Main
```

> **VS Code shortcut:** Add the `lib/` folder as a referenced library in `.vscode/settings.json`, then click **Run** on `Main.java`.

---

## 🗄 Database Schema

```sql
CREATE TABLE sdata (
    student_id  VARCHAR(10)   NOT NULL PRIMARY KEY,
    first_name  VARCHAR(50)   NOT NULL,
    last_name   VARCHAR(50)   NOT NULL,
    major       VARCHAR(50)   NOT NULL,
    phone       VARCHAR(15),
    gpa         DECIMAL(4,2)  CHECK (gpa >= 0.0 AND gpa <= 10.0),
    dob         DATE
);
```

---

## 🔮 Planned Improvements

- [ ] JavaFX UI with modern styling
- [ ] Login & role-based access (Admin / Teacher / Student)
- [ ] Export to CSV / Excel
- [ ] Pagination for large datasets
- [ ] Attendance & fee tracking modules
- [ ] REST API version (Spring Boot)

---


## 📄 License

MIT License — see [LICENSE](LICENSE) for details.
