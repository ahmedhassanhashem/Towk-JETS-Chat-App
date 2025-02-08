# Use the official MySQL image
FROM mysql:8.0

# Set environment variables for MySQL
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=chat_db
ENV MYSQL_USER=user
ENV MYSQL_PASSWORD=pass

# Copy the initialization script to the Docker container
COPY init.sql /docker-entrypoint-initdb.d/

# Expose the default MySQL port
EXPOSE 3306

# Command to run MySQL
CMD ["mysqld"]