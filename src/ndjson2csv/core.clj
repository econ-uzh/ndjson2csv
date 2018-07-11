(ns ndjson2csv.core
  (:require
   [jsonista.core :as j])
  (:gen-class))

(def memory (atom {}))

(defn deep-merge [v & vs]
  (letfn [(rec-merge [v1 v2]
            (if (and (map? v1) (map? v2))
              (merge-with deep-merge v1 v2)
              v2))]
    (if (some identity vs)
      (reduce #(rec-merge %1 %2) v vs)
      v)))

(defn merge-document
  [subjects doc]
  (update subjects
          (get-in doc ["subject" "$oid"])
          deep-merge
          doc))

(defn -main
  [& args]
  (with-open [rdr (clojure.java.io/reader (clojure.java.io/resource "results"))]
    (let [results (line-seq rdr)]
      (doseq [result results]
        (swap! memory merge-document (j/read-value result)))))
  (prn (count (keys @memory))))
