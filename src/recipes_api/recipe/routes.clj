(ns recipes-api.recipe.routes
  (:require
   [recipes-api.recipe.handlers :as recipe]
   [recipes-api.responses :as responses]
   [recipes-api.middleware :as mw]))

(defn routes [env]
  (let [{:keys [jwk-endpoint]
         db :jdbc-url} env]
    ["/recipes" {:swagger {:tags ["recipes"]}
                 :middleware [[(mw/wrap-auth jwk-endpoint)]]}
     ["" {:get {:handler (recipe/list-all-recipes db)
                :responses {200 {:body responses/recipes}}
                :summary "List all recipes"}

          :post {:handler (recipe/create-recipe! db)
                 :parameters {:body {:name string?
                                     :prep-time number?
                                     :img string?}}
                 :responses {201 {:body {:recipe-id string?}}}
                 :summary "Create new recipe"}}]

     ["/:recipe-id"
      ["" {:get {:handler (recipe/retrieve-recipe db)
                 :parameters {:path {:recipe-id string?}}
                 :responses {200 {:body responses/recipe}}
                 :summary "Retrieve recipe"}

           :delete {:handler (recipe/delete-recipe! db)
                    :middleware [[mw/wrap-check-recipe-owner db]]
                    :parameters {:path {:recipe-id string?}}
                    :responses {204 {:body nil?}}
                    :summary "Delete recipe"}

           :put {:handler (recipe/update-recipe! db)
                 :middleware [[mw/wrap-check-recipe-owner db]]
                 :parameters {:path {:recipe-id string?}
                              :body {:name string?
                                     :prep-time number?
                                     :public boolean?
                                     :img string?}}
                 :responses {204 {:body nil?}}
                 :summary "Update recipe"}}]

      ["/steps" {:post {:handler (recipe/create-step! db)
                        :parameters {:path {:recipe-id string?}
                                     :body {:description string?
                                            :sort int?}}
                        :responses {201 {:body {:step-id string?}}}
                        :summary "Create step"}

                 :delete {:handler (recipe/delete-step! db)
                          :middleware [[mw/wrap-check-recipe-owner db]]
                          :parameters {:path {:recipe-id string?}
                                       :body {:step-id string?}}
                          :responses {204 {:body nil?}}
                          :summary "Delete step"}

                 :put {:handler (recipe/update-step! db)
                       :middleware [[mw/wrap-check-recipe-owner db]]
                       :parameters {:path {:recipe-id string?}
                                    :body {:description string?
                                           :step-id string?
                                           :sort int?}}
                       :responses {204 {:body nil?}}
                       :summary "Update step"}}]

      ["/favorite" {:post {:handler (recipe/favorite-recipe! db)
                           :parameters {:path {:recipe-id string?}}
                           :responses {204 {:body nil?}}
                           :summary "Favorite recipe"}

                    :delete {:handler (recipe/unfavorite-recipe! db)
                             :parameters {:path {:recipe-id string?}}
                             :responses {204 {:body nil?}}
                             :summary "Unfavorite recipe"}}]]]))
