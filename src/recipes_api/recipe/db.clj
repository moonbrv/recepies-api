(ns recipes-api.recipe.db
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [recipes-api.utils :as u]))

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

(defn insert-recipe! [db recipe]
  (sql/insert! db :recipe (assoc recipe
                                 :favorite-count 0
                                 :public false)))

(defn update-recipe! [db {:keys [recipe-id] :as recipe}]
  (u/db-data-updated? (sql/update! db :recipe recipe {:recipe-id recipe-id})))

(defn delete-recipe! [db id]
  (u/db-data-updated? (sql/delete! db :recipe {:recipe-id id})))

(defn favorite-recipe! [db id uid]
  (-> (jdbc/with-transaction [tx db]
        (sql/insert! tx :recipe-favorite {:uid uid
                                          :recipe-id id} (:options db))
        (jdbc/execute-one! tx ["UPDATE recipe 
                                SET favorite_count = favorite_count + 1 
                                WHERE recipe_id = ?" id]))
      u/db-data-updated?))

(defn unfavorite-recipe! [db id uid]
  (-> (jdbc/with-transaction [tx db]
        (sql/delete! tx :recipe-favorite {:uid uid
                                          :recipe-id id} (:options db))
        (jdbc/execute-one! tx ["UPDATE recipe 
                                SET favorite_count = favorite_count - 1 
                                WHERE recipe_id = ?" id]))
      u/db-data-updated?))

(defn insert-step! [db step]
  (sql/insert! db :step step))

(defn update-step! [db {:keys [step-id] :as step}]
  (u/db-data-updated? (sql/update! db :step step {:step-id step-id})))

(defn delete-step! [db step-id]
  (u/db-data-updated? (sql/delete! db :step {:step-id step-id})))
