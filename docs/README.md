# MLlab

[![Build status](https://travis-ci.org/andb0t/MLlab.svg?branch=master)](https://travis-ci.org/andb0t)

This is an experimental platform-independent machine learning library. Born from the desire to implement modern machine learning algorithms by hand, this project has grown considerably and provides now basic algorithms for various classification and regression tasks.

The library is written in [Scala](https://www.scala-lang.org/) and can therefore make use of the [JVM](https://java.com/), making it platform-independent. The library is documented in the [Scaladoc API](https://andb0t.github.io/MLlab/api/index.html).


#### Content
* [Implemented algorithms](#implemented-algorithms)
  * [Classification](#classification)
    * [Logistic Regression](#logistic-regression-classification)
    * [Perceptron](#perceptron-classification)
    * [Naive Bayes](#naive-bayes-classification)
    * [k-Nearest Neighbors](#k-nearest-neighbors-classification)
    * [Decision Tree](#decision-tree-classification)
    * [Multilayer Neural Network](#multilayer-neural-network-classification)
  * [Regression](#regression)
    * [Linear](#linear-regression)
    * [Bayes](#bayes-regression)
    * [Decision Tree](#decision-tree-regression)
* [Algorithm details](#algorithm-details)




## Implemented algorithms
This section gives some impressions of the usage and what the implemented algorithms can perform.
Please consult the [Scaladoc API](https://andb0t.github.io/MLlab/api/index.html) for detailed and up-to-date information on the algorithms, e.g. the extensive list of implemented hyper parameters.





### Classification

The basic usage of classifiers is as follows:

```scala
/**
 * @param X_train List of feature vectors for training
 * @param y_train List of labels
 * @param X_test List of feature vectors for prediction
 */
val clf = new Classifier()  // optional hyperparameters as arguments
clf.train(X_train, y_train)  // perform the training
val y_pred = clf.predict(X_test)  // make a prediction
val diag = clf.diagnostics  // obtain a map of metrics for algorithm training

```


#### Logistic Regression Classification
Classification via logistic regression is a standard algorithm for binary linear classification. It optimizes the parameters of a linear hyperplane in the feature space to separate regions of different label classes. For this, the linear transformation of the instance vectors is further transformed with the logistic sigmoid function to obtain probabilities of belonging to a specific class. It can be instantiated as follows:

```scala
val clf = new LogisticRegressionClassifier()
```

The application of the standard settings to a test dataset of a shifted diagonal as class separator is shown below.

<img src="logisticregression_classification_example.png" width="500">

Using a trick, the internal addition of higher orders of the features, linear algorithms can be applied to non-linear datasets.

```scala
val clf = new LogisticRegressionClassifier(degree=2)
```
This adds powers up until quadratic powers of the feature to the feature vector. Here is an example of the same algorithm applied to circular data. The classifier can now solve the corresponding classification task.

<img src="logisticregression_classification_quadratic_example.png" width="500">



#### Perceptron Classification
The perceptron algorithm tries to find any linear boundary to perfectly separate the classes. It updates the weights of the linear model sequentially for each training instance in each training step in the direction of correct classification. This is repeated until a chosen precision or maximum of iterations is reached.

```scala
val clf = new PerceptronClassifier()
```

<img src="perceptron_classification_example.png" width="500">

Applying the same feature transformation as above, also this linear algorithm can solve the circular data task.

```scala
val clf = new PerceptronClassifier(degree=2)
```

<img src="perceptron_classification_quadratic_example.png" width="500">



#### Naive Bayes Classification
This algorithm bases on the assumption of mutually uncorrelated features. It uses Bayes' theorem to infer probabilities for each instance to belong to each class. The model parameters for the feature likelihoods are inferred from the training feature vectors.

```scala
val clf = new NaiveBayesClassifier()
```

The application of the algorithm to the diagonal dataset shows a bias. This is due to the fact, that the assumption of Gaussian distributed features for each class does not hold here.

<img src="naivebayes_classification_example.png" width="500">


Here is the example using the circular dataset, analyzed under addition of the quadratic feature terms. The algorithm performs very well.

```scala
val clf = new NaiveBayesClassifier(degree=2)
```

<img src="naivebayes_classification_quadratic_example.png" width="500">

This shows that the naive Bayes' algorithm provides good results in many cases, despite the limits of the naive assumption. It is especially efficient for highly dimensional datasets with little training data, since it treats all feature instances separately.


#### k-Nearest Neighbors Classification
This algorithm determines predicts the label of an instance from the k (e.g. k=3) nearest instances in the training set, according to some specified metric (mostly Euclidian). It can in principle be applied to arbitrary dimensional and complex features, but has limited performance for large training sets and/or number of features.

```scala
val clf = new kNNClassifier()
```
Here is its performance on the circular dataset.

<img src="kNN_classification_circle_example.png" width="500">


#### Decision Tree Classification
A decision tree is based on a binary tree datastructure and applies a one dimensional cut-off based on one feature at each node. The cut-off threshold and the feature it is applied to are chosen to maximize a purity metric for the resulting split in the training data. The depth of the tree sets the number of binary decisions to be taken.

```scala
val clf = new DecisionTreeClassifier()
```

This is the application of a decision tree algorithm to the circular dataset. In the default configuration the number of decisions is not sufficient to appropriately detecting the circle:

<img src="decisiontree_classification_example.png" width="500">

Increasing the tree depth roughly doubles the number of nodes. With this setting, the decision tree is able to classify the test data reasonably well.

```scala
val clf = new DecisionTreeClassifier(depth=4)
```

<img src="decisiontree_classification_deep_example.png" width="500">


#### Multilayer Neural Network Classification
The implemented neural networks (NN) are feedforward NNs trained using the backpropagation algorithm. They are very versatile but require extensive tuning and experience for successful application.

```scala
val clf = new NeuralNetworkClassifier()
```

A naive application of the NN with the default settings to the circular dataset fails:

<img src="neuralnetwork_classification_default_example.png" width="500">

After increasing the number of neurons on the second layer to 16, the NN performs very well.

```scala
val clf = new NeuralNetworkClassifier(layers=List(2, 16, 2))
```

<img src="neuralnetwork_classification_example.png" width="500">





### Regression

The basic usage of regressors is as follows:

```scala
/**
 * @param X_train List of feature vectors for training
 * @param y_train List of labels
 * @param X_test List of feature vectors for prediction
 */
val reg = new Regressor()  // optional hyperparameters as arguments
reg.train(X_train, y_train)  // perform the training
val y_pred = reg.predict(X_test)  // make a prediction
val diag = reg.diagnostics  // obtain a map of metrics for algorithm training

```

#### Linear Regression
MLlab provides several algorithms for regression tasks. The most basic is the linear regression. It is designed to fit data with an underlying linear dependencies on an arbitrary dimensional set of features.

```scala
val reg = new LinearRegressor()
```

This is a basic example for the case of a 1D feature vector and a 1D label. The training and testing data have been drawn from a linear function with Gaussian noise.

<img src="linear_regression_example.png" width="500">

Using a trick, the internal addition of higher orders of the features, linear algorithms can be applied to non-linear datasets.

```scala
val reg = new LinearRegressor(degree=3)
```

This is an example of the same algorithm applied to data with a cubic dependence, where powers up until cubic powers of the feature have been added to the feature vector.

<img src="linear_regression_cubic_example.png" width="500">


#### Bayes Regression
Linear Bayesian regression is the Bayesian counterpart to the linear regression of the frequentist approach. It assumes prior probability density functions for the linear parameters and the posterior width and determines them from the maximum likelihood parameters using the posterior function, given the training data.

```scala
val reg = new BayesRegressor()
```

This is an application to the linear dataset. The assumed prior and posterior probabilities for the posterior width and the linear parameters are shown as well. Given the training data, the probabilities collapse to the expected values.

<img src="bayes_regression_example.png" width="500">

<img src="bayes_regression_priors.png" width="250"> <img src="bayes_regression_posteriors.png" width="250">

With the same trick of adding higher feature orders, linear Bayesian regression can be applied to datasets with polynomial truth.

```scala
val reg = new BayesRegressor(degree=3)
```

<img src="bayes_regression_cubic_example.png" width="500">


#### Decision Tree Regression
The decision tree regression determines the predicted value associated to a given test instance from the average of the training labels at the corresponding decision node. The decision threshold is determined from the minimum quadratic sum of the label distance. Let's try a decision tree depth of 3 again.

```scala
val reg = new DecisionTreeRegressor(depth=3)
```

This is the application of a decision tree regression algorithm to the cubic dataset. The prediction is quite crude.

<img src="decisiontree_regression_example.png" width="500">

Increasing the tree depth increases the number of nodes by about factors of two. With this setting, the decision tree is able to fit the test data reasonably well.

```scala
val reg = new DecisionTreeRegressor(depth=6)
```

<img src="decisiontree_regression_more_example.png" width="500">






## Algorithm details

This section explains the implemented algorithms in more depth. It is currently work in progress!
