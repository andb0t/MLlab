---
layout: page
title: Regression
subtitle: Multilayer Neural Network
---

Using an output layer with a single neuron, a neural network can be used for data regression.

```scala
val reg = new NeuralNetworkRegressor()
```

This is the application of a neural network regression algorithm to the linear dataset

![Linear test]({{ "/img/reg_NeuralNetwork_reg_test_linear.png" | relative_url }})

Without further hyperparameter tuning, the same network can also predict the cubic dataset reasonably well.

![Nonlinear test]({{ "/img/reg_NeuralNetwork_reg_test_nonlinear.png" | relative_url }})
