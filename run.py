import numpy as np
import matplotlib.pyplot as plt
from __builtin__ import range
import gmm
import draw
import pylab as lab

# # of samples
N = 100

# # of clusters
K = 2

# dimension
D = 2

# prior probabilities
P = np.random.random((1, K))
P = P / np.sum(P)
# miu [0,10)
mean = np.random.random((K, D)) * 2;
# for i in range(K):
# 	for j in range(D):
# 		mean[i, j] = i * 2 
# covariance matrix
cov = 2 * np.random.random((K, D, D)) - 1
# cov = np.zeros((K, D, D)) 
for i in range(0, K):
	for j in range(0, D):
		for k in range(j, D):
			cov[i, j, k] = cov[i, k, j]
	cov[i, j, j] = np.random.random();

for i in range(0, K):
	while not np.all(np.linalg.eigvals(cov[i, :, :]) > 0):
		cov[i, 0, 1] = cov[i, 0, 1] / 2
		cov[i, 1, 0] = cov[i, 1, 0] / 2
		cov[i, 0, 0] = cov[i, 0, 0] + 1
		cov[i, 1, 1] = cov[i, 1, 1] + 1

# generate tringing set
X = np.zeros((N, D))
label_for_plot = np.zeros(N)
for i in range(N):
	ith = np.random.randint(K);
	X[i, :] = np.random.multivariate_normal(mean[ith, :], cov[ith, :, :], 1)
	label_for_plot[i] = ith

xmin = np.min(X[:, 0])
xmax = np.max(X[:, 0])
ymin = np.min(X[:, 1])
ymax = np.max(X[:, 1])

[prior, mean_train, cov_train, label] = gmm.train(X, K, label_for_plot)

plt.figure('final result')
plt.scatter(X[:, 0], X[:, 1], c=label , cmap='cool')
for i in range(K):
	x1, x2 = draw.gauss_ellipse_2d(mean_train[i, :], cov_train[i, :, :])
	plt.plot(x1, x2, 'k')
lab.savefig('final result.png')


plt.figure('real class and real gaussian distribution')
plt.scatter(X[:, 0], X[:, 1], c=label_for_plot , cmap='cool')
for i in range(K):
	x1, x2 = draw.gauss_ellipse_2d(mean[i, :], cov[i, :, :])
	plt.plot(x1, x2, 'k')
lab.savefig('real class and real gaussian distribution.png')


plt.show()
