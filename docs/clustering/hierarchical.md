---
layout: page
title: Clustering
subtitle: Hierarchical
---

Hierarchical clustering algorithms merge instances one after the other, in order of distance according to a chosen metric. The clustering stops, when a specified number of merged instances is left.

```scala
val clu = new HierarchicalClustering(k=3)
```

Below is the result of the clustering applied to the dataset of three distinct point clouds.

![Centroids]({{ "/img/clu_Hierarchical_train_centroids_three.png" | relative_url }})
