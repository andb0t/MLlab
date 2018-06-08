---
layout: page
title: Bayes Regression
---

Linear Bayesian regression is the Bayesian counterpart to the linear regression of the frequentist approach. It assumes prior probability density functions for the linear parameters and the posterior width and determines them from the maximum likelihood parameters using the posterior function, given the training data.

```scala
val reg = new BayesRegressor()
```

This is an application to the linear dataset. The assumed prior and posterior probabilities for the posterior width and the linear parameters are shown as well. Given the training data, the probabilities collapse to the expected values.

![Linear test]({{ "/img/reg_Bayes_reg_test_linear.png" | relative_url }})

![Bayes priors]({{ "/img/reg_Bayes_priors.png" | relative_url }}) ![Bayes posteriors]({{ "/img/reg_Bayes_posteriors.png" | relative_url }})

With the same trick of adding higher feature orders, linear Bayesian regression can be applied to datasets with polynomial truth.

```scala
val reg = new BayesRegressor(degree=3)
```

![Nonlinear test]({{ "/img/reg_Bayes_reg_test_nonlinear.png" | relative_url }})
