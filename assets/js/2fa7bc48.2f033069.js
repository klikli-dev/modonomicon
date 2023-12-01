"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[2972],{7170:function(e,n,s){s.r(n),s.d(n,{assets:function(){return d},contentTitle:function(){return a},default:function(){return l},frontMatter:function(){return t},metadata:function(){return r},toc:function(){return c}});var o=s(5893),i=s(1151);const t={sidebar_position:50},a="Commands",r={id:"basics/structure/commands",title:"Commands",description:"Commands are defined in json files placed in the /data//modonomicons//commands/ folder.",source:"@site/docs/basics/structure/commands.md",sourceDirName:"basics/structure",slug:"/basics/structure/commands",permalink:"/modonomicon/docs/basics/structure/commands",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/structure/commands.md",tags:[],version:"current",sidebarPosition:50,frontMatter:{sidebar_position:50},sidebar:"tutorialSidebar",previous:{title:"Entries",permalink:"/modonomicon/docs/basics/structure/entries"},next:{title:"Page Types",permalink:"/modonomicon/docs/basics/page-types/"}},d={},c=[{value:"Command IDs",id:"command-ids",level:2},{value:"Attributes",id:"attributes",level:2},{value:"<strong>command</strong> (String, <em>mandatory</em>)",id:"command-string-mandatory",level:3},{value:"<strong>permission_level</strong> (Integer, <em>optional</em>)",id:"permission_level-integer-optional",level:3},{value:"<strong>max_usages</strong> (Integer, <em>optional</em>)",id:"max_usages-integer-optional",level:3},{value:"<strong>failure_message</strong> (DescriptionId, <em>optional</em>)",id:"failure_message-descriptionid-optional",level:3},{value:"<strong>success_message</strong> (DescriptionId, <em>optional</em>)",id:"success_message-descriptionid-optional",level:3},{value:"Usage Examples",id:"usage-examples",level:2}];function m(e){const n={a:"a",admonition:"admonition",br:"br",code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",p:"p",pre:"pre",strong:"strong",...(0,i.a)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsx)(n.h1,{id:"commands",children:"Commands"}),"\n",(0,o.jsxs)(n.p,{children:["Commands are defined in json files placed in the ",(0,o.jsx)(n.code,{children:"/data/<mod_id>/modonomicons/<book_id>/commands/"})," folder.",(0,o.jsx)(n.br,{}),"\n","They are intended to give book creators more flexibility. Commands can be triggered either via a ",(0,o.jsx)(n.a,{href:"/modonomicon/docs/basics/formatting#command-link",children:"Command Link"})," or when an entry is first read/openend(See ",(0,o.jsx)(n.a,{href:"/modonomicon/docs/basics/structure/entries#attributes",children:"Entry - command_to_run_on_first_read"}),")."]}),"\n",(0,o.jsxs)(n.p,{children:["Commands are guarded against abuse and by default can only be run once per player per world (even if the player resets the book).",(0,o.jsx)(n.br,{}),"\n","In the book you need to specify the command's id (ResourceLocation) instead of the actual minecraft command."]}),"\n",(0,o.jsx)(n.h2,{id:"command-ids",children:"Command IDs"}),"\n",(0,o.jsxs)(n.p,{children:["The id for ",(0,o.jsx)(n.code,{children:"/data/<mod_id>/modonomicons/my_book/commands/test_command"})," would be ",(0,o.jsx)(n.code,{children:"<mod_id>:test_command"}),".",(0,o.jsx)(n.br,{}),"\n","The book id is not part of the command id, because the book id is taken from context (from the command link or the entry)."]}),"\n",(0,o.jsxs)(n.p,{children:["For ",(0,o.jsx)(n.code,{children:"/data/<mod_id>/modonomicons/my_book/commands/rewards/apple"})," the id would be ",(0,o.jsx)(n.code,{children:"<mod_id>:rewards/apple"}),"."]}),"\n",(0,o.jsx)(n.h2,{id:"attributes",children:"Attributes"}),"\n",(0,o.jsxs)(n.h3,{id:"command-string-mandatory",children:[(0,o.jsx)(n.strong,{children:"command"})," (String, ",(0,o.jsx)(n.em,{children:"mandatory"}),")"]}),"\n",(0,o.jsx)(n.p,{children:'The minecraft command to run. Commands will be run with the player as "sender", so take that into account.'}),"\n",(0,o.jsxs)(n.h3,{id:"permission_level-integer-optional",children:[(0,o.jsx)(n.strong,{children:"permission_level"})," (Integer, ",(0,o.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,o.jsxs)(n.p,{children:["Defaults to ",(0,o.jsx)(n.code,{children:"0"}),".",(0,o.jsx)(n.br,{}),"\n","The permission level to run the command with. This will be used instead of the actual permission level of the player."]}),"\n",(0,o.jsx)(n.admonition,{type:"tip",children:(0,o.jsx)(n.p,{children:"For most commands (such as /give) you will need at least permission level 2."})}),"\n",(0,o.jsx)(n.admonition,{type:"tip",children:(0,o.jsx)(n.p,{children:"When testing and the command fails with an obscure error message (such as: incomplete command), despite the same command working fine when running it in chat, try to increase the permission level up to 4."})}),"\n",(0,o.jsxs)(n.h3,{id:"max_usages-integer-optional",children:[(0,o.jsx)(n.strong,{children:"max_usages"})," (Integer, ",(0,o.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,o.jsxs)(n.p,{children:["Defaults to ",(0,o.jsx)(n.code,{children:"-1"}),".",(0,o.jsx)(n.br,{}),"\n","The maximum amount of times the command can be used. Modonomicon will keep track of this per command json, independent of how the command is triggered. Resetting the modonomicon will not reset the command count."]}),"\n",(0,o.jsx)(n.admonition,{type:"tip",children:(0,o.jsx)(n.p,{children:"This ensures that commands cannot be abused to cheat a reward multiple times."})}),"\n",(0,o.jsxs)(n.h3,{id:"failure_message-descriptionid-optional",children:[(0,o.jsx)(n.strong,{children:"failure_message"})," (DescriptionId, ",(0,o.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,o.jsxs)(n.p,{children:["A custom failure message to display, if the command has been used beyond max_usages already.",(0,o.jsx)(n.br,{}),"\n","I none is provided, a default message will be shown.\nOther failures (such as invalid command syntax) are handled by minecraft and have a failure message rom there."]}),"\n",(0,o.jsx)(n.admonition,{type:"tip",children:(0,o.jsx)(n.p,{children:"In most cases this message will never show. One of the possible scenarios is with an entry read command, if the book gets reset and the player reads the entry again. The command will not execute again, and instead this message (or the default) will show."})}),"\n",(0,o.jsxs)(n.h3,{id:"success_message-descriptionid-optional",children:[(0,o.jsx)(n.strong,{children:"success_message"})," (DescriptionId, ",(0,o.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,o.jsxs)(n.p,{children:["A custom success message to display, if the command has executed successfully.\nI none is provided, ",(0,o.jsx)(n.strong,{children:"no"})," message will be shown."]}),"\n",(0,o.jsx)(n.admonition,{type:"tip",children:(0,o.jsx)(n.p,{children:"This can be used to e.g. let the player know why they suddenly have a new item in their inventory."})}),"\n",(0,o.jsx)(n.h2,{id:"usage-examples",children:"Usage Examples"}),"\n",(0,o.jsxs)(n.p,{children:[(0,o.jsx)(n.code,{children:"/data/<mod_id>/modonomicons/<book_id>/commands/test_command.json"}),":"]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-json",children:'{\n  "command": "/give @s minecraft:apple 1",\n  "max_uses": 1,\n  "permission_level": 2,\n  "success_message": "modonomicon.command.test_command.success"\n}\n'})}),"\n",(0,o.jsxs)(n.p,{children:[(0,o.jsx)(n.code,{children:"/lang/*.json"}),":"]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-json",children:'{\n    "modonomicon.command.test_command.success": "You got an apple, because reading is cool!",\n}\n'})})]})}function l(e={}){const{wrapper:n}={...(0,i.a)(),...e.components};return n?(0,o.jsx)(n,{...e,children:(0,o.jsx)(m,{...e})}):m(e)}},1151:function(e,n,s){s.d(n,{Z:function(){return r},a:function(){return a}});var o=s(7294);const i={},t=o.createContext(i);function a(e){const n=o.useContext(t);return o.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function r(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:a(e.components),o.createElement(t.Provider,{value:n},e.children)}}}]);