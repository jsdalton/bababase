(ns bababase.testing
  (:use
        [ring.middleware.params :only (wrap-params)])
  (:require [caribou.core :as caribou-core]
            [caribou.config :as caribou-config]
            [clojure.java.io :as io]
            [caribou.index :as index]
            [caribou.query :as query]
            [caribou.model :as model]
            [bababase.core :as bababase-core]
            [bababase.boot :as boot]
            [ring.mock.request :as request]
            [caribou.migration :as migration]))

(def project-mock {:migration-namespace 'bababase.migrations})
(def config-file (-> "config/test.clj" io/resource .getFile))

(defn do-migrations
  "Run all migrations for project"
  []
  (with-out-str (caribou.migration/run-migrations project-mock config-file false)))

(defn do-rollbacks
  "Run all rollbacks for project"
  []
  (with-out-str (caribou.migration/run-rollbacks project-mock config-file false)))

(defn truncate-unlocked-models
  "Destroys ALL records in unlocked models"
  []
  (doseq [m (map :slug (model/gather :model {:where {:locked false}}))]
    (doseq [record (model/gather (keyword m))]
      (model/destroy (keyword m) (:id record)))))

(defn foobar
  [handler]
  (fn [request]
    (let [request (merge request {:foo :bar})]
      (handler request))))


(defn mock-request
  "Creates a mock request. Wrapper around ring.mock.request with same API"
  [& args]
  ((bababase-core/init false) (apply request/request args)))

(defn with-config
  "Wrap config context around function and cleanup any created models"
  [f]
  (let [config (boot/boot)]
    (caribou-core/with-caribou config
      (f))))

(defn do-cleanup
  ([]
   (do-cleanup (caribou-config/environment)))
  ([env]
   (if (= env :test)
     (do
       (truncate-unlocked-models)
       (index/purge)
       (query/clear-queries)))))

(defn cleanup
  "Clean up any data added. SHOULD USE FOR TESTING ONLY. But you can cheat if you want to."
  ([]
   (cleanup (caribou-config/environment)))

  ([env]
   (with-config
     #(do-cleanup env))))

(defn with-clean-project
  "Wrap config context around function and cleanup any created models"
  [f]
  (with-config
    (fn []
      (do-cleanup)
      (f))))


