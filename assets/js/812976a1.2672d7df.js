"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[775],{9565:function(n,o,e){e.r(o),e.d(o,{assets:function(){return d},contentTitle:function(){return r},default:function(){return u},frontMatter:function(){return s},metadata:function(){return c},toc:function(){return l}});var t=e(5893),i=e(1151);const s={sidebar_position:30},r="Unlock Conditions",c={id:"basics/unlock-conditions/unlock-conditions",title:"Unlock Conditions",description:"Conditions can be used to keep entries or whole categories hidden until the condition is met. This is useful to give players a sense of progression.",source:"@site/docs/basics/unlock-conditions/unlock-conditions.md",sourceDirName:"basics/unlock-conditions",slug:"/basics/unlock-conditions/",permalink:"/modonomicon/docs/basics/unlock-conditions/",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/unlock-conditions/unlock-conditions.md",tags:[],version:"current",sidebarPosition:30,frontMatter:{sidebar_position:30},sidebar:"tutorialSidebar",previous:{title:"Formatting",permalink:"/modonomicon/docs/basics/formatting"},next:{title:"Entry Read Condition",permalink:"/modonomicon/docs/basics/unlock-conditions/entry-read-condition"}},d={},l=[{value:"Common Attributes",id:"common-attributes",level:2},{value:"<strong>type</strong> (String, <em>mandatory</em>)",id:"type-string-mandatory",level:3},{value:"<strong>tooltip</strong> (DescriptionId or Component JSON, <em>optional</em>)",id:"tooltip-descriptionid-or-component-json-optional",level:3}];function a(n){const o={a:"a",br:"br",code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",p:"p",pre:"pre",strong:"strong",...(0,i.a)(),...n.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(o.h1,{id:"unlock-conditions",children:"Unlock Conditions"}),"\n",(0,t.jsx)(o.p,{children:"Conditions can be used to keep entries or whole categories hidden until the condition is met. This is useful to give players a sense of progression."}),"\n",(0,t.jsx)(o.p,{children:'Conditions are JSON Objects that can be set as value for the "condition" field on entry or category JSONs as follows:'}),"\n",(0,t.jsx)(o.pre,{children:(0,t.jsx)(o.code,{className:"language-json",children:'"condition": {\n    "type": "<type>",\n    ... //condition specific fields\n},\n'})}),"\n",(0,t.jsxs)(o.p,{children:["Note that only one condition can be supplied per entry or category. If you want to combine multiple conditions, you can use the ",(0,t.jsx)(o.a,{href:"./and-condition",children:(0,t.jsx)(o.code,{children:"modonomicon:and"})})," or ",(0,t.jsx)(o.a,{href:"./or-condition",children:(0,t.jsx)(o.code,{children:"modonomicon:or"})})," condition types."]}),"\n",(0,t.jsx)(o.h2,{id:"common-attributes",children:"Common Attributes"}),"\n",(0,t.jsx)(o.p,{children:"The following attributes are available for all condition types:"}),"\n",(0,t.jsxs)(o.h3,{id:"type-string-mandatory",children:[(0,t.jsx)(o.strong,{children:"type"})," (String, ",(0,t.jsx)(o.em,{children:"mandatory"}),")"]}),"\n",(0,t.jsxs)(o.p,{children:["The type of condition, it determines which loader is used to load the json data.\nNeeds to be fully qualified ",(0,t.jsx)(o.code,{children:"domain:name"}),", e.g. ",(0,t.jsx)(o.code,{children:"modonomicon:read_entry"}),"."]}),"\n",(0,t.jsxs)(o.h3,{id:"tooltip-descriptionid-or-component-json-optional",children:[(0,t.jsx)(o.strong,{children:"tooltip"})," (DescriptionId or Component JSON, ",(0,t.jsx)(o.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(o.p,{children:["Default Value: ",(0,t.jsx)(o.em,{children:"A tooltip that explains what needs to be done to unlock the entry based on the condition type."}),(0,t.jsx)(o.br,{}),"\n","The tooltip to display when hovering over the entry locked by this condition. Will only display while the entry is still locked."]})]})}function u(n={}){const{wrapper:o}={...(0,i.a)(),...n.components};return o?(0,t.jsx)(o,{...n,children:(0,t.jsx)(a,{...n})}):a(n)}},1151:function(n,o,e){e.d(o,{Z:function(){return c},a:function(){return r}});var t=e(7294);const i={},s=t.createContext(i);function r(n){const o=t.useContext(s);return t.useMemo((function(){return"function"==typeof n?n(o):{...o,...n}}),[o,n])}function c(n){let o;return o=n.disableParentContext?"function"==typeof n.components?n.components(i):n.components||i:r(n.components),t.createElement(s.Provider,{value:o},n.children)}}}]);