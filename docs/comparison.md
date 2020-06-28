---
layout: page
title: Comparison
subtitle: Performance and purpose of algorithms
---

The following table lists the different algorithms and gives some basic information about them. "O" denotes the big "O" notation of training and prediction complexity, separated by a semi-colon.

Algorithm               | Type            | Supervised? | Training                  | O      | Online?   
---------               | -----           | ----------- | -------                   | ----   | -------
Logistic Regression     | Classification  | Yes         | Logistic cost + GD        | n; m   | No
Perceptron              | Classification  | Yes         | Update linear boundary    | n; m   | Yes
Naive Bayes             | Classification  | Yes         | Likelihood determination  | n; m   | No
SVM                     | Classification  | Yes         | Kernel trick              | n; m   | No
Decision Trees          | Classification  | Yes         | Iteration                 | n; m   | No
Neural Networks         | Classification  | Yes         | Backpropagation           | n; m   | No
Linear Regression       | Regression      | Yes         | Normal Eqn.               | n; m   | No
Bayes                   | Regression      | Yes         | Max likelihood posteriors | n; m   | No
k-Nearest-Neighbors     | Regression      | Yes         | -                         | 0; n*m | Yes
Decision Trees          | Regression      | Yes         | Iteration                 | n; m   | No
Neural Networks         | Regression      | Yes         | Backpropagation           | n; m   | No
k-Means                 | Clustering      | No          | Iteration                 | n; m   | No
Hierarchical            | Clustering      | No          | Merge instances           | 0; n^2 | No
Self-Organizing Map     | Clustering      | No          | Shift nodes               | n; m   | No
PCA                     | Dim. Reduction  | No          | Transform to "Eigenspace" | n; m   | No

### Abbreviations

| Acronym          | Meaning                      |
| ---------        | -----                        |
| n                | Number of training instances |
| m                | Number of test instances     |
| GD               | Gradient Descent             |
