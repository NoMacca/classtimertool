(ns classtimertool.views.timer
  (:require
   [re-frame.core :as re-frame]
   [classtimertool.toolsview :as vt]
   [classtimertool.toolsreframe :as h]
   [cljs.pprint :as pp]
   [classtimertool.stylesgarden :as gstyle]
   [reagent.core  :as reagent]
   [classtimertool.db :as db]
   ))
;; CURRENT TIME
(defn dispatch-timer-event
  []
  (let [now (h/now)]
    (re-frame/dispatch [:now now])))

(defonce do-timer (js/setInterval dispatch-timer-event 1000))

(defn timer [now {:keys [id name start end]}]
  (let [length (h/duration start end)
        time-used (h/time-used now start)
        time-left (h/time-left now end)

        ;;Above progress bar
        time-used-str (h/duration->string time-used)
        length-str (h/duration->minutes-seconds-string length)
        time-left-str (h/duration->string time-left)

        ;; For progress bar
        progress-bar-length-str (h/duration->seconds->string length)
        progress-bar-time-used-str (h/duration->seconds->string time-used)

        ;; Below progress bar
        start-str (h/twelve-hour-time start)
        now-str (h/twelve-hour-time now)
        end-str (h/twelve-hour-time end)]

    ;; (if (= time-left-str nil)
    ;;     (js/alert (str name " has finished"))
    ;;     )

    (if (h/running? now start end)
      [:div.grid.grid-cols-3.p-6.m-4.rounded-xl.shadow-lg.items-center

       [:div.col-start-1.col-span-2 [:h2.font-bold name]]
       [:div.col-start-3.text-right [:button.w-6.h-6.rounded.bg-cyan-200.hover:bg-blue-700
                                     {:on-click #(re-frame/dispatch [:kill-timer id])}
                                     "X"]]

       [:div.col-start-1 "Used: " time-used-str]
       [:div.col-start-2.text-center "Length: " length-str]
       [:div.col-start-3.text-right "Left: " time-left-str]

       [:div.col-start-1.col-span-3.bg-blue-50
        [:progress.w-full.h-6 {:value progress-bar-time-used-str :max progress-bar-length-str }]]
       [:div.col-start-1 "Start: " start-str]
       [:div.col-start-2.text-center "Now: " now-str]
       [:div.col-start-3.text-right "End: " end-str]
       ]
      [:div.grid.grid-cols-3.p-6.m-4.rounded-xl.shadow-lg.items-center.bg-gray-300
       [:div.col-start-1.col-span-2 [:h2.font-bold name]]
       [:div.col-start-3.text-right [:button.w-6.h-6.rounded.bg-cyan-200.hover:bg-blue-700
                                     {:on-click #(re-frame/dispatch [:kill-timer id])}
                                     "X"]]
       [:div.col-start-1.col-span-2 [:p length-str " timer" ]]
       [:div.col-start-1.col-span-2 [:p start-str "-" end-str ]]
       (if (h/timer-ended? now end)
         ;; (do (if (h/timer-ended-for-hour? now end) (re-frame/dispatch [:kill-timer id]))
         [:div.col-start-1.col-span-2 [:h2.font-bold "Ended " time-left-str " ago"]]
             ;; )
         [:div.col-start-1.col-span-2 [:h2.font-bold "Starts in " time-used-str]]
         )]
      )))

;;================================================================================

(defn timers []
  (let [running @(re-frame/subscribe [:running])
        now @(re-frame/subscribe [:now])]
    [:div
     (for [t running]
       ^{:key (:id t)} [timer now t])]
    ))


(defn add-class []
  (let [open-dialog? (reagent/atom false)
        title  (reagent/atom nil)
        length  (reagent/atom nil)
        ]
    (fn []
      (if @open-dialog?
        [:div.fixed.top-0.left-0.w-full.h-full.bg-black.bg-opacity-50.flex.items-center.justify-center
         [:dialog.grid.grid-cols-2.gap-1.bg-white.p-6.rounded.shadow-lg {:id "my-dialog"
                                                                         :open @open-dialog?
                                                                         }

          [:div.col-start-1 [:h2.font-bold "Create a class"]]
          [:div.col-start-2.text-right [:button.w-6.h-6.rounded.bg-cyan-200
                                        {:on-click #(reset! open-dialog? false)
                                         }
                                        "X"]]
          [:div.col-start-1 "Title"]
          [:div.col-start-2 "Length (m)"]

          [:div.col-start-1
           [:input.mt-1.block.w-full.px-3.py-2.bg-white.border.border-slate-300.rounded-md.text-sm.shadow-sm.placeholder-slate-400.focus:outline-none.focus:border-sky-500.focus:ring-1.focus:ring-sky-500.disabled:bg-slate-50.disabled:text-slate-500.disabled:border-slate-200.disabled:shadow-none.invalid:border-pink-500.invalid:text-pink-600.focus:invalid:border-pink-500.focus:invalid:ring-pink-500
            {:type "text"
             ;; :value @title
             :on-change #(reset! title (-> % .-target .-value))
             }]]

          [:div.col-start-2       [:input.mt-1.block.w-full.px-3.py-2.bg-white.border.border-slate-300.rounded-md.text-sm.shadow-sm.placeholder-slate-400.focus:outline-none.focus:border-sky-500.focus:ring-1.focus:ring-sky-500.disabled:bg-slate-50.disabled:text-slate-500.disabled:border-slate-200.disabled:shadow-none.invalid:border-pink-500.invalid:text-pink-600.focus:invalid:border-pink-500.focus:invalid:ring-pink-500
                                   {
                                    :type "number"
                                    :value @length
                                    :on-change #(reset! length (-> % .-target .-value))
                                    :autocomplete "off"
                                    ;; :value "tbone"
                                    ;; :disabled true
                                    }]]

          [:div.col-span-full.text-right [:button.rounded.bg-blue-600.py-1.w-full.hover:bg-blue-700
                                        {:on-click #(do
                                                      (reset! open-dialog? false)
                                                      (re-frame/dispatch [:add-timer [@title @length]])
                                                      )
                                         }
                                        "Start"]]]]
        [:div.grid.grid-cols-2.gap-2
           {:style {:position "fixed" :bottom "10%" :right "10%"}}
         [:button.btn.btn-primary.btn-lg.bg-red-500.rounded.rounded.p-6.hover:bg-red-700
          {:on-click #(re-frame/dispatch [:delete-all-running])}
          "Delete All"]
         [:button.btn.btn-primary.btn-lg.bg-blue-500.rounded.rounded.p-6.hover:bg-blue-700
          {:on-click #(reset! open-dialog? true)
           ;; :style {:position "fixed" :bottom "20%" :right "30%"}
           }
          "Add Timer"]]
        ))))

(defn quick-timers []

  (let [quick-timers-display (reagent/atom false)]
    (fn []
      (if @quick-timers-display
        [:div.p-6.m-4.rounded-xl.shadow-lg.items-center.grid.gap-4 {:class (gstyle/grid-auto-fit)}
         [:div.col-span-full
          [:button.font-bold.text-blue-500
           {:on-click #(reset! quick-timers-display false)}
           "Quick timers"]]

         (for [length-seconds [15 30 60 90 120 180 240 300 600 900 1200 1500 1800 2400]]
           (let [length (h/seconds->duration length-seconds)]
             [:button.bg-white.shadow-md.rounded-full.flex.items-center.justify-center.h-24.w-24.active:bg-blue-700
              {
               :on-click #(re-frame/dispatch [:running-quick-timer length])
               :key (str length-seconds)}
              [:span.text-lg.font-bold (h/duration->minutes-seconds-string length)]]))]
        [:div.p-6.m-4.rounded-xl.shadow-lg.items-center.grid.gap-4 {:class (gstyle/grid-auto-fit)}
         [:div.col-span-full
          [:button.font-bold.text-blue-500
           {:on-click #(reset! quick-timers-display true)}
           "Quick timers"]]]
      ))))
;;================================================================================
(defn brain-breaks []
  (let [brain-breaks-display (reagent/atom false)]
    (fn []
      (let [brainbreak @(re-frame/subscribe [:brainbreak])
            now @(re-frame/subscribe [:now])
            breaking (:breaking brainbreak)
            last-str (h/duration->string (h/time-used now (:last brainbreak)))]

        (if @brain-breaks-display
          [:div.grid.grid-cols-4.p-6.m-4.rounded-xl.shadow-lg.items-center
           [:div.col-start-1.col-span-3 [:button.font-bold.text-blue-500
                                         {:on-click #(reset! brain-breaks-display false)}
                                         "Brain Breaks"]]
           [:div.col-start-1.col-span-3 [:p
                                         (if breaking
                                           (str "Breaking for " last-str)
                                           (str "Last brainbreak was " last-str " ago" ))
                                         ]]
           [:div.cold-start-3.text-right
            (if breaking
              [:button.rounded.bg-blue-600.py-1.w-full.hover:bg-blue-700
               {:on-click #(re-frame/dispatch [:toggle-brainbreak now])}
               "Stop"]
              [:button.rounded.bg-green-600.py-1.w-full.hover:bg-green-700
               {:on-click #(re-frame/dispatch [:toggle-brainbreak now])}
               "Start"]
              )]]

          [:div.grid.grid-cols-4.p-6.m-4.rounded-xl.shadow-lg.items-center
           [:div.col-start-1.col-span-3 [:button.font-bold.text-blue-500
                                         {:on-click #(reset! brain-breaks-display true)}
                                         "Brain Breaks"]]]
          )
        ))))

(defn main []
  [:<>
   [timers]
   [brain-breaks]
   [quick-timers]
   [add-class]
   ]
  )

;; ROUTING
(def toolbar-items
  [
   ["Timers" :routes/#frontpage]
   ["Classes" :routes/#classes]
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
