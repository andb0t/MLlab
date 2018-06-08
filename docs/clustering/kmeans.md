---
layout: page
title: k-Means Clustering
---

This clustering algorithm starts with an arbitrary number random positions (centroids) and associates the closest instances with respect to some specified metric to the given cluster. It then reassigns the centroids to the mean positions of their cluster and repeats until no more change in association appears.

```scala
val clu = new kMeansClustering(k=3)
```

Below is a dataset with three distinct clusters of data. Using three centroids, the algorithm finds the correct association after some iterations. The evolution of the respective cluster means is shown as well.

![Centroids]({{ "/img/clu_kMeans_train_centroids_three.png" | relative_url }})
