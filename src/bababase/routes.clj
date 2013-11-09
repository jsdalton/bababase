(ns bababase.routes
  (:require [caribou.app.pages :as pages]))

(def api-routing
  ["api/v1"    :api
   [["states"  :api.states []]]])

(def routes
  [["/" :home [api-routing]]])

(def pages
  {:home       {:GET {:controller 'home 
                      :action     'home 
                      :template   "home.html"}}
   :api        {:GET {:controller 'api
                      :action     'index}}
   :api.states {:GET {:controller 'api
                      :action     'states-index}}})

(defn page-tree
  []
  (pages/build-page-tree routes pages))

(defn gather-pages
  []
  (let [db-pages (try 
                   (pages/all-pages)
                   (catch Exception e nil))]
    (pages/merge-page-trees db-pages (page-tree))))
