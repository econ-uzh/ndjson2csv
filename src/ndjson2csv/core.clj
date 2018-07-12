(ns ndjson2csv.core
  (:require
   [jsonista.core :as j]
   [clojure.data.csv :as csv]
   [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(defn deep-merge [v & vs]
  "Deeply merges multiple maps."
  (letfn [(rec-merge [v1 v2]
            (if (and (map? v1) (map? v2))
              (merge-with deep-merge v1 v2)
              v2))]
    (if (some identity vs)
      (reduce #(rec-merge %1 %2) v vs)
      v)))

(defn merge-document
  [acc doc]
  (update acc
          (get doc "subjectId")
          deep-merge
          doc))

(defn flatten-map
  "Flattens a map in a performant way using a `separator`, doesn't flat vectors."
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
  "Given a list of maps `acc`, returns a set of all keys found."
  [acc]
  (reduce (fn [all-keys item]
            (into #{} (concat all-keys (keys item))))
          #{}
          acc))

(defn map->cells [fields m]
  (map (fn [k] (get m k "")) fields))

(defn write-csv!
  "Given flattened `maps` and a list of csv `fields`, writes the result to file."
  [fields maps file-name]
  (println (str "Writing csv file"))
  (let [cells (vec (map (partial map->cells fields) maps))]
    (with-open [w (clojure.java.io/writer file-name)]
      (csv/write-csv w
           (concat (conj [] (vec (map name fields))) cells))))
  (println (str "Lines written: " (count maps))))

(defn ndjson->map [lines]
  (let [memory (atom {})]
    (println (str "Processing ndjson lines"))
    (doseq [line lines]
      ;; load lines one by one into memory and process them
      (swap! memory merge-document (j/read-value line)))
    (swap! memory vals)
    (swap! memory (fn [item] (map #(flatten-map % ".") item)))
    @memory))

(def cli-options
  [["-l" "--lines NUMBER_LINES" "Number of lines to read from ndjson, can be used for testing"
    :id :lines
    :default 0
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 %) "Must be positive"]]
   ["-i" "--input FILE" "Input file containing ndjson"
    :validate [(complement clojure.string/blank?) "Must not be empty"]]
   ["-o" "--output FILE" "File to write csv in, will be created if it doesn't exist"
    :validate [(complement clojure.string/blank?) "Must not be empty"]
    :default "results.csv"]
   ["-h" "--help"
    :default false]])

(defn -main
  [& args]
  (let [{:keys [options arguments summary errors]}
        (parse-opts args cli-options)]
    (cond
      (not-empty errors) (println errors)
      (:help options) (println summary)
      (nil? (:input options)) (-main "-h")
      :else
      (with-open [rdr (clojure.java.io/reader (:input options))]
        (let [acc (ndjson->map (if (zero? (:lines options))
                                 (line-seq rdr)
                                 (take (:lines options) (line-seq rdr))))]
          (write-csv! (extract-keys acc) acc (:output options)))))))
