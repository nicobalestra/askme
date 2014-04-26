(ns askme.views.layout
  (:require [selmer.parser :as parser]
            [clojure.string :as s]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]
            [noir.session :as session]))

(def template-path "askme/views/templates/")

(deftype RenderableTemplate [template params]
  Renderable
  (render [this request]
    (content-type
      (->>
         (parser/render-file (str template-path template)
                            (assoc params
          (keyword (s/replace template #".html" "-selected")) "active"
          :servlet-context (:context request)
          :user (session/get :user))
                           {:tag-open \[
                            :tag-close \]})


        response)
      "text/html; charset=utf-8")
     ))

(defn render [template & [params]]
  (RenderableTemplate. template params))


(defn render-html [page]
  (render page))
