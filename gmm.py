# train gmm model
# X is N * D, K is the number of clusters
# returns K gaussian model 
import numpy as np
import matplotlib.pylab as plt
import draw
import pylab as lab
def train(X, K, label_for_plot):
	[N, D] = np.shape(X)
	xmin = np.min(X[:, 0])
	xmax = np.max(X[:, 0])
	ymin = np.min(X[:, 1])
	ymax = np.max(X[:, 1])

	prior = np.ones((K)) * 1 / K
	
	# random initial mean will leads to bad result
# 	mean = np.zeros((K, D));
# 	for i in range(K):
# 		for j in range(D):
# 			mean[i, j] = i * 10 
# 	for i in range(D):
# 		max_X = max(X[:, i])
# 		min_X = min(X[:, i])
# 		mean[:, i] = np.random.random((K)) * (max_X - min_X) + min_X

	# select farest X as initial mean
	d = np.zeros((N, N))
	selected = set()
	mean = np.zeros((K, D))
	for i in range(N):
		for j in range(N):
			d[i, j] = np.linalg.norm(X[i, :] - X[j, :])
	selected.add(int(np.argmax(d)) / N)
	selected.add(int(np.argmax(d)) % N)
	
	mean = np.random.random((K, D)) * 4;
	for j in range(K - 2):
		d_sum = np.zeros(N)
		for i in range(N):
			if i in selected:
				d_sum[i] = 0
			else:
				for s in selected:
					d_sum[i] += d[s, i]
		selected.add(np.argmax(d_sum))
	tmp = 0
	for s in selected:
		mean[tmp, :] = X[s, :]
		tmp = tmp + 1
	
	cov = np.zeros((K, D, D))
	for i in range(K):
		cov[i, :, :] = np.identity(D)
	
	for time in range(10):
		if time % 1 == 0 :
			plt.figure('iterations:' + str(time))
			plt.scatter(X[:, 0], X[:, 1] , cmap='cool', c=label_for_plot)
			for i in range(K):
				x1, x2 = draw.gauss_ellipse_2d(mean[i, :], cov[i, :, :])
				plt.plot(x1, x2, 'k')
			lab.savefig('iterations:' + str(time) + '.png')

		# E: soft assign
		P = np.zeros((N, K))
		
		for i in range(N):
			for j in range(K):
				tmp = np.reshape(X[i, :] - mean[j], (D, 1))				
				P[i, j] = prior[j] / np.sqrt(np.linalg.det(cov[j, :, :])) * np.exp(-0.5 * np.dot(np.dot(tmp.T, np.linalg.inv(cov[j, :, :])) , tmp))
		for i in range(N):
			P[i, :] = P[i, :] / np.sum(P[i, :])
		# M: re-estimate parameters
		label = np.argmax(P, axis=1)
		n = np.sum(P, axis=0)
		
		prior_new = n / np.sum(n)
		mean_new = np.zeros((K, D))
# 		for j in range(K):
# 			mean_new[j, :] = np.dot(P[:, j].T, X) / n[j]
		for j in range(K):
			for i in range(N):
				mean_new[j, :] = mean_new[j, :] + P[i, j] * X[i, :] / n[j]
				
		cov_new = np.zeros((K, D, D))
		for j in range(K):
			for i in range(N):
				tmp = np.reshape(X[i, :] - mean_new[j, :], (D, 1)) 
				cov_new[j, :, :] = cov_new[j, :, :] + P[i, j] * np.dot(tmp , tmp.T) / n[j]
		prior = prior_new
		mean = mean_new
		cov = cov_new
		
		
	return [prior, mean, cov, label]
