---
layout: page
title: Classification
subtitle: Random Forest
---

A random forest classifier uses a set of weak learners (in this case, classifiers), and trains them on a random subset of features (and, if desired, instances with replacement). The prediction is done by majority vote of the learners. The training and prediction are implemented in parallel, making full use of the available cores of the machine.


```scala
val clf = new RandomForestClassifier(depth=3, n_estimators=100, subSampleSize=0.2)
```

This is the application of the random forest algorithm to the circular dataset. Even with very weak individual learners (trees of depth 3), the circle can be identified reasonably well.

![Nonlinear test]({{ "/img/clf_RandomForest_clf_test_nonlinear.png" | relative_url }})
