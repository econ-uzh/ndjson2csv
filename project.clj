(defproject ndjson2csv "0.1.0"
  :description "NDJSON to CSV converter"
  :url "https://econ.uzh.ch"
  :license {:name "Proprietary"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [metosin/jsonista "0.2.1"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main ^:skip-aot ndjson2csv.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-binplus "0.6.4"]]}}
  :bin {:name "ndjson2csv"})
