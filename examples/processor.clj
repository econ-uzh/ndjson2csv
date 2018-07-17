;; Function must be called `process`, it must take and return one document
(defn process [doc]
  (assoc doc :foo "bar"))
