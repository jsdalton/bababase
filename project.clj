(defproject bababase "0.12.30"
  :description "The page routing ring handler for caribou"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [org.immutant/immutant "1.0.2"]
                 [caribou/caribou-admin "0.12.32"]
                 [caribou/caribou-api "0.12.30"]
                 [org.clojure/tools.nrepl "0.2.3"]]
  :plugins [[lein-ring "0.8.6"]
            [caribou/lein-caribou "2.4.30"]
            [lein-cljsbuild "0.3.3"]
            [speclj "2.5.0"]]
  :dev-dependencies [[speclj "2.5.0"]]
  :profiles {:dev {:dependencies [[speclj "2.5.0"]
                                  [ring-mock "0.1.5"]]}}
  :test-paths ["spec"]
  :jvm-opts ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n" "-Xmx2g" "-XX:MaxPermSize=128m"]
  :source-paths ["src"]
  :resource-paths ["resources/"]
  :min-lein-version "2.0.0"
  :migration-namespace bababase.migrations
  :main bababase.core
  :ring {:handler bababase.core/handler
         :init bababase.core/init
         :port 33333
         :auto-reload? false
         :servlet-name "bababase-frontend"}
  :immutant {:context-path "/"
             :init bababase.immutant/init}
  :cljsbuild {:repl-listen-port 44994
              :builds
              [{:source-paths ["resources/cljs"]
                :compiler {:output-to "resources/public/js/app/skel.js"
                           :optimizations :whitespace
                           :pretty-print true}}]})
