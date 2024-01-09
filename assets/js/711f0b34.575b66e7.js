"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[4145],{5300:function(e,n,t){t.r(n),t.d(n,{assets:function(){return c},contentTitle:function(){return r},default:function(){return p},frontMatter:function(){return s},metadata:function(){return a},toc:function(){return l}});var o=t(5893),i=t(1151);const s={sidebar_position:20},r="Page Types",a={id:"basics/page-types/page-types",title:"Page Types",description:"Common Attributes",source:"@site/docs/basics/page-types/page-types.md",sourceDirName:"basics/page-types",slug:"/basics/page-types/",permalink:"/modonomicon/docs/basics/page-types/",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/page-types/page-types.md",tags:[],version:"current",sidebarPosition:20,frontMatter:{sidebar_position:20},sidebar:"tutorialSidebar",previous:{title:"Commands",permalink:"/modonomicon/docs/basics/structure/commands"},next:{title:"Text Page",permalink:"/modonomicon/docs/basics/page-types/text-page"}},c={},l=[{value:"Common Attributes",id:"common-attributes",level:2},{value:"<strong>type</strong> (String, <em>mandatory</em>)",id:"type-string-mandatory",level:3},{value:"<strong>anchor</strong> (String, <em>optional</em>)",id:"anchor-string-optional",level:3},{value:"A note on texts",id:"a-note-on-texts",level:3}];function d(e){const n={a:"a",admonition:"admonition",br:"br",code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",li:"li",p:"p",strong:"strong",ul:"ul",...(0,i.a)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(n.h1,{id:"page-types",children:"Page Types"}),"\n",(0,o.jsx)(n.h2,{id:"common-attributes",children:"Common Attributes"}),"\n",(0,o.jsx)(n.p,{children:"The following attributes are available for all page types"}),"\n",(0,o.jsxs)(n.h3,{id:"type-string-mandatory",children:[(0,o.jsx)(n.strong,{children:"type"})," (String, ",(0,o.jsx)(n.em,{children:"mandatory"}),")"]}),"\n",(0,o.jsxs)(n.p,{children:["The type of page, it determines which loader is used to load the json data and how the page will be displayed.\nNeeds to be fully qualified ",(0,o.jsx)(n.code,{children:"domain:name"}),", e.g. ",(0,o.jsx)(n.code,{children:"modonomicon:text"}),"."]}),"\n",(0,o.jsxs)(n.h3,{id:"anchor-string-optional",children:[(0,o.jsx)(n.strong,{children:"anchor"})," (String, ",(0,o.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,o.jsx)(n.p,{children:"A string to uniquely identify the page within the entry it belongs to. Allows to link to specific pages even if the number of pages changes."}),"\n",(0,o.jsx)(n.h3,{id:"a-note-on-texts",children:"A note on texts"}),"\n",(0,o.jsxs)(n.p,{children:["See also ",(0,o.jsx)(n.a,{href:"../../advanced/localization",children:"Localization"}),"."]}),"\n",(0,o.jsx)(n.p,{children:"Whenever a page supports texts there are two options:"}),"\n",(0,o.jsxs)(n.ul,{children:["\n",(0,o.jsxs)(n.li,{children:[(0,o.jsx)(n.strong,{children:"supply a DescriptionId"})," (= Translation Key) with corresponding value in the ",(0,o.jsx)(n.code,{children:"/lang/*.json"})," file.",(0,o.jsx)(n.br,{}),"\n","In many cases that value can contain markdown styling instructions."]}),"\n",(0,o.jsxs)(n.li,{children:["supply a vanilla component JSON (not recommended). This can contain untranslated texts and will ",(0,o.jsx)(n.strong,{children:"not"})," support markdown styling."]}),"\n"]}),"\n",(0,o.jsx)(n.admonition,{type:"tip",children:(0,o.jsx)(n.p,{children:"It is highly recommend to only use DescriptionIds (= Translation Keys) whenever you supply text for a page, and provide the actual content and (markdown) formatting via corresponding entry in the language file."})})]})}function p(e={}){const{wrapper:n}={...(0,i.a)(),...e.components};return n?(0,o.jsx)(n,{...e,children:(0,o.jsx)(d,{...e})}):d(e)}},1151:function(e,n,t){t.d(n,{Z:function(){return a},a:function(){return r}});var o=t(7294);const i={},s=o.createContext(i);function r(e){const n=o.useContext(s);return o.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function a(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:r(e.components),o.createElement(s.Provider,{value:n},e.children)}}}]);