---
layout: page
title: Clustering
subtitle: Self-organizing map
---

A self-organizing map (SOM) consists of a mesh of connected nodes. In this case a planar layout has been chosen. An SOM can be used for classification, dimensionality reduction and, as shown here, for clustering. Every node is initialized with a random weight vector of the same dimensionality as the training instances. For every training instance, the best matching unit (BMU) is determined as the node with the smallest Euclidian distance to the training instance. Its and its neighbors' weights are shifted closer to the training instance. This process leads to a self-organized distribution of weights, where similar instances are assigned to neighboring BOMs.

```scala
val clu = new SelfOrganizingMapClustering(width=2, height=2)
```

Below is a dataset with three distinct clusters of data. Using the 2x2 node map initialized with random weight vectors, the algorithm finds the correct cluster association. The evolution of the nodes during training is shown as well.

![Centroids]({{ "/img/clu_SelfOrganizingMap_train_centroids.png" | relative_url }})
