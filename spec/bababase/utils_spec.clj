(ns bababase.utils-spec
  (:use [speclj.core])
  (:require [bababase.utils :as utils]))

(def current-dir (-> (ClassLoader/getSystemResource *file*) clojure.java.io/file .getParent))
(def utils-test-dir (clojure.string/join [current-dir "/../test-data/utils"]))

(defn get-test-file
  [filename]
  (clojure.java.io/file (clojure.string/join [utils-test-dir "/" filename])))

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

(run-specs)
