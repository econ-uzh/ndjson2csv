(defproject ndjson2csv "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [metosin/jsonista "0.2.1"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main ^:skip-aot ndjson2csv.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
