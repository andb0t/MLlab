---
layout: page
title: Classification
subtitle: Decision Tree
---

A decision tree is based on a binary tree datastructure and applies a one dimensional cut-off based on one feature at each node. The cut-off threshold and the feature it is applied to are chosen to maximize a purity metric for the resulting split in the training data. The depth of the tree sets the number of binary decisions to be taken.

```scala
val clf = new DecisionTreeClassifier()
```

This is the application of a decision tree algorithm to the circular dataset. In the default configuration the number of decisions is not sufficient to appropriately detect the circle:

![Nonlinear test bad]({{ "/img/clf_DecisionTree_clf_test_nonlinear_bad.png" | relative_url }})

Increasing the tree depth roughly doubles the number of nodes. With this setting, the decision tree is able to classify the test data reasonably well.

```scala
val clf = new DecisionTreeClassifier(depth=4)
```

![Nonlinear test]({{ "/img/clf_DecisionTree_clf_test_nonlinear.png" | relative_url }})
