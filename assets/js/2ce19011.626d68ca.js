"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[230],{3905:(e,t,n)=>{n.d(t,{Zo:()=>m,kt:()=>u});var o=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function r(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,o)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?r(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):r(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function p(e,t){if(null==e)return{};var n,o,a=function(e,t){if(null==e)return{};var n,o,a={},r=Object.keys(e);for(o=0;o<r.length;o++)n=r[o],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(o=0;o<r.length;o++)n=r[o],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var s=o.createContext({}),l=function(e){var t=o.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},m=function(e){var t=l(e.components);return o.createElement(s.Provider,{value:t},e.children)},c="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},g=o.forwardRef((function(e,t){var n=e.components,a=e.mdxType,r=e.originalType,s=e.parentName,m=p(e,["components","mdxType","originalType","parentName"]),c=l(n),g=a,u=c["".concat(s,".").concat(g)]||c[g]||d[g]||r;return n?o.createElement(u,i(i({ref:t},m),{},{components:n})):o.createElement(u,i({ref:t},m))}));function u(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var r=n.length,i=new Array(r);i[0]=g;var p={};for(var s in t)hasOwnProperty.call(t,s)&&(p[s]=t[s]);p.originalType=e,p[c]="string"==typeof e?e:a,i[1]=p;for(var l=2;l<r;l++)i[l]=n[l];return o.createElement.apply(null,i)}return o.createElement.apply(null,n)}g.displayName="MDXCreateElement"},3994:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>s,contentTitle:()=>i,default:()=>d,frontMatter:()=>r,metadata:()=>p,toc:()=>l});var o=n(7462),a=(n(7294),n(3905));const r={sidebar_position:40},i="Image Page",p={unversionedId:"basics/page-types/image-page",id:"basics/page-types/image-page",title:"Image Page",description:"Image Page",source:"@site/docs/basics/page-types/image-page.md",sourceDirName:"basics/page-types",slug:"/basics/page-types/image-page",permalink:"/modonomicon/docs/basics/page-types/image-page",draft:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/page-types/image-page.md",tags:[],version:"current",sidebarPosition:40,frontMatter:{sidebar_position:40},sidebar:"tutorialSidebar",previous:{title:"Multiblock Page",permalink:"/modonomicon/docs/basics/page-types/multiblock-page"},next:{title:"Entity Page",permalink:"/modonomicon/docs/basics/page-types/entity-page"}},s={},l=[{value:"Attributes",id:"attributes",level:2},{value:"<strong>title</strong> (DescriptionId or Component JSON, <em>optional</em>)",id:"title-descriptionid-or-component-json-optional",level:3},{value:"<strong>images</strong> (ResourceLocation[], <em>mandatory</em>)",id:"images-resourcelocation-mandatory",level:3},{value:"<strong>text</strong> (DescriptionId or Component JSON, <em>optional</em>)",id:"text-descriptionid-or-component-json-optional",level:3},{value:"<strong>border</strong> (Boolean, <em>optional</em>)",id:"border-boolean-optional",level:3},{value:"Usage Examples",id:"usage-examples",level:2}],m={toc:l},c="wrapper";function d(e){let{components:t,...r}=e;return(0,a.kt)(c,(0,o.Z)({},m,r,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"image-page"},"Image Page"),(0,a.kt)("p",null,(0,a.kt)("img",{alt:"Image Page",src:n(8012).Z,width:"1109",height:"732"})),(0,a.kt)("p",null,(0,a.kt)("strong",{parentName:"p"},"Page type:")," ",(0,a.kt)("inlineCode",{parentName:"p"},"modonomicon:image")),(0,a.kt)("p",null,"Displays an image and optionally a title and text."),(0,a.kt)("h2",{id:"attributes"},"Attributes"),(0,a.kt)("h3",{id:"title-descriptionid-or-component-json-optional"},(0,a.kt)("strong",{parentName:"h3"},"title")," (DescriptionId or Component JSON, ",(0,a.kt)("em",{parentName:"h3"},"optional"),")"),(0,a.kt)("p",null,"The page title. Will not parse markdown, instead it uses the default title color as defined in the ",(0,a.kt)("inlineCode",{parentName:"p"},"book.json"),".",(0,a.kt)("br",{parentName:"p"}),"\n","See ",(0,a.kt)("a",{parentName:"p",href:"../structure/book"},"Book.json")," for details."),(0,a.kt)("h3",{id:"images-resourcelocation-mandatory"},(0,a.kt)("strong",{parentName:"h3"},"images")," (ResourceLocation[], ",(0,a.kt)("em",{parentName:"h3"},"mandatory"),")"),(0,a.kt)("p",null,"Array of ResourceLocations of the textures to display.",(0,a.kt)("br",{parentName:"p"}),"\n","The files should be 256px by 256px. The upper 200px by 200px will be rendered. "),(0,a.kt)("h3",{id:"text-descriptionid-or-component-json-optional"},(0,a.kt)("strong",{parentName:"h3"},"text")," (DescriptionId or Component JSON, ",(0,a.kt)("em",{parentName:"h3"},"optional"),")"),(0,a.kt)("p",null,"The page text. Can be styled using markdown."),(0,a.kt)("h3",{id:"border-boolean-optional"},(0,a.kt)("strong",{parentName:"h3"},"border")," (Boolean, ",(0,a.kt)("em",{parentName:"h3"},"optional"),")"),(0,a.kt)("p",null,"Defaults to ",(0,a.kt)("inlineCode",{parentName:"p"},"true"),". If true, render a border around the image."),(0,a.kt)("h2",{id:"usage-examples"},"Usage Examples"),(0,a.kt)("p",null,(0,a.kt)("inlineCode",{parentName:"p"},"<entry>.json"),":"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-json"},'{\n  ...\n  "pages": [\n    {\n      "type": "modonomicon:image",\n      "border": true,\n      "images": [\n        "modonomicon:textures/gui/default_background.png",\n        "modonomicon:textures/gui/dark_slate_seamless.png"\n      ],\n      "text": "book.modonomicon.demo.features.image.image.text",\n      "title": "book.modonomicon.demo.features.image.image.title"\n    }\n  ]\n}\n')),(0,a.kt)("p",null,(0,a.kt)("inlineCode",{parentName:"p"},"/lang/*.json"),":"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "book.modonomicon.demo.features.image.image.text": "A sample text for the sample image.",\n  "book.modonomicon.demo.features.image.image.title": "Sample image!",\n}\n')))}d.isMDXComponent=!0},8012:(e,t,n)=>{n.d(t,{Z:()=>o});const o=n.p+"assets/images/image-page-dcbfe0670c11c67a2fcadc4c58282f0e.png"}}]);