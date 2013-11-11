(ns bababase.tasks-spec
  (:use [speclj.core])
  (:require [bababase.tasks :as tasks]
            [bababase.utils :as utils]
            [bababase.testing :as testing]
            [clojure.java.io :as io]
            [caribou.model :as model]))

(def ssa-data-test-dir (str (utils/current-dir) "/../test-data/ssa-data"))
(def ssa-data-us-test-dir (str (utils/current-dir) "/../test-data/ssa-data-us"))

(describe "bababase.tasks/line-to-map [line]"
  (it "should map line to fields"
    (should=
      {:count "14", :name "Mary", :year "1910", :gender "F", :region "AK"}
      (bababase.tasks/line-to-map "AK,F,1910,Mary,14"))))

(describe "(with full project)"
  (around [it]
    (testing/with-full-project it))

  (describe "bababase.tasks/load-ssa-data-names-by-state [dir]"
    (it "should load all years from files in dir into Year model"
      (bababase.tasks/load-ssa-data-names-by-state ssa-data-test-dir utils/provide)
        (let [expected-years [1910 1911 1912 1913 1914 1915]
              found-years (map :year (model/gather :year))]
          (should= (count expected-years) (count found-years))
          (doseq [expected-year expected-years]
            (should-contain expected-year found-years))))

    (it "should load all names (gender-specified) from files in dir into GivenName model"
      (bababase.tasks/load-ssa-data-names-by-state ssa-data-test-dir utils/provide)
      (let [found-names (map #(hash-map :name (:name %) :gender (:gender %)) (model/gather :givenname))]
        ; There are 83 unique name/gender combons in test text files
        (should= 83 (count found-names))
        ; Spot check
        (should-contain {:name "Agnes" :gender "F"} found-names)))

    (it "should load all states from files in dir into Region model"
      (bababase.tasks/load-ssa-data-names-by-state ssa-data-test-dir utils/provide)
        (let [expected-regions [{:name "Alaska" :code "AK"} {:name "Wyoming" :code "WY"}]
              found-regions (map #(hash-map :name (:name %) :code (:code %)) (model/gather :region))]
          (should= (count expected-regions) (count found-regions))
          (doseq [expected-region expected-regions]
            (should-contain expected-region found-regions)))))


  (describe "bababase.tasks/load-ssa-count-data [dir]"
    (it "should load all entries from files in dir into YearRegionName model"
      (bababase.tasks/load-ssa-data-count-data ssa-data-test-dir)
      (let [found-yearregionnamecounts (model/gather :yearregionnamecount)]
        ; There are 200 entries on the test test files
        (should= 200 (count found-yearregionnamecounts))
        ; Spot check AK,F,1912,Louise,7
        (should-contain
            {:region "AK" :year 1912 :gender "F" :name "Louise" :count 7}
            (map #(hash-map
                    :region (:region %)
                    :year (:year %)
                    :gender (:gender %)
                    :name (:name %)
                    :count (:count %)) found-yearregionnamecounts)))))

  (describe "bababase.tasks/load-ssa-us-data [dir]"
    (it "should load all years from files in dir into Year model"
      (bababase.tasks/load-ssa-us-data ssa-data-us-test-dir)
        (let [expected-years [2011 2012]
              found-years (map :year (model/gather :year))]
          (should= (count expected-years) (count found-years))
          (doseq [expected-year expected-years]
            (should-contain expected-year found-years))))

    (it "should load all names (gender-specified) from files in dir into GivenName model"
      (bababase.tasks/load-ssa-us-data ssa-data-us-test-dir)
      (let [found-names (map #(hash-map :name (:name %) :gender (:gender %)) (model/gather :givenname))]
        ; There are 83 unique name/gender combo in test text files
        (should= 105 (count found-names))
        ; Spot check
        (should-contain {:name "Caleb" :gender "M"} found-names)))

    (it "should load US into Region model"
      (bababase.tasks/load-ssa-us-data ssa-data-us-test-dir)
        (let [found-regions (map #(hash-map :name (:name %) :code (:code %)) (model/gather :region))]
          (should= 1 (count found-regions))
          (should-contain {:name "United States" :code "US"} found-regions)))

    (it "should load all entries from files in dir into YearRegionName model"
      (bababase.tasks/load-ssa-us-data ssa-data-us-test-dir)
      (let [found-yearregionnamecounts (model/gather :yearregionnamecount)]
        ; There are 200 entries on the test test files
        (should= 200 (count found-yearregionnamecounts))
        ; Spot check Isaac,M,9560
        (should-contain
            {:region "US" :year 2011 :gender "M" :name "Isaac" :count 9560}
            (map #(hash-map
                    :region (:region %)
                    :year (:year %)
                    :gender (:gender %)
                    :name (:name %)
                    :count (:count %)) found-yearregionnamecounts))))))

(run-specs)
