# MLlab

[![Build status](https://travis-ci.org/andb0t/MLlab.svg?branch=master)](https://travis-ci.org/andb0t)

This is an experimental platform-independent machine learning library. Born from the desire to implement modern machine learning algorithms by hand, this project has grown considerably and provides now basic algorithms for various classification, regression and clustering tasks.

For further information on implemented algorithms and usage examples, please consult the project's [website](https://andb0t.github.io/MLlab).


## Implemented algorithms
Please consult the [API](https://andb0t.github.io/MLlab/api/index.html) for detailed and up-to-date information on the algorithms, e.g. the implemented hyper parameters.

### Classification
- [x] random
- [x] logistic regression
- [x] perceptron
- [x] k-nearest neighbors
- [x] decision tree
- [x] multilayer neural network
- [x] naive Bayes
- [x] boosted decision tree
  - [ ] AdaBoost
- [ ] SVM with linear and non-linear kernel (see [here](http://alex.smola.org/teaching/pune2007/pune_3.pdf) or [here](https://oceandatamining.sciencesconf.org/conference/oceandatamining/program/OBIDAM14_Canu.pdf))
- [ ] random forest
- [ ] convolutional neural network
- [ ] recurrent neural network

### Regression
- [x] random
- [x] linear regression
- [x] decision tree
- [x] Bayes
- [x] k-nearest neighbors
- [X] neural network regression
- [ ] SVM

### Clustering
- [x] k-means
- [x] self-organizing map

### Misc
- [x] extension of linear models to polynomial dependencies via feature transformation




## Installation
This app uses [sbt](https://www.scala-sbt.org/index.html) as build tool. Installation for Ubuntu:

```shell
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get install sbt
# sbt new sbt/scala-seed.g8  # set up a dummy project
```


## Execution

From sbt:
```shell
cd mllab
sbt
# compile and run the app, use ~ for automatic updates and recompilation
[~]run # run the default random classifier
run --help  # get more information on options and commands
test  # compile and execute tests  
compile  # only compile the app
console  # start scala console for this project
```

Create test data in the `data` directory:
```bash
python3 bin/create_data.py --reg linear  # create dummy regression data
python3 bin/create_data.py --clf circles  # create dummy classification data
```

Then run MLlab on it, e.g. with `sbt run --clf DecisionTree --data data`


## Development

### Create executable jar
This will package everything in a fat jar, using [sbt-assembly](https://github.com/sbt/sbt-assembly).

```shell
sbt assembly
```

Run the compiled jar e.g. with python like in `examples/run_jar.py`

### Create API documentation
```shell
sbt doc
```

### Style check and linter
This will check the code style, using [scalastyle](http://www.scalastyle.org/) and [Linter Compiler Plugin](https://github.com/HairyFotr/linter).

```shell
sbt
scalastyle  # style check
[compile, run]  # linter runs as compilation hook
```


## Contribution
Everyone is welcome to contribute! I would especially appreciate support in
* a static webpage for the project
* a GUI to select datasets, algorithms and hyper parameters, run the analysis
  and do grid hyper parameter optimization

PRs and issues are always welcome.


### Testing
Please write unit tests for your methods.

### Contributors
This code is developed and maintained by me. List of contributors in alphabetical order:
* no one yet :grin:
* maybe you? :satisfied:


## Some useful links

### Some remarks about scala
* [Scala API](https://www.scala-lang.org/api/current/)
* [Java API](https://docs.oracle.com/javase/8/docs/api/)
* [Style guide](https://docs.scala-lang.org/style/overview.html)
* [Quick syntax lookup](https://www.tutorialspoint.com/scala/index.htm)
* [Cheatsheet](https://docs.scala-lang.org/cheatsheets/)
* [Reactive cheatsheet](https://github.com/sjuvekar/reactive-programming-scala/blob/master/ReactiveCheatSheet.md)
* [Extensive cheatsheet](https://github.com/lampepfl/progfun-wiki/blob/gh-pages/CheatSheet.md)
* [Exercises](https://www.scala-exercises.org/)

### Some remarks about spark
* [Spark API](https://spark.apache.org/docs/2.2.0/api/scala/index.html)
* [Spark examples](https://github.com/apache/spark/tree/master/examples/src/main/scala/org/apache/spark/examples)
