(ns bababase.controllers.api-spec
  (:use [speclj.core]
        [cheshire.core])
  (:require [bababase.controllers.api :as api]
            [bababase.testing :as testing]
            [bababase.tasks :as tasks]
            [bababase.tasks-spec]
            ))

(describe "bababase.controllers.api.index"
  (it "should return 200"
    (should= 200 (:status (api/index (testing/mock-request :get "/api/v1")))))

  (it "should have \"ok\" status "
    (should= {"status" "ok"} (parse-string (:body (api/index (testing/mock-request :get "/api/v1")))))))

(describe "bababase.controllers.api.name-index"
  (before-all
    (testing/with-clean-project
      #(bababase.tasks/load-ssa-us-data bababase.tasks-spec/ssa-data-us-test-dir)))

  (after-all
    (testing/cleanup))

  (around [it]
    (testing/with-config it))


  (describe "when no params are passed..."
    (with request (api/names-index (testing/mock-request :get "/api/v1/names")))

    (it "should return 200"
      (should= 200 (:status @request)))

    (it "should return a bunch of names"
      (let [result (parse-string (:body @request))]
        (should= 20 (count (get result "response"))))))

  (describe "when q param is passed...."
    (with request (api/names-index (testing/mock-request :get "/api/v1/names?q=ja")))

    (it "should return a bunch of names that start with q"
      (let [result (parse-string (:body @request))
            names (map #(get % "name") (get result "response"))]
        (should (every? #(.startsWith % "Ja") names)))))

  (describe "when limit is passed..."
    (with request (api/names-index (testing/mock-request :get "/api/v1/names?limit=10"))) 

    (it "should limit results to limit"
      (let [result (parse-string (:body @request))]
        (should= 10 (count (get result "response"))))))

  (describe "when gender is passed..."
    (with request (api/names-index (testing/mock-request :get "/api/v1/names?gender=f")))

    (it "should limit results to that gender"
      (let [result (parse-string (:body @request))
            genders (map #(get % "gender") (get result "response"))]
        (should (every? #{"F"} genders))))))
(run-specs)

