# SafeQR Spring Boot Project

This is a Spring Boot project built with Java 17. This guide will help you set up the project and install the necessary dependencies using Maven.

## Prerequisites

Before you begin, ensure you have the following software installed on your system:

- **Java 17**: This project requires Java 17. You can download it from the [official Oracle website](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or install it via a package manager (e.g., `sdkman` or `brew` on macOS).
- **Maven 3.6+**: Apache Maven is used to manage the project's dependencies. You can download Maven from the [official Apache website](https://maven.apache.org/download.cgi) or install it via a package manager.

## Installation

Follow these steps to set up and run the project locally:

### 1. Clone the Repository

Clone the project repository to your local machine using the following command:

```bash
git clone https://github.com/safeqr/backend-springboot.git
cd backend-springboot
```

### 2. Verify Java and Maven Installation

Ensure that Java 17 and Maven are installed and available on your system by running the following commands:

```bash
java -version
mvn -version
```

You should see output indicating that Java 17 and Maven 3.6+ are installed.

### 3. Install Project Dependencies

Navigate to the root directory of the project (if you haven't already) and run the following command to clean the project and install all necessary dependencies:

```bash
mvn clean install
```

This command will:

- **Clean**: Remove any previously compiled files.
- **Install**: Download all required dependencies as defined in the `pom.xml` file and compile the project.

### 4. Run the Application

Once the dependencies are installed, you can run the application with the following command:

```bash
mvn spring-boot:run
```

This will start the Spring Boot application, and you should see the application logs in the terminal. By default, the application will be available at `http://localhost:8080`.

## Additional Information

- **Configuration**: Any necessary configurations can be adjusted in the `application.properties` or `application.yml` files located in the `src/main/resources` directory.
- **Building the Project**: To build a standalone JAR file, you can use `mvn package`, which will generate a JAR file in the `target` directory.
