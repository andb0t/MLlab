# Use an official open jdk runtime as a parent image
FROM openjdk:8

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
ADD . /app

ENV SBT_VERSION 1.0.2
ENV DISPLAY :99

# Install any needed packages
RUN \
  curl -L -o sbt-$SBT_VERSION.deb http://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

RUN sbt compile

RUN apt-get install -y xvfb
RUN chmod +x init.sh

# Make port 80 available to the world outside this container
# EXPOSE 80

# Define environment variable
# ENV NAME World

# Run app.py when the container launches
CMD ./init.sh && sbt "run --help"
