# students-report-alkarim

[![Release](https://img.shields.io/badge/release-v1.0.0-blue.svg)](https://github.com/ichwansh03/report-alkarim-be/releases)
[![Build](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Quarkus](https://img.shields.io/badge/Quarkus-Framework-red)](https://quarkus.io)
[![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)](LICENSE)

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/students-report-alkarim-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Build with Docker

First, run the following command to generate jar file:

```shell script
./mvnw clean install
```

You can build the application Docker image using:

```shell script
docker build -f src/main/docker/Dockerfile.jvm -t report-student-alkarim .
```

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- SmallRye JWT ([guide](https://quarkus.io/guides/security-jwt)): Secure your applications with JSON Web Token
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)

[Related Hibernate with Panache section...](https://quarkus.io/guides/hibernate-orm-panache)


### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

## API Contract

### AuthResource (`/auth`)
- **POST** `/register`  
  Register a new user.  
  Request body: AuthRequest (JSON):
  ```json
  {
    "name": "John Doe",
    "clsroom": "10A",
    "gender": "male",
    "roles": "STUDENT",
    "regnumber": "123456",
    "password": "yourPassword"
  }
  ```
  Response: 201 Created, "user created"

- **PUT** `/update/{id}`  
  Update user by ID.  
  Request body: AuthRequest (JSON):
  ```json
  {
    "name": "John Doe",
    "clsroom": "10A",
    "gender": "male",
    "roles": "STUDENT",
    "regnumber": "123456",
    "password": "yourPassword"
  }
  ```
  Response: 200 OK, "user updated"

- **GET** `/class/{class}/roles/{roles}`  
  Get users by class and roles.  
  Response: 200 OK, List<User>

- **GET** `/user/{regnumber}`  
  Get user by registration number.  
  Response: 200 OK, User

- **GET** `/roles/{roles}`  
  Get users by roles.  
  Response: 200 OK, List<User>

- **POST** `/login`  
  Authenticate user and return JWT.  
  Request body: AuthRequest (JSON)  
  Response: 200 OK, AuthResponse (JSON with token)

---

### CategoryResource (`/category`)
- **GET** `/`  
  Get all categories.  
  Response: 200 OK, List<Category>

- **POST** `/create`  
  Create a new category.  
  Request body: CategoryRequest (JSON):
  ```json
  {
    "name": "Mathematics"
  }
  ```
  Response: 201 Created, "Category created"

- **DELETE** `/delete/{id}`  
  Delete category by ID.  
  Response: 200 OK, "Category deleted"

---

### QuestionResource (`/questions`)
- **POST** `/create`  
  Create a new question.  
  Request body: QuestionRequest (JSON):
  ```json
  {
    "question": "What is 2+2?",
    "category": "Mathematics",
    "target": "STUDENT",
    "options": "A,B,C,D"
  }
  ```
  Response: 201 Created, "Question created"

- **GET** `/target/{target}`  
  Get questions by target.  
  Response: 200 OK, List<Question>

- **GET** `/category/{category}`  
  Get questions by category.  
  Response: 200 OK, List<Question>

- **GET** `/category/{category}/target/{target}`  
  Get questions by category and target.  
  Response: 200 OK, List<Question>

- **PUT** `/update/{id}`  
  Update question by ID.  
  Request body: QuestionRequest (JSON):
  ```json
  {
    "question": "What is 2+2?",
    "category": "Mathematics",
    "target": "STUDENT",
    "options": "A,B,C,D"
  }
  ```
  Response: 200 OK, "Question updated"

- **DELETE** `/delete/{id}`  
  Delete question by ID.  
  Response: 200 OK, "Question deleted"

---

### ReportResource (`/reports`)
- **POST** `/create`  
  Create a new report.  
  Request body: ReportRequest (JSON):
  ```json
  {
    "category": "Mathematics",
    "content": "Report content here",
    "regnumber": "123456",
    "score": "95",
    "answer": "B"
  }
  ```
  Response: 201 Created, "report created"

- **GET** `/report/{regnumber}`  
  Get reports by user registration number.  
  Response: 200 OK, List<Report>

- **GET** `/report/{name}`  
  Get reports by user name.  
  Response: 200 OK, List<Report>

- **PUT** `/update/{id}`  
  Update report by ID.  
  Request body: ReportRequest (JSON):
  ```json
  {
    "category": "Mathematics",
    "content": "Report content here",
    "regnumber": "123456",
    "score": "95",
    "answer": "B"
  }
  ```
  Response: 200 OK, "report updated"

- **DELETE** `/delete/{id}`  
  Delete report by ID.  
  Response: 200 OK, "report deleted"
