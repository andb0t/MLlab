---
layout: page
title: Classification
subtitle: k-Nearest Neighbors
---

This algorithm determines predicts the label of an instance from the k (e.g. k=3) nearest instances in the training set, according to some specified metric (mostly Euclidian). It can in principle be applied to arbitrary dimensional and complex features, but has limited performance for large training sets and/or number of features.

```scala
val clf = new kNNClassifier()
```
Here is its performance on the circular dataset.

![Nonlinear test]({{ "/img/clf_kNN_clf_test_nonlinear.png" | relative_url }})
