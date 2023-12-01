"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[4219],{9004:function(e,n,t){t.r(n),t.d(n,{assets:function(){return r},contentTitle:function(){return c},default:function(){return u},frontMatter:function(){return s},metadata:function(){return l},toc:function(){return a}});var o=t(5893),i=t(1151);const s={sidebar_position:20},c="Sparse Multiblock",l={id:"multiblocks/defining-multiblocks/sparse-multiblocks",title:"Sparse Multiblock",description:"Multiblock typesparse",source:"@site/docs/multiblocks/defining-multiblocks/sparse-multiblocks.md",sourceDirName:"multiblocks/defining-multiblocks",slug:"/multiblocks/defining-multiblocks/sparse-multiblocks",permalink:"/modonomicon/docs/multiblocks/defining-multiblocks/sparse-multiblocks",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/multiblocks/defining-multiblocks/sparse-multiblocks.md",tags:[],version:"current",sidebarPosition:20,frontMatter:{sidebar_position:20},sidebar:"tutorialSidebar",previous:{title:"Dense Multiblocks",permalink:"/modonomicon/docs/multiblocks/defining-multiblocks/dense-multiblocks"},next:{title:"State Matchers",permalink:"/modonomicon/docs/multiblocks/state-matchers/"}},r={},a=[{value:"Pattern",id:"pattern",level:2},{value:"Usage Examples",id:"usage-examples",level:2}];function d(e){const n={a:"a",admonition:"admonition",code:"code",h1:"h1",h2:"h2",mdxAdmonitionTitle:"mdxAdmonitionTitle",p:"p",pre:"pre",strong:"strong",...(0,i.a)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(n.h1,{id:"sparse-multiblock",children:"Sparse Multiblock"}),"\n",(0,o.jsxs)(n.p,{children:[(0,o.jsx)(n.strong,{children:"Multiblock type:"})," ",(0,o.jsx)(n.code,{children:"modonomicon:sparse"})]}),"\n",(0,o.jsxs)(n.p,{children:["Unlike Dense multiblocks, sparse multiblocks only define blocks that need to be of a specific type, all other blocks are considered ",(0,o.jsxs)(n.a,{href:"../state-matchers/any-matcher",children:[(0,o.jsx)(n.code,{children:"any"})," blocks"]}),". They are somewhat less intuitive to define, but offer great performance."]}),"\n",(0,o.jsxs)(n.admonition,{type:"tip",children:[(0,o.jsx)(n.mdxAdmonitionTitle,{}),(0,o.jsx)(n.p,{children:"In almost all scenarios Dense Multiblocks are the best choice. Only if you have multiblocks that are exceedingly big - say, more than 32x32x32 blocks - you should consider using Sparse Multiblocks instead."})]}),"\n",(0,o.jsx)(n.h2,{id:"pattern",children:"Pattern"}),"\n",(0,o.jsxs)(n.p,{children:["Sparse multiblocks are defined as key-value pairs in the ",(0,o.jsx)(n.code,{children:"pattern"})," field of the multiblock definition JSON file, similar to how ",(0,o.jsx)(n.code,{children:"mappings"})," are defined.\nThe key is the character corresponding to the ",(0,o.jsx)(n.code,{children:"mappings"})," entry, and the value is a set of 3D coordinates representing the location of the block in the multiblock, relative to the center of the multiblock (which is represented by ",(0,o.jsx)(n.a,{href:"./#multiblock-center-0",children:(0,o.jsx)(n.code,{children:"0"})}),")."]}),"\n",(0,o.jsx)(n.p,{children:"It could look something like this:"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-json",children:' "pattern": {\n    "N": [\n      [-1, 0, -2], [0, 0, -2], [1, 0, -2]\n    ],\n    "S": [\n      [-1, 0, 2], [0, 0, 2], [1, 0, 2]\n    ],\n    "W": [\n      [-2, 0, -1], [-2, 0, 0], [-2, 0, 1]\n    ],\n    "E": [\n      [2, 0, -1], [2, 0, 0], [2, 0, 1]\n    ]\n  },\n'})}),"\n",(0,o.jsx)(n.h2,{id:"usage-examples",children:"Usage Examples"}),"\n",(0,o.jsxs)(n.p,{children:["The Multiblock with the id ",(0,o.jsx)(n.code,{children:"<modid>:sparse_test"})," would be placed in ",(0,o.jsx)(n.code,{children:"resources/data/<modid>/modonomicon_multiblocks/sparse_test.json"})," as follows:"]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-json",children:'{\n  "type": "modonomicon:sparse",\n  "pattern": {\n    "N": [\n      [-1, 0, -2], [0, 0, -2], [1, 0, -2]\n    ],\n    "S": [\n      [-1, 0, 2], [0, 0, 2], [1, 0, 2]\n    ],\n    "W": [\n      [-2, 0, -1], [-2, 0, 0], [-2, 0, 1]\n    ],\n    "E": [\n      [2, 0, -1], [2, 0, 0], [2, 0, 1]\n    ]\n  },\n  "mapping": {\n    "N": {\n      "type": "modonomicon:blockstate",\n      "block": "minecraft:oak_stairs[facing=north]"\n    },\n    "S":  {\n      "type": "modonomicon:blockstate",\n      "block": "minecraft:oak_stairs[facing=south]"\n    },\n    "W":  {\n      "type": "modonomicon:blockstate",\n      "block": "minecraft:oak_stairs[facing=west]"\n    },\n    "E":  {\n      "type": "modonomicon:blockstate",\n      "block": "minecraft:oak_stairs[facing=east]"\n    }\n  }\n}\n'})})]})}function u(e={}){const{wrapper:n}={...(0,i.a)(),...e.components};return n?(0,o.jsx)(n,{...e,children:(0,o.jsx)(d,{...e})}):d(e)}},1151:function(e,n,t){t.d(n,{Z:function(){return l},a:function(){return c}});var o=t(7294);const i={},s=o.createContext(i);function c(e){const n=o.useContext(s);return o.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:c(e.components),o.createElement(s.Provider,{value:n},e.children)}}}]);