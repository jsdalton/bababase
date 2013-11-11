(ns leiningen.loadssausdata
  (:require [leiningen.core.eval :as eval]))


(defn loadssausdata
  "Load SSA US data found in given directory."
  [project config-file dir]
  (eval/eval-in-project project
    `(bababase.tasks/load-ssa-us-data-task '~config-file '~dir true)
    '(require 'bababase.tasks)))
