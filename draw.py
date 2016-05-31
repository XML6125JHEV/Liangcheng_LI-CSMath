import numpy as np
# copied from pypr project 
# see http://pypr.sourceforge.net/mog.html
def gauss_ellipse_2d(centroid, ccov, sdwidth=1, points=100):
    """Returns x,y vectors corresponding to ellipsoid at standard deviation sdwidth.
    """
    # from: http://www.mathworks.com/matlabcentral/fileexchange/16543
    mean = np.c_[centroid]
    tt = np.c_[np.linspace(0, 2 * np.pi, points)]
    x = np.cos(tt); y = np.sin(tt);
    ap = np.concatenate((x, y), axis=1).T
    d, v = np.linalg.eig(ccov);
    d = np.diag(d)
    d = sdwidth * np.sqrt(d);  # convert variance to sdwidth*sd
    bp = np.dot(v, np.dot(d, ap)) + np.tile(mean, (1, ap.shape[1])) 
    return bp[0, :], bp[1, :]
