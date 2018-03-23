# MLlab

[![Build status](https://travis-ci.org/andb0t/MLlab.svg?branch=master)](https://travis-ci.org/andb0t)

This is an experimental platform-independent machine learning library. Born from the desire to implement modern machine learning algorithms by hand, this project has grown considerably and provides now basic algorithms for various classification and regression tasks.


## Implemented algorithms
Please consult the [API](https://andb0t.github.io/MLlab/api/index.html) for detailed and up-to-date information on the algorithms, e.g. the implemented hyper parameters.

### Classification
- [x] random
- [x] k-nearest neighbors
- [x] decision tree
- [x] perceptron
- [x] multilayer neural network
- [x] logistic regression
- [ ] SVM with linear and non-linear kernel (see [here](http://alex.smola.org/teaching/pune2007/pune_3.pdf) or [here](https://oceandatamining.sciencesconf.org/conference/oceandatamining/program/OBIDAM14_Canu.pdf))
- [ ] naive Bayesian classification

### Regression
- [x] random
- [x] linear regression
- [x] polynomial regression with linear kernel
- [ ] naive Bayesian regression

### Misc
- [x] extension of linear models to polynomial dependencies via feature transformation


## Examples of usage
Machine learning algorithms and [APIs](https://andb0t.github.io/MLlab/api/index.html) may seem quite abstract. This section gives you some examples of how the algorithms can be applied.

### Regression

#### Linear regression
MLlab provides several algorithms for regression tasks. The most basic is the linear regression. It is designed to fit data with a underlying linear dependencies on an arbitrary dimensional set of features. This is a basic example for the case of a 1D feature vector and a 1D label. The training and testing data have been drawn from a linear function with Gaussian noise.

![Linear regression](./linear_regression_example.png)

Using a trick, the internal addition of higher orders of the features, linear algorithms can be applied to non-linear datasets. This is an example of the same algorithm applied to 3D data, where powers of the feature have been added to the feature vector.

![Linear regression](./linear_regression_cubic_example.png)

### Classification

<!-- ## Algorithm details

### Linear regression
### Linear regression
### Linear regression
### Linear regression
### Linear regression -->
