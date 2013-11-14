(ns bababase.utils-spec
  (:use [speclj.core])
  (:require [bababase.utils :as utils]
            [caribou.model :as model]
            [bababase.testing :as testing]))

(def utils-test-dir (str (utils/current-dir) "/../test-data/utils"))

(defn get-test-file
  [filename]
  (clojure.java.io/file (str utils-test-dir "/" filename)))

(describe "bababase.utils/crawl [file]"
  (it "should return the file extension of file"
    (should= "txt" (utils/file-ext (get-test-file "foo.txt"))))
  (it "should convert to lowercase"
    (should= "txt" (utils/file-ext (get-test-file "bar.TXT")))))

(describe "bababase.utils/file-has-ext? [file ext]"
  (it "should return true if file has ext"
    (should (utils/file-has-ext? (get-test-file "foo.txt") "txt")))
  (it "should return false if file does not have ext"
    (should-not (utils/file-has-ext? (get-test-file "baz.txt.bak") "txt"))))

(describe "bababase.utils/list-directory-contents"
  (describe "[dir]"
    (it "should list all files in dir, not including dir itself"
      (should= 5 (count (utils/list-directory-contents utils-test-dir))))
    (it "should return nil if dir does not exist"
      (should-be-nil (utils/list-directory-contents "/director/does/not/exist/"))))
  (describe "[dir ext]"
    (it "should list all files in dir matching ext"
      (should= 2 (count (utils/list-directory-contents utils-test-dir "txt"))))
    (it "should return nil if dir does not exist"
      (should-be-nil (utils/list-directory-contents "/director/does/not/exist/" "txt")))))

(describe "bababase.utils/current-dir"
  (it "should return directory where called from"
    (should-contain "/spec/bababase" (utils/current-dir))))

(describe "bababase.utils/provide [slug spec]"
  (around [it]
    (testing/with-clean-project it))

  (it "should create and return a model if none exists"
    (let [provided (utils/provide :year {:year 2012})]
      (should= (model/pick :year {:where {:year 2012}}) provided)))

  (it "should not create a model if already exists"
    ; first create one
    (model/create :year {:year 2012})
    ; now provide
    (let [provided (utils/provide :year {:year 2012})]
      (should= (model/pick :year {:where {:year 2012}}) provided)
      (should= 1 (count (model/gather :year {:where {:year 2012}}))))))

(describe "bababase.utils/fence [number minimum maximum]"
  (it "should return number if number is between minimum and maximum"
    (should= 27 (utils/fence 27 0 100)))

  (it "should return minimum if number is below minimum"
    (should= 0 (utils/fence -56 0 100)))

  (it "should return maxiumum if number is above maximum"
    (should= 100 (utils/fence 342432 0 100))))

(run-specs)
