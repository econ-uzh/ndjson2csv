# ndjson2csv

A small cli tool that prerocesses and transforms large ndjson (http://ndjson.org/) files without running out of memory. It is especially useful to quickly process large JSON exports from MongoDB.

## Usage

`./ndjson2csv --help`

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
  -h, --help
```

## Examples

> ndjson2csv -i input.json -l 1000 -m subjectId -o output.csv

## License

Copyright © 2018 University of Zurich Department of Economics
