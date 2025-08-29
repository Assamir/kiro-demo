# Development Dockerfile for React application
FROM node:18-alpine

WORKDIR /app

# Install serve to serve the built application
RUN npm install -g serve

# Copy the pre-built application
COPY build ./build

# Expose port
EXPOSE 3000

# Serve the built application
CMD ["serve", "-s", "build", "-l", "3000"]