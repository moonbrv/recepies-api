(ns user
  (:require
   [integrant.repl :as ig-repl]
   [integrant.core :as ig]
   [integrant.repl.state :as state]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [recipes-api.server]))

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
  (app {:request-method :get
        :uri "/swagger.json"})
  (jdbc/execute! db ["SELECT * FROM recipe WHERE public = true"])

  (with-open [conn (jdbc/get-connection db)]
    (let [recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"
          recipe (sql/get-by-id conn :recipe recipe-id :recipe_id {})
          steps (sql/find-by-keys conn :step {:recipe_id recipe-id})
          ingredients (sql/find-by-keys conn :ingredient {:recipe_id recipe-id})]
      (assoc recipe
             :recipe/steps steps
             :recipe/ingredients ingredients)))

  (sql/find-by-keys db :recipe {:public false})
  (go)
  (halt)
  (reset)
  ;;
  )
