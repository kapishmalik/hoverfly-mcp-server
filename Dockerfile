FROM spectolabs/hoverfly:latest

# Install OpenJDK JRE 17
RUN apk update && \
    apk add --no-cache openjdk17-jre && \
    # Create a new user and group
    addgroup -S hoverflymcp && \
    adduser -S -G hoverflymcp -h /opt/hoverfly-mcp hoverflymcp && \
    # Create application directory
    mkdir -p /opt/hoverfly-mcp && \
    chown -R hoverflymcp:hoverflymcp /opt/hoverfly-mcp

# Set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copy the JAR
COPY target/hoverfly-mcp-server-0.0.1-SNAPSHOT.jar /opt/hoverfly-mcp/hoverfly-mcp.jar

# Set ownership of the JAR
RUN chown hoverflymcp:hoverflymcp /opt/hoverfly-mcp/hoverfly-mcp.jar

# Switch to the non-root user
USER hoverflymcp

# Set working directory
WORKDIR /opt/hoverfly-mcp

# Run the jar
ENTRYPOINT ["java", "-jar", "hoverfly-mcp.jar"]