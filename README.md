# MLlab

## Introduction
This is a work-in-progress basic machine learning library.

## Implemented algorithms

### Classification
* random
* k-nearest neigbours
* decision tree
* perceptron

### Todo
* regression
* linear support vector machine

## Installation

### Set up sbt
This app uses [sbt](https://www.scala-sbt.org/index.html) as build tool.

```shell
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt
# sbt new sbt/scala-seed.g8  # set up a dummy project
```

### Run the app
```shell
cd mllab
sbt
[~]run  # compile and run the app, use ~ for automatic updates and recompilation
test  # compile and execute tests  
compile  # only compile the app
console  # start scala console for this project
```

### Create executable app
This will package everything in a fat jar, using [sbt-assembly](https://github.com/sbt/sbt-assembly).

```shell
sbt
assembly
```

### Style check and linter
This will check the code style, using [scalastyle](http://www.scalastyle.org/) and [Linter Compiler Plugin](https://github.com/HairyFotr/linter).

```shell
sbt
scalastyle  # style check
[compile, run]  # linter runs as compilation hook
```

## Some remarks about scala
Some useful links:
* [Scala API](https://www.scala-lang.org/api/current/)
* [Style guide](https://docs.scala-lang.org/style/overview.html)
* [Quick syntax lookup](https://www.tutorialspoint.com/scala/index.htm)
* [Cheatsheet](https://docs.scala-lang.org/cheatsheets/)
* [Extensive cheatsheet](https://github.com/lampepfl/progfun-wiki/blob/gh-pages/CheatSheet.md)

## Some remarks about spark
* [Spark API](https://spark.apache.org/docs/2.2.0/api/scala/index.html)
* [Spark examples](https://github.com/apache/spark/tree/master/examples/src/main/scala/org/apache/spark/examples)
