# ExpertConnect

ExpertConnect is a platform that connects experts with clients for one-on-one meetings, featuring Zoom integration, email notifications, and AI-powered chat and summarization.

## Prerequisites

Before you begin, ensure you have the following installed:

1. **Java 17 or later**
   - Download from: https://adoptium.net/
   - Verify installation: `java -version`

2. **Python 3.11 or later**
   - Download from: https://www.python.org/downloads/
   - Verify installation: `python --version`

3. **Docker Desktop**
   - Download from: https://www.docker.com/products/docker-desktop
   - For Mac with Apple Silicon (M1/M2): Download the Apple Silicon version
   - For Mac with Intel chip: Download the Intel chip version
   - For Windows: Download the Windows version
   - For Linux: Follow instructions at https://docs.docker.com/engine/install/

4. **Postman** (for API testing)
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

2. Create a `secrets.sh` file in the root directory with your API keys:
   ```bash
   # OpenAI API Key
   export OPENAI_API_KEY="your-openai-api-key"
   
   # AWS Credentials
   export AWS_ACCESS_KEY_ID="your-aws-access-key"
   export AWS_SECRET_ACCESS_KEY="your-aws-secret-key"
   export AWS_REGION="your-aws-region"
   export AWS_S3_BUCKET="your-s3-bucket"
   ```


### 3. Run the Application

1. Start the application:
   ```bash
   ./run.sh
   ```

   This script will:
   - Source your secrets from `secrets.sh`
   - Create a Docker network
   - Set up a PostgreSQL database with persistent storage
   - Build the application
   - Start Python services (chat, embedding, and summarization)
   - Start the application container

2. Wait for the application to start (this may take a few minutes on first run)
3. You should see messages indicating:
   - Python services starting
   - Application container starting
   - "Application is starting up..."
4. The application will be available at: http://localhost:8080/expert-connect

### 4. Verify the Setup

1. Check if the containers are running:
   ```bash
   docker ps
   ```
   You should see two containers:
   - expertconnect-app
   - expertconnect-postgres

2. Check if Python services are running:
   ```bash
   ps aux | grep uvicorn
   ```
   You should see three Python services:
   - Chat service (port 8001)
   - Embedding service (port 8002)
   - Summarization service (port 8003)

3. View application logs:
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
     "name": "Akshay AS",
     "email": "acchu@example.com",
     "password": "password123",
     "role": "EXPERT"
   }
   ```

3. Create an expert profile:
   ```
   POST http://localhost:8080/expert-connect/v1/expert
   Content-Type: application/json

   {
     "email": "acchu@example.com",
     "expertise": ["Java", "Spring Boot"],
     "hourlyRate": 100,
     "bio": "Experienced developer",
     "availability": "{\"monday\": [\"9:00-17:00\"]}"
   }
   ```

4. Test the chat service:
   ```
   POST http://localhost:8001/chat
   Content-Type: application/json

   {
     "userId": "1",
     "message": "Hello, wassuupppppp?"
   }
   ```

## Troubleshooting

### Common Issues

1. **Docker not running**
   - Start Docker Desktop
   - Wait for the whale icon to stop animating

2. **Port conflicts**
   - Stop any existing containers:
     ```bash
     docker stop expertconnect-app
     docker rm expertconnect-app
     ```
   - Kill any running Python services:
     ```bash
     pkill -f uvicorn
     ```
   - Or change the ports in `application.properties` and Python service files

3. **Database connection issues**
   - Check if PostgreSQL container is running:
     ```bash
     docker ps | grep expertconnect-postgres
     ```
   - View PostgreSQL logs:
     ```bash
     docker logs expertconnect-postgres
     ```

4. **Python service issues**
   - Check if Python services are running:
     ```bash
     ps aux | grep uvicorn
     ```
   - Check Python service logs in the terminal where you ran `run.sh`
   - Verify OpenAI API key in `secrets.sh`

### Useful Commands

1. Stop all containers and services:
   ```bash
   docker stop expertconnect-app expertconnect-postgres
   pkill -f uvicorn
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

4. AI Services:
   - POST `http://localhost:8001/chat` - Chat with AI
   - POST `http://localhost:8002/generate-embedding` - Generate embeddings
   - POST `http://localhost:8003/summarize-chat` - Summarize conversations

For detailed API documentation, refer to the Swagger UI at:
http://localhost:8080/expert-connect/swagger-ui/index.html

## Development

### Configuration Files

1. `application.properties`: Main configuration file
2. `build.sh`: Script to build the application and set up Docker
3. `run.sh`: Script to run the application in Docker and Python services
4. `start_services.sh`: Script to start Python services
5. `Dockerfile`: Docker configuration for the application
6. `secrets.sh`: Environment variables for API keys (not in version control)

### Python Services

The application includes three Python services:

1. Chat Service (port 8001):
   - Handles AI-powered chat interactions
   - Uses OpenAI's GPT model
   - File: `src/main/java/com/uci/expertConnect/python_services/chatting_service.py`

2. Embedding Service (port 8002):
   - Generates embeddings for text
   - Uses OpenAI's embedding model
   - File: `src/main/java/com/uci/expertConnect/python_services/generate_embedding_service.py`

3. Summarization Service (port 8003):
   - Summarizes conversations
   - Uses OpenAI's GPT model
   - File: `src/main/java/com/uci/expertConnect/python_services/summarize_convo_service.py`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
6. Please raise issues if you see any bugs/ improvements/ facing trouble starting the application.