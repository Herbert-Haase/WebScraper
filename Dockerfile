FROM sbtscala/scala-sbt:eclipse-temurin-jammy-21.0.2_13_1.9.9_2.13.13

RUN apt-get update && apt-get install -y \
    libxrender1 libxtst6 libxi6 libgtk-3-0 libglu1-mesa libxxf86vm1 libasound2 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy only the files that define dependencies
COPY build.sbt /app/
COPY project /app/project/

# Run update to download jars into the image layer
RUN sbt update

COPY . /app

CMD ["sbt"]
