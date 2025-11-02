import time

# uses simple clauses format
def sat_solver(clauses, n):
    for mask in range(1 << n):
        result = True
        for clause in clauses:
            satisfied = False
            for lit in clause:
                v = abs(lit) - 1
                val = (mask >> v) & 1
                if (lit > 0 and val == 1) or (lit < 0 and val == 0):
                    satisfied = True
                    break
            if not satisfied:
                result = False
                break
        if result:
            return True
    return False


for n in range(10,28,2):

    clauses = [[i] for i in range(1, n + 1)]

    start_time = time.perf_counter()

    sat_solver(clauses, n)

    print(f"N = {n} - execution time: {time.perf_counter() - start_time:.6f} seconds")