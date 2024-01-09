"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[486],{4308:function(e,n,i){i.r(n),i.d(n,{assets:function(){return r},contentTitle:function(){return l},default:function(){return h},frontMatter:function(){return s},metadata:function(){return d},toc:function(){return a}});var t=i(5893),o=i(1151);const s={sidebar_position:30,toc_max_heading_level:5},l="Formatting",d={id:"basics/formatting",title:"Formatting",description:"Modonomicon texts support a subset of Markdown, with some quirks due to how minecraft text rendering works.",source:"@site/docs/basics/formatting.md",sourceDirName:"basics",slug:"/basics/formatting",permalink:"/modonomicon/docs/basics/formatting",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/basics/formatting.md",tags:[],version:"current",sidebarPosition:30,frontMatter:{sidebar_position:30,toc_max_heading_level:5},sidebar:"tutorialSidebar",previous:{title:"Empty Page",permalink:"/modonomicon/docs/basics/page-types/empty-page"},next:{title:"Unlock Conditions",permalink:"/modonomicon/docs/basics/unlock-conditions/"}},r={},a=[{value:"General",id:"general",level:2},{value:"Bold",id:"bold",level:3},{value:"Italics",id:"italics",level:3},{value:"Underline",id:"underline",level:3},{value:"Strikethrough",id:"strikethrough",level:3},{value:"Newline",id:"newline",level:3},{value:"Empty Line",id:"empty-line",level:4},{value:"Lists",id:"lists",level:2},{value:"Headings",id:"headings",level:2},{value:"Images",id:"images",level:2},{value:"Translatable Content nested in Markdown Texts",id:"translatable-content-nested-in-markdown-texts",level:2},{value:"Links",id:"links",level:2},{value:"HTTP Links",id:"http-links",level:3},{value:"Book Links",id:"book-links",level:3},{value:"Book Link",id:"book-link",level:4},{value:"Category Link",id:"category-link",level:4},{value:"Entry Link",id:"entry-link",level:4},{value:"Command Link",id:"command-link",level:3},{value:"Item / Block Link",id:"item--block-link",level:3},{value:"Patchouli Links",id:"patchouli-links",level:3},{value:"Translations",id:"translations",level:4},{value:"Non-Standard Markdown",id:"non-standard-markdown",level:2},{value:"Text Color",id:"text-color",level:3}];function c(e){const n={a:"a",admonition:"admonition",code:"code",em:"em",h1:"h1",h2:"h2",h3:"h3",h4:"h4",li:"li",mdxAdmonitionTitle:"mdxAdmonitionTitle",p:"p",pre:"pre",ul:"ul",...(0,o.a)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(n.h1,{id:"formatting",children:"Formatting"}),"\n",(0,t.jsxs)(n.p,{children:["Modonomicon texts support a subset of ",(0,t.jsx)(n.a,{href:"https://www.markdownguide.org/cheat-sheet/",children:"Markdown"}),", with some quirks due to how minecraft text rendering works.\nAdditionally there is some non-standard functionality to support minecraft-specific use cases."]}),"\n",(0,t.jsx)(n.h2,{id:"general",children:"General"}),"\n",(0,t.jsx)(n.h3,{id:"bold",children:"Bold"}),"\n",(0,t.jsxs)(n.p,{children:["Format: ",(0,t.jsx)(n.code,{children:"**bold**"})]}),"\n",(0,t.jsx)(n.h3,{id:"italics",children:"Italics"}),"\n",(0,t.jsxs)(n.p,{children:["Format: ",(0,t.jsx)(n.code,{children:"*italics*"})," or ",(0,t.jsx)(n.code,{children:"_italics_"})]}),"\n",(0,t.jsx)(n.h3,{id:"underline",children:"Underline"}),"\n",(0,t.jsxs)(n.p,{children:["Format: ",(0,t.jsx)(n.code,{children:"++underline++"})]}),"\n",(0,t.jsx)(n.h3,{id:"strikethrough",children:"Strikethrough"}),"\n",(0,t.jsxs)(n.p,{children:["Format: ",(0,t.jsx)(n.code,{children:"~~stricken through~~"})]}),"\n",(0,t.jsx)(n.h3,{id:"newline",children:"Newline"}),"\n",(0,t.jsxs)(n.p,{children:["Newlines ",(0,t.jsx)(n.em,{children:"generally"})," work like in markdown, meaning a linebreak only renders as newline if it is preceded by three spaces."]}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsxs)(n.p,{children:["If using Java Text Blocks to datagen texts java actually trims spaces at the end of lines so you need to use forced line breaks!\nAlternatively you can place ",(0,t.jsx)(n.code,{children:"\\s"})," at the end of the line after the three spaces to prevent java from trimming the spaces."]})}),"\n",(0,t.jsxs)(n.p,{children:["In markdown linebreak can also be forced with a backslash ",(0,t.jsx)(n.code,{children:"\\"}),". In modonomicon, due to the underlying techonology you need to escape the backslash in both Java and JSON by simply doing two backslashes: ",(0,t.jsx)(n.code,{children:"\\\\"}),"."]}),"\n",(0,t.jsx)(n.h4,{id:"empty-line",children:"Empty Line"}),"\n",(0,t.jsx)(n.p,{children:'Because in many cases a line of text does not perfectly and at the end of the maximum length a line can have in the book, you need to use a little "trick" to force a fully empty line.'}),"\n",(0,t.jsxs)(n.p,{children:["You need two escaped backslashes (= 4 backslashes) like so ",(0,t.jsx)(n.code,{children:"\\\\\\\\"}),", which will tell the markdown parser to do two line breaks. The first linebreak ends the previous text line, the second line then is the empty line."]}),"\n",(0,t.jsx)(n.p,{children:"As seen in the code sample below, you can also spread the two escaped backslashes over two lines for better readability:"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-java",children:'this.add(helper.pageText(),\n  """\n      Chalk is used to draw pentacle runes and define the pentacle shape. Different types of chalk are used for different purposes, as outlined on the next pages.\n      \\\\\n      \\\\\n      The different runes are purely decorative.\n      """);\n'})}),"\n",(0,t.jsx)(n.p,{children:"or"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-java",children:'this.add(helper.pageText(),\n  """\n      Chalk is used to draw pentacle runes and define the pentacle shape. Different types of chalk are used for different purposes, as outlined on the next pages.\\\\\n      \\\\\n      The different runes are purely decorative.\n      """);\n'})}),"\n",(0,t.jsx)(n.admonition,{type:"info",children:(0,t.jsx)(n.p,{children:'This is only necessary in modonomicon (but not in "normal" markdown editors) because of the above-mentioned behaviour of java to strip trailing spaces.\nIn normal markdown you would simply add three spaces at the end of your text line, and then insert the forced line break with a backslash.'})}),"\n",(0,t.jsx)(n.h2,{id:"lists",children:"Lists"}),"\n",(0,t.jsxs)(n.p,{children:["Lists work like in markdown using ",(0,t.jsx)(n.code,{children:"-"})," or ",(0,t.jsx)(n.code,{children:"*"})," for unordered lists and ",(0,t.jsx)(n.code,{children:"1."}),", ",(0,t.jsx)(n.code,{children:"2."}),", ... for ordered lists."]}),"\n",(0,t.jsx)(n.admonition,{type:"caution",children:(0,t.jsx)(n.p,{children:"A newline/empty line should be placed after a list, otherwise the parser will add content of the line to the last list item."})}),"\n",(0,t.jsx)(n.admonition,{type:"caution",children:(0,t.jsxs)(n.p,{children:["Nested lists only have limited support and may not correctly wrap to the next line. If there are issues reduce nesting level or force a line break with ",(0,t.jsx)(n.code,{children:"\\\\"}),"."]})}),"\n",(0,t.jsx)(n.h2,{id:"headings",children:"Headings"}),"\n",(0,t.jsx)(n.p,{children:"Markdown headings are not supported. Page titles are a separate JSON element."}),"\n",(0,t.jsx)(n.h2,{id:"images",children:"Images"}),"\n",(0,t.jsx)(n.p,{children:"Like headings, images are not supported. There are specific page types to display images."}),"\n",(0,t.jsx)(n.h2,{id:"translatable-content-nested-in-markdown-texts",children:"Translatable Content nested in Markdown Texts"}),"\n",(0,t.jsxs)(n.p,{children:["To included the translated contents from a DescriptionId contained in ",(0,t.jsx)(n.code,{children:"lang.json"})," simply use ",(0,t.jsx)(n.code,{children:"<t>my.description.id</t>"}),"."]}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsx)(n.p,{children:"The translated contents will not be sent through the markdown renderer, however you can use this to e.g. have an item (or any vanilla/modded object type that uses the minecraft translation system) name automatically be translated."})}),"\n",(0,t.jsx)(n.h2,{id:"links",children:"Links"}),"\n",(0,t.jsxs)(n.p,{children:["Markdown links use the following syntax: ",(0,t.jsx)(n.code,{children:"[text](url)"}),"."]}),"\n",(0,t.jsx)(n.h3,{id:"http-links",children:"HTTP Links"}),"\n",(0,t.jsx)(n.p,{children:"HTTP URLs will be handled like in the minecraft chat, which means clicking them will show an approval dialogue window and will then open the URL in an external browser."}),"\n",(0,t.jsx)(n.h3,{id:"book-links",children:"Book Links"}),"\n",(0,t.jsx)(n.p,{children:"Book links are special links between different pages of the same book, or even to other books.\nThere are three types:"}),"\n",(0,t.jsx)(n.h4,{id:"book-link",children:"Book Link"}),"\n",(0,t.jsx)(n.p,{children:"Open another book on it's default page.\nSyntax:"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:(0,t.jsx)(n.code,{children:"[display text](book://<book-id>)"})}),"\n"]}),"\n",(0,t.jsx)(n.h4,{id:"category-link",children:"Category Link"}),"\n",(0,t.jsx)(n.p,{children:"Open a category (in the same book, or in another book).\nSyntax:"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:(0,t.jsx)(n.code,{children:"[display text](book://<book-id>/<category-id>)"})}),"\n",(0,t.jsx)(n.li,{children:(0,t.jsx)(n.code,{children:"[display text](book://<category-id>)"})}),"\n"]}),"\n",(0,t.jsxs)(n.admonition,{type:"tip",children:[(0,t.jsx)(n.mdxAdmonitionTitle,{}),(0,t.jsxs)(n.p,{children:["If ",(0,t.jsx)(n.code,{children:"<book-id>"})," is ommitted the current book is assumed."]})]}),"\n",(0,t.jsx)(n.h4,{id:"entry-link",children:"Entry Link"}),"\n",(0,t.jsx)(n.p,{children:"Opens an entry (in the same book, or in another book), optionally at either a given page number or page anchor.\nSyntax:"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.code,{children:"[display text](book://<book-id>/<entry-id>[#page-number][@page-anchor])"}),"."]}),"\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.code,{children:"[display text](book://<entry-id>[#page-number][@page-anchor])"}),"."]}),"\n"]}),"\n",(0,t.jsxs)(n.admonition,{type:"tip",children:[(0,t.jsx)(n.mdxAdmonitionTitle,{}),(0,t.jsxs)(n.p,{children:["If ",(0,t.jsx)(n.code,{children:"<book-id>"})," is ommitted the current book is assumed."]})]}),"\n",(0,t.jsx)(n.admonition,{type:"tip",children:(0,t.jsxs)(n.p,{children:["The entry id is the full path of the entry within the ",(0,t.jsx)(n.code,{children:"/entries/"})," folder, that often includes the category id, if it is used as a subdirectory, e.g. for ",(0,t.jsx)(n.code,{children:"/entries/my-category/my-entry"})," the entry id is ",(0,t.jsx)(n.code,{children:"my-category/my-entry"}),"."]})}),"\n",(0,t.jsx)(n.h3,{id:"command-link",children:"Command Link"}),"\n",(0,t.jsxs)(n.p,{children:["Command links are links that can run ",(0,t.jsx)(n.a,{href:"/modonomicon/docs/basics/structure/commands",children:"Commands"})," when clicked.\nSee ",(0,t.jsx)(n.a,{href:"/modonomicon/docs/basics/structure/commands",children:"Commands"})," on how to create commands.\nSyntax:"]}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.code,{children:"[display text](command://<book-id>/<command-id>"}),"."]}),"\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.code,{children:"[display text](command://<command-id>"}),"."]}),"\n"]}),"\n",(0,t.jsx)(n.p,{children:"If no book id is provided the link will look for the command in the currently open book."}),"\n",(0,t.jsxs)(n.admonition,{type:"tip",children:[(0,t.jsx)(n.mdxAdmonitionTitle,{}),(0,t.jsx)(n.p,{children:"Command links can be used to e.g. give rewards to players by adding them to a page."})]}),"\n",(0,t.jsx)(n.h3,{id:"item--block-link",children:"Item / Block Link"}),"\n",(0,t.jsx)(n.p,{children:"Item links are links that cannot be clicked, but that will show the (translated) item or block name if hovered, and if not provided with a link text, will also automatically have the translated name as rendered text."}),"\n",(0,t.jsx)(n.p,{children:"Syntax:"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:(0,t.jsx)(n.code,{children:"[optional display text](item://minecraft:apple)"})}),"\n",(0,t.jsx)(n.li,{children:(0,t.jsx)(n.code,{children:"[](item://minecraft:chest)"})}),"\n"]}),"\n",(0,t.jsx)(n.h3,{id:"patchouli-links",children:"Patchouli Links"}),"\n",(0,t.jsx)(n.p,{children:"Patchouli links are special links that can be used to link to a particular patchouli entry and open it on click."}),"\n",(0,t.jsx)(n.p,{children:"Syntax:"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.code,{children:"[display text](patchouli://<mod_id>:<patchouli_book_id>//<entry_id>#<page_number>)"}),"."]}),"\n",(0,t.jsxs)(n.li,{children:[(0,t.jsx)(n.code,{children:"[display text](patchouli://<mod_id>:<patchouli_book_id>//<entry_id>)"}),"."]}),"\n"]}),"\n",(0,t.jsx)(n.p,{children:"Example:"}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:(0,t.jsx)(n.code,{children:"[Link to a Patchouli Entry](patchouli://occultism:dictionary_of_spirits//misc/books_of_calling)"})}),"\n"]}),"\n",(0,t.jsxs)(n.admonition,{type:"caution",children:[(0,t.jsx)(n.mdxAdmonitionTitle,{}),(0,t.jsxs)(n.p,{children:["Note the double ",(0,t.jsx)(n.code,{children:"//"})," separating the book id from the entry id. This is required, because both book and entry ids may contain one or multile  ",(0,t.jsx)(n.code,{children:"/"})," characters if the files are in subdirectories."]})]}),"\n",(0,t.jsxs)(n.admonition,{type:"tip",children:[(0,t.jsx)(n.mdxAdmonitionTitle,{}),(0,t.jsxs)(n.p,{children:["The entry id is the full path of the entry within the ",(0,t.jsx)(n.code,{children:"/entries/"})," folder in ",(0,t.jsx)(n.code,{children:"patchouli_books"}),", the path you would also use for links within patchouli with the ",(0,t.jsx)(n.code,{children:"$(l:<entry_id>)"})," syntax."]})]}),"\n",(0,t.jsx)(n.h4,{id:"translations",children:"Translations"}),"\n",(0,t.jsxs)(n.p,{children:["On hover the link will attempt to display the name of the patchouli page that will be opened on click.\nPatchouli does not have a standard DescriptionId format for entry names (in fact, entry names can be provided without using the translation system at all), so you need to manually include the translation for the link text in your ",(0,t.jsx)(n.code,{children:"lang.json"})," file."]}),"\n",(0,t.jsxs)(n.p,{children:["The DescriptionId used for hover texts is ",(0,t.jsx)(n.code,{children:"patchouli.<patchouli_book_id>.<entry_id>.name"}),", e.g. ",(0,t.jsx)(n.code,{children:"patchouli.occultism.dictionary_of_spirits.misc.books_of_calling.name"}),". Make sure to provide a translation for this DescriptionId in your ",(0,t.jsx)(n.code,{children:"lang.json"})," file (or better, in your language datagen), otherwise the hover text will show the DescriptionId itself."]}),"\n",(0,t.jsxs)(n.admonition,{type:"tip",children:[(0,t.jsx)(n.mdxAdmonitionTitle,{}),(0,t.jsxs)(n.p,{children:["The ",(0,t.jsx)(n.code,{children:"<patchouli_book_id>"})," will include the mod id, the ",(0,t.jsx)(n.code,{children:"<entry_id>"})," will not."]})]}),"\n",(0,t.jsx)(n.h2,{id:"non-standard-markdown",children:"Non-Standard Markdown"}),"\n",(0,t.jsx)(n.p,{children:"In order to provide a bit more flexibility, the markdown parser supports a few non-standard instructions."}),"\n",(0,t.jsx)(n.h3,{id:"text-color",children:"Text Color"}),"\n",(0,t.jsxs)(n.p,{children:["Color instructions (ab)use the link syntax as follows: to start a colored region use ",(0,t.jsx)(n.code,{children:"[#](<hexcode>)"}),", to reset to the default color use ",(0,t.jsx)(n.code,{children:"[#]()"}),".\nExample:"]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-markdown",children:"[#](ff0000)Red text [#](00ff00)from here on green [#](0000ff)now blue [#]()and finally back to default color.\n"})})]})}function h(e={}){const{wrapper:n}={...(0,o.a)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(c,{...e})}):c(e)}},1151:function(e,n,i){i.d(n,{Z:function(){return d},a:function(){return l}});var t=i(7294);const o={},s=t.createContext(o);function l(e){const n=t.useContext(s);return t.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function d(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:l(e.components),t.createElement(s.Provider,{value:n},e.children)}}}]);