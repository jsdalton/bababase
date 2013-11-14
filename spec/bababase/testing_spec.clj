(ns bababase.testing-spec
  (:use [speclj.core])
  (:require [bababase.testing :as testing]
            [clojure.java.io :as io]))

(describe "bababase.testing/mock-request [request]"
  (it "should wrap the middleware found in bababase.core/init"
    ; :params not included in request by default -- added by middleware
    (should= {:foo "bar"} (:params (bababase.testing/mock-request :get "/some-page?foo=bar")))))

(run-specs)
