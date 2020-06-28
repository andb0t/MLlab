---
layout: page
title: Regression
subtitle: k-Nearest Neighbors
---

This algorithm predicts the true value of an instance from the mean of the values of the k nearest instances in the training set.

```scala
val clf = new kNNRegressor(k=40)
```
Here is its performance on the cubic dataset.

![Nonlinear test]({{ "/img/reg_kNN_reg_test_nonlinear.png" | relative_url }})
