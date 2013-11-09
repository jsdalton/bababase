(ns leiningen.loadssadata
  (:require [leiningen.core.eval :as eval]))


(defn loadssadata
  "Load raw SSA data found in given directory."
  [project dir]
  (eval/eval-in-project project
    `(bababase.tasks/load-ssa-data '~project '~dir true)
    '(require 'bababase.tasks)))




