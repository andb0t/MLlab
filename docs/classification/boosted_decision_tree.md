---
layout: page
title: Boosted Decision Tree Classification
---

A boosted decision tree (BDT) classifier consists of a series of decision trees (weak learners), which are trained in sequence. The default settings use the AdaBoost algorithm: each learner is assigned a weight, based on its classification performance. The instances are then assigned weights, which are updated depending on the correct or wrong classification and the corresponding learner weight. The weighted majority vote of all learners is taken as prediction.

```scala
val clf = new BoostedDecisionTreeClassifier(depth=4, n_estimators=100)
```

This is the application of the BDT algorithm to the circular dataset. The majority vote allows for deviations from the rectangular patterns, observed above.

![Nonlinear test]({{ "/img/clf_BoostedDecisionTree_clf_test_nonlinear.png" | relative_url }})
