# ExpertConnect

ExpertConnect is a platform that connects experts with clients for one-on-one meetings, featuring Zoom integration and email notifications.

## Prerequisites

Before you begin, ensure you have the following installed:

1. **Java 17 or later**
   - Download from: https://adoptium.net/
   - Verify installation: `java -version`

2. **Docker Desktop**
   - Download from: https://www.docker.com/products/docker-desktop
   - For Mac with Apple Silicon (M1/M2): Download the Apple Silicon version
   - For Mac with Intel chip: Download the Intel chip version
   - For Windows: Download the Windows version
   - For Linux: Follow instructions at https://docs.docker.com/engine/install/

3. **Postman** (for API testing)
   - Download from: https://www.postman.com/downloads/

## Setup Instructions

### 1. Install Docker Desktop

1. Download Docker Desktop from the link above
2. Install the application
3. Start Docker Desktop
4. Wait for Docker to start (you'll see the whale icon in your menu bar)
5. Verify Docker is running:
   ```bash
   docker --version
   docker ps
   ```

### 2. Clone and Setup the Project

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd expertConnect
   ```

2. Make the scripts executable:
   ```bash
   chmod +x build.sh run.sh
   ```

### 3. Run the Application

1. Start the application:
   ```bash
   ./run.sh
   ```

   This script will:
   - Create a Docker network
   - Set up a PostgreSQL database with persistent storage
   - Build the application
   - Start the application container

2. Wait for the application to start (this may take a few minutes on first run)
3. You should see a message: "Application is starting up..."
4. The application will be available at: http://localhost:8080/expert-connect

### 4. Verify the Setup

1. Check if the containers are running:
   ```bash
   docker ps
   ```
   You should see two containers:
   - expertconnect-app
   - expertconnect-postgres

2. View application logs:
   ```bash
   docker logs -f expertconnect-app
   ```

### 5. Test the Application

1. Open Postman
2. Create a new user:
   ```
   POST http://localhost:8080/expert-connect/v1/users/register
   Content-Type: application/json

   {
     "name": "John Doe",
     "email": "john@example.com",
     "password": "password123",
     "role": "EXPERT"
   }
   ```

3. Create an expert profile:
   ```
   POST http://localhost:8080/expert-connect/v1/expert
   Content-Type: application/json

   {
     "email": "john@example.com",
     "expertise": ["Java", "Spring Boot"],
     "hourlyRate": 100,
     "bio": "Experienced developer",
     "availability": "{\"monday\": [\"9:00-17:00\"]}"
   }
   ```

## Troubleshooting

### Common Issues

1. **Docker not running**
   - Start Docker Desktop
   - Wait for the whale icon to stop animating

2. **Port 8080 already in use**
   - Stop any existing containers:
     ```bash
     docker stop expertconnect-app
     docker rm expertconnect-app
     ```
   - Or change the port in `application.properties`

3. **Database connection issues**
   - Check if PostgreSQL container is running:
     ```bash
     docker ps | grep expertconnect-postgres
     ```
   - View PostgreSQL logs:
     ```bash
     docker logs expertconnect-postgres
     ```

### Useful Commands

1. Stop all containers:
   ```bash
   docker stop expertconnect-app expertconnect-postgres
   ```

2. Remove all containers:
   ```bash
   docker rm expertconnect-app expertconnect-postgres
   ```

3. Remove the database volume (WARNING: This will delete all data):
   ```bash
   docker volume rm expertconnect-postgres-data
   ```

4. View application logs:
   ```bash
   docker logs -f expertconnect-app
   ```

## API Documentation

The application exposes the following main endpoints:

1. User Management:
   - POST `/v1/users/register` - Register a new user
   - POST `/v1/users/login` - User login

2. Expert Management:
   - POST `/v1/expert` - Create expert profile
   - GET `/v1/expert/{id}` - Get expert details

3. Meeting Management:
   - POST `/v1/meetings` - Schedule a meeting
   - GET `/v1/meetings` - List meetings

For detailed API documentation, refer to the Swagger UI at:
http://localhost:8080/expert-connect/swagger-ui/index.html

## Development

### Configuration Files

1. `application.properties`: Main configuration file
2. `build.sh`: Script to build the application and set up Docker
3. `run.sh`: Script to run the application in Docker
4. `Dockerfile`: Docker configuration for the application

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
6. Please raise issues if you see any bugs/ improvements/ facing trouble starting the application.