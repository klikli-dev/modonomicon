"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[6048],{3905:(e,n,t)=>{t.d(n,{Zo:()=>l,kt:()=>f});var r=t(7294);function o(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function i(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);n&&(r=r.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,r)}return t}function a(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?i(Object(t),!0).forEach((function(n){o(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):i(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function c(e,n){if(null==e)return{};var t,r,o=function(e,n){if(null==e)return{};var t,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)t=i[r],n.indexOf(t)>=0||(o[t]=e[t]);return o}(e,n);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)t=i[r],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(o[t]=e[t])}return o}var s=r.createContext({}),p=function(e){var n=r.useContext(s),t=n;return e&&(t="function"==typeof e?e(n):a(a({},n),e)),t},l=function(e){var n=p(e.components);return r.createElement(s.Provider,{value:n},e.children)},u="mdxType",m={inlineCode:"code",wrapper:function(e){var n=e.children;return r.createElement(r.Fragment,{},n)}},d=r.forwardRef((function(e,n){var t=e.components,o=e.mdxType,i=e.originalType,s=e.parentName,l=c(e,["components","mdxType","originalType","parentName"]),u=p(t),d=o,f=u["".concat(s,".").concat(d)]||u[d]||m[d]||i;return t?r.createElement(f,a(a({ref:n},l),{},{components:t})):r.createElement(f,a({ref:n},l))}));function f(e,n){var t=arguments,o=n&&n.mdxType;if("string"==typeof e||o){var i=t.length,a=new Array(i);a[0]=d;var c={};for(var s in n)hasOwnProperty.call(n,s)&&(c[s]=n[s]);c.originalType=e,c[u]="string"==typeof e?e:o,a[1]=c;for(var p=2;p<i;p++)a[p]=t[p];return r.createElement.apply(null,a)}return r.createElement.apply(null,t)}d.displayName="MDXCreateElement"},7328:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>a,default:()=>m,frontMatter:()=>i,metadata:()=>c,toc:()=>p});var r=t(7462),o=(t(7294),t(3905));const i={sidebar_position:5},a="Book Crafting Recipes",c={unversionedId:"basics/crafting",id:"basics/crafting",title:"Book Crafting Recipes",description:"Recipe Result",source:"@site/docs/basics/crafting.md",sourceDirName:"basics",slug:"/basics/crafting",permalink:"/modonomicon/docs/basics/crafting",draft:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/crafting.md",tags:[],version:"current",sidebarPosition:5,frontMatter:{sidebar_position:5},sidebar:"tutorialSidebar",previous:{title:"Maven Dependencies",permalink:"/modonomicon/docs/getting-started/maven-dependencies"},next:{title:"Book Structure",permalink:"/modonomicon/docs/basics/structure/"}},s={},p=[{value:"Recipe Result",id:"recipe-result",level:2},{value:"Usage Examples",id:"usage-examples",level:2}],l={toc:p},u="wrapper";function m(e){let{components:n,...t}=e;return(0,o.kt)(u,(0,r.Z)({},l,t,{components:n,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"book-crafting-recipes"},"Book Crafting Recipes"),(0,o.kt)("h2",{id:"recipe-result"},"Recipe Result"),(0,o.kt)("p",null,"To craft your book, you need a recipe that outputs an item of the type ",(0,o.kt)("inlineCode",{parentName:"p"},"modonomicon:modonomicon")," with the NBT tag ",(0,o.kt)("inlineCode",{parentName:"p"},'"modonomicon:book_id":"<your_mod_id>:<your_book_id>"'),":"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json"},'{\n  ...\n  "result": {\n    "item": "modonomicon:modonomicon",\n    "nbt": {\n      "modonomicon:book_id": "<your_mod_id>:<your_book_id>"\n    }\n  }\n  ...\n}\n')),(0,o.kt)("h2",{id:"usage-examples"},"Usage Examples"),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Example:"),' A shapeless recipe for Theurgy\'s "The Hermetica":'),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "type": "minecraft:crafting_shapeless",\n  "ingredients": [\n    {\n      "item": "minecraft:book"\n    },\n    {\n      "tag": "forge:sand"\n    },\n    {\n      "tag": "forge:sand"\n    }\n  ],\n  "result": {\n    "item": "modonomicon:modonomicon",\n    "nbt": {\n      "modonomicon:book_id": "theurgy:the_hermetica"\n    }\n  }\n}\n')))}m.isMDXComponent=!0}}]);