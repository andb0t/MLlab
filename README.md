# MLlab

## Introduction
This is a work-in-progress basic machine learning library.

## Installation

### Set up sbt
This app uses [sbt](https://www.scala-sbt.org/index.html) as build tool.

```shell
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt
```

### Create dummy project
```shell
sbt new sbt/scala-seed.g8
```

### Run the app
```shell
cd mllab
sbt
[~]run  # run the app, use ~ for automatic updates and recompilation
[~]test  # execute tests  
```

### Create executable app
This will package everything in a fat jar, using [sbt-assembly](https://github.com/sbt/sbt-assembly).

```shell
sbt
assembly
```
