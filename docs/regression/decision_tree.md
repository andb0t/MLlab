---
layout: page
title: Regression
subtitle: Decision Tree
---

The decision tree regression determines the predicted value associated to a given test instance from the average of the training labels at the corresponding decision node. The decision threshold is determined from the minimum quadratic sum of the label distance. Let's try a decision tree depth of 3 again.

```scala
val reg = new DecisionTreeRegressor(depth=3)
```

This is the application of a decision tree regression algorithm to the cubic dataset. The prediction is quite crude.

![Nonlinear bad test]({{ "/img/reg_DecisionTree_reg_test_nonlinear_bad.png" | relative_url }})

Increasing the tree depth increases the number of nodes by about factors of two. With this setting, the decision tree is able to fit the test data reasonably well.

```scala
val reg = new DecisionTreeRegressor(depth=6)
```

![Nonlinear test]({{ "/img/reg_DecisionTree_reg_test_nonlinear.png" | relative_url }})
