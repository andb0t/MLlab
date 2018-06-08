---
layout: page
title: Regression
subtitle: Linear Regression
---

MLlab provides several algorithms for regression tasks. The most basic is the linear regression. It is designed to fit data with an underlying linear dependencies on an arbitrary dimensional set of features.

```scala
val reg = new LinearRegressor()
```

This is a basic example for the case of a 1D feature vector and a 1D label. The training and testing data have been drawn from a linear function with Gaussian noise. The figures show the performance on the test set.

![Linear test]({{ "/img/reg_Linear_reg_test_linear.png" | relative_url }})

Using a trick, the internal addition of higher orders of the features, linear algorithms can be applied to non-linear datasets.

```scala
val reg = new LinearRegressor(degree=3)
```

This is an example of the same algorithm applied to data with a cubic dependence, where powers up until cubic powers of the feature have been added to the feature vector.

![Nonlinear test]({{ "/img/reg_Linear_reg_test_nonlinear.png" | relative_url }})
