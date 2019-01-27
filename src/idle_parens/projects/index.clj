(ns idle-parens.projects.index
  (:require
   [idle-parens.base :as base]
   [idle-parens.projects.project :refer [project-img]]
   [idle-parens.formats :refer [format-date format-time]]
   [clojure.string :refer [join trim]]))

(defn cover-style
  [{:keys [bg fg]}]
  (join ";" (filter some? [(when bg
                             (str "background-color:" bg))
                           (when fg
                             (str "color:" fg))])))

(defn render-summary
  [project]
  (let [img (project-img (:slug project))]
    [:article.project-summary
     {:class (trim
              (join " "
                    [(str "project-summary--slug_" (:slug project))
                     (:class project "")]))}
     [:img.project-summary__img
      {:src (str "/img/projects/" (:slug project) ".png")
       :alt (str "Cover art of " (:title project))}]
     [:a.project-summary__link
      {:style (cover-style project)
       :href (:permalink project)}
      [:span.project-summary__title.borders.padding
       (:title project)]
      [:span.project__meta
       [:time.project__field.icon.fa-calendar-day
        {:datetime (str (:date-published project))}
        (str "Released " (format-date (:date-published project)))]
       [:span.project__langs
        [:span.tags
         (for [lang (:languages project)]
           [:span.tag lang])]]]
      [:span.project__blurb.text-copy
       (:description project)]]]))


(defn render
  [{global-meta :meta projects :entries :as data}]
  (base/render
   :title (:site-title global-meta)
   :subtitle "active-parens/projects"
   :current :projects
   :content [:div.projects
             [:ul.projects__list
              (for [project projects]
                [:li.projects__item
                 (render-summary project)])]]))
