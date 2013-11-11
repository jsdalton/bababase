(ns bababase.testing
  (:require [caribou.core :as core]
            [clojure.java.io :as io]
            [caribou.index :as index]
            [caribou.query :as query]
            [caribou.model :as model]
            [bababase.boot :as boot]
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

(defn with-full-project
  "Wrap config context around function and cleanup any created models"
  [f]
  (let [config (boot/boot)]
    (core/with-caribou config
      (truncate-unlocked-models)
      (index/purge)
      (query/clear-queries)
      (f)
      (truncate-unlocked-models))))
