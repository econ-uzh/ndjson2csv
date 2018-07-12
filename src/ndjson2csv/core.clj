(ns ndjson2csv.core
  (:require
   [jsonista.core :as j]
   [clojure.data.csv :as csv])
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
          (get doc "subjectId")
          deep-merge
          doc))

(defn flatten-map
  ([form separator]
   (into {} (flatten-map form separator nil)))
  ([form separator pre]
   (mapcat (fn [[k v]]
             (let [prefix (if pre (str pre separator (name k)) (name k))]
               (if (map? v)
                 (flatten-map v separator prefix)
                 [[(keyword prefix) v]])))
           form)))

(defn compare-fields
  [a b]
  (cond
    (and (clojure.string/includes? (name a) "data")
         (clojure.string/includes? (name b) "data"))
    0
    (clojure.string/includes? (name a) "data") 1
    :else -1))

(defn extract-keys
  [subjects]
  (->> (reduce (fn [all-keys subject]
                 (into #{} (concat all-keys (keys subject))))
               #{}
               subjects)
       (sort compare-fields)))

(defn map->cells [fields m]
  (vec (map (fn [k] (get m k "")) fields)))

(defn write-csv! [fields maps]
  (let [cells (vec (map (partial map->cells fields) maps))]
    (with-open [w (clojure.java.io/writer "results.csv")]
      (csv/write-csv w
           (concat (conj [] (vec (map name fields))) cells))))
  (prn (str "Lines written: " (count maps))))

(defn -main
  [& args]
  (with-open [rdr (clojure.java.io/reader (clojure.java.io/resource "results"))]
    (let [results (line-seq rdr)]
      (doseq [result results]
        (swap! memory merge-document (j/read-value result)))))
  (swap! memory vals)
  (swap! memory (fn [subjects] (map #(flatten-map % ".") subjects)))
  (write-csv! (extract-keys @memory) @memory))
