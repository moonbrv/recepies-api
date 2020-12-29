(ns recipes-api.recipe.db
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]))

(defn find-all-recipes [db uid]
  (with-open [conn (jdbc/get-connection db)]
    (let [public (sql/find-by-keys conn :recipe {:public true})]
      (if uid
        (let [drafts (sql/find-by-keys conn :recipe {:public false
                                                     :uid uid})]
          {:public public
           :drafts drafts})
        {:public public}))))

(defn find-recipe-by-id [db recipe-id]
  (with-open [conn (jdbc/get-connection db)]
    (when-let [recipe (sql/get-by-id conn :recipe recipe-id :recipe_id {})]
      (let [steps (sql/find-by-keys conn :step {:recipe_id recipe-id})
            ingredients (sql/find-by-keys conn :ingredient {:recipe_id recipe-id})]
        (assoc recipe
               :recipe/steps steps
               :recipe/ingredients ingredients)))))
