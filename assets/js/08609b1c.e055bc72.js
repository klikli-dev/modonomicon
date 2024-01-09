"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[7304],{7116:function(e,n,t){t.r(n),t.d(n,{assets:function(){return T},contentTitle:function(){return E},default:function(){return R},frontMatter:function(){return I},metadata:function(){return C},toc:function(){return V}});var r=t(5893),s=t(1151),o=t(7294),i=t(512),a=t(2466),l=t(6550),c=t(469),u=t(1980),d=t(7392),h=t(12);function b(e){return o.Children.toArray(e).filter((e=>"\n"!==e)).map((e=>{if(!e||(0,o.isValidElement)(e)&&function(e){const{props:n}=e;return!!n&&"object"==typeof n&&"value"in n}(e))return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))?.filter(Boolean)??[]}function p(e){const{values:n,children:t}=e;return(0,o.useMemo)((()=>{const e=n??function(e){return b(e).map((e=>{let{props:{value:n,label:t,attributes:r,default:s}}=e;return{value:n,label:t,attributes:r,default:s}}))}(t);return function(e){const n=(0,d.l)(e,((e,n)=>e.value===n.value));if(n.length>0)throw new Error(`Docusaurus error: Duplicate values "${n.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[n,t])}function f(e){let{value:n,tabValues:t}=e;return t.some((e=>e.value===n))}function m(e){let{queryString:n=!1,groupId:t}=e;const r=(0,l.k6)(),s=function(e){let{queryString:n=!1,groupId:t}=e;if("string"==typeof n)return n;if(!1===n)return null;if(!0===n&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return t??null}({queryString:n,groupId:t});return[(0,u._X)(s),(0,o.useCallback)((e=>{if(!s)return;const n=new URLSearchParams(r.location.search);n.set(s,e),r.replace({...r.location,search:n.toString()})}),[s,r])]}function j(e){const{defaultValue:n,queryString:t=!1,groupId:r}=e,s=p(e),[i,a]=(0,o.useState)((()=>function(e){let{defaultValue:n,tabValues:t}=e;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(n){if(!f({value:n,tabValues:t}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${n}" but none of its children has the corresponding value. Available values are: ${t.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return n}const r=t.find((e=>e.default))??t[0];if(!r)throw new Error("Unexpected error: 0 tabValues");return r.value}({defaultValue:n,tabValues:s}))),[l,u]=m({queryString:t,groupId:r}),[d,b]=function(e){let{groupId:n}=e;const t=function(e){return e?`docusaurus.tab.${e}`:null}(n),[r,s]=(0,h.Nk)(t);return[r,(0,o.useCallback)((e=>{t&&s.set(e)}),[t,s])]}({groupId:r}),j=(()=>{const e=l??d;return f({value:e,tabValues:s})?e:null})();(0,c.Z)((()=>{j&&a(j)}),[j]);return{selectedValue:i,selectValue:(0,o.useCallback)((e=>{if(!f({value:e,tabValues:s}))throw new Error(`Can't select invalid tab value=${e}`);a(e),u(e),b(e)}),[u,b,s]),tabValues:s}}var x=t(2389),g={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function k(e){let{className:n,block:t,selectedValue:s,selectValue:o,tabValues:l}=e;const c=[],{blockElementScrollPositionUntilNextRender:u}=(0,a.o5)(),d=e=>{const n=e.currentTarget,t=c.indexOf(n),r=l[t].value;r!==s&&(u(n),o(r))},h=e=>{let n=null;switch(e.key){case"Enter":d(e);break;case"ArrowRight":{const t=c.indexOf(e.currentTarget)+1;n=c[t]??c[0];break}case"ArrowLeft":{const t=c.indexOf(e.currentTarget)-1;n=c[t]??c[c.length-1];break}}n?.focus()};return(0,r.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,i.Z)("tabs",{"tabs--block":t},n),children:l.map((e=>{let{value:n,label:t,attributes:o}=e;return(0,r.jsx)("li",{role:"tab",tabIndex:s===n?0:-1,"aria-selected":s===n,ref:e=>c.push(e),onKeyDown:h,onClick:d,...o,className:(0,i.Z)("tabs__item",g.tabItem,o?.className,{"tabs__item--active":s===n}),children:t??n},n)}))})}function v(e){let{lazy:n,children:t,selectedValue:s}=e;const i=(Array.isArray(t)?t:[t]).filter(Boolean);if(n){const e=i.find((e=>e.props.value===s));return e?(0,o.cloneElement)(e,{className:"margin-top--md"}):null}return(0,r.jsx)("div",{className:"margin-top--md",children:i.map(((e,n)=>(0,o.cloneElement)(e,{key:n,hidden:e.props.value!==s})))})}function A(e){const n=j(e);return(0,r.jsxs)("div",{className:(0,i.Z)("tabs-container",g.tabList),children:[(0,r.jsx)(k,{...e,...n}),(0,r.jsx)(v,{...e,...n})]})}function y(e){const n=(0,x.Z)();return(0,r.jsx)(A,{...e,children:b(e.children)},String(n))}var S={tabItem:"tabItem_Ymn6"};function w(e){let{children:n,hidden:t,className:s}=e;return(0,r.jsx)("div",{role:"tabpanel",className:(0,i.Z)(S.tabItem,s),hidden:t,children:n})}const I={sidebar_position:10},E="Book Structure",C={id:"basics/structure/structure",title:"Book Structure",description:"Each Modonomicon Book consists of Categories, Entries and Pages. Entries are grouped into Categories, which are grouped into the Book itself. Pages are part of the Entries and represent the actual content of the book, they can be used to display text, images, recipes and more.",source:"@site/docs/basics/structure/structure.md",sourceDirName:"basics/structure",slug:"/basics/structure/",permalink:"/modonomicon/docs/basics/structure/",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/structure/structure.md",tags:[],version:"current",sidebarPosition:10,frontMatter:{sidebar_position:10},sidebar:"tutorialSidebar",previous:{title:"Book Crafting Recipes",permalink:"/modonomicon/docs/basics/crafting"},next:{title:"Book.json",permalink:"/modonomicon/docs/basics/structure/book"}},T={},V=[{value:"Book.json",id:"bookjson",level:2},{value:"Categories",id:"categories",level:2},{value:"Entries",id:"entries",level:2},{value:"Pages",id:"pages",level:2}];function P(e){const n={a:"a",br:"br",code:"code",h1:"h1",h2:"h2",img:"img",li:"li",p:"p",ul:"ul",...(0,s.a)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(n.h1,{id:"book-structure",children:"Book Structure"}),"\n",(0,r.jsx)(n.p,{children:"Each Modonomicon Book consists of Categories, Entries and Pages. Entries are grouped into Categories, which are grouped into the Book itself. Pages are part of the Entries and represent the actual content of the book, they can be used to display text, images, recipes and more."}),"\n",(0,r.jsx)(n.p,{children:"The file structure looks as follows:"}),"\n","\n","\n",(0,r.jsxs)(y,{children:[(0,r.jsx)(w,{value:"hierarchy",label:"Hierarchy",default:!0,children:(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsxs)(n.li,{children:[(0,r.jsx)(n.code,{children:"data/<mod_id>/modonomicon/books/<book_id>/"}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsx)(n.li,{children:(0,r.jsx)(n.code,{children:"book.json"})}),"\n",(0,r.jsxs)(n.li,{children:[(0,r.jsx)(n.code,{children:"categories/"}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsx)(n.li,{children:(0,r.jsx)(n.code,{children:"<category_id1>.json"})}),"\n",(0,r.jsx)(n.li,{children:(0,r.jsx)(n.code,{children:"<category_id2>.json"})}),"\n",(0,r.jsx)(n.li,{children:"..."}),"\n"]}),"\n"]}),"\n",(0,r.jsxs)(n.li,{children:[(0,r.jsx)(n.code,{children:"entries/"}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsxs)(n.li,{children:[(0,r.jsx)(n.code,{children:"<category_id1>"}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsx)(n.li,{children:(0,r.jsx)(n.code,{children:"<entry_id1>.json"})}),"\n",(0,r.jsx)(n.li,{children:(0,r.jsx)(n.code,{children:"<entry_id2>.json"})}),"\n",(0,r.jsx)(n.li,{children:"..."}),"\n"]}),"\n"]}),"\n",(0,r.jsxs)(n.li,{children:[(0,r.jsx)(n.code,{children:"<category_id2>"}),"\n",(0,r.jsxs)(n.ul,{children:["\n",(0,r.jsx)(n.li,{children:(0,r.jsx)(n.code,{children:"<entry_id3>.json"})}),"\n",(0,r.jsx)(n.li,{children:(0,r.jsx)(n.code,{children:"<entry_id4>.json"})}),"\n",(0,r.jsx)(n.li,{children:"..."}),"\n"]}),"\n"]}),"\n"]}),"\n"]}),"\n"]}),"\n"]}),"\n"]})}),(0,r.jsxs)(w,{value:"screenshot",label:"Example Screenshot",children:[(0,r.jsxs)(n.p,{children:["A book with the book id ",(0,r.jsx)(n.code,{children:"demo"})," and the mod id ",(0,r.jsx)(n.code,{children:"modonomicon"})," would look like this:"]}),(0,r.jsx)(n.p,{children:(0,r.jsx)(n.img,{alt:"File Structure",src:t(2104).Z+"",width:"272",height:"468"})})]})]}),"\n",(0,r.jsx)(n.h2,{id:"bookjson",children:"Book.json"}),"\n",(0,r.jsxs)(n.p,{children:["The ",(0,r.jsx)(n.code,{children:"book.json"})," file contains the general settings for the book. It is located in the root of the book folder.",(0,r.jsx)(n.br,{}),"\n","See ",(0,r.jsx)(n.a,{href:"./book",children:"Book.json"})," for details."]}),"\n",(0,r.jsx)(n.h2,{id:"categories",children:"Categories"}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.img,{alt:"Categories",src:t(8937).Z+"",width:"1920",height:"1009"})}),"\n",(0,r.jsxs)(n.p,{children:['Categories are a type of "Quest/Advancement"-style 2D view. A book consists of one or more categories, and each category can contain multiple entries that can be linked to each other.',(0,r.jsx)(n.br,{}),"\n","See ",(0,r.jsx)(n.a,{href:"./categories",children:"Categories"})," for details."]}),"\n",(0,r.jsx)(n.h2,{id:"entries",children:"Entries"}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.img,{alt:"Entries",src:t(774).Z+"",width:"61",height:"61"})}),"\n",(0,r.jsxs)(n.p,{children:["Each entry is part of one category and consists of multiple pages, and may be linked to other entries.",(0,r.jsx)(n.br,{}),"\n","See ",(0,r.jsx)(n.a,{href:"./entries",children:"Entries"})," for details."]}),"\n",(0,r.jsx)(n.h2,{id:"pages",children:"Pages"}),"\n",(0,r.jsx)(n.p,{children:(0,r.jsx)(n.img,{alt:"Pages",src:t(6511).Z+"",width:"1121",height:"759"})}),"\n",(0,r.jsxs)(n.p,{children:["Pages are placed inside entries and represent the actual content of the book. They can be used to display text, images, recipes and more.",(0,r.jsx)(n.br,{}),"\n","See ",(0,r.jsx)(n.a,{href:"../page-types",children:"Page Types"})," for details."]})]})}function R(e={}){const{wrapper:n}={...(0,s.a)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(P,{...e})}):P(e)}},8937:function(e,n,t){n.Z=t.p+"assets/images/categories-52684c93bd762016d793140076468da2.png"},774:function(e,n){n.Z="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAD0AAAA9CAYAAAAeYmHpAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAT4SURBVGhD7Zq7TuRIGEaJlghtsMFsNisxy1WAaMQdcb8IwTAiQEhoIUBCJCQkhCQ8AO8w4b7SPoyXY+2Hfn+q6bbBC+6hgyPbdbHr1F9VvnT3tVqtLDIxMdGWubm5Al5/ZmamwPLycoFO9Z2pqalKeH2//sLCQvYxpVMVIy4xPT3dFk4a6VTfG+n5k5OTBTwInj8/P98WztmTTtGp0U5PulukZ2dnC3RqtOd7fS/v+PUdlxobGyvQqRMcylSW7uvrayy1S+vE/3z/rbGojSlZ8XGll5aWsna47N/3vzaW/blfctTm8fHxJD3pFD3pn0Xab95M9Eg3S6fu4zwbvJv0t69fs7/OzvItpMpUpbHSy09T5vr6Oru7u8seHx/z7cPDQ54OqTplaZz0t6doIgdEWCAdoRykztGJ0tJ6+BAUjLxWmuhpGBNhycZoO6RTjzqpc/4Il04tYjyg/G/SGsaSc0EiThrbmK48dQziZeXfTVoRjbgUKA/xWFZoKui4jPybS6tRNFARVKN9K7wjNMcpxzFbjkFTpJ34i6X9I0BZ6ZlWK0cNi40FBKKMOoHj2BGUJY26ytM5f//0Kfvj8+fk9cGleYHyhSwPZJ3SK0/zmO3Q0NDzLYjGgiRAHRHTPI8OUV1EEUaikdJbW1vZ4eFhIfLII6joSgxJRTtKq7MQlezo6GhO46QlfH5+nu+fnJw8Rx4JX9HVCTrW6IiRRXRxcTHfh1qk/QXj4OCgQBVpIo0oxKizRVyRByQlLlnNWQkiy4cM0tmSxn7q+uDSqhNBvDZppBBFEmmizbE6Qh1AOUVeICpZDWMaPDAwkNPf35/L0gmNktbwjhGO+xIXlAdkkVSjXDaCdJXh/SaRRo4ISxhR9pEknWNBPtISVhSjrIvXFumjo6Mscva0ekaqSCOHUFzIXFbpdAbSf375kjcOoiwgCKRtbm5WjjQPI8PDwwXyF466pDW8FVnJQewEIkwZ9hVphCUHkoXd3d1cmChRtnHS9/f32e3tbS6IXBzibNUZ6iBGh2RcGFlQvtpHXur68ObSCFxdXWU3Nze5PCiq3gGAeEpasgz7KEu0Sa8l0uvr61lke3u7QFVpINou/6OFTNJIAbKkuSzzmf0q0oODg9nIyEgBzl2bNAKIKdqAPEhewoq2Io2Q5CWrxY0FjY5AljKNkz4+Pn4WbyePsOY2jUAuRjau5JHV1dVKc/pNpLVQxYi7PMeIc0wdRBm+GsZ+b47Q4FqkuVjE/5hSVhqQAEXSI+7ylNV1GeIMYwnqXi20qqeuK1waQRazSP5wEoXhNdKCuSp5SIkzGijHkOW6zNWUMOmdZMW7SosYeYjznX3yiDC4MLLtFq0UjZAWRBQ07BFm0UOahkn6pbKitPTe3l4W0a1C1CEtNOy14CnSNK7MnO2ES6+srGRra2sF+MPem0oLZAX35tfKikZLC6RT6S+lK6TrprQ0DwQR5lekm6X5M55/6OxJ96QD/PaT//7z34l04iYSZUVS+uLiIotsbGwUkHw3SccXDF46IrlLWWmXbyJRtif9Wmn/Tu5zJs4nYA5F/D8ufn6tIcLz/Vk6JRpJSusNSFxeXhbw/J2dnQLeKMcl+TAX8Ua6lJ+PF4aIf/jjB7qI1+ecPWnoSf+s0v7RYH9/v8Dp6WkBX+hcwi/iP/r7Que4dCcJv76/MPn1qdOT/hjSU9m/onrqXQZ6yHsAAAAASUVORK5CYII="},2104:function(e,n,t){n.Z=t.p+"assets/images/files-b95e083e1cb34193bf9808c4df5469f9.png"},6511:function(e,n,t){n.Z=t.p+"assets/images/pages-9ae09098da9546451b319ddefb1487c9.png"},1151:function(e,n,t){t.d(n,{Z:function(){return a},a:function(){return i}});var r=t(7294);const s={},o=r.createContext(s);function i(e){const n=r.useContext(o);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function a(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:i(e.components),r.createElement(o.Provider,{value:n},e.children)}}}]);