(ns leiningen.loadssanamesbystate
  (:require [leiningen.core.eval :as eval]))


(defn loadssanamesbystate
  "Load raw SSA data found in given directory."
  [project config-file dir]
  (eval/eval-in-project project
    `(bababase.tasks/load-ssa-data-names-by-state-task '~config-file '~dir true)
    '(require 'bababase.tasks)))




