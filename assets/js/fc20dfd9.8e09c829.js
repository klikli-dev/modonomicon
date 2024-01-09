"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[4910],{5294:function(e,t,n){n.r(t),n.d(t,{assets:function(){return a},contentTitle:function(){return r},default:function(){return m},frontMatter:function(){return s},metadata:function(){return c},toc:function(){return l}});var o=n(5893),i=n(1151);const s={sidebar_position:40},r="Display (Only) Matcher",c={id:"multiblocks/state-matchers/display-matcher",title:"Display (Only) Matcher",description:"Typedisplay",source:"@site/docs/multiblocks/state-matchers/display-matcher.md",sourceDirName:"multiblocks/state-matchers",slug:"/multiblocks/state-matchers/display-matcher",permalink:"/modonomicon/docs/multiblocks/state-matchers/display-matcher",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/multiblocks/state-matchers/display-matcher.md",tags:[],version:"current",sidebarPosition:40,frontMatter:{sidebar_position:40},sidebar:"tutorialSidebar",previous:{title:"Block State Property Matcher",permalink:"/modonomicon/docs/multiblocks/state-matchers/blockstate-property-matcher"},next:{title:"Predicate Matchers",permalink:"/modonomicon/docs/multiblocks/state-matchers/predicate-matcher"}},a={},l=[{value:"Attributes",id:"attributes",level:2},{value:"<strong>display</strong> (BlockState, <em>optional</em>)",id:"display-blockstate-optional",level:3},{value:"Usage Examples",id:"usage-examples",level:2}];function d(e){const t={admonition:"admonition",code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",p:"p",pre:"pre",strong:"strong",...(0,i.a)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(t.h1,{id:"display-only-matcher",children:"Display (Only) Matcher"}),"\n",(0,o.jsxs)(t.p,{children:[(0,o.jsx)(t.strong,{children:"Type:"})," ",(0,o.jsx)(t.code,{children:"modonomicon:display"})]}),"\n",(0,o.jsx)(t.p,{children:"Display matchers will render the configured block in the multiblock book page, but will match any blocks, including air.\nDisplay matchers will not render in the in-world preview to avoid confusing players.\nTheir main use is to provide background blocks for the multiblock when rendering in the book for better contrast and visibility."}),"\n",(0,o.jsx)(t.h2,{id:"attributes",children:"Attributes"}),"\n",(0,o.jsxs)(t.h3,{id:"display-blockstate-optional",children:[(0,o.jsx)(t.strong,{children:"display"})," (BlockState, ",(0,o.jsx)(t.em,{children:"optional"}),")"]}),"\n",(0,o.jsx)(t.p,{children:"The BlockState to display in the multiblock preview."}),"\n",(0,o.jsx)(t.admonition,{type:"info",children:(0,o.jsxs)(t.p,{children:["If you omit the BlockState properties (",(0,o.jsx)(t.code,{children:"[key=value]"}),"), Modonomicon will display the Block's default BlockState."]})}),"\n",(0,o.jsx)(t.h2,{id:"usage-examples",children:"Usage Examples"}),"\n",(0,o.jsx)(t.pre,{children:(0,o.jsx)(t.code,{className:"language-json",children:'{\n    "type": "modonomicon:display",\n    "display": "minecraft:chest[facing=west]",\n}\n'})})]})}function m(e={}){const{wrapper:t}={...(0,i.a)(),...e.components};return t?(0,o.jsx)(t,{...e,children:(0,o.jsx)(d,{...e})}):d(e)}},1151:function(e,t,n){n.d(t,{Z:function(){return c},a:function(){return r}});var o=n(7294);const i={},s=o.createContext(i);function r(e){const t=o.useContext(s);return o.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function c(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:r(e.components),o.createElement(s.Provider,{value:t},e.children)}}}]);