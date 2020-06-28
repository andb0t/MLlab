---
layout: page
title: Classification
subtitle: Naive Bayes
---

This algorithm bases on the assumption of mutually uncorrelated features. It uses Bayes' theorem to infer probabilities for each instance to belong to a specific class. The model parameters for the class-specific feature likelihoods are inferred from the training feature vectors.

```scala
val clf = new NaiveBayesClassifier()
```

The application of the algorithm to the diagonal dataset shows a bias. This is due to the fact, that the assumption of Gaussian distributed features for each class does not hold here.

![Linear test]({{ "/img/clf_NaiveBayes_clf_test_linear.png" | relative_url }})


Here is the example using the circular dataset, analyzed under addition of the quadratic feature terms. The algorithm performs very well.

```scala
val clf = new NaiveBayesClassifier(degree=2)
```

![Nonlinear test]({{ "/img/clf_NaiveBayes_clf_test_nonlinear.png" | relative_url }})

This shows that the naive Bayes' algorithm provides good results in many cases, despite the limits of the naive assumption. It is especially efficient for highly dimensional datasets with little training data, since it treats all feature instances separately.
