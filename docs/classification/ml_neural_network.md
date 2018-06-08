---
layout: page
title: Multilayer Neural Network Classification
---

The implemented neural networks (NN) are feedforward NNs trained using the backpropagation algorithm. They are very versatile but require extensive tuning and experience for successful application.

```scala
val clf = new NeuralNetworkClassifier()
```

A naive application of the NN with the default settings to the circular dataset fails:

![Nonlinear failed test]({{ "/img/clf_NeuralNetwork_clf_test_nonlinear_fail.png" | relative_url }})

After increasing the number of neurons on the second layer to 16, the NN performs very well.

```scala
val clf = new NeuralNetworkClassifier(layers=List(2, 16, 2))
```

![Nonlinear test]({{ "/img/clf_NeuralNetwork_clf_test_nonlinear.png" | relative_url }})
