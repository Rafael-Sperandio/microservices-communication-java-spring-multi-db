FROM node:20
WORKDIR .
COPY package*.json ./
RUN yarn
COPY . .
EXPOSE 8080
CMD ["node", "app.js"]