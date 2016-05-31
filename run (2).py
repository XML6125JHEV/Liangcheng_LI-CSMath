import lm
[var, f_value, iters] = lm.lm('cos(exp(x))', 'x')

[var, f_value, iters] = lm.lm('(x-1)**2+(exp(y)-2)**2', "x y")

[var, f_value, iters] = lm.lm('(x+y+2)**2+(x-y+1)**2', 'x y')