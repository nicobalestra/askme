(ns askme.models.questions-db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [askme.models.schema :as schema]
            [taoensso.timbre :as timbre]
            [black.water.korma :refer [decorate-korma!]]))

(defdb db schema/db-spec)

(defentity answers)

(defentity users)

(defentity questions
  (has-many answers)
  (belongs-to users))

(defentity users
  (has-many questions)
  (has-many answers))


(defn search-user-questions [user question page num-records]
  (timbre/info "Search questions for User ? " (first user))
  (decorate-korma!)
  (select questions
          (where (and
                  {:question [like (str "%" question "%")]}
                  {:users_id (:id user)}))
          (with users)))

(defn search-all-questions [question page num-records]
  (timbre/info "Search questions all questions across all users ")
  (decorate-korma!)
  (select questions
       (where {:question [like (str "%" question "%")]})
          (with users)))

(defn search-questions [question page num-records & user]
  (let [user (first user)]
    (if-not (nil? user)
      (search-user-questions user question page num-records)
      (search-all-questions question page num-records))))



(defn insert-question [user_id question]
  (decorate-korma!)
  (insert questions
          (values {:question question :users_id user_id})))

(defn get-question [question]
  (decorate-korma!)
   (first (select questions
                    (where {:question question}))))

(defn get-user-questions [user page num]
 (timbre/warn "NEED TO IMPLEMENT PAGINATION")
  (decorate-korma!)
   (select questions
              (where  {:users_id (:id user)})))
