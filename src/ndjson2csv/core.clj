(ns ndjson2csv.core
  (:require
   [jsonista.core :as j])
  (:gen-class))

(def memory (atom {}))
(def mongo-keys [:_id :__v])

(defn remove-keys
  [m ks]
  (reduce #(dissoc %1 %2) m ks))

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

(defn extract-keys
  [subjects]
  (reduce (fn [all-keys subject]
            (into #{} (concat all-keys (keys subject))))
          #{}
          subjects))

(defn maps->lines [fields m]
  (->> (map (fn [k] (get m k "")) fields)
      (clojure.string/join ",")))

(defn write-csv! [fields maps]
  (let [lines (map (partial maps->lines fields) maps)]
    (with-open [w (clojure.java.io/writer "results.csv")]
       (.write w (clojure.string/join "," fields))
       (.newLine w)
      (doseq [line lines]
        (.write w line)
        (.newLine w)))))

(defn clean-document [doc]
  doc)

(defn -main
  [& args]
  (with-open [rdr (clojure.java.io/reader (clojure.java.io/resource "results"))]
    (let [results (take 300000 (line-seq rdr))]
      (doseq [result results]
        (swap! memory merge-document (clean-document (j/read-value result))))))
  (swap! memory vals)
  (swap! memory (fn [subjects] (map #(flatten-map % ".") subjects)))
  (write-csv! (extract-keys @memory) @memory))
