"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[8083],{3905:(e,t,a)=>{a.d(t,{Zo:()=>d,kt:()=>c});var n=a(7294);function o(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function r(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,n)}return a}function i(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?r(Object(a),!0).forEach((function(t){o(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):r(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function l(e,t){if(null==e)return{};var a,n,o=function(e,t){if(null==e)return{};var a,n,o={},r=Object.keys(e);for(n=0;n<r.length;n++)a=r[n],t.indexOf(a)>=0||(o[a]=e[a]);return o}(e,t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(n=0;n<r.length;n++)a=r[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(o[a]=e[a])}return o}var p=n.createContext({}),s=function(e){var t=n.useContext(p),a=t;return e&&(a="function"==typeof e?e(t):i(i({},t),e)),a},d=function(e){var t=s(e.components);return n.createElement(p.Provider,{value:t},e.children)},m="mdxType",h={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},k=n.forwardRef((function(e,t){var a=e.components,o=e.mdxType,r=e.originalType,p=e.parentName,d=l(e,["components","mdxType","originalType","parentName"]),m=s(a),k=o,c=m["".concat(p,".").concat(k)]||m[k]||h[k]||r;return a?n.createElement(c,i(i({ref:t},d),{},{components:a})):n.createElement(c,i({ref:t},d))}));function c(e,t){var a=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var r=a.length,i=new Array(r);i[0]=k;var l={};for(var p in t)hasOwnProperty.call(t,p)&&(l[p]=t[p]);l.originalType=e,l[m]="string"==typeof e?e:o,i[1]=l;for(var s=2;s<r;s++)i[s]=a[s];return n.createElement.apply(null,i)}return n.createElement.apply(null,a)}k.displayName="MDXCreateElement"},582:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>p,contentTitle:()=>i,default:()=>h,frontMatter:()=>r,metadata:()=>l,toc:()=>s});var n=a(7462),o=(a(7294),a(3905));const r={sidebar_position:40},i="Step 4: Adding texts",l={unversionedId:"getting-started/step-by-step-with-datagen/step4",id:"getting-started/step-by-step-with-datagen/step4",title:"Step 4: Adding texts",description:'Finally, it is time to add our texts. That means we will now tell the game what texts to display for all the language keys (or "Description Ids") we have seen in the previous step. To this end we will once again use Modonomicon\'s LanguageHelper to make things easier, as it will help us keep track of the correct language keys so we just have to focus on the texts.',source:"@site/docs/getting-started/step-by-step-with-datagen/step4.md",sourceDirName:"getting-started/step-by-step-with-datagen",slug:"/getting-started/step-by-step-with-datagen/step4",permalink:"/modonomicon/docs/getting-started/step-by-step-with-datagen/step4",draft:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/getting-started/step-by-step-with-datagen/step4.md",tags:[],version:"current",sidebarPosition:40,frontMatter:{sidebar_position:40},sidebar:"tutorialSidebar",previous:{title:"Step 3: Creating the Book",permalink:"/modonomicon/docs/getting-started/step-by-step-with-datagen/step3"},next:{title:"Maven Dependencies",permalink:"/modonomicon/docs/getting-started/maven-dependencies"}},p={},s=[{value:"Preparations: Java Imports",id:"preparations-java-imports",level:2},{value:"Adding translations for the Book Name and Tooltip",id:"adding-translations-for-the-book-name-and-tooltip",level:2},{value:"Adding translations for the Features Category",id:"adding-translations-for-the-features-category",level:2},{value:"Addint translations for our Entry and Pages",id:"addint-translations-for-our-entry-and-pages",level:2},{value:"Results",id:"results",level:2}],d={toc:s},m="wrapper";function h(e){let{components:t,...r}=e;return(0,o.kt)(m,(0,n.Z)({},d,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"step-4-adding-texts"},"Step 4: Adding texts"),(0,o.kt)("p",null,'Finally, it is time to add our texts. That means we will now tell the game what texts to display for all the language keys (or "Description Ids") we have seen in the previous step. To this end we will once again use Modonomicon\'s LanguageHelper to make things easier, as it will help us keep track of the correct language keys so we just have to focus on the texts.'),(0,o.kt)("p",null,"If you are impatient you can skip ahead to ",(0,o.kt)("strong",{parentName:"p"},(0,o.kt)("a",{parentName:"strong",href:"#results"},"Results"))," to see what we will be creating."),(0,o.kt)("h2",{id:"preparations-java-imports"},"Preparations: Java Imports"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"Open ",(0,o.kt)("inlineCode",{parentName:"li"},"EnUsProvider.java")," in your IDE or text editor."),(0,o.kt)("li",{parentName:"ol"},"Below ",(0,o.kt)("inlineCode",{parentName:"li"},"package ...")," but above ",(0,o.kt)("inlineCode",{parentName:"li"},"public class ...")," add the following lines:",(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre",className:"language-java"},"import com.klikli_dev.modonomicon.api.ModonomiconAPI;\nimport com.klikli_dev.modonomicon.api.datagen.BookLangHelper;\nimport com.klikli_dev.modonomicon_demo_book.ModonomiconDemoBook;\n")))),(0,o.kt)("admonition",{type:"tip"},(0,o.kt)("p",{parentName:"admonition"},"If you are using an IDE it might do this step for you.")),(0,o.kt)("h2",{id:"adding-translations-for-the-book-name-and-tooltip"},"Adding translations for the Book Name and Tooltip"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"Open ",(0,o.kt)("inlineCode",{parentName:"li"},"EnUsProvider.java")),(0,o.kt)("li",{parentName:"ol"},"Into the existing method ",(0,o.kt)("inlineCode",{parentName:"li"},"addTranslations")," add:",(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre",className:"language-java"}," this.addDemoBook();\n"))),(0,o.kt)("li",{parentName:"ol"},"And at the bottom of the file, before the last ",(0,o.kt)("inlineCode",{parentName:"li"},"}"),", add the ",(0,o.kt)("inlineCode",{parentName:"li"},"addDemoBook()")," method we are calling above:",(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre",className:"language-java"},' private void addDemoBook(){\n     //We again set up a lang helper to keep track of the translation keys for us.\n     //Forge language provider does not give us access to this.modid, so we get it from our main mod class\n     var helper = ModonomiconAPI.get().getLangHelper(ModonomiconDemoBook.MODID);\n     helper.book("demo"); //we tell the helper the book we\'re in.\n     this.add(helper.bookName(), "Demo Book"); //and now we add the actual textual book name\n     this.add(helper.bookTooltip(), "A book to showcase & test Modonomicon features."); //and the tooltip text\n }\n')))),(0,o.kt)("p",null,"What is happening here? ",(0,o.kt)("inlineCode",{parentName:"p"},"this.add(<language key>, <text>)")," adds a mapping for a given translation key, which we get from our helper, to the given text. The game will then display the text wherever the translation key is used in the book. "),(0,o.kt)("admonition",{type:"tip"},(0,o.kt)("p",{parentName:"admonition"},"The final ingredient is the language code we talked about in ",(0,o.kt)("a",{parentName:"p",href:"./step2#enusproviderjava"},"Step 2"),". This way we can have different texts for different languages, by providing a different Language Provider that uses a different text (such as, a French version) for the same language key. Minecraft will select the correct text based on the language settings of each player.")),(0,o.kt)("p",null,"Let's take a look at our all-new translated book item:"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"In the terminal, run ",(0,o.kt)("inlineCode",{parentName:"li"},"./gradlew runData")," to generate the json file(s)."),(0,o.kt)("li",{parentName:"ol"},"After it is complete, run ",(0,o.kt)("inlineCode",{parentName:"li"},"./gradlew runClient")," to start Minecraft."),(0,o.kt)("li",{parentName:"ol"},"Re-join our old world."),(0,o.kt)("li",{parentName:"ol"},"Hover over the book item - you should now see the name and tooltip we just added, instead of the language key we saw before.")),(0,o.kt)("h2",{id:"adding-translations-for-the-features-category"},"Adding translations for the Features Category"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("p",{parentName:"li"},"Open ",(0,o.kt)("inlineCode",{parentName:"p"},"EnUsProvider.java"))),(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("p",{parentName:"li"},"And at the bottom of the file, before the last ",(0,o.kt)("inlineCode",{parentName:"p"},"}"),", add:"),(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre",className:"language-java"},' private void addDemoBookFeaturesCategory(BookLangHelper helper) {\n     helper.category("features"); //tell the helper the category we are in\n     this.add(helper.categoryName(), "Features Category"); //annd provide the category name text\n }\n'))),(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("p",{parentName:"li"},"Now, at the end of the ",(0,o.kt)("inlineCode",{parentName:"p"},"addDemoBook")," method, add:"),(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre",className:"language-java"},"this.addDemoBookFeaturesCategory(helper);\n")),(0,o.kt)("p",{parentName:"li"},"so it looks something like this"),(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre",className:"language-java"},"private void addDemoBook(){\n     //existing code (unchanged)\n     //...\n     this.addDemoBookFeaturesCategory(helper);\n }\n\n")))),(0,o.kt)("p",null,"Now we can test our translated category:"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"In the terminal, run ",(0,o.kt)("inlineCode",{parentName:"li"},"./gradlew runData")," to generate the json file(s)."),(0,o.kt)("li",{parentName:"ol"},"After it is complete, run ",(0,o.kt)("inlineCode",{parentName:"li"},"./gradlew runClient")," to start Minecraft."),(0,o.kt)("li",{parentName:"ol"},"Re-join our old world."),(0,o.kt)("li",{parentName:"ol"},"Open the book by right-clicking with the book item."),(0,o.kt)("li",{parentName:"ol"},"Hover over the category button on the top leftyou should now see the correct name.")),(0,o.kt)("h2",{id:"addint-translations-for-our-entry-and-pages"},"Addint translations for our Entry and Pages"),(0,o.kt)("p",null,"Finally it is time to add our page texts!"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("p",{parentName:"li"},"Open ",(0,o.kt)("inlineCode",{parentName:"p"},"EnUsProvider.java"))),(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("p",{parentName:"li"},"And at the bottom of the file, before the last ",(0,o.kt)("inlineCode",{parentName:"p"},"}"),", add:"),(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre",className:"language-java"},' private void addDemoBookMultiblockEntry(BookLangHelper helper) {\n     helper.entry("multiblock"); //tell the helper the entry we are in\n     this.add(helper.entryName(), "Multiblock Entry"); //provide the entry name\n     this.add(helper.entryDescription(), "An entry showcasing a multiblock."); //and description\n\n     helper.page("intro"); //now we configure the intro page\n     this.add(helper.pageTitle(), "Multiblock Page"); //page title\n     this.add(helper.pageText(), "Multiblock pages allow to preview multiblocks both in the book and in the world."); //page text\n\n     helper.page("multiblock"); //and finally the multiblock page\n     //now provide the multiblock name\n     //the lang helper does not handle multiblocks, so we manually add the same key we provided in the DemoBookProvider\n     this.add("multiblocks.modonomicon.blockentity", "Blockentity Multiblock.");\n     this.add(helper.pageText(), "A sample multiblock."); //and the multiblock page text \n }\n'))),(0,o.kt)("li",{parentName:"ol"},(0,o.kt)("p",{parentName:"li"},"Now, at the end of the ",(0,o.kt)("inlineCode",{parentName:"p"},"addDemoBookFeaturesCategory")," method, add:"),(0,o.kt)("pre",{parentName:"li"},(0,o.kt)("code",{parentName:"pre",className:"language-java"},"this.addDemoBookMultiblockEntry(helper);\n")))),(0,o.kt)("admonition",{type:"info"},(0,o.kt)("p",{parentName:"admonition"},"Note how we mirror the book setup we prepared in DemoBookProvider here in the EnUSProvider. The language helper works hierarchically - if we call ",(0,o.kt)("inlineCode",{parentName:"p"},"page()")," it keeps the book, category and entry, but changes the page, if we call ",(0,o.kt)("inlineCode",{parentName:"p"},"entry()")," it keeps the book and category, but switches the entry, and so on. This way we can easily keep track of what we are configuring, we just need to remember in each ",(0,o.kt)("inlineCode",{parentName:"p"},"addDemoBook<...>()")," method to call the correct helper method before we add the texts.")),(0,o.kt)("admonition",{type:"tip"},(0,o.kt)("p",{parentName:"admonition"},"If a text element - such as a title or text - is shown is governed in the DemoBookProvider. If we e.g. do not call the ",(0,o.kt)("inlineCode",{parentName:"p"},"withTitle()")," method, there will not be a title on that page. Here in the Language provider we only need to provide the text for elements we configured in the Book Provider. If we do not provide a text for any element the language key will be shown in game, reminding us to add a translation.")),(0,o.kt)("p",null,"Now let's take the entry and pages for a spin:"),(0,o.kt)("ol",null,(0,o.kt)("li",{parentName:"ol"},"In the terminal, run ",(0,o.kt)("inlineCode",{parentName:"li"},"./gradlew runData")," to generate the json file(s)."),(0,o.kt)("li",{parentName:"ol"},"After it is complete, run ",(0,o.kt)("inlineCode",{parentName:"li"},"./gradlew runClient")," to start Minecraft."),(0,o.kt)("li",{parentName:"ol"},"Re-join our old world."),(0,o.kt)("li",{parentName:"ol"},"Open the book by right-clicking with the book item."),(0,o.kt)("li",{parentName:"ol"},"Hover over the entry to see the name and description in the tooltip."),(0,o.kt)("li",{parentName:"ol"},"Click the entry to open it."),(0,o.kt)("li",{parentName:"ol"},"You should see the page texts:\n",(0,o.kt)("img",{alt:"Entry Texts",src:a(5609).Z,width:"1920",height:"1009"}))),(0,o.kt)("h2",{id:"results"},"Results"),(0,o.kt)("p",null,"You can view the final code for this step in the branch for step 3: ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step4"},"https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step4")," "),(0,o.kt)("p",null,'Using the green "Code" Button and "Download ZIP" you can download the code for this step as a zip file to compare to your code.'))}h.isMDXComponent=!0},5609:(e,t,a)=>{a.d(t,{Z:()=>n});const n=a.p+"assets/images/step4-add-entry-texts-4cadd097589036bfa420f753a928c587.png"}}]);