# Currency exchange service

This is a currency exchange service built using Spring boot
that allows users to retrieve exchange rates for supported currencies.
The application integrates with external API providers to fetch exchange rate data.

## Features

- Retrieve all supported currencies through REST API using a GET request.
- Add any currency identified by their ISO 4217 currency code
  through REST API using a POST request.
- Retrieve exchange rates for supported currency and provided amount
  through REST API using a GET request.
- All endpoints secured with basic authentication.
- Error handling for invalid currency codes.
- Swagger API documentation for easy testing.

## Technologies Used

- **Java**: Programming language used to build the application.
- **Spring Boot**: Framework for building REST API.
- **PostgreSQL**: Database for storing data.
- **Liquibase**: Database migration tool.
- **Docker-compose**: Containerization tool for database deployment.
- **Gradle**: Dependency management and build tool.

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://gitlab.godeltech.com/a.semenas/currency-exchange.git

2. **Set Up PostgreSQL**
Run PostgreSQL in a container:

    `docker-compose up -d`

3. **Configure Application Properties**:
   Update the application.properties file to connect to your PostgreSQL database.

4. **Build the Application**

5. **Run the Application**

## Accessing the API

All endpoints require **authorization and authentication**. Predefined users are:

- **ADMIN role**: Username `Ben`, Password `test123`
- **USER role**: Username `Peter`, Password `test123`

- GET All Currencies Endpoint

- **Retrieve all currencies by visiting:**  
  `http://localhost:8081/api/v1/currencies`

- **Add Currency Endpoint**  
  To add a currency, run the following cURL command (ADMIN role required):
  ```bash
  curl -X POST "http://localhost:8081/api/v1/currencies?currency=EUR" -u Ben:test123
  ```
- **GET currency exchange rates using provided currency code and amount**
  To get exchange rates, run the following cURL command:
  ```bash
  curl -X GET "http://localhost:8081/api/v1/exchange-rates?currency=EUR&amount=1.0" -u Peter:test123
  ```

- **Swagger UI**  
  Explore the API documentation using Swagger UI at:  
  `http://localhost:8081/swagger-ui/index.html`

### Docker Compose support

This project contains a Docker Compose file named `docker-compose.yml`.
In this file, the following services have been defined:

* postgres: [`postgres:latest`](https://hub.docker.com/_/postgres)
* pgadmin: [`dpage/pgadmin4:latest`](https://hub.docker.com/r/dpage/pgadmin4/)

To start the services, run:
- ```bash
  docker-compose up -d
