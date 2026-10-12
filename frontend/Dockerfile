# Stage 1: Build the React SPA with Node.js
FROM node:20-alpine AS builder

WORKDIR /app

# Copy dependency manifests
COPY package.json package-lock.json ./

# Install clean production and development dependencies
RUN npm ci

# Copy source code and build production bundle
COPY . .
RUN npm run build

# Stage 2: Serve with lightweight, secure Nginx Alpine
FROM nginx:alpine

LABEL maintainer="AURA Luxury Jewelry Tech Team <tech@aurajewelry.com>"
LABEL description="Production Nginx Docker image for AURA Fine Jewelry Frontend"

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy built static assets from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
