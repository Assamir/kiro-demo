# Frontend Dockerfile placeholder
# This will be updated when implementing React components

FROM node:18-alpine

WORKDIR /app

# Create a placeholder index.html for now
RUN echo '<!DOCTYPE html><html><head><title>Insurance Backoffice</title></head><body><h1>Frontend Coming Soon</h1><p>React frontend will be implemented in later tasks.</p></body></html>' > index.html

# Simple HTTP server for placeholder
RUN npm install -g http-server

EXPOSE 3000

CMD ["http-server", "-p", "3000", "-a", "0.0.0.0"]