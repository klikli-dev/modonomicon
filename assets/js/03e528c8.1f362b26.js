"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[5838],{3905:function(e,t,n){n.d(t,{Zo:function(){return m},kt:function(){return u}});var a=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function r(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,a,o=function(e,t){if(null==e)return{};var n,a,o={},i=Object.keys(e);for(a=0;a<i.length;a++)n=i[a],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(a=0;a<i.length;a++)n=i[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var p=a.createContext({}),c=function(e){var t=a.useContext(p),n=t;return e&&(n="function"==typeof e?e(t):r(r({},t),e)),n},m=function(e){var t=c(e.components);return a.createElement(p.Provider,{value:t},e.children)},s={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},k=a.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,p=e.parentName,m=l(e,["components","mdxType","originalType","parentName"]),k=c(n),u=o,d=k["".concat(p,".").concat(u)]||k[u]||s[u]||i;return n?a.createElement(d,r(r({ref:t},m),{},{components:n})):a.createElement(d,r({ref:t},m))}));function u(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,r=new Array(i);r[0]=k;var l={};for(var p in t)hasOwnProperty.call(t,p)&&(l[p]=t[p]);l.originalType=e,l.mdxType="string"==typeof e?e:o,r[1]=l;for(var c=2;c<i;c++)r[c]=n[c];return a.createElement.apply(null,r)}return a.createElement.apply(null,n)}k.displayName="MDXCreateElement"},1063:function(e,t,n){n.r(t),n.d(t,{assets:function(){return p},contentTitle:function(){return r},default:function(){return s},frontMatter:function(){return i},metadata:function(){return l},toc:function(){return c}});var a=n(3117),o=(n(7294),n(3905));const i={sidebar_position:30,title:"State Matchers"},r=void 0,l={unversionedId:"multiblocks/state-matchers",id:"multiblocks/state-matchers",title:"State Matchers",description:"Each state matcher represents one block at one specific position in the multiblock, and depending on the type of matcher it may only allow one specific block, or a wide range of blocks in that position.",source:"@site/docs/multiblocks/state-matchers.md",sourceDirName:"multiblocks",slug:"/multiblocks/state-matchers",permalink:"/modonomicon/docs/multiblocks/state-matchers",draft:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/multiblocks/state-matchers.md",tags:[],version:"current",sidebarPosition:30,frontMatter:{sidebar_position:30,title:"State Matchers"},sidebar:"tutorialSidebar",previous:{title:"Defining Multiblocks",permalink:"/modonomicon/docs/multiblocks/defining-multiblocks"},next:{title:"Localization",permalink:"/modonomicon/docs/advanced/localization"}},p={},c=[{value:"Common Attributes",id:"common-attributes",level:2},{value:"Attribute Types",id:"attribute-types",level:2},{value:"Block Matcher",id:"block-matcher",level:2},{value:"Attributes",id:"attributes",level:3},{value:"Usage Examples",id:"usage-examples",level:3},{value:"Block State Matcher",id:"block-state-matcher",level:2},{value:"Attributes",id:"attributes-1",level:3},{value:"Usage Examples",id:"usage-examples-1",level:3}],m={toc:c};function s(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,a.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"Each state matcher represents one block at one specific position in the multiblock, and depending on the type of matcher it may only allow one specific block, or a wide range of blocks in that position. "),(0,o.kt)("p",null,"State matchers are defined ",(0,o.kt)("a",{parentName:"p",href:"./defining-multiblocks#mappings"},"in the ",(0,o.kt)("inlineCode",{parentName:"a"},"mappings")," part of the multiblock definition"),"."),(0,o.kt)("h2",{id:"common-attributes"},"Common Attributes"),(0,o.kt)("p",null,"All state matchers need to have the following attributes:"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("strong",{parentName:"li"},"type")," (State Matcher Type, ",(0,o.kt)("em",{parentName:"li"},"mandatory"),")")),(0,o.kt)("p",null,"A ResourceLocation identifying the type of state matcher to use.",(0,o.kt)("br",{parentName:"p"}),"\n","Example: ",(0,o.kt)("inlineCode",{parentName:"p"},"modonomicon:block")," "),(0,o.kt)("h2",{id:"attribute-types"},"Attribute Types"),(0,o.kt)("p",null,"Besides standard JSON types, state matchers support the following attributes:"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("strong",{parentName:"p"},"Block")," (String)"),(0,o.kt)("p",{parentName:"li"},"A ResourceLocation for a block, in the format ",(0,o.kt)("inlineCode",{parentName:"p"},"modid:block"),".",(0,o.kt)("br",{parentName:"p"}),"\n","Example: ",(0,o.kt)("inlineCode",{parentName:"p"},"minecraft:stone")," ")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("strong",{parentName:"p"},"BlockState")," (String)"),(0,o.kt)("p",{parentName:"li"},"A BlockState string as used in the Minecraft ",(0,o.kt)("inlineCode",{parentName:"p"},"setblock")," command.",(0,o.kt)("br",{parentName:"p"}),"\n","Example: ",(0,o.kt)("inlineCode",{parentName:"p"},"minecraft:chest[facing=east]"),".  "),(0,o.kt)("p",{parentName:"li"},"The block state properties can be omitted, in which case the default BlockState will be used.",(0,o.kt)("br",{parentName:"p"}),"\n","Example: ",(0,o.kt)("inlineCode",{parentName:"p"},"minecraft:chest"),"."),(0,o.kt)("p",{parentName:"li"},"See ",(0,o.kt)("a",{parentName:"p",href:"https://minecraft.fandom.com/wiki/Commands/setblock"},"https://minecraft.fandom.com/wiki/Commands/setblock")," for more information")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("strong",{parentName:"p"},"Tag")," (String)"),(0,o.kt)("p",{parentName:"li"},"A Tag string that is based on the BlockState string as used in the ",(0,o.kt)("inlineCode",{parentName:"p"},"setblock")," command, but prefixed with ",(0,o.kt)("inlineCode",{parentName:"p"},"#"),".",(0,o.kt)("br",{parentName:"p"}),"\n","Example: ",(0,o.kt)("inlineCode",{parentName:"p"},"#forge:chests[facing=east]")),(0,o.kt)("p",{parentName:"li"},"The block state properties can be omitted, in which case the block state properties will be ignored when matching.",(0,o.kt)("br",{parentName:"p"}),"\n","Example: ",(0,o.kt)("inlineCode",{parentName:"p"},"#forge:chests"),"."))),(0,o.kt)("h2",{id:"block-matcher"},"Block Matcher"),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Type:")," ",(0,o.kt)("inlineCode",{parentName:"p"},"modonomicon:block")),(0,o.kt)("p",null,"Block matchers will ignore the BlockState and check only if the placed block fits the configured block."),(0,o.kt)("h3",{id:"attributes"},"Attributes"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("strong",{parentName:"p"},"block")," (Block, ",(0,o.kt)("em",{parentName:"p"},"mandatory"),")"),(0,o.kt)("p",{parentName:"li"},"The Block to match against when checking if a given block fits this matcher."))),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("strong",{parentName:"p"},"display")," (BlockState, ",(0,o.kt)("em",{parentName:"p"},"optional"),")"),(0,o.kt)("p",{parentName:"li"},"Defaults to the default BlockState of the ",(0,o.kt)("inlineCode",{parentName:"p"},"block")," property.",(0,o.kt)("br",{parentName:"p"}),"\n","The BlockState to display in the multiblock preview. "),(0,o.kt)("admonition",{parentName:"li",type:"info"},(0,o.kt)("p",{parentName:"admonition"},"If you omit the BlockState properties (",(0,o.kt)("inlineCode",{parentName:"p"},"[key=value]"),"), Modonomicon will display the Block's default BlockState.")))),(0,o.kt)("h3",{id:"usage-examples"},"Usage Examples"),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Example 1:")," Matching (and displaying) a stone block"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "type": "modonomicon:block",\n  "block": "minecraft:stone"\n}\n')),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Example 3:")," Matching any chest, but displaying a west-facing chest"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json"},'{\n    "type": "modonomicon:block",\n    "display": "minecraft:chest[facing=west]",\n    "block": "minecraft:chest"\n}\n')),(0,o.kt)("h2",{id:"block-state-matcher"},"Block State Matcher"),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Type:")," ",(0,o.kt)("inlineCode",{parentName:"p"},"modonomicon:blockstate")),(0,o.kt)("p",null,"BlockState matchers will check for the exact BlockState properties provided."),(0,o.kt)("h3",{id:"attributes-1"},"Attributes"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("strong",{parentName:"p"},"block")," (BlockState, ",(0,o.kt)("em",{parentName:"p"},"mandatory"),")"),(0,o.kt)("p",{parentName:"li"},"BlockState to match against when checking if a given block fits this matcher."),(0,o.kt)("admonition",{parentName:"li",type:"info"},(0,o.kt)("p",{parentName:"admonition"},"If you omit the BlockState properties (",(0,o.kt)("inlineCode",{parentName:"p"},"[key=value]"),"), Modonomicon will match against it's default BlockState. "))),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("strong",{parentName:"p"},"display")," (BlockState, ",(0,o.kt)("em",{parentName:"p"},"optional"),")"),(0,o.kt)("p",{parentName:"li"},"Defaults to the value of the ",(0,o.kt)("inlineCode",{parentName:"p"},"block")," property.",(0,o.kt)("br",{parentName:"p"}),"\n","The block to display in the multiblock preview. "),(0,o.kt)("admonition",{parentName:"li",type:"info"},(0,o.kt)("p",{parentName:"admonition"},"If you omit the BlockState properties (",(0,o.kt)("inlineCode",{parentName:"p"},"[key=value]"),"), Modonomicon will display the Block's default BlockState.")))),(0,o.kt)("h3",{id:"usage-examples-1"},"Usage Examples"),(0,o.kt)("p",null,(0,o.kt)("strong",{parentName:"p"},"Example:")," Matching only west-facing chests, but displaying an east-facing chest"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json"},'{\n    "type": "modonomicon:blockstate",\n    "display": "minecraft:chest[facing=east]",\n    "block": "minecraft:chest[facing=west]"\n}\n')))}s.isMDXComponent=!0}}]);