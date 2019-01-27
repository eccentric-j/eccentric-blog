---
type: post
title: "Site Launch :tada:"
description: A brief introduction to this blog, development process, and goals.
author: Eccentric J
author-email: jayzawrotny@gmail.com
location: New York, USA
date-created: 2019-01-18T13:20:00-5:00
date-modified: 2019-01-23T13:35:00-5:00
date-published: 2019-01-25T18:00:00-5:00
in-language: en
keywords: a,b,c
canonical-url: http://blog.hashobject.com/celebrare-gutture
uuid: a811549b-242e-46dc-8813-27c9d20e5a9d
tags:
 - blog
 - projects
---
# Why even make a blog in 2019?

Likely fruitless, I admit, but some of my lengthy functional programming subreddit comments earned encouragement to be published somewhere. Well, now I have a _somewhere_ to publish to.

# What will this blog cover?

This blog covers functional programming topics in languages like Clojure, JavaScript, Elixir, Haskell, and Elm. However, since I enjoy Clojure and ClojureScript the most a lot of content will be focused there.

Additionally I will publish announcements of upcoming projects, tutorials\learning resources, open-ended ideas, and opinions on professional practices.

# What is this blog made with?

This site is statically generated using the Clojure https://perun.io/ which runs on top of [Boot](https://github.com/boot-clj) build tasks. I really like the concept of breaking the generation process into a pipeline of reusable tasks or functions that generate each type of page and process my source files like ClojureScript, SASS, and Markdown. If I don't like the way any one part behaves I can replace it pretty painlessly leveraging Boot's composability and immutable fileset system.

For example the production build pipeline looks like this:

```clj
(deftask build
  "Build the blog source and output to target/public"
  [e build-env BUILD-ENV kw    "Environment keyword like :dev or :production"]
  (let [prod? (= build-env :prod)]
   (pipeline
     (generate-site :prod? prod?)
     (seo-files prod?)
     (cljs :ids ["prod"])
     (sass :output-style :compressed :source-map false)
     (clean :exclude [#".git"])
     (target :no-clean true)
     (notify))))
```

# Are you really _eccentric_?

No. Probably not. It felt like a nice word to embody my awkwardness along with my love of programming, cats, atmospheric black metal, skulls, and occult imagery.