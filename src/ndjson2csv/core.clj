(ns ndjson2csv.core
  (:require
   [jsonista.core :as j]
   [cheshire.core :refer [parse-string]])
  (:gen-class))

(defn deep-merge [v & vs]
  (letfn [(rec-merge [v1 v2]
            (if (and (map? v1) (map? v2))
              (merge-with deep-merge v1 v2)
              v2))]
    (if (some identity vs)
      (reduce #(rec-merge %1 %2) v vs)
      v)))

(defn merge-documents
  [docs]
  (loop [to-merge docs
         subjects {}
         n 0]
    (if (empty? to-merge)
      subjects
      (let [current-result (first to-merge)]
        ;;(prn (str "progress: " n "/" (count docs)))
        ;;(prn (get-in current-result ["subject" "$oid"]))
        (recur (rest to-merge)
               (update subjects
                       (get-in current-result ["subject" "$oid"])
                       deep-merge
                       current-result)
               (inc n))))))

(defn dnjson2csv
  [results]
  (let [partition-size (int (/ (count results) 4))]
     (->> (map j/read-value results)
          (partition partition-size)
          (map merge-documents)
          (reduce deep-merge))))

(defn -main
  [& args]
  (prn "Start reading file")
  (with-open [rdr (clojure.java.io/reader (clojure.java.io/resource "results"))]
    (let [results (take 10000 (line-seq rdr))]
      (prn (count (dnjson2csv results))))))
