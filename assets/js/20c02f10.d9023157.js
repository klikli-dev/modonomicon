"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[5060],{6178:function(e,n,o){o.r(n),o.d(n,{assets:function(){return d},contentTitle:function(){return s},default:function(){return h},frontMatter:function(){return r},metadata:function(){return a},toc:function(){return l}});var t=o(5893),i=o(1151);const r={sidebar_position:40,toc_max_heading_level:5},s="Entries",a={id:"basics/structure/entries",title:"Entries",description:"Entries are defined in json files placed in the /data//modonomicon/books//entries// folder.",source:"@site/docs/basics/structure/entries.md",sourceDirName:"basics/structure",slug:"/basics/structure/entries",permalink:"/modonomicon/docs/basics/structure/entries",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/structure/entries.md",tags:[],version:"current",sidebarPosition:40,frontMatter:{sidebar_position:40,toc_max_heading_level:5},sidebar:"tutorialSidebar",previous:{title:"Categories",permalink:"/modonomicon/docs/basics/structure/categories"},next:{title:"Commands",permalink:"/modonomicon/docs/basics/structure/commands"}},d={},l=[{value:"Attributes",id:"attributes",level:2},{value:"<strong>category</strong> (ResourceLocation, <em>mandatory</em>)",id:"category-resourcelocation-mandatory",level:3},{value:"<strong>name</strong> (DescriptionId, <em>mandatory</em>)",id:"name-descriptionid-mandatory",level:3},{value:"<strong>description</strong> (DescriptionId, <em>optional</em>)",id:"description-descriptionid-optional",level:3},{value:"<strong>icon</strong> (ResourceLocation, <em>mandatory</em>)",id:"icon-resourcelocation-mandatory",level:3},{value:"<strong>x</strong> (Integer, <em>mandatory</em>)",id:"x-integer-mandatory",level:3},{value:"<strong>y</strong> (Integer, <em>mandatory</em>)",id:"y-integer-mandatory",level:3},{value:"<strong>hide_while_locked</strong> (Boolean, <em>optional</em>)",id:"hide_while_locked-boolean-optional",level:3},{value:"<strong>background_u_index</strong> (Integer, <em>optional</em>)",id:"background_u_index-integer-optional",level:3},{value:"<strong>background_v_index</strong> (Integer, <em>optional</em>)",id:"background_v_index-integer-optional",level:3},{value:"<strong>condition</strong> (Condition, <em>optional</em>)",id:"condition-condition-optional",level:3},{value:"<strong>parents</strong> (Parent[], <em>optional</em>)",id:"parents-parent-optional",level:3},{value:"<strong>pages</strong> (Page[], <em>optional</em>)",id:"pages-page-optional",level:3},{value:"<strong>category_to_open</strong> (ResourceLocation, <em>optional</em>)",id:"category_to_open-resourcelocation-optional",level:3},{value:"<strong>command_to_run_on_first_read</strong> (ResourceLocation, <em>optional</em>)",id:"command_to_run_on_first_read-resourcelocation-optional",level:3},{value:"Parents",id:"parents",level:2},{value:"Attributes",id:"attributes-1",level:3},{value:"<strong>entry</strong> (ResourceLocation, <em>mandatory</em>)",id:"entry-resourcelocation-mandatory",level:4},{value:"<strong>draw_arrow</strong> (Boolean, <em>optional</em>)",id:"draw_arrow-boolean-optional",level:4},{value:"<strong>line_enabled</strong> (Boolean, <em>optional</em>)",id:"line_enabled-boolean-optional",level:4},{value:"<strong>line_reversed</strong> (Boolean, <em>optional</em>)",id:"line_reversed-boolean-optional",level:4},{value:"Usage Examples",id:"usage-examples",level:2}];function c(e){const n={a:"a",admonition:"admonition",br:"br",code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",h4:"h4",p:"p",pre:"pre",strong:"strong",...(0,i.a)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(n.h1,{id:"entries",children:"Entries"}),"\n",(0,t.jsxs)(n.p,{children:["Entries are defined in json files placed in the ",(0,t.jsx)(n.code,{children:"/data/<mod_id>/modonomicon/books/<book_id>/entries/<category_id>/"})," folder."]}),"\n",(0,t.jsx)(n.h2,{id:"attributes",children:"Attributes"}),"\n",(0,t.jsxs)(n.h3,{id:"category-resourcelocation-mandatory",children:[(0,t.jsx)(n.strong,{children:"category"})," (ResourceLocation, ",(0,t.jsx)(n.em,{children:"mandatory"}),")"]}),"\n",(0,t.jsx)(n.p,{children:"The ResourceLocation of the category this entry should be placed in."}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsx)(n.p,{children:"The file hierarchy of the entry's json file does not actually assign the entry to the category, only this field does!"})}),"\n",(0,t.jsxs)(n.h3,{id:"name-descriptionid-mandatory",children:[(0,t.jsx)(n.strong,{children:"name"})," (DescriptionId, ",(0,t.jsx)(n.em,{children:"mandatory"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["The entry name, will be shown in ",(0,t.jsx)(n.strong,{children:"bold"})," when hovering over the Entry. Will not parse markdown."]}),"\n",(0,t.jsxs)(n.h3,{id:"description-descriptionid-optional",children:[(0,t.jsx)(n.strong,{children:"description"})," (DescriptionId, ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsx)(n.p,{children:"The entry description, will be shown below the name when hovering over the Entry. Will not parse markdown."}),"\n",(0,t.jsxs)(n.h3,{id:"icon-resourcelocation-mandatory",children:[(0,t.jsx)(n.strong,{children:"icon"})," (ResourceLocation, ",(0,t.jsx)(n.em,{children:"mandatory"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.strong,{children:"Either"})," an item/block ResourceLocation that should be used as icon. E.g.:  ",(0,t.jsx)(n.code,{children:"minecraft:nether_star"})," or ",(0,t.jsx)(n.code,{children:"minecraft:chest"}),".",(0,t.jsx)(n.br,{}),"\n",(0,t.jsx)(n.strong,{children:"Or"})," the ResourceLocation to a texture. The texture must be 16x16 pixels. E.g.:  ",(0,t.jsx)(n.code,{children:"modonomicon:textures/gui/some_random_icon.png"}),"."]}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsxs)(n.p,{children:["To use a texture make sure the ResourceLocation includes the file endinge ",(0,t.jsx)(n.code,{children:".png"})," as seen in the example above."]})}),"\n",(0,t.jsxs)(n.h3,{id:"x-integer-mandatory",children:[(0,t.jsx)(n.strong,{children:"x"})," (Integer, ",(0,t.jsx)(n.em,{children:"mandatory"}),")"]}),"\n",(0,t.jsx)(n.p,{children:"The x coordinate (horizontal) of the entry in the category."}),"\n",(0,t.jsxs)(n.h3,{id:"y-integer-mandatory",children:[(0,t.jsx)(n.strong,{children:"y"})," (Integer, ",(0,t.jsx)(n.em,{children:"mandatory"}),")"]}),"\n",(0,t.jsx)(n.p,{children:"The y coordinate (vertical) of the entry in the category."}),"\n",(0,t.jsxs)(n.h3,{id:"hide_while_locked-boolean-optional",children:[(0,t.jsx)(n.strong,{children:"hide_while_locked"})," (Boolean, ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["Default value: ",(0,t.jsx)(n.code,{children:"false"}),". If true, this entry will not be shown as greyed out while it is locked, instead it will be entirely hidden."]}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsxs)(n.p,{children:["Usually the default value is what you want. Regardless of this setting, locked entries will be hidden fully until the entry just before them (= the immediate parent) becomes unlocked, and only then show greyed out. ",(0,t.jsx)(n.code,{children:"true"})," will cause the entry to stay hidden even then."]})}),"\n",(0,t.jsxs)(n.h3,{id:"background_u_index-integer-optional",children:[(0,t.jsx)(n.strong,{children:"background_u_index"})," (Integer, ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["Default value: ",(0,t.jsx)(n.code,{children:"0"}),". Use this to select a different entry background from the ",(0,t.jsx)(n.code,{children:"entry_textures"})," texture configured in the ",(0,t.jsx)(n.a,{href:"./categories#attributes",children:"Category"}),". ",(0,t.jsx)(n.code,{children:"u"})," represents the Y Axis (vertical). The index is zero-based, so the first entry background is ",(0,t.jsx)(n.code,{children:"0"}),", the second is ",(0,t.jsx)(n.code,{children:"1"}),", etc."]}),"\n",(0,t.jsxs)(n.h3,{id:"background_v_index-integer-optional",children:[(0,t.jsx)(n.strong,{children:"background_v_index"})," (Integer, ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["Default value: ",(0,t.jsx)(n.code,{children:"0"}),". Use this to select a different entry background from the ",(0,t.jsx)(n.code,{children:"entry_textures"})," texture configured in the ",(0,t.jsx)(n.a,{href:"./categories#attributes",children:"Category"}),". ",(0,t.jsx)(n.code,{children:"v"})," represents the X Axis (horizontal). The index is zero-based, so the first entry background is ",(0,t.jsx)(n.code,{children:"0"}),", the second is ",(0,t.jsx)(n.code,{children:"1"}),", etc."]}),"\n",(0,t.jsxs)(n.h3,{id:"condition-condition-optional",children:[(0,t.jsx)(n.strong,{children:"condition"})," (Condition, ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["Entries, like Categories, can be hidden until an Unlock Condition is fulfilled. Conditions are JSON objects.",(0,t.jsx)(n.br,{}),"\n","See ",(0,t.jsx)(n.strong,{children:(0,t.jsx)(n.a,{href:"../unlock-conditions",children:"Unlock Conditions"})})," for details."]}),"\n",(0,t.jsxs)(n.h3,{id:"parents-parent-optional",children:[(0,t.jsx)(n.strong,{children:"parents"})," (Parent[], ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["Entry Parents are JSON Objects that define Entries this Entry should be connected to. See ",(0,t.jsx)(n.a,{href:"#parents",children:"Parents"})," for details.\nA parent connection does not imply an unlock condition or any logical dependency, by default it is just a visual connection, however it can be used to automatically define unlock conditions. See ",(0,t.jsx)(n.code,{children:"auto_add_read_conditions"})," in ",(0,t.jsx)(n.a,{href:"../structure/book#attributes",children:"Book.json"}),"."]}),"\n",(0,t.jsxs)(n.h3,{id:"pages-page-optional",children:[(0,t.jsx)(n.strong,{children:"pages"})," (Page[], ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["Pages are JSON Objects that define the actual book content. See ",(0,t.jsx)(n.a,{href:"/modonomicon/docs/basics/page-types/",children:"Page Types"})," for the available types of pages."]}),"\n",(0,t.jsxs)(n.h3,{id:"category_to_open-resourcelocation-optional",children:[(0,t.jsx)(n.strong,{children:"category_to_open"})," (ResourceLocation, ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsx)(n.p,{children:"The resource location to the category that should be opened when this entry is clicked.\nIf this is set, the entry will never show it's pages, but instead open the category directly."}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsxs)(n.p,{children:['This allows to create "sub-category" that does not show up in the category navigation menu, if combined with ',(0,t.jsx)(n.code,{children:'"show_category_button" : false'})," setting for the target category. See also ",(0,t.jsx)(n.a,{href:"./categories#attributes",children:"Categories"}),"."]})}),"\n",(0,t.jsxs)(n.h3,{id:"command_to_run_on_first_read-resourcelocation-optional",children:[(0,t.jsx)(n.strong,{children:"command_to_run_on_first_read"})," (ResourceLocation, ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["The resource location to the command that should be opened when this entry is read/opened for the first time.\nSee ",(0,t.jsx)(n.a,{href:"/modonomicon/docs/basics/structure/commands",children:"Commands"})," on how to create the command."]}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsx)(n.p,{children:"This can be used to e.g. give rewards to players for reaching a certain part of the book."})}),"\n",(0,t.jsx)(n.h2,{id:"parents",children:"Parents"}),"\n",(0,t.jsx)(n.p,{children:"Entry Parents define which entry comes visually before this entry in the book, as in, which entry points an arrow at this entry."}),"\n",(0,t.jsx)(n.h3,{id:"attributes-1",children:"Attributes"}),"\n",(0,t.jsxs)(n.h4,{id:"entry-resourcelocation-mandatory",children:[(0,t.jsx)(n.strong,{children:"entry"})," (ResourceLocation, ",(0,t.jsx)(n.em,{children:"mandatory"}),")"]}),"\n",(0,t.jsx)(n.p,{children:"The ResourceLocation of the parent entry. This is the main attribute."}),"\n",(0,t.jsxs)(n.h4,{id:"draw_arrow-boolean-optional",children:[(0,t.jsx)(n.strong,{children:"draw_arrow"})," (Boolean, ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["Default value: ",(0,t.jsx)(n.code,{children:"true"}),". If false, the line connecting parent and this entry will ",(0,t.jsx)(n.strong,{children:"not"})," have an arrow at the end."]}),"\n",(0,t.jsxs)(n.h4,{id:"line_enabled-boolean-optional",children:[(0,t.jsx)(n.strong,{children:"line_enabled"})," (Boolean, ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["Default value: ",(0,t.jsx)(n.code,{children:"true"}),". If false, there will be no connecting line. This is useful if you want to use the parent connection to define an automatic unlock condition, but don't want to show the line. "]}),"\n",(0,t.jsxs)(n.h4,{id:"line_reversed-boolean-optional",children:[(0,t.jsx)(n.strong,{children:"line_reversed"})," (Boolean, ",(0,t.jsx)(n.em,{children:"optional"}),")"]}),"\n",(0,t.jsxs)(n.p,{children:["Default value: ",(0,t.jsx)(n.code,{children:"false"}),". If true, the line will be drawn from this entry to the parent entry and the arrow will point to the parent, instead of the other way around."]}),"\n",(0,t.jsx)(n.h2,{id:"usage-examples",children:"Usage Examples"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-json",children:'{\n  "name": "multiblock",\n  "description": "modonomicon.test.entries.test_category.multiblock.description",\n  "icon": "minecraft:chest",\n  "x": 6,\n  "y": 2,\n  "category": "modonomicon:test_category",\n  "pages": [\n    {\n      "type": "modonomicon:text",\n      ...\n    },\n    {\n      "type": "modonomicon:multiblock",\n      ...\n    }\n  ],\n  "parents": [\n    {\n      "entry": "modonomicon:test_category/test_entry"\n    }\n  ]\n}\n'})})]})}function h(e={}){const{wrapper:n}={...(0,i.a)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(c,{...e})}):c(e)}},1151:function(e,n,o){o.d(n,{Z:function(){return a},a:function(){return s}});var t=o(7294);const i={},r=t.createContext(i);function s(e){const n=t.useContext(r);return t.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function a(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:s(e.components),t.createElement(r.Provider,{value:n},e.children)}}}]);