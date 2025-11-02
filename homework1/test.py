import re, time
pattern = re.compile(r'^(a+)+b$')
for n in [10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30]:
    s = 'a' * n
    t0 = time.time()
    m = pattern.match(s)
    dt = time.time() - t0
    print(f"n={n:2d} -> matched={bool(m)}, time={dt:.6f}s")
