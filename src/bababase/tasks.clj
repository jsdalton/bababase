(ns bababase.tasks
  (:require [leiningen.core.main :as lein]
            [bababase.utils :as utils]
            ))

(defn load-ssa-data
  "Load raw SSA data found in given directory."
  [project dir exit?]
  (println (utils/list-directory-contents dir))
  (if exit? (lein/exit)))


