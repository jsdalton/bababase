(ns bababase.tasks-spec
  (:use [speclj.core])
  (:require [bababase.tasks :as tasks]))

(def current-dir (-> (ClassLoader/getSystemResource *file*) clojure.java.io/file .getParent))
(def ssa-data-test-dir (clojure.string/join [current-dir "/../test-data/ssa-data"]))

(describe "bababase.utils/load-ssa-data [project dir exit?]"
  (it "should load data from files in dir into project"
          (should (tasks/load-ssa-data nil ssa-data-test-dir false))
          ))

(run-specs)
