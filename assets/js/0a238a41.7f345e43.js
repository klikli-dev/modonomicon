"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[9781],{3905:(e,t,n)=>{n.d(t,{Zo:()=>l,kt:()=>y});var o=n(7294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,o)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function c(e,t){if(null==e)return{};var n,o,r=function(e,t){if(null==e)return{};var n,o,r={},i=Object.keys(e);for(o=0;o<i.length;o++)n=i[o],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(o=0;o<i.length;o++)n=i[o],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var s=o.createContext({}),d=function(e){var t=o.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},l=function(e){var t=d(e.components);return o.createElement(s.Provider,{value:t},e.children)},p="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},m=o.forwardRef((function(e,t){var n=e.components,r=e.mdxType,i=e.originalType,s=e.parentName,l=c(e,["components","mdxType","originalType","parentName"]),p=d(n),m=r,y=p["".concat(s,".").concat(m)]||p[m]||u[m]||i;return n?o.createElement(y,a(a({ref:t},l),{},{components:n})):o.createElement(y,a({ref:t},l))}));function y(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=n.length,a=new Array(i);a[0]=m;var c={};for(var s in t)hasOwnProperty.call(t,s)&&(c[s]=t[s]);c.originalType=e,c[p]="string"==typeof e?e:r,a[1]=c;for(var d=2;d<i;d++)a[d]=n[d];return o.createElement.apply(null,a)}return o.createElement.apply(null,n)}m.displayName="MDXCreateElement"},8747:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>s,contentTitle:()=>a,default:()=>u,frontMatter:()=>i,metadata:()=>c,toc:()=>d});var o=n(7462),r=(n(7294),n(3905));const i={sidebar_position:10},a="Entry Read Condition",c={unversionedId:"basics/unlock-conditions/entry-read-condition",id:"basics/unlock-conditions/entry-read-condition",title:"Entry Read Condition",description:"Condition typeentry_read",source:"@site/docs/basics/unlock-conditions/entry-read-condition.md",sourceDirName:"basics/unlock-conditions",slug:"/basics/unlock-conditions/entry-read-condition",permalink:"/modonomicon/docs/basics/unlock-conditions/entry-read-condition",draft:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/unlock-conditions/entry-read-condition.md",tags:[],version:"current",sidebarPosition:10,frontMatter:{sidebar_position:10},sidebar:"tutorialSidebar",previous:{title:"Unlock Conditions",permalink:"/modonomicon/docs/basics/unlock-conditions/"},next:{title:"Entry Unlocked Condition",permalink:"/modonomicon/docs/basics/unlock-conditions/entry-unlocked-condition"}},s={},d=[{value:"Attributes",id:"attributes",level:2},{value:"<strong>entry_id</strong> (ResourceLocation, <em>mandatory</em>)",id:"entry_id-resourcelocation-mandatory",level:3},{value:"Usage Examples",id:"usage-examples",level:2}],l={toc:d},p="wrapper";function u(e){let{components:t,...n}=e;return(0,r.kt)(p,(0,o.Z)({},l,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"entry-read-condition"},"Entry Read Condition"),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Condition type:")," ",(0,r.kt)("inlineCode",{parentName:"p"},"modonomicon:entry_read")),(0,r.kt)("p",null,"This condition will be met, if the entry with the specified ID has been read by the player."),(0,r.kt)("p",null,"This condition type will be automatically generated for all entries if ",(0,r.kt)("inlineCode",{parentName:"p"},"auto_add_read_conditions")," is set to ",(0,r.kt)("inlineCode",{parentName:"p"},"true")," in ",(0,r.kt)("inlineCode",{parentName:"p"},"book.json"),". See ",(0,r.kt)("a",{parentName:"p",href:"../structure/book#attributes"},"Book.json")," for details."),(0,r.kt)("h2",{id:"attributes"},"Attributes"),(0,r.kt)("h3",{id:"entry_id-resourcelocation-mandatory"},(0,r.kt)("strong",{parentName:"h3"},"entry_id")," (ResourceLocation, ",(0,r.kt)("em",{parentName:"h3"},"mandatory"),")"),(0,r.kt)("p",null,"The ResourceLocation of the entry that needs to be read to unlock."),(0,r.kt)("h2",{id:"usage-examples"},"Usage Examples"),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"<my-entry>.json")," "),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-json"},'{\n  ...\n  "condition": {\n      "type": "modonomicon:entry_read",\n      "entry_id": "modonomicon:features/condition_root"\n  },\n  ...\n}\n')))}u.isMDXComponent=!0}}]);