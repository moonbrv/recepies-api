(ns recipes-api.recipe.routes)

(defn routes [env]
  ["/recipes" {:get {:handler (fn [req] {:status 200
                                         :body "Hello, Recipes"})}}])
