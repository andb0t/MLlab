import subprocess


algoDict = {'clf': ['Random',
                    'kNN',
                    'DecisionTree',
                    'Perceptron',
                    'NeuralNetwork',
                    'SVM',
                    'LogisticRegression'],
            'reg': ['Random',
                    'Linear']}

for task, algorithms in algoDict.items():
    for algo in algorithms:
        print('Now running', task, algo)
        subprocess.call(['java',
                         '-jar',
                         'target/scala-2.11/mllab-assembly-0.1.0-SNAPSHOT.jar',
                         task,
                         algo])
