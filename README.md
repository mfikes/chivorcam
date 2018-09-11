# chivorcam
Macros directly in ClojureScript

[![Clojars Project](https://img.shields.io/clojars/v/chivorcam.svg)](https://clojars.org/chivorcam)

# Usage

You can use this library to define macros directly in ClojureScript source. 
Or even directly in the REPL:

```
cljs.user=> (require-macros '[chivorcam.core :refer [defmacro defmacfn]])
nil
cljs.user=> (defmacro add [a b] `(+ ~a ~b))
#'cljs.user/add
cljs.user=> (add 1 2)
3
```

What if your macro needs to call a function upon macro expansion? It can't call a 
ClojureScript function, as those aren't available at compilation time.
The `defmacfn` macro is like `defn` but defines a Clojure function directly from ClojureScript.

Example use:

```
cljs.user=> (defmacfn ensure-symbol [x] 
  (when-not (symbol? x) (throw (ex-info "Not a symbol" {:x x}))))
#'cljs.user/ensure-symbol
cljs.user=> (defmacro namespaced-symbol [ns sym]
 (ensure-symbol ns)
 (ensure-symbol sym)
 `'~(symbol (str ns) (str sym)))
#'cljs.user/namespaced-symbol
cljs.user=> (namespaced-symbol abc def)
abc/def
```

# Explanation

This works by defining `defmacro` and `defmacfn` as macros which simply define macros and functions in the “macros namespace” corresponding to the runtime namespace you are currently in.

Because of this, this makes it easy to mess with macros directly in the REPL. This could even be used in your production code, but a couple of things to consider:

1. It doesn't provide a way to work properly across namespaces. For real production code involving macros it is better to use ClojureScript's support for macros which is really designed to support [Namespaces](https://clojurescript.org/guides/ns-forms).
2. The `defmacfn` macro is non-standard (compared to just using `defn`), along with the concept of placing macros via `defmacro` directly in your ClojureScript source. Making use of this will hinder the ability of others to readily understand your code.
