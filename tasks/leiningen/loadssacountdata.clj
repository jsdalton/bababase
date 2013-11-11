(ns leiningen.loadssacountdata
  (:require [leiningen.core.eval :as eval]))


(defn loadssacountdata
  "Load SSA count data found in given directory."
  [project config-file dir]
  (eval/eval-in-project project
    `(bababase.tasks/load-ssa-data-count-data-task '~config-file '~dir true)
    '(require 'bababase.tasks)))




