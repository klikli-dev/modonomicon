"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[4219],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>k});var o=n(7294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,o)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,o,r=function(e,t){if(null==e)return{};var n,o,r={},i=Object.keys(e);for(o=0;o<i.length;o++)n=i[o],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(o=0;o<i.length;o++)n=i[o],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var s=o.createContext({}),c=function(e){var t=o.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},p=function(e){var t=c(e.components);return o.createElement(s.Provider,{value:t},e.children)},m="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},d=o.forwardRef((function(e,t){var n=e.components,r=e.mdxType,i=e.originalType,s=e.parentName,p=l(e,["components","mdxType","originalType","parentName"]),m=c(n),d=r,k=m["".concat(s,".").concat(d)]||m[d]||u[d]||i;return n?o.createElement(k,a(a({ref:t},p),{},{components:n})):o.createElement(k,a({ref:t},p))}));function k(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=n.length,a=new Array(i);a[0]=d;var l={};for(var s in t)hasOwnProperty.call(t,s)&&(l[s]=t[s]);l.originalType=e,l[m]="string"==typeof e?e:r,a[1]=l;for(var c=2;c<i;c++)a[c]=n[c];return o.createElement.apply(null,a)}return o.createElement.apply(null,n)}d.displayName="MDXCreateElement"},3986:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>s,contentTitle:()=>a,default:()=>u,frontMatter:()=>i,metadata:()=>l,toc:()=>c});var o=n(7462),r=(n(7294),n(3905));const i={sidebar_position:20},a="Sparse Multiblock",l={unversionedId:"multiblocks/defining-multiblocks/sparse-multiblocks",id:"multiblocks/defining-multiblocks/sparse-multiblocks",title:"Sparse Multiblock",description:"Multiblock typesparse",source:"@site/docs/multiblocks/defining-multiblocks/sparse-multiblocks.md",sourceDirName:"multiblocks/defining-multiblocks",slug:"/multiblocks/defining-multiblocks/sparse-multiblocks",permalink:"/modonomicon/docs/multiblocks/defining-multiblocks/sparse-multiblocks",draft:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/multiblocks/defining-multiblocks/sparse-multiblocks.md",tags:[],version:"current",sidebarPosition:20,frontMatter:{sidebar_position:20},sidebar:"tutorialSidebar",previous:{title:"Dense Multiblocks",permalink:"/modonomicon/docs/multiblocks/defining-multiblocks/dense-multiblocks"},next:{title:"State Matchers",permalink:"/modonomicon/docs/multiblocks/state-matchers/"}},s={},c=[{value:"Pattern",id:"pattern",level:2},{value:"Usage Examples",id:"usage-examples",level:2}],p={toc:c},m="wrapper";function u(e){let{components:t,...n}=e;return(0,r.kt)(m,(0,o.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"sparse-multiblock"},"Sparse Multiblock"),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Multiblock type:")," ",(0,r.kt)("inlineCode",{parentName:"p"},"modonomicon:sparse")),(0,r.kt)("p",null,"Unlike Dense multiblocks, sparse multiblocks only define blocks that need to be of a specific type, all other blocks are considered ",(0,r.kt)("a",{parentName:"p",href:"../state-matchers/any-matcher"},(0,r.kt)("inlineCode",{parentName:"a"},"any")," blocks"),". They are somewhat less intuitive to define, but offer great performance. "),(0,r.kt)("admonition",{type:"tip"},(0,r.kt)("p",{parentName:"admonition"},"In almost all scenarios Dense Multiblocks are the best choice. Only if you have multiblocks that are exceedingly big - say, more than 32x32x32 blocks - you should consider using Sparse Multiblocks instead.")),(0,r.kt)("h2",{id:"pattern"},"Pattern"),(0,r.kt)("p",null,"Sparse multiblocks are defined as key-value pairs in the ",(0,r.kt)("inlineCode",{parentName:"p"},"pattern")," field of the multiblock definition JSON file, similar to how ",(0,r.kt)("inlineCode",{parentName:"p"},"mappings")," are defined.\nThe key is the character corresponding to the ",(0,r.kt)("inlineCode",{parentName:"p"},"mappings")," entry, and the value is a set of 3D coordinates representing the location of the block in the multiblock, relative to the center of the multiblock (which is represented by ",(0,r.kt)("a",{parentName:"p",href:"./#multiblock-center-0"},(0,r.kt)("inlineCode",{parentName:"a"},"0")),")."),(0,r.kt)("p",null,"It could look something like this:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-json"},' "pattern": {\n    "N": [\n      [-1, 0, -2], [0, 0, -2], [1, 0, -2]\n    ],\n    "S": [\n      [-1, 0, 2], [0, 0, 2], [1, 0, 2]\n    ],\n    "W": [\n      [-2, 0, -1], [-2, 0, 0], [-2, 0, 1]\n    ],\n    "E": [\n      [2, 0, -1], [2, 0, 0], [2, 0, 1]\n    ]\n  },\n')),(0,r.kt)("h2",{id:"usage-examples"},"Usage Examples"),(0,r.kt)("p",null,"The Multiblock with the id ",(0,r.kt)("inlineCode",{parentName:"p"},"<modid>:sparse_test")," would be placed in ",(0,r.kt)("inlineCode",{parentName:"p"},"resources/data/<modid>/modonomicon_multiblocks/sparse_test.json")," as follows:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "type": "modonomicon:sparse",\n  "pattern": {\n    "N": [\n      [-1, 0, -2], [0, 0, -2], [1, 0, -2]\n    ],\n    "S": [\n      [-1, 0, 2], [0, 0, 2], [1, 0, 2]\n    ],\n    "W": [\n      [-2, 0, -1], [-2, 0, 0], [-2, 0, 1]\n    ],\n    "E": [\n      [2, 0, -1], [2, 0, 0], [2, 0, 1]\n    ]\n  },\n  "mapping": {\n    "N": {\n      "type": "modonomicon:blockstate",\n      "block": "minecraft:oak_stairs[facing=north]"\n    },\n    "S":  {\n      "type": "modonomicon:blockstate",\n      "block": "minecraft:oak_stairs[facing=south]"\n    },\n    "W":  {\n      "type": "modonomicon:blockstate",\n      "block": "minecraft:oak_stairs[facing=west]"\n    },\n    "E":  {\n      "type": "modonomicon:blockstate",\n      "block": "minecraft:oak_stairs[facing=east]"\n    }\n  }\n}\n')))}u.isMDXComponent=!0}}]);