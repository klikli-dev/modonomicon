"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[2589],{4521:function(t,e,o){o.r(e),o.d(e,{assets:function(){return s},contentTitle:function(){return a},default:function(){return h},frontMatter:function(){return c},metadata:function(){return i},toc:function(){return l}});var n=o(5893),r=o(1151);const c={sidebar_position:31},a="Block State Property Matcher",i={id:"multiblocks/state-matchers/blockstate-property-matcher",title:"Block State Property Matcher",description:"Typeblockstateproperty",source:"@site/docs/multiblocks/state-matchers/blockstate-property-matcher.md",sourceDirName:"multiblocks/state-matchers",slug:"/multiblocks/state-matchers/blockstate-property-matcher",permalink:"/modonomicon/docs/multiblocks/state-matchers/blockstate-property-matcher",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/multiblocks/state-matchers/blockstate-property-matcher.md",tags:[],version:"current",sidebarPosition:31,frontMatter:{sidebar_position:31},sidebar:"tutorialSidebar",previous:{title:"Block State Matcher",permalink:"/modonomicon/docs/multiblocks/state-matchers/blockstate-matcher"},next:{title:"Display (Only) Matcher",permalink:"/modonomicon/docs/multiblocks/state-matchers/display-matcher"}},s={},l=[{value:"Attributes",id:"attributes",level:2},{value:"<strong>block</strong> (BlockState, <em>mandatory</em>)",id:"block-blockstate-mandatory",level:3},{value:"<strong>display</strong> (BlockState, <em>optional</em>)",id:"display-blockstate-optional",level:3},{value:"Usage Examples",id:"usage-examples",level:2}];function d(t){const e={admonition:"admonition",br:"br",code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",p:"p",pre:"pre",strong:"strong",...(0,r.a)(),...t.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsx)(e.h1,{id:"block-state-property-matcher",children:"Block State Property Matcher"}),"\n",(0,n.jsxs)(e.p,{children:[(0,n.jsx)(e.strong,{children:"Type:"})," ",(0,n.jsx)(e.code,{children:"modonomicon:blockstateproperty"})]}),"\n",(0,n.jsxs)(e.p,{children:["BlockStateProperty matchers will check for ",(0,n.jsx)(e.strong,{children:"only the properties you provide"}),", not all existing BlockState properties for that block."]}),"\n",(0,n.jsx)(e.admonition,{type:"tip",children:(0,n.jsxs)(e.p,{children:["This matcher check only against the properties you actually provide, and ignore all other properties.\nE.g.: If you provide a ",(0,n.jsx)(e.code,{children:"facing"})," property, but the block you are checking against also has a ",(0,n.jsx)(e.code,{children:"waterlogged"})," property, it will match as long as the ",(0,n.jsx)(e.code,{children:"facing"})," property is correct, and ignore the value of the ",(0,n.jsx)(e.code,{children:"waterlogged"})," property."]})}),"\n",(0,n.jsx)(e.h2,{id:"attributes",children:"Attributes"}),"\n",(0,n.jsxs)(e.h3,{id:"block-blockstate-mandatory",children:[(0,n.jsx)(e.strong,{children:"block"})," (BlockState, ",(0,n.jsx)(e.em,{children:"mandatory"}),")"]}),"\n",(0,n.jsx)(e.p,{children:"BlockState to match against when checking if a given block fits this matcher."}),"\n",(0,n.jsx)(e.admonition,{type:"info",children:(0,n.jsxs)(e.p,{children:["If you omit the BlockState properties (",(0,n.jsx)(e.code,{children:"[key=value]"}),"), Modonomicon will match against it's default BlockState."]})}),"\n",(0,n.jsxs)(e.h3,{id:"display-blockstate-optional",children:[(0,n.jsx)(e.strong,{children:"display"})," (BlockState, ",(0,n.jsx)(e.em,{children:"optional"}),")"]}),"\n",(0,n.jsxs)(e.p,{children:["Defaults to the value of the ",(0,n.jsx)(e.code,{children:"block"})," property.",(0,n.jsx)(e.br,{}),"\n","The block to display in the multiblock preview."]}),"\n",(0,n.jsx)(e.admonition,{type:"info",children:(0,n.jsxs)(e.p,{children:["If you omit the BlockState properties (",(0,n.jsx)(e.code,{children:"[key=value]"}),"), Modonomicon will display the Block's default BlockState."]})}),"\n",(0,n.jsx)(e.h2,{id:"usage-examples",children:"Usage Examples"}),"\n",(0,n.jsxs)(e.p,{children:[(0,n.jsx)(e.strong,{children:"Example:"})," Matches lit candles, inependent of the amount of candles, and if they are waterlogged or not (although the latter of course has the in-game constraint that waterlogged will unlight the candle ...)."]}),"\n",(0,n.jsx)(e.pre,{children:(0,n.jsx)(e.code,{className:"language-json",children:'{\n    "type": "modonomicon:blockstateproperty",\n    "display": "minecraft:white_candle[lit=true]",\n    "block": "minecraft:white_candle[lit=true]"\n}\n'})})]})}function h(t={}){const{wrapper:e}={...(0,r.a)(),...t.components};return e?(0,n.jsx)(e,{...t,children:(0,n.jsx)(d,{...t})}):d(t)}},1151:function(t,e,o){o.d(e,{Z:function(){return i},a:function(){return a}});var n=o(7294);const r={},c=n.createContext(r);function a(t){const e=n.useContext(c);return n.useMemo((function(){return"function"==typeof t?t(e):{...e,...t}}),[e,t])}function i(t){let e;return e=t.disableParentContext?"function"==typeof t.components?t.components(r):t.components||r:a(t.components),n.createElement(c.Provider,{value:e},t.children)}}}]);