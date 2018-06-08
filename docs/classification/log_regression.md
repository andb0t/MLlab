---
layout: page
title: Logistic Regression Classification
---

Classification via logistic regression is a standard algorithm for binary linear classification. It optimizes the parameters of a linear hyperplane in the feature space to separate regions of different label classes. For this, the linear transformation of the instance vectors is further transformed with the logistic sigmoid function to obtain probabilities of belonging to a specific class. It can be instantiated as follows:

```scala
val clf = new LogisticRegressionClassifier()
```

The output of the algorithm using the standard settings trained on a training set and applied to a test set both with a shifted diagonal as class separator is shown below.

![Linear test]({{ "/img/clf_LogisticRegression_clf_test_linear.png"}})

Using a trick, the internal addition of higher orders of the features, linear algorithms can be applied to non-linear datasets.

```scala
val clf = new LogisticRegressionClassifier(degree=2)
```
This adds powers up until quadratic powers of the feature to the feature vector. Here is an example of the same algorithm applied to circular data. The classifier can now solve the corresponding classification task.

![Nonlinear test]({{ "/img/clf_LogisticRegression_clf_test_nonlinear.png"}})
