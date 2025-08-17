# KeyKlash

KeyKlash is a real-time multiplayer typing game where you can challenge your friends to see who can type the fastest and most accurately.

## Features

* **Real-time multiplayer:** Race against your friends in real-time to see who can type a given text the fastest.
* **Game rooms:** Create public or private game rooms to play with your friends or other players.
* **Customizable text:** Practice your typing skills with a variety of texts, or add your own custom text for a unique challenge.

---

## Technologies Used

* **Backend:**
    * Java
    * Spring Boot
    * WebSocket
    * PostgreSQL
    * Valkey/Redis
* **Frontend:**
    * React
    * SockJS

---

## CI/CD Pipeline: From GitHub to Google Cloud Run

This project is set up with a CI/CD pipeline that automatically builds and deploys the application to Google Cloud Run whenever changes are pushed to the `main` branch. Here's how it works:

### 1. GitHub Actions

The CI/CD pipeline is triggered by a push to the main branch. A GitHub Actions workflow, defined in a `.github/workflows/main.yml` file, builds the application and pushes the resulting Docker image to Google Artifact Registry.

### 2. Google Artifact Registry

The Docker image is stored in Google Artifact Registry, a fully-managed Docker registry that provides secure and reliable storage for your container images.

### 3. Google Cloud Run

The application is deployed to Google Cloud Run, a fully-managed serverless platform that automatically scales your application up or down based on traffic. Cloud Run is a great choice for this application because it's easy to use, cost-effective, and provides a secure and reliable environment for running your application.
