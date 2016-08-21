# imperimetric

#### Live at http://www.imperimetric.com/

Webapp for converting texts with some system of measurement to another, such as imperial to metric.
Uses [Clojure](http://clojure.org/), [ClojureScript](http://clojurescript.org/), [Reagent](https://reagent-project.github.io/), and [re-frame](https://github.com/Day8/re-frame).

Libraries that have been very helpful:

[Instaparse](https://github.com/Engelberg/instaparse)

[frinj](https://github.com/martintrojer/frinj)

## Development Mode

### Compile css:

Compile css file once.

```
lein less once
```

Automatically recompile css file on change.

```
lein less auto
```

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Run tests:

```
lein clean
lein midje
```
or, to have midje continuously run tests as you edit files:
````
lein clean
lein midje :autotest
```

## Production Build

```
lein clean
lein uberjar
```

That should compile the clojurescript code first, and then create the standalone jar.

When you run the jar you can set the port the ring server will use by setting the environment variable PORT.
If it's not set, it will run on port 3000 by default.

If you only want to compile the clojurescript code:

```
lein clean
lein cljsbuild once min
```
