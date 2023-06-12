"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[8691],{3905:(e,t,n)=>{n.d(t,{Zo:()=>l,kt:()=>g});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function p(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function i(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var s=r.createContext({}),c=function(e){var t=r.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):p(p({},t),e)),n},l=function(e){var t=c(e.components);return r.createElement(s.Provider,{value:t},e.children)},m="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},y=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,o=e.originalType,s=e.parentName,l=i(e,["components","mdxType","originalType","parentName"]),m=c(n),y=a,g=m["".concat(s,".").concat(y)]||m[y]||u[y]||o;return n?r.createElement(g,p(p({ref:t},l),{},{components:n})):r.createElement(g,p({ref:t},l))}));function g(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=n.length,p=new Array(o);p[0]=y;var i={};for(var s in t)hasOwnProperty.call(t,s)&&(i[s]=t[s]);i.originalType=e,i[m]="string"==typeof e?e:a,p[1]=i;for(var c=2;c<o;c++)p[c]=n[c];return r.createElement.apply(null,p)}return r.createElement.apply(null,n)}y.displayName="MDXCreateElement"},7340:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>s,contentTitle:()=>p,default:()=>u,frontMatter:()=>o,metadata:()=>i,toc:()=>c});var r=n(7462),a=(n(7294),n(3905));const o={sidebar_position:80},p="Empty Page",i={unversionedId:"basics/page-types/empty-page",id:"basics/page-types/empty-page",title:"Empty Page",description:"Empty Page",source:"@site/docs/basics/page-types/empty-page.md",sourceDirName:"basics/page-types",slug:"/basics/page-types/empty-page",permalink:"/modonomicon/docs/basics/page-types/empty-page",draft:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/page-types/empty-page.md",tags:[],version:"current",sidebarPosition:80,frontMatter:{sidebar_position:80},sidebar:"tutorialSidebar",previous:{title:"Recipe Pages",permalink:"/modonomicon/docs/basics/page-types/recipe-pages"},next:{title:"Formatting",permalink:"/modonomicon/docs/basics/formatting"}},s={},c=[{value:"Attributes",id:"attributes",level:2},{value:"Usage Examples",id:"usage-examples",level:2}],l={toc:c},m="wrapper";function u(e){let{components:t,...o}=e;return(0,a.kt)(m,(0,r.Z)({},l,o,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"empty-page"},"Empty Page"),(0,a.kt)("p",null,(0,a.kt)("img",{alt:"Empty Page",src:n(5244).Z,width:"1133",height:"766"})),(0,a.kt)("p",null,(0,a.kt)("strong",{parentName:"p"},"Page type:")," ",(0,a.kt)("inlineCode",{parentName:"p"},"modonomicon:empty")),(0,a.kt)("p",null,"Displays an empty page. Not really sure what you may want to use that for, but here we are."),(0,a.kt)("h2",{id:"attributes"},"Attributes"),(0,a.kt)("p",null,"None."),(0,a.kt)("h2",{id:"usage-examples"},"Usage Examples"),(0,a.kt)("p",null,(0,a.kt)("inlineCode",{parentName:"p"},"<entry>.json"),":"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-json"},'{\n  ...\n  "pages": [\n    {\n      "type": "modonomicon:empty",\n      "anchor": ""\n    }\n  ]\n}\n')))}u.isMDXComponent=!0},5244:(e,t,n)=>{n.d(t,{Z:()=>r});const r=n.p+"assets/images/empty-page-b92fff8e8becb7d3901ee77b7aa2cbb0.png"}}]);