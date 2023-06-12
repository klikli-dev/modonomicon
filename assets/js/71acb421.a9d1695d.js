"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[3575],{3905:(e,t,n)=>{n.d(t,{Zo:()=>s,kt:()=>k});var o=n(7294);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,o)}return n}function r(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,o,i=function(e,t){if(null==e)return{};var n,o,i={},a=Object.keys(e);for(o=0;o<a.length;o++)n=a[o],t.indexOf(n)>=0||(i[n]=e[n]);return i}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(o=0;o<a.length;o++)n=a[o],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(i[n]=e[n])}return i}var p=o.createContext({}),d=function(e){var t=o.useContext(p),n=t;return e&&(n="function"==typeof e?e(t):r(r({},t),e)),n},s=function(e){var t=d(e.components);return o.createElement(p.Provider,{value:t},e.children)},m="mdxType",c={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},u=o.forwardRef((function(e,t){var n=e.components,i=e.mdxType,a=e.originalType,p=e.parentName,s=l(e,["components","mdxType","originalType","parentName"]),m=d(n),u=i,k=m["".concat(p,".").concat(u)]||m[u]||c[u]||a;return n?o.createElement(k,r(r({ref:t},s),{},{components:n})):o.createElement(k,r({ref:t},s))}));function k(e,t){var n=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var a=n.length,r=new Array(a);r[0]=u;var l={};for(var p in t)hasOwnProperty.call(t,p)&&(l[p]=t[p]);l.originalType=e,l[m]="string"==typeof e?e:i,r[1]=l;for(var d=2;d<a;d++)r[d]=n[d];return o.createElement.apply(null,r)}return o.createElement.apply(null,n)}u.displayName="MDXCreateElement"},4033:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>p,contentTitle:()=>r,default:()=>c,frontMatter:()=>a,metadata:()=>l,toc:()=>d});var o=n(7462),i=(n(7294),n(3905));const a={sidebar_position:10},r="Step 1: Setting up the Demo Project",l={unversionedId:"getting-started/step-by-step-with-datagen/step1",id:"getting-started/step-by-step-with-datagen/step1",title:"Step 1: Setting up the Demo Project",description:"Installing OpenJDK",source:"@site/docs/getting-started/step-by-step-with-datagen/step1.md",sourceDirName:"getting-started/step-by-step-with-datagen",slug:"/getting-started/step-by-step-with-datagen/step1",permalink:"/modonomicon/docs/getting-started/step-by-step-with-datagen/step1",draft:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/getting-started/step-by-step-with-datagen/step1.md",tags:[],version:"current",sidebarPosition:10,frontMatter:{sidebar_position:10},sidebar:"tutorialSidebar",previous:{title:"Step by Step Guide for Book Datagen",permalink:"/modonomicon/docs/getting-started/step-by-step-with-datagen/"},next:{title:"Step 2: A first look at the Demo Project",permalink:"/modonomicon/docs/getting-started/step-by-step-with-datagen/step2"}},p={},d=[{value:"Installing OpenJDK",id:"installing-openjdk",level:2},{value:"Downloading the Demo Project",id:"downloading-the-demo-project",level:2},{value:"via Git (Mod Developers)",id:"via-git-mod-developers",level:3},{value:"via ZIP File (Modpack Creators)",id:"via-zip-file-modpack-creators",level:3},{value:"First Test Run",id:"first-test-run",level:2},{value:"IDE Setup",id:"ide-setup",level:3},{value:"Running Minecraft",id:"running-minecraft",level:3}],s={toc:d},m="wrapper";function c(e){let{components:t,...a}=e;return(0,i.kt)(m,(0,o.Z)({},s,a,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"step-1-setting-up-the-demo-project"},"Step 1: Setting up the Demo Project"),(0,i.kt)("h2",{id:"installing-openjdk"},"Installing OpenJDK"),(0,i.kt)("p",null,"The Demo Project uses the Forge Mod loader, which requires OpenJDK 17 installed on your system.\nStart by installing OpenJDK 17 from ",(0,i.kt)("a",{parentName:"p",href:"https://adoptium.net/temurin/releases/?version=17"},"https://adoptium.net/temurin/releases/?version=17"),"."),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},"Make sure to select your correct operating system, and a 64 bit version of the JDK.",(0,i.kt)("br",{parentName:"p"}),"\n","32 bit is not fully supported by Forge.")),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},'If the installer asks to "Add Java to PATH" and or "Set JAVA_HOME" please accept / check the corresponding boxes.')),(0,i.kt)("h2",{id:"downloading-the-demo-project"},"Downloading the Demo Project"),(0,i.kt)("p",null,"For the purposes of this Guide we will start with an empty version of the Demo Project."),(0,i.kt)("admonition",{type:"info"},(0,i.kt)("p",{parentName:"admonition"},"The demo project is on 1.19.2. However, the generated book should work on both 1.18.2 and 1.192. If not please create a github issue at ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/klikli-dev/modonomicon"},"https://github.com/klikli-dev/modonomicon"),".")),(0,i.kt)("h3",{id:"via-git-mod-developers"},"via Git (Mod Developers)"),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"Open terminal"),(0,i.kt)("li",{parentName:"ol"},"Go to a folder of your choice that you want to work in "),(0,i.kt)("li",{parentName:"ol"},"Run ",(0,i.kt)("inlineCode",{parentName:"li"},"git clone git@github.com:klikli-dev/modonomicon-demo-book.git")," "),(0,i.kt)("li",{parentName:"ol"},"Run ",(0,i.kt)("inlineCode",{parentName:"li"},"git branch guide/step1"))),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},"If you know what you are doing you can also skip ahead by skipping step 4, as step 3 will give you the final version of the Demo Project, allowing you to directly edit the provided files.")),(0,i.kt)("h3",{id:"via-zip-file-modpack-creators"},"via ZIP File (Modpack Creators)"),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"Go to ",(0,i.kt)("a",{parentName:"li",href:"https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step1"},"https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step1")," "),(0,i.kt)("li",{parentName:"ol"},'Click the green "Code" button'),(0,i.kt)("li",{parentName:"ol"},'Click "Download Zip"',(0,i.kt)("br",{parentName:"li"}),(0,i.kt)("img",{alt:"Download Zip",src:n(548).Z,width:"400",height:"349"})),(0,i.kt)("li",{parentName:"ol"},"Extract the downloaded zip file to a folder of your choice.")),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},'If you know what you are doing you can also skip ahead and download the "main" branch from ',(0,i.kt)("a",{parentName:"p",href:"https://github.com/klikli-dev/modonomicon-demo-book/tree/main"},"https://github.com/klikli-dev/modonomicon-demo-book/tree/main")," and directly edit the provided files.")),(0,i.kt)("h2",{id:"first-test-run"},"First Test Run"),(0,i.kt)("p",null,"The next steps are required to set up a minecraft development environment allowing you to run the datagen as well as minecraft locally for testing the generated files."),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"Open Terminal in the folder you downloaded and extracted the Demo Project to",(0,i.kt)("ol",{parentName:"li"},(0,i.kt)("li",{parentName:"ol"},"If you see the files ",(0,i.kt)("inlineCode",{parentName:"li"},"gradlew")," and ",(0,i.kt)("inlineCode",{parentName:"li"},"gradlew.bat")," you are in the right folder")))),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},"Not sure how to open the terminal in the folder? "),(0,i.kt)("ol",{parentName:"admonition"},(0,i.kt)("li",{parentName:"ol"},'Most Operating Systems will allow you to right click or shift + right click in the folder and select "Open in Terminal" or similar.'),(0,i.kt)("li",{parentName:"ol"},"Otherwise use the command ",(0,i.kt)("inlineCode",{parentName:"li"},"cd")," like so: ",(0,i.kt)("inlineCode",{parentName:"li"},'cd "<path to folder>"'),(0,i.kt)("ol",{parentName:"li"},(0,i.kt)("li",{parentName:"ol"},"Note: On windows you might first have to switch to the correct drive. If e.g. terminal shows ",(0,i.kt)("inlineCode",{parentName:"li"},"C:\\\\")," but your path starts with ",(0,i.kt)("inlineCode",{parentName:"li"},"D:\\\\")," you can switch to the correct drive by running simply ",(0,i.kt)("inlineCode",{parentName:"li"},"D:")," (no ",(0,i.kt)("inlineCode",{parentName:"li"},"cd")," or any other command/prefix required)"))))),(0,i.kt)("h3",{id:"ide-setup"},"IDE Setup"),(0,i.kt)("admonition",{type:"info"},(0,i.kt)("p",{parentName:"admonition"},"If you are not using an IDE you can skip this step.")),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"Open Terminal in the folder you downloaded and extracted the Demo Project to"),(0,i.kt)("li",{parentName:"ol"},"Generate run configurations:",(0,i.kt)("ul",{parentName:"li"},(0,i.kt)("li",{parentName:"ul"},"For IntelliJ Users: Run ",(0,i.kt)("inlineCode",{parentName:"li"},"./gradlew genIntellijRuns")," "),(0,i.kt)("li",{parentName:"ul"},"For Eclipse Users: Run ",(0,i.kt)("inlineCode",{parentName:"li"},"./gradlew genEclipseRuns")),(0,i.kt)("li",{parentName:"ul"},"For VSCode Users: Run ",(0,i.kt)("inlineCode",{parentName:"li"},"./gradlew genVSCodeRuns"))))),(0,i.kt)("p",null,"In future steps you can use the provided run configurations to run the datagen and minecraft, instead of the commands this guide will use."),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},(0,i.kt)("strong",{parentName:"p"},"Windows Users:")," If you are getting a message along the lines of ",(0,i.kt)("inlineCode",{parentName:"p"},"command . not found")," try running ",(0,i.kt)("inlineCode",{parentName:"p"},"gradlew.bat <...>")," instead of ",(0,i.kt)("inlineCode",{parentName:"p"},"./gradlew <...>"))),(0,i.kt)("h3",{id:"running-minecraft"},"Running Minecraft"),(0,i.kt)("ol",null,(0,i.kt)("li",{parentName:"ol"},"Open Terminal in the folder you downloaded the Demo Project to."),(0,i.kt)("li",{parentName:"ol"},"Run ",(0,i.kt)("inlineCode",{parentName:"li"},"./gradlew runClient"),"."),(0,i.kt)("li",{parentName:"ol"},"After a few seconds (possibly minutes) Minecraft should open and show the main menu.   "),(0,i.kt)("li",{parentName:"ol"},"Success!")),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},(0,i.kt)("strong",{parentName:"p"},"Windows Users:")," If you are getting a message along the lines of ",(0,i.kt)("inlineCode",{parentName:"p"},"command . not found")," try running ",(0,i.kt)("inlineCode",{parentName:"p"},"gradlew.bat <...>")," instead of ",(0,i.kt)("inlineCode",{parentName:"p"},"./gradlew <...>"))))}c.isMDXComponent=!0},548:(e,t,n)=>{n.d(t,{Z:()=>o});const o=n.p+"assets/images/step1-download-zip-fd203ef8b5032d261e76cbffa4034c20.png"}}]);