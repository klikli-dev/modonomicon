"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[9661],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>g});var o=n(7294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,o)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,o,r=function(e,t){if(null==e)return{};var n,o,r={},a=Object.keys(e);for(o=0;o<a.length;o++)n=a[o],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(o=0;o<a.length;o++)n=a[o],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var l=o.createContext({}),p=function(e){var t=o.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},u=function(e){var t=p(e.components);return o.createElement(l.Provider,{value:t},e.children)},c="mdxType",m={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},d=o.forwardRef((function(e,t){var n=e.components,r=e.mdxType,a=e.originalType,l=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),c=p(n),d=r,g=c["".concat(l,".").concat(d)]||c[d]||m[d]||a;return n?o.createElement(g,i(i({ref:t},u),{},{components:n})):o.createElement(g,i({ref:t},u))}));function g(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var a=n.length,i=new Array(a);i[0]=d;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s[c]="string"==typeof e?e:r,i[1]=s;for(var p=2;p<a;p++)i[p]=n[p];return o.createElement.apply(null,i)}return o.createElement.apply(null,n)}d.displayName="MDXCreateElement"},3688:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>i,default:()=>m,frontMatter:()=>a,metadata:()=>s,toc:()=>p});var o=n(7462),r=(n(7294),n(3905));const a={sidebar_position:30},i="Categories",s={unversionedId:"basics/structure/categories",id:"basics/structure/categories",title:"Categories",description:"Categories are defined in json files placed in the /data//modonomicons//categories/ folder.",source:"@site/docs/basics/structure/categories.md",sourceDirName:"basics/structure",slug:"/basics/structure/categories",permalink:"/modonomicon/docs/basics/structure/categories",draft:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/structure/categories.md",tags:[],version:"current",sidebarPosition:30,frontMatter:{sidebar_position:30},sidebar:"tutorialSidebar",previous:{title:"Book.json",permalink:"/modonomicon/docs/basics/structure/book"},next:{title:"Entries",permalink:"/modonomicon/docs/basics/structure/entries"}},l={},p=[{value:"Attributes",id:"attributes",level:2},{value:"<strong>name</strong> (DescriptionId, <em>mandatory</em>)",id:"name-descriptionid-mandatory",level:3},{value:"<strong>icon</strong> (ResourceLocation, <em>mandatory</em>)",id:"icon-resourcelocation-mandatory",level:3},{value:"<strong>sort_number</strong> (Integer, <em>optional</em>)",id:"sort_number-integer-optional",level:3},{value:"<strong>condition</strong> (Condition, <em>optional</em>)",id:"condition-condition-optional",level:3},{value:"<strong>background</strong> (ResourceLocation, <em>optional</em>)",id:"background-resourcelocation-optional",level:3},{value:"<strong>background_parallax_layers</strong> (JSON Array of JSON Objects, <em>optional</em>)",id:"background_parallax_layers-json-array-of-json-objects-optional",level:3},{value:"<strong>background_height</strong> (Integer, <em>optional</em>)",id:"background_height-integer-optional",level:3},{value:"<strong>background_width</strong> (Integer, <em>optional</em>)",id:"background_width-integer-optional",level:3},{value:"<strong>entry_textures</strong> (ResourceLocation, <em>optional</em>)",id:"entry_textures-resourcelocation-optional",level:3},{value:"<strong>show_category_button</strong> (Boolean, <em>optional</em>)",id:"show_category_button-boolean-optional",level:3},{value:"Usage Examples",id:"usage-examples",level:2}],u={toc:p},c="wrapper";function m(e){let{components:t,...n}=e;return(0,r.kt)(c,(0,o.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"categories"},"Categories"),(0,r.kt)("p",null,"Categories are defined in json files placed in the ",(0,r.kt)("inlineCode",{parentName:"p"},"/data/<mod_id>/modonomicons/<book_id>/categories/")," folder. "),(0,r.kt)("h2",{id:"attributes"},"Attributes"),(0,r.kt)("h3",{id:"name-descriptionid-mandatory"},(0,r.kt)("strong",{parentName:"h3"},"name")," (DescriptionId, ",(0,r.kt)("em",{parentName:"h3"},"mandatory"),")"),(0,r.kt)("p",null,"The category name. Will not parse markdown."),(0,r.kt)("h3",{id:"icon-resourcelocation-mandatory"},(0,r.kt)("strong",{parentName:"h3"},"icon")," (ResourceLocation, ",(0,r.kt)("em",{parentName:"h3"},"mandatory"),")"),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Either")," an item/block ResourceLocation that should be used as icon. E.g.:  ",(0,r.kt)("inlineCode",{parentName:"p"},"minecraft:nether_star")," or ",(0,r.kt)("inlineCode",{parentName:"p"},"minecraft:chest"),".",(0,r.kt)("br",{parentName:"p"}),"\n",(0,r.kt)("strong",{parentName:"p"},"Or")," the ResourceLocation to a texture. The texture must be 16x16 pixels. E.g.:  ",(0,r.kt)("inlineCode",{parentName:"p"},"modonomicon:textures/gui/some_random_icon.png"),". "),(0,r.kt)("admonition",{type:"tip"},(0,r.kt)("p",{parentName:"admonition"},"To use a texture make sure the ResourceLocation includes the file endinge ",(0,r.kt)("inlineCode",{parentName:"p"},".png")," as seen in the example above.")),(0,r.kt)("h3",{id:"sort_number-integer-optional"},(0,r.kt)("strong",{parentName:"h3"},"sort_number")," (Integer, ",(0,r.kt)("em",{parentName:"h3"},"optional"),")"),(0,r.kt)("p",null,"Defaults to ",(0,r.kt)("inlineCode",{parentName:"p"},"-1"),".",(0,r.kt)("br",{parentName:"p"}),"\n",'Category "Bookmark"-Buttos on the left side of the Book will be sorted by this number.'),(0,r.kt)("h3",{id:"condition-condition-optional"},(0,r.kt)("strong",{parentName:"h3"},"condition")," (Condition, ",(0,r.kt)("em",{parentName:"h3"},"optional"),")"),(0,r.kt)("p",null,"Categories, like Entries, can be hidden until an Unlock Condition is fulfilled. Conditions are JSON objects.",(0,r.kt)("br",{parentName:"p"}),"\n","See ",(0,r.kt)("strong",{parentName:"p"},(0,r.kt)("a",{parentName:"strong",href:"../unlock-conditions"},"Unlock Conditions"))," for details."),(0,r.kt)("h3",{id:"background-resourcelocation-optional"},(0,r.kt)("strong",{parentName:"h3"},"background")," (ResourceLocation, ",(0,r.kt)("em",{parentName:"h3"},"optional"),")"),(0,r.kt)("p",null,"Defaults to ",(0,r.kt)("inlineCode",{parentName:"p"},"modonomicon:textures/gui/dark_slate_seamless.png"),".",(0,r.kt)("br",{parentName:"p"}),"\n","The ResourceLocation for the Background texture to use for this category. The texture must be 512px by 512px."),(0,r.kt)("h3",{id:"background_parallax_layers-json-array-of-json-objects-optional"},(0,r.kt)("strong",{parentName:"h3"},"background_parallax_layers")," (JSON Array of JSON Objects, ",(0,r.kt)("em",{parentName:"h3"},"optional"),")"),(0,r.kt)("p",null,"If any parallax layers are supplied, the ",(0,r.kt)("inlineCode",{parentName:"p"},"background")," property will be ignored.   "),(0,r.kt)("p",null,"Parallax layers allow a multi-layered background with a parallax effect. That means, the textures supplied here likely will feature transparent elements, however the first layer should be fully opaque to avoid visual artifacts.   "),(0,r.kt)("p",null,"Sample Value: "),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-json"},'"background_parallax_layers": [\n    {\n      "background": "modonomicon:textures/gui/parallax/flow/base.png",\n      "speed": 0.7\n    },\n    {\n      "background": "modonomicon:textures/gui/parallax/flow/1.png",\n      "speed": 1.0\n    },\n    {\n      "background": "modonomicon:textures/gui/parallax/flow/2.png",\n      "speed": 1.4,\n      "vanish_zoom": 0.9\n    }\n  ],\n')),(0,r.kt)("h3",{id:"background_height-integer-optional"},(0,r.kt)("strong",{parentName:"h3"},"background_height")," (Integer, ",(0,r.kt)("em",{parentName:"h3"},"optional"),")"),(0,r.kt)("p",null,"Default value: ",(0,r.kt)("inlineCode",{parentName:"p"},"512"),(0,r.kt)("br",{parentName:"p"}),"\n","The height of the background texture. Applies both to the ",(0,r.kt)("inlineCode",{parentName:"p"},"background")," property as well as the ",(0,r.kt)("inlineCode",{parentName:"p"},"background_parallax_layers")," property."),(0,r.kt)("h3",{id:"background_width-integer-optional"},(0,r.kt)("strong",{parentName:"h3"},"background_width")," (Integer, ",(0,r.kt)("em",{parentName:"h3"},"optional"),")"),(0,r.kt)("p",null,"Default value: ",(0,r.kt)("inlineCode",{parentName:"p"},"512"),(0,r.kt)("br",{parentName:"p"}),"\n","The width of the background texture. Applies both to the ",(0,r.kt)("inlineCode",{parentName:"p"},"background")," property as well as the ",(0,r.kt)("inlineCode",{parentName:"p"},"background_parallax_layers")," property."),(0,r.kt)("h3",{id:"entry_textures-resourcelocation-optional"},(0,r.kt)("strong",{parentName:"h3"},"entry_textures")," (ResourceLocation, ",(0,r.kt)("em",{parentName:"h3"},"optional"),")"),(0,r.kt)("p",null,"Defaults to ",(0,r.kt)("inlineCode",{parentName:"p"},"modonomicon:textures/gui/entry_textures.png"),".",(0,r.kt)("br",{parentName:"p"}),"\n","The ResourceLocation for the Entry textures to use for this category. The texture must be 512px by 512px.",(0,r.kt)("br",{parentName:"p"}),"\n","Entry textures govern how the Entry background behind the Icon as well as the arrows connecting entries look.   "),(0,r.kt)("p",null,"If you want to use a custom texture, make sure to copy the default file from ",(0,r.kt)("a",{parentName:"p",href:"https://github.com/klikli-dev/modonomicon/blob/version/1.19/src/main/resources/assets/modonomicon/textures/gui/entry_textures.png"},(0,r.kt)("inlineCode",{parentName:"a"},"/assets/modonomicon/textures/gui/entry_textures.png"))," and modify it in order to preserve the UV coordinates of all parts."),(0,r.kt)("h3",{id:"show_category_button-boolean-optional"},(0,r.kt)("strong",{parentName:"h3"},"show_category_button")," (Boolean, ",(0,r.kt)("em",{parentName:"h3"},"optional"),")"),(0,r.kt)("p",null,"Defaults to ",(0,r.kt)("inlineCode",{parentName:"p"},"true"),".",(0,r.kt)("br",{parentName:"p"}),"\n","If false, the book overview screen will not show a button/bookmark for this category. "),(0,r.kt)("admonition",{type:"tip"},(0,r.kt)("p",{parentName:"admonition"},'This is intended to be used with an entry that links to this category to effectively create "sub-categories". See also ',(0,r.kt)("strong",{parentName:"p"},(0,r.kt)("a",{parentName:"strong",href:"./entries"},"Entries"))," for the ",(0,r.kt)("inlineCode",{parentName:"p"},"category_to_open")," attribute.")),(0,r.kt)("h2",{id:"usage-examples"},"Usage Examples"),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"/data/<mod_id>/modonomicons/<book_id>/categories/features.json"),":"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "background": "modonomicon:textures/gui/dark_slate_seamless.png",\n  "entry_textures": "modonomicon:textures/gui/entry_textures.png",\n  "icon": "minecraft:nether_star",\n  "name": "book.modonomicon.demo.features.name",\n  "sort_number": -1\n}\n')),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"/lang/*.json"),":"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-json"},'{\n    "book.modonomicon.demo.features.name": "Features Category",\n}\n')))}m.isMDXComponent=!0}}]);