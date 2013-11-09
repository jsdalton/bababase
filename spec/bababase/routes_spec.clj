(ns bababase.routes-spec
  (:use [speclj.core]))

(defn true-or-false []
  true)

(describe "truthiness"
  (it "tests if true-or-false returns true"
    (should (true-or-false))))

(run-specs)
