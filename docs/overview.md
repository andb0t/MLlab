---
layout: page
title: Overview
subtitle: Implemented algorithms
---

These webpages give some impressions of the usage and what the implemented algorithms can perform.
Please consult the [Scaladoc API]({{ "/api/index.html" | relative_url }}) for detailed and up-to-date information on the algorithms, e.g. the extensive list of implemented hyper parameters.

Individual pages describe the respective algorithms in more detail and provide examples.

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

### Clustering
Clustering or unsupervised classification algorithms are applied to data without or very little or insufficient training data.
They are meant to find patterns in the data with a minimal amount of domain knowledge required to steer the algorithm.

The basic usage of clustering algorithms is as follows:

```scala
/**
 * @param X_train List of feature vectors for training
 * @param X_test List of feature vectors for prediction
 */
val clu = new Clustering()  // optional hyperparameters as arguments
clu.train(X_train)  // perform the training
val y_pred = clu.predict(X_test)  // make a prediction

```
