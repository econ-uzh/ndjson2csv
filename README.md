# ndjson2csv

A small cli tool that prerocesses and transforms large ndjson (http://ndjson.org/) files without setting your hair on fire. It is especially useful to quickly process large JSON exports from MongoDB.

The CSV fields/headers are computed automatically by flattening the documents.

## Usage

> ndjson2csv --help

## Compiling from source

Requirements: JDK 8+ and Leiningen

Run `lein bin` in project root, the binary is `target/default/ndjson2csv`.

## Options

```
  -l, --lines NUMBER_LINES      0            Number of lines to read from ndjson, can be used for testing
  -i, --input FILE                           Input file containing ndjson.
  -o, --output FILE             results.csv  File to write csv in, will be created if it doesn't exist.
  -m, --merge-with DOCUMENT_ID               Preprocesses the ndjson by deep-merging the documents, using the provided id as unique identifier. Useful when multiple documents should make up a line in the CSV file.
  -s, --separator CHARACTER     .            The separating character for the CSV fields that are generated by deep-merging the documents.
  -p, --pre--processor FILE                  Clojure file, containing a function named `process` that takes a map document as input and returns a processed version. This function is applied to all documents right after loading and parsing it.
  -h, --help
```

## Examples

Reading the first 1000 lines of input.json, merging by using the top-level key `subjectId` as id, preprocessing by calling the function `process` from the file `processor.clj` on each parsed document and writing to output.csv:

> ndjson2csv -i input.json -l 1000 -p examples/processor.clj -m subjectId -o output.csv

## License

Copyright © 2018 University of Zurich Department of Economics
