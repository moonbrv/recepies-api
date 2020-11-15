(ns user
  (:require
   [integrant.repl :as ig-repl]
   [integrant.core :as ig]
   [integrant.repl.state :as state]
   [recipes-api.server]))

(ig-repl/set-prep!
 (fn []
   (-> "resources/config.edn" slurp ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(defn app []
  (-> state/system :recipes/app))

(defn db []
  (-> state/system :db/postgress))

(comment
  (go)
  (halt)
  (reset)
  ;;
  )
