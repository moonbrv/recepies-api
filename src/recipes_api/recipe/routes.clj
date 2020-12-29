(ns recipes-api.recipe.routes
  (:require
   [recipes-api.recipe.handlers :as handler]))

(defn routes [env]
  (let [db (:jdbc-url env)]
    ["/recipes" {:swagger {:tags ["recipes"]}}
     ["" {:get {:handler (handler/list-all-recipes db)
                :summary "List all recipes"}}]
     ["/:recipe-id" {:get {:handler (handler/retrieve-recipe db)
                           :summary "Retrieve recipe"}}]]))
