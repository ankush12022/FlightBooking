# Flight Booking Frontend

Basic React frontend for the Spring Boot Flight Booking Management System.

## Backend URL

The frontend expects backend to run on:

```text
http://localhost:6996
```

## Run

```bash
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

## Important Spring Boot CORS

In `SecurityConfig.java`, add:

```java
.cors(cors -> {})
```

Also allow the frontend origin:

```java
.allowedOrigins("http://localhost:5173")
```

## Features

- Register USER or ADMIN
- Login with JWT token
- ADMIN can add flights
- Search flights
- Book multiple tickets
- Available seats update after booking
