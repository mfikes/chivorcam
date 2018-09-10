(ns chivorcam.core
  (:refer-clojure :exclude [defmacro])
  (:require
   [cljs.env :as env]
   [cljs.analyzer :as ana :refer [*cljs-ns*]]))

(defn- eval-form
  [form ns]
  (when-not (find-ns ns)
    #?(:clj  (create-ns ns)
       :cljs (eval `(~'ns ~ns))))
  (binding #?(:clj  [*ns* (the-ns ns)]
              :cljs [*ns* (find-ns ns)])
    (eval `(do
             (clojure.core/refer-clojure)
             ~form))))

(defn- fake-var [ns sym]
  (symbol (str "#'" ns) (str sym)))

(defn- macros-ns [sym]
  #?(:clj sym
     :cljs (symbol (str sym "$macros"))))

(clojure.core/defmacro defmacfn
  [name & args]
  (eval-form (list* 'clojure.core/defn name args) (macros-ns *cljs-ns*))
  `'~(fake-var *cljs-ns* name))

(clojure.core/defmacro defmacro
  [name & args]
  (eval-form (list* 'clojure.core/defmacro name args) (macros-ns *cljs-ns*))
  (swap! env/*compiler* update-in [::ana/namespaces *cljs-ns* :require-macros] assoc *cljs-ns* *cljs-ns*)
  (swap! env/*compiler* update-in [::ana/namespaces *cljs-ns* :use-macros] assoc name *cljs-ns*)
  `'~(fake-var *cljs-ns* name))
