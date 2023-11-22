(ns hyperfiddle.electric-svg
  "SVG support is experimental, API subject to change"
  (:refer-clojure :exclude [filter set symbol use])
  (:require [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.electric :as e])
  #?(:cljs (:require-macros [hyperfiddle.electric-svg])))

#?(:cljs
   (defn new-svg-node [parent type]
     (let [el (.createElementNS js/document dom/SVG-NS type)]
       (.appendChild parent el)
       el)))

(defmacro svg-element [t & body] ; todo unify with dom/element
  `(e/client
     (dom/with (new-svg-node dom/node ~(name t))
                                        ; hack: speed up streamy unmount by removing from layout first
                                        ; it also feels faster visually
       (e/on-unmount (partial dom/hide dom/node)) ; hack
       ~@body)))

(defmacro a                   [& body] `(svg-element :a ~@body))
(defmacro altGlyph            [& body] `(svg-element :altGlyph ~@body))
(defmacro altGlyphDef         [& body] `(svg-element :altGlyphDef ~@body))
(defmacro altGlyphItem        [& body] `(svg-element :altGlyphItem ~@body))
(defmacro animate             [& body] `(svg-element :animate ~@body))
(defmacro animateMotion       [& body] `(svg-element :animateMotion ~@body))
(defmacro animateTransform    [& body] `(svg-element :animateTransform ~@body))
(defmacro circle              [& body] `(svg-element :circle ~@body))
(defmacro clipPath            [& body] `(svg-element :clipPath ~@body))
(defmacro color-profile       [& body] `(svg-element :color-profile ~@body))
(defmacro cursor              [& body] `(svg-element :cursor ~@body))
(defmacro defs                [& body] `(svg-element :defs ~@body))
(defmacro desc                [& body] `(svg-element :desc ~@body))
(defmacro ellipse             [& body] `(svg-element :ellipse ~@body))
(defmacro feBlend             [& body] `(svg-element :feBlend ~@body))
(defmacro feColorMatrix       [& body] `(svg-element :feColorMatrix ~@body))
(defmacro feComponentTransfer [& body] `(svg-element :feComponentTransfer ~@body))
(defmacro feComposite         [& body] `(svg-element :feComposite ~@body))
(defmacro feConvolveMatrix    [& body] `(svg-element :feConvolveMatrix ~@body))
(defmacro feDiffuseLighting   [& body] `(svg-element :feDiffuseLighting ~@body))
(defmacro feDisplacementMap   [& body] `(svg-element :feDisplacementMap ~@body))
(defmacro feDistantLight      [& body] `(svg-element :feDistantLight ~@body))
(defmacro feFlood             [& body] `(svg-element :feFlood ~@body))
(defmacro feFuncA             [& body] `(svg-element :feFuncA ~@body))
(defmacro feFuncB             [& body] `(svg-element :feFuncB ~@body))
(defmacro feFuncG             [& body] `(svg-element :feFuncG ~@body))
(defmacro feFuncR             [& body] `(svg-element :feFuncR ~@body))
(defmacro feGaussianBlur      [& body] `(svg-element :feGaussianBlur ~@body))
(defmacro feImage             [& body] `(svg-element :feImage ~@body))
(defmacro feMerge             [& body] `(svg-element :feMerge ~@body))
(defmacro feMergeNode         [& body] `(svg-element :feMergeNode ~@body))
(defmacro feMorphology        [& body] `(svg-element :feMorphology ~@body))
(defmacro feOffset            [& body] `(svg-element :feOffset ~@body))
(defmacro fePointLight        [& body] `(svg-element :fePointLight ~@body))
(defmacro feSpecularLighting  [& body] `(svg-element :feSpecularLighting ~@body))
(defmacro feSpotLight         [& body] `(svg-element :feSpotLight ~@body))
(defmacro feTile              [& body] `(svg-element :feTile ~@body))
(defmacro feTurbulence        [& body] `(svg-element :feTurbulence ~@body))
(defmacro filter              [& body] `(svg-element :filter ~@body))
(defmacro font                [& body] `(svg-element :font ~@body))
(defmacro font-face           [& body] `(svg-element :font-face ~@body))
(defmacro font-face-format    [& body] `(svg-element :font-face-format ~@body))
(defmacro font-face-name      [& body] `(svg-element :font-face-name ~@body))
(defmacro font-face-src       [& body] `(svg-element :font-face-src ~@body))
(defmacro font-face-uri       [& body] `(svg-element :font-face-uri ~@body))
(defmacro foreignObject       [& body] `(svg-element :foreignObject ~@body))
(defmacro g                   [& body] `(svg-element :g ~@body))
(defmacro glyph               [& body] `(svg-element :glyph ~@body))
(defmacro glyphRef            [& body] `(svg-element :glyphRef ~@body))
(defmacro hkern               [& body] `(svg-element :hkern ~@body))
(defmacro image               [& body] `(svg-element :image ~@body))
(defmacro line                [& body] `(svg-element :line ~@body))
(defmacro linearGradient      [& body] `(svg-element :linearGradient ~@body))
(defmacro marker              [& body] `(svg-element :marker ~@body))
(defmacro mask                [& body] `(svg-element :mask ~@body))
(defmacro metadata            [& body] `(svg-element :metadata ~@body))
(defmacro missing-glyph       [& body] `(svg-element :missing-glyph ~@body))
(defmacro mpath               [& body] `(svg-element :mpath ~@body))
(defmacro path                [& body] `(svg-element :path ~@body))
(defmacro pattern             [& body] `(svg-element :pattern ~@body))
(defmacro polygon             [& body] `(svg-element :polygon ~@body))
(defmacro polyline            [& body] `(svg-element :polyline ~@body))
(defmacro radialGradient      [& body] `(svg-element :radialGradient ~@body))
(defmacro rect                [& body] `(svg-element :rect ~@body))
(defmacro script              [& body] `(svg-element :script ~@body))
(defmacro set                 [& body] `(svg-element :set ~@body))
(defmacro stop                [& body] `(svg-element :stop ~@body))
(defmacro style               [& body] `(svg-element :style ~@body))
(defmacro svg                 [& body] `(svg-element :svg ~@body))
(defmacro switch              [& body] `(svg-element :switch ~@body))
(defmacro symbol              [& body] `(svg-element :symbol ~@body))
(defmacro text                [& body] `(svg-element :text ~@body))
(defmacro textPath            [& body] `(svg-element :textPath ~@body))
(defmacro title               [& body] `(svg-element :title ~@body))
(defmacro tref                [& body] `(svg-element :tref ~@body))
(defmacro tspan               [& body] `(svg-element :tspan ~@body))
(defmacro use                 [& body] `(svg-element :use ~@body))
(defmacro view                [& body] `(svg-element :view ~@body))
(defmacro vkern               [& body] `(svg-element :vkern ~@body))


