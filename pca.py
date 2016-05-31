

import numpy as np
import matplotlib.pyplot as plt

def GenerateData():
    dataStr = {}
    lines = open("data").read().split("\n")
    i = 21
    while len(lines)-i > 32:
        digit = ""
        for j in range(i, i+32):
            digit += lines[j]
        dtxt = lines[i+32]
        i += 33
        if dtxt not in dataStr:
            dataStr[dtxt] = []
        dataStr[dtxt].append(digit)
    objdata = dataStr[" 3"]
    m = len(objdata)
    n = len(objdata[0])
    data = np.zeros((m, n))
    for i in range(m):
        for j in range(n):
            data[i][j] = int(objdata[i][j])
    return data

def PCA(data):
    avg = []
    for di in data.T:
        avg.append(np.average(di))

    S = np.zeros((len(data[0]),len(data[0])))
    for i in range(len(data)):
        ti = np.array([data[i]-avg])
        S += np.dot(ti.T, ti)
   
    
            
    w, v = np.linalg.eig(S)
    return  w, v

def select(data2):
    data3 = []
    minX = -2
    maxX = 6
    minY = -4
    maxY = 4
    xstep = 2
    ystep = 2
    temp = []
    
    for i in range(int((maxX-minX)/xstep)):
        for j in range(int((maxY-minY)/ystep)):
            temp.append([minX+i*xstep, minY+j*ystep])
    
    indexList = []
    pos = []
    index = 0
    for di in (data2):
        if len(temp) > 0:
            obi = None
            for ti in temp:
                a = ti[0]
                b = ti[1]
                if di[0]>=a and di[0]<(a+xstep) and di[1]>=b and di[1]<(b+ystep):
                    data3.append([di])
                    obi = ti
                    indexList.append(index)
                    pos.append([int((a-minX)/xstep)+1,int((b-minY)/ystep)+1])
                    break
            if obi:
                temp.remove(obi)
        index += 1
    data3 = np.array(data3)
    return data3, indexList, pos

if __name__ == "__main__":   
    data = GenerateData()
    w, v = PCA(data)
    v = v.T
    s = []
    for i in range(len(w)):
        s.append([w[i], v[i]])
    s2 = sorted(s, key = lambda a:a[0], reverse= True)
    data2 = np.dot(data, np.array([s2[0][1],s2[1][1]]).T)
    plt.figure(1)
    plt.grid(True)
    plt.scatter(list(data2.T[0]),list(data2.T[1]), color="g")

    data3, indexList, pos = select(data2)
    plt.scatter(list(data3.T[0]),list(data3.T[1]), color="r")   
    
    plt.figure(2)     
    imgList = []
    for i in indexList:
        imgList.append(data[i].reshape((32,32)))
    print indexList
    count = 0
    for each in imgList:
        plt.subplot(4,4,str((pos[count][0]-1)*4+pos[count][1]))
        plt.imshow(each, cmap = "Greys")
        count += 1
    plt.show()
