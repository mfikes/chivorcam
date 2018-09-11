# Chivorcam

Macros directly in ClojureScript.

[![Clojars Project](https://img.shields.io/clojars/v/chivorcam.svg)](https://clojars.org/chivorcam)

# Usage

You can use this library to define macros directly in ClojureScript source.

Or even directly in the REPL:

```
cljs.user=> (require '[chivorcam.core :refer [defmacro defmacfn]])
nil
cljs.user=> (defmacro add [a b]
       #_=>  `(+ ~a ~b))
#'cljs.user/add
cljs.user=> (add 1 2)
3
```

This library works with both JVM and self-hosted ClojureScript, as well as Clojure.

## Defining and Calling Functions

What if your macro needs to call a helper function upon macro expansion? 
It can't call a ClojureScript function, as those aren't available at 
compilation time.

To solve this, `defmacfn` is like `defn` but defines functions that can
be called by macros. 

Example use:

```
cljs.user=> (defmacfn to-prefix [[lhs op rhs]]
       #_=>  (list op lhs rhs))
#'cljs.user/to-prefix
cljs.user=> (defmacro eval-infix [form]
       #_=>  (to-prefix form))
#'cljs.user/eval-infix
cljs.user=> (eval-infix (1 + 2))
3
```

# Explanation

This library works by defining `defmacro` and `defmacfn` as macros which simply 
define macros and functions in the “macros namespace” corresponding to the 
runtime namespace they are being used in.

Because of this, this makes it easy to mess with macros directly in the REPL. 
This could even be used in your production code, but a couple of things to consider:

1. It doesn't really provide a way to work properly across namespaces. 
For production code involving macros it is better to use ClojureScript's 
support for macros which is really designed to support 
[Namespaces](https://clojurescript.org/guides/ns-forms).
2. The `defmacfn` macro is non-standard (compared to just using `defn`), 
along with the concept of placing macros via `defmacro` directly in your 
ClojureScript source. Making use of this will hinder the ability of others 
to readily understand your code.
3. Even though this library works with self-hosted ClojureScript, if your 
macros need to perform host interop, that would be difficult to manage 
using reader conditionals because the `:cljs` branch would always be taken.
