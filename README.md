# Jackson Benchmark

A simple benchmark to compare the performance of non-blocking parser with a blocking one.

I don't really trust these results that just yet and I ran benchmarks with a single iteration for 60 seconds only.

```
Benchmark                             Mode  Cnt   Score   Error  Units
JacksonBenchmark.defaultParser       thrpt       43,292          ops/s
JacksonBenchmark.defaultParserEmpty  thrpt       56,857          ops/s
JacksonBenchmark.nonBlockingParser   thrpt       61,202          ops/s
```

