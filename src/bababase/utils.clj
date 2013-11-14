(ns bababase.utils
  (:require [caribou.model :as model]
            [caribou.logger :as log]))

(defn file-ext
  "Returns file extension, e.g. \"txt\" for .txt"
  [file]
  (clojure.string/lower-case
    (last
      (clojure.string/split (.getName file) #"\."))))

(defn file-has-ext?
  [file ext]
  "Checks if file name ends in ext"
  (= (file-ext file) ext))

(defn list-directory-contents
  "List files in a directory, scoped by optional ext"
  ([dir]
   (let [file (clojure.java.io/file dir)]
     (if (not (.exists file))
       nil
       ; Only want contents, not container
       (next
         (file-seq file)))))
  ([dir ext]
   (let [file (clojure.java.io/file dir)]
     (if (not (.exists file))
       nil
       (filter
         #(file-has-ext? % ext)
         (file-seq (clojure.java.io/file dir)))))))

(defn current-dir
  "Returns string of current directory *where function is called from*"
  []
  (-> (ClassLoader/getSystemResource *file*) clojure.java.io/file .getParent))

(defn provide
  "Creates a model for slug with provided spec if none exists."
  [slug spec]
  (let [existing (model/pick slug {:where spec})]
    (if (nil? existing)
      (do
        (log/info (str "Creating " slug " " spec))
        (model/create slug spec))
      existing)))


(defn fence
  "Ensures number is between maxi and mini"
  [number minimum maximum]
  (-> number (min maximum) (max minimum)))
