# ---------- BUILD STAGE ----------
FROM node:22.6.0-alpine AS builder

ARG NPM_COMMAND
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install
COPY . .
RUN npm run ${NPM_COMMAND}


# ---------- PRODUCTION STAGE ----------
FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
