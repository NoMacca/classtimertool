(ns classtimertool.views.timer
  (:require
   [re-frame.core :as re-frame]
   [tools.viewtools :as vt]
   [tools.reframetools :as h]
   [cljs.pprint :as pp]
   [classtimertool.stylesgarden :as gstyle]
   ;; [reagent.core :as r]
   [reagent.core  :as reagent]
   [classtimertool.db :as db]
   ))
;; CURRENT TIME
(defn dispatch-timer-event
  []
  (let [now (h/now)]
    (re-frame/dispatch [:now now])))

(defonce do-timer (js/setInterval dispatch-timer-event 1000))

(defn timer [{:keys [id name start end length]}]
      (let [now @(re-frame/subscribe [:now])
            length-str (h/time-string :minutes length)
            time-used-mins-str (h/time-string :minutes (h/time-used now start))
            start-str (h/time-string :hours-minutes-seconds start)
            end-str (h/time-string :hours-minutes-seconds end)
            time-used-str (h/time-string :minutes-seconds (h/time-used now start))
            time-left-str (h/time-string :minutes-seconds (h/time-left now end))]

        (if (h/ended? now end)
          (do
            (re-frame/dispatch [:kill-timer id])
            (js/alert (str name " has finished"))
            )
          )

        (if (h/running? now start end)
          [:div.grid.grid-cols-3.p-6.m-4.rounded-xl.shadow-lg.items-center
           [:div.col-start-1.col-span-2 [:h2.font-bold name " for " length-str " min"]]
           [:div.col-start-3.text-right [:button.w-6.h-6.rounded.bg-cyan-200.hover:bg-blue-700
                                         {:on-click #(re-frame/dispatch [:kill-timer id])}
                                         "X"]]
           [:div.col-start-1 "Used: " time-used-str]
           [:div.col-start-3.text-right "Left: " time-left-str]

           [:div.col-start-1.col-span-3.bg-blue-50
            [:progress.w-full.h-6 {:value time-used-mins-str :max length-str }]]

           [:div.col-start-1 "Start: " start-str]
           [:div.col-start-2.text-center "Now: " (h/time-string :hours-minutes-seconds now)]
           [:div.col-start-3.text-right "End: " end-str]
           ]
          [:div.grid.grid-cols-3.p-6.m-4.rounded-xl.shadow-lg.items-center
           [:div.col-start-1.col-span-2 [:h2.font-bold name " for " length-str " min"]]
           [:div.col-start-3.text-right [:button.w-6.h-6.rounded.bg-cyan-200.hover:bg-blue-700
                                         {:on-click #(re-frame/dispatch [:kill-timer id])}
                                         "X"]]
           [:div.col-start-1.col-span-2 [:h2.font-bold "Class Starts at " start-str" in " (h/time-string :hours-minutes-seconds (h/time-left now start))]]
           ]
      )))

(defn timers []
  (let [running @(re-frame/subscribe [:running])]
    [:div
     (for [t running]
       ^{:key (:id t)} [timer t])]
    ))

(defn button []
  [:div
   [:button.btn.btn-primary.btn-lg.bg-red-500.rounded.rounded.p-6.hover:bg-blue-700
    {:style {:position "fixed" :bottom "10%" :right "10%"}}
    "Break!"]])

(defn quick-timers []
  [:div.p-6.m-4.rounded-xl.shadow-lg.items-center.grid.gap-4 {:class (gstyle/grid-auto-fit)}
   [:div.col-span-full [:h2.font-bold "Quick Timers"]]
   (for [length [

                 ;; {:hours 0 :minutes 0 :seconds 10}
                 {:hours 0 :minutes 0 :seconds 30}
                 {:hours 0 :minutes 1 :seconds 0}
                 {:hours 0 :minutes 1 :seconds 30}
                 {:hours 0 :minutes 2 :seconds 0}
                 {:hours 0 :minutes 3 :seconds 0}
                 {:hours 0 :minutes 4 :seconds 0}
                 {:hours 0 :minutes 5 :seconds 0}
                 {:hours 0 :minutes 10 :seconds 0}
                 {:hours 0 :minutes 15 :seconds 0}
                 {:hours 0 :minutes 20 :seconds 0}
                 {:hours 0 :minutes 25 :seconds 0}
                 {:hours 0 :minutes 30 :seconds 0}
                 {:hours 0 :minutes 40 :seconds 0}
                 ]]
     [:button.bg-white.shadow-md.rounded-full.flex.items-center.justify-center.h-24.w-24.active:bg-blue-700
      {
       :on-click #(re-frame/dispatch [:running-quick-timer length])
       :key (str length)}
      [:span.text-lg.font-bold (h/time-string :minutes-seconds length)]])])

(defn brain-breaks []
  [:div.grid.grid-cols-4.p-6.m-4.rounded-xl.shadow-lg.items-center
   [:div.col-start-1.col-span-3 [:h2.font-bold "Brain Breaks"]]
   [:div.col-start-4.text-right [:button.w-6.h-6.rounded.bg-cyan-200.hover:bg-blue-700 "X"]]
   [:div.col-start-1.col-span-3 [:p "Time Since Brain Break: 12 minutes"]]
   ]
  )

(defn main []
  [:<>
   [timers]
   ;; [brain-breaks]
   [quick-timers]
   ;; [button]
   ]
  )

;; ROUTING
(def toolbar-items
  [
   ["Timers" :routes/#frontpage]
   ["Classes" :routes/#classes]
   ;; ["About" :routes/#about]
   ])

(defn route-info [route]
  [:div.m-4
   [:p "Routeinfo"]
   [:pre.border-solid.border-2.rounded
    (with-out-str (pp/pprint route))]])

(defn show-panel [route]
  (when-let [route-data (:data route)]
    (let [view (:view route-data)]
      [:<>
       [view]
       ;; [route-info route]
       ])))

(defn main-panel []
  (let [active-route (re-frame/subscribe [:routes/current-route])]
    [:div
     [:nav.bg-gray-200.p-4
      [:div.container.mx-auto.flex.justify-between.items-center
       [:div.text-black.font-bold.text-lg "Class Timer"]
       [vt/navigation toolbar-items]]]
     [show-panel @active-route]]))
