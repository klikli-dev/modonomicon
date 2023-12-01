"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[6341],{3890:function(t,e,n){n.r(e),n.d(e,{assets:function(){return a},contentTitle:function(){return r},default:function(){return h},frontMatter:function(){return i},metadata:function(){return c},toc:function(){return l}});var s=n(5893),o=n(1151);const i={sidebar_position:20},r="State Matchers",c={id:"multiblocks/state-matchers/state-matchers",title:"State Matchers",description:"Each state matcher represents one block at one specific position in the multiblock, and depending on the type of matcher it may only allow one specific block, or a wide range of blocks in that position.",source:"@site/docs/multiblocks/state-matchers/state-matchers.md",sourceDirName:"multiblocks/state-matchers",slug:"/multiblocks/state-matchers/",permalink:"/modonomicon/docs/multiblocks/state-matchers/",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/multiblocks/state-matchers/state-matchers.md",tags:[],version:"current",sidebarPosition:20,frontMatter:{sidebar_position:20},sidebar:"tutorialSidebar",previous:{title:"Sparse Multiblock",permalink:"/modonomicon/docs/multiblocks/defining-multiblocks/sparse-multiblocks"},next:{title:"Any Matcher",permalink:"/modonomicon/docs/multiblocks/state-matchers/any-matcher"}},a={},l=[{value:"Common Attributes",id:"common-attributes",level:2},{value:"<strong>type</strong> (State Matcher Type, <em>mandatory</em>)",id:"type-state-matcher-type-mandatory",level:3},{value:"Attribute Types",id:"attribute-types",level:2},{value:"<strong>Block</strong> (String)",id:"block-string",level:3},{value:"<strong>BlockState</strong> (String)",id:"blockstate-string",level:3},{value:"<strong>Tag</strong> (String)",id:"tag-string",level:3}];function d(t){const e={a:"a",br:"br",code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",p:"p",strong:"strong",...(0,o.a)(),...t.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsx)(e.h1,{id:"state-matchers",children:"State Matchers"}),"\n",(0,s.jsx)(e.p,{children:"Each state matcher represents one block at one specific position in the multiblock, and depending on the type of matcher it may only allow one specific block, or a wide range of blocks in that position."}),"\n",(0,s.jsxs)(e.p,{children:["State matchers are defined ",(0,s.jsxs)(e.a,{href:"/modonomicon/docs/multiblocks/defining-multiblocks/#mappings",children:["in the ",(0,s.jsx)(e.code,{children:"mappings"})," part of the multiblock definition"]}),"."]}),"\n",(0,s.jsx)(e.h2,{id:"common-attributes",children:"Common Attributes"}),"\n",(0,s.jsx)(e.p,{children:"All state matchers need to have the following attributes:"}),"\n",(0,s.jsxs)(e.h3,{id:"type-state-matcher-type-mandatory",children:[(0,s.jsx)(e.strong,{children:"type"})," (State Matcher Type, ",(0,s.jsx)(e.em,{children:"mandatory"}),")"]}),"\n",(0,s.jsxs)(e.p,{children:["A ResourceLocation identifying the type of state matcher to use.",(0,s.jsx)(e.br,{}),"\n","Example: ",(0,s.jsx)(e.code,{children:"modonomicon:block"})]}),"\n",(0,s.jsx)(e.h2,{id:"attribute-types",children:"Attribute Types"}),"\n",(0,s.jsx)(e.p,{children:"Besides standard JSON types, state matchers support the following attributes:"}),"\n",(0,s.jsxs)(e.h3,{id:"block-string",children:[(0,s.jsx)(e.strong,{children:"Block"})," (String)"]}),"\n",(0,s.jsxs)(e.p,{children:["A ResourceLocation for a block, in the format ",(0,s.jsx)(e.code,{children:"modid:block"}),".",(0,s.jsx)(e.br,{}),"\n","Example: ",(0,s.jsx)(e.code,{children:"minecraft:stone"})]}),"\n",(0,s.jsxs)(e.h3,{id:"blockstate-string",children:[(0,s.jsx)(e.strong,{children:"BlockState"})," (String)"]}),"\n",(0,s.jsxs)(e.p,{children:["A BlockState string as used in the Minecraft ",(0,s.jsx)(e.code,{children:"setblock"})," command.",(0,s.jsx)(e.br,{}),"\n","Example: ",(0,s.jsx)(e.code,{children:"minecraft:chest[facing=east]"}),"."]}),"\n",(0,s.jsxs)(e.p,{children:["The block state properties can be omitted, in which case the default BlockState will be used.",(0,s.jsx)(e.br,{}),"\n","Example: ",(0,s.jsx)(e.code,{children:"minecraft:chest"}),"."]}),"\n",(0,s.jsxs)(e.p,{children:["See ",(0,s.jsx)(e.a,{href:"https://minecraft.fandom.com/wiki/Commands/setblock",children:"https://minecraft.fandom.com/wiki/Commands/setblock"})," for more information"]}),"\n",(0,s.jsxs)(e.h3,{id:"tag-string",children:[(0,s.jsx)(e.strong,{children:"Tag"})," (String)"]}),"\n",(0,s.jsxs)(e.p,{children:["A Tag string that is based on the BlockState string as used in the ",(0,s.jsx)(e.code,{children:"setblock"})," command, but prefixed with ",(0,s.jsx)(e.code,{children:"#"}),".",(0,s.jsx)(e.br,{}),"\n","Example: ",(0,s.jsx)(e.code,{children:"#forge:chests[facing=east]"})]}),"\n",(0,s.jsxs)(e.p,{children:["The block state properties can be omitted, in which case the block state properties will be ignored when matching.",(0,s.jsx)(e.br,{}),"\n","Example: ",(0,s.jsx)(e.code,{children:"#forge:chests"}),"."]})]})}function h(t={}){const{wrapper:e}={...(0,o.a)(),...t.components};return e?(0,s.jsx)(e,{...t,children:(0,s.jsx)(d,{...t})}):d(t)}},1151:function(t,e,n){n.d(e,{Z:function(){return c},a:function(){return r}});var s=n(7294);const o={},i=s.createContext(o);function r(t){const e=s.useContext(i);return s.useMemo((function(){return"function"==typeof t?t(e):{...e,...t}}),[e,t])}function c(t){let e;return e=t.disableParentContext?"function"==typeof t.components?t.components(o):t.components||o:r(t.components),s.createElement(i.Provider,{value:e},t.children)}}}]);