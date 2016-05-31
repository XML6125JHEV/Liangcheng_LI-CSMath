
import numpy as np
import copy
import matplotlib.pyplot as plt

def generate_data(sample):
    start = -np.pi
#    x = []
    x = np.arange(-np.pi, np.pi+0.1, 2.0*np.pi/(sample-1))
    print len(x)
#    for i in range(10):
#        x.append(start+i*(np.pi/5.0))
    y = np.sin(x)
    mu, sigma = 0, 0.5/np.sqrt(np.pi*2)
    noise = np.random.normal(mu, sigma, len(y))
    y += noise
#    for i in range(len(y)):
#        y[i] += np.random.rand()*0.3
    return x, y

def compute_line(x, y, order, r):
    p = []
    for xi in x:
        pi = []
        for j in range(0, order+1):
            pi.append(np.power(xi, j))
        p.append(pi)
    P = np.array(p)
    Y = np.array([y])
#    W = np.dot(np.dot(np.linalg.inv(np.dot(P.T, P)), P.T), Y.T)
    
    W = np.dot(np.dot(np.linalg.inv(np.dot(P.T, P)+np.eye(order+1, order+1)*r), P.T), Y.T)
    print W
    x2 = np.arange(-np.pi-1, np.pi+1, 0.01)
    y2 = []
    for xi in x2:
        sum = 0
        for j in range(0 , order+1):
            sum += np.power(xi, j)*W[j][0]
        y2.append(sum)
    
    return x2, y2

if __name__ == '__main__':
    x, y = generate_data(10)
    plt.scatter(x, y, color='b')
    
    x2, y2 = compute_line(x, y, 9, 0.09)
    plt.plot(x2, y2, color='r')
    
    x3 = np.arange(-np.pi-1, np.pi+1, 0.01)
    y3 = np.sin(x3)
    plt.plot(x3, y3, color='g',)
    
    plt.axis([-np.pi-0.1,np.pi+0.1, -2,2])
    plt.show()
    
#    mu, sigma = 0, 1.0/np.sqrt(np.pi*2)
#    print np.random.normal(mu, sigma, 100)
