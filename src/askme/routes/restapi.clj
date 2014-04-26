(ns askme.routes.restapi
  (:use compojure.core)
  (:require [ring.util.response :refer [content-type response]]
            [taoensso.timbre :as timbre]
            [liberator.core :refer [resource defresource]]
            [askme.util :refer [check-content-type parse-json jsonify]]
            [askme.models.questions-db :as questions]
            [environ.core :refer [env]]
            [noir.session :as session]
            [askme.logic.user :as user]
            [clojure.data.json :as json]))

(def MAX-PAGE-SIZE ((env :rest) :max-page-size))

(defn search-question [query user ]
  (timbre/info "Call to search question with query " query " and user " user)
  (let [res (questions/search-questions query 1 MAX-PAGE-SIZE user)]
     (if-not (empty? res)
        (timbre/info "Found matches " res)
        (timbre/info "No matches found"))
     {:response "ok" :results res}))


(defresource ask-question [question]
  :known-content-type? #(check-content-type % ["application/json"])
  :handle-ok   (fn [ctx]
                 (timbre/info "*** Get /question/" question " ***")
                 (timbre/info "REQUEST: " ctx)
                 (let [response-json (-> (session/get! :user)
                                         (search-question  question)
                                         (json/write-str :value-fn jsonify))]
                     response-json))

  :malformed? #(parse-json % ::data)
  :authorized? (fn [{{method :request-method } :request}]
                   [(not (and (= method :post) (user/is-anonymous)))  {:message "You need to login to ask a question"}]
               )
  :available-media-types ["application/json"]
  :allowed-methods [:post :get]
  :post! (fn [ctx]
           (let [question (get ctx ::data)]
             (timbre/info "Insert new question " question)
             (-> (session/get! :user)
                 (questions/insert-question question)
                 (json/write-str :value-fn jsonify))))
  )


(defresource get-recent []
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :handle-ok (fn[ctx]
               (timbre/info "Call to recent questions...")
               (-> (questions/search-all-questions "" 1 MAX-PAGE-SIZE)
                  (json/write-str  :value-fn jsonify)))

)


(defroutes liberator-res
  (ANY "/questions/ask/:question" [question]   (ask-question question))
  (GET "/questions/recents" [] (get-recent)))
