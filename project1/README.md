# CMSC 451 Project 1 – Benchmark Suite

This repo contains Evan Moseman’s CMSC‑451 Project 1 code. The focus is a benchmarking platform for Merge Sort and Shell Sort, plus a Swing-based viewer for the generated CSV output. Below is an overview of the main executables and how to run them.

## Requirements
- Java 21+ (Gradle wrapper downloads its own JDK toolchain)
- Unix-like environment for the provided scripts (tested on Linux)

## Building
From the repo root (`/home/emoseman/workspace/cmsc451/project1`):

```bash
./gradlew clean build
```

On Windows, use the batch wrapper:

```cmd
gradlew.bat clean build
```
Gradle will compile the code and run unit tests. To improve repeatability, the build uses Gradle’s application plugin so `Platform` is the default entry point.

## Running the Benchmark (Platform)
The `Platform` class manages benchmark execution, optional CPU affinity, and invokes the `Benchmark` runner. Run it via Gradle:

```bash
./gradlew run
```

On Windows:

```cmd
gradlew.bat run
```
### Warmup Behavior
Before timing the actual datasets, the benchmark performs a JVM warmup:
- Dataset size: `ELEMENT_COUNT_INC * 20` (by default 10,000 elements)
- Iterations: continues until the duration of consecutive runs converges within 0.5% for each sorter or until 1,000 iterations pass, with at least 5 iterations and 3 stable confirmations
- If stabilization is not reached in time, a warning prints but benchmarking proceeds

### CPU Affinity
`Platform` uses [OpenHFT Affinity](https://github.com/OpenHFT/Java-Thread-Affinity) to pin the benchmark thread to a single core when native libraries are available. Details:
- Checks `Affinity.isJNAAvailable()`
- If available, locks to a core using `AffinityLock.acquireCore()` and runs `Benchmark` inside that affinity context
- If not available (or on systems without JNA), it simply runs `Benchmark` in a regular single-thread executor

## Running the Swing Benchmark Viewer
`BenchmarkReport` is a desktop UI that reads the output CSVs and recomputes the summary stats for each row. The Gradle task:

```bash
./gradlew runBenchmarkReport --args="/path/to/benchmark-results-MergeSort.csv"
```

Windows:

```cmd
gradlew.bat runBenchmarkReport --args="C:\path\to\benchmark-results-MergeSort.csv"
```
If no argument is provided, the UI opens to a blank table—use `File → Open…` to select a CSV. The first (size) column is left-aligned; all computed metric columns are right-aligned for readability.

### CSV Format
Lines in `benchmark-results-*.csv` look like:
```
<size>,count:time,count:time,...
```
`BenchmarkReport` parses these into averages and coefficients of variation for both counts and times.

## Alternate Run Configurations
- `./gradlew runHotspotOptimized` – Runs the benchmark with `-XX:CompileThreshold=1000` and `-XX:+TieredCompilation` to speed up JIT optimization.
- Customize dataset sizes, run count, or warmup thresholds by editing constants at the top of `Benchmark.java`.

Windows equivalents:

- `gradlew.bat runHotspotOptimized`
- Same customization guidance applies.

## Troubleshooting
- Missing JNA/Affinity: the benchmark still runs; you’ll just see “Affinity JNA available = false”.
- CSV parsing failures in `BenchmarkReport`: ensure the file uses the expected `<count>:<time>` pair format.
