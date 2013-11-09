(ns bababase.controllers.api-spec
  (:use [speclj.core]
        [ring.mock.request]
        [cheshire.core])
  (:require [bababase.controllers.api :as api]))

(describe "bababase.controllers.api.index"
  (it "should return 200"
    (should= 200 (:status (api/index (request :get "/api/v1")))))
  (it "should have \"ok\" status "
    (should= {"status" "ok"} (parse-string (:body (api/index (request :get "/api/v1")))))))

(run-specs)

