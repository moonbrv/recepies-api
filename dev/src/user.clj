(ns user
  (:require
   [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
   [integrant.repl :as ig-repl]
   [integrant.core :as ig]
   [integrant.repl.state :as state]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [recipes-api.server]
   [recipes-api.recipe.handlers :as rh]))

(set-refresh-dirs "src" "test")

(ig-repl/set-prep!
 (fn []
   (-> "resources/config.edn" slurp ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(def app (-> state/system :recipes/app))

(def db (-> state/system :db/postgress))

(comment

  (set! *print-namespace-maps* false)

  (go)
  (halt)
  (reset)

  (reset-all)


  (-> (app {:request-method :get
            :uri "/v1/recipes"})
      :body
      slurp)

  (app {:request-method :get
        :uri "/v1/recipes/a3dde84c-4a33-45aa-b0f3-4bf9ac997680"})

  (-> (app {:request-method :post
            :uri "/v1/recipes"
            :body-params {:name "New test recipe"
                          :prep-time 15
                          :img "https://someimageurl.com/img.png"}})
      :body
      slurp)


  (jdbc/execute! db ["SELECT * FROM recipe WHERE public = true"])

  (sql/find-by-keys db :recipe {:public false}))
