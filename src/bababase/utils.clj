(ns bababase.utils)

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
       (filter #(file-has-ext? % ext) (file-seq (clojure.java.io/file dir)))))))
