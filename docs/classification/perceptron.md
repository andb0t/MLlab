---
layout: page
title: Classification
subtitle: Perceptron
---

The perceptron algorithm tries to find any linear boundary to perfectly separate the classes. It updates the weights of the linear model sequentially for each training instance in each training step in the direction of correct classification. This is repeated until a chosen precision or maximum of iterations is reached.

```scala
val clf = new PerceptronClassifier()
```
![Linear test]({{ "/img/clf_Perceptron_clf_test_linear.png" | relative_url }})

Applying the same feature transformation as above, also this linear algorithm can solve the circular data task.

```scala
val clf = new PerceptronClassifier(degree=2)
```

![Nonlinear test]({{ "/img/clf_Perceptron_clf_test_nonlinear.png" | relative_url }})
