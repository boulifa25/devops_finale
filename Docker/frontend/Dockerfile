# Multi-stage build for Angular frontend

# 1) Build stage
FROM node:20-alpine AS build

WORKDIR /app

# Install dependencies
COPY ../../student-frontend/package*.json ./
RUN npm install

# Copy source and build
COPY ../../student-frontend/ ./
RUN npm run build -- --configuration=production

# 2) Runtime stage
FROM nginx:1.27-alpine

# Remove default nginx page
RUN rm -rf /usr/share/nginx/html/*

# Copy built Angular app
COPY --from=build /app/dist/student-frontend/browser/ /usr/share/nginx/html/

# Expose Angular port
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
