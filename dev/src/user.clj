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


  (sql/insert! db :recipe-favorite {:uid "auth0|607ac0f60686bf00757e502b"
                                    :recipe-id "de174076-8304-44bc-bb1e-ce2c1ce2e66b"})
  (jdbc/execute-one! db ["UPDATE recipe 
                                SET favorite_count = favorite_count + 1 
                                WHERE recipe_id = ?" "de174076-8304-44bc-bb1e-ce2c1ce2e66b"])

  (sql/insert! db :ingredient {:recipe-id "de174076-8304-44bc-bb1e-ce2c1ce2e66b"
                               :ingredient-id "tes1t"
                               :name "some name"
                               :amount 1
                               :measure "kg"
                               :sort 1})

  ((rh/create-ingredient! db) {:parameters {:path {:recipe-id "de174076-8304-44bc-bb1e-ce2c1ce2e66b"}
                                            :body {:name "some name"
                                                   :amount 1
                                                   :measure "kg"
                                                   :sort 1}}})




  (sql/find-by-keys db :recipe {:public false}))
