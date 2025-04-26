# Stage 1: Base image with JDK and Node.js capabilities
FROM eclipse-temurin:21-jdk-jammy as base

# Install Node.js, npm, AND dos2unix
# Use backslash (\) as the *very last* character for line continuation
RUN apt-get update && \
    apt-get install -y curl gnupg dos2unix && \
    # Download and execute NodeSource setup script for LTS Node.js
    curl -fsSL https://deb.nodesource.com/setup_lts.x | bash - && \
    # Install Node.js (includes npm)
    apt-get install -y nodejs && \
    # Clean up apt cache to reduce image size
    rm -rf /var/lib/apt/lists/*

# Set working directory inside the container
WORKDIR /app

# Copy essential build files first to leverage Docker layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the Maven wrapper executable AND fix line endings
RUN chmod +x mvnw && \
    dos2unix mvnw # Combine chmod and dos2unix, ensure \ is last

# OPTIONAL: Download Maven dependencies
# RUN ./mvnw dependency:go-offline

# Copy the rest of the application source code and configuration
COPY src src
COPY package.json package-lock.json ./
COPY .gitignore .
COPY .prettierrc.json .
COPY eclipse-formatter.xml .
COPY LICENSE.md .
COPY tsconfig.json .
COPY vite.config.ts .
COPY types.d.ts .

# Install Node.js dependencies
RUN npm install

# Expose the port
EXPOSE 8080

# The command to run the application IN DEVELOPMENT MODE
CMD ["./mvnw", "spring-boot:run"]