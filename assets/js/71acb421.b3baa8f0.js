"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[3575],{8225:function(e,n,t){t.r(n),t.d(n,{assets:function(){return l},contentTitle:function(){return r},default:function(){return h},frontMatter:function(){return s},metadata:function(){return d},toc:function(){return c}});var i=t(5893),o=t(1151);const s={sidebar_position:10},r="Step 1: Setting up the Demo Project",d={id:"getting-started/step-by-step-with-datagen/step1",title:"Step 1: Setting up the Demo Project",description:"Installing OpenJDK",source:"@site/docs/getting-started/step-by-step-with-datagen/step1.md",sourceDirName:"getting-started/step-by-step-with-datagen",slug:"/getting-started/step-by-step-with-datagen/step1",permalink:"/modonomicon/docs/getting-started/step-by-step-with-datagen/step1",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/getting-started/step-by-step-with-datagen/step1.md",tags:[],version:"current",sidebarPosition:10,frontMatter:{sidebar_position:10},sidebar:"tutorialSidebar",previous:{title:"Step by Step Guide for Book Datagen",permalink:"/modonomicon/docs/getting-started/step-by-step-with-datagen/"},next:{title:"Step 2: A first look at the Demo Project",permalink:"/modonomicon/docs/getting-started/step-by-step-with-datagen/step2"}},l={},c=[{value:"Installing OpenJDK",id:"installing-openjdk",level:2},{value:"Downloading the Demo Project",id:"downloading-the-demo-project",level:2},{value:"via Git (Mod Developers)",id:"via-git-mod-developers",level:3},{value:"via ZIP File (Modpack Creators)",id:"via-zip-file-modpack-creators",level:3},{value:"First Test Run",id:"first-test-run",level:2},{value:"IDE Setup",id:"ide-setup",level:3},{value:"Running Minecraft",id:"running-minecraft",level:3},{value:"Important: Improvements &amp; Convenience",id:"important-improvements--convenience",level:2}];function a(e){const n={a:"a",admonition:"admonition",br:"br",code:"code",h1:"h1",h2:"h2",h3:"h3",img:"img",li:"li",mdxAdmonitionTitle:"mdxAdmonitionTitle",ol:"ol",p:"p",strong:"strong",ul:"ul",...(0,o.a)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.h1,{id:"step-1-setting-up-the-demo-project",children:"Step 1: Setting up the Demo Project"}),"\n",(0,i.jsx)(n.h2,{id:"installing-openjdk",children:"Installing OpenJDK"}),"\n",(0,i.jsxs)(n.p,{children:["The Demo Project uses the Forge Mod loader, which requires OpenJDK 17 installed on your system.\nStart by installing OpenJDK 17 from ",(0,i.jsx)(n.a,{href:"https://adoptium.net/temurin/releases/?version=17",children:"https://adoptium.net/temurin/releases/?version=17"}),"."]}),"\n",(0,i.jsx)(n.admonition,{type:"tip",children:(0,i.jsxs)(n.p,{children:["Make sure to select your correct operating system, and a 64 bit version of the JDK.",(0,i.jsx)(n.br,{}),"\n","32 bit is not fully supported by Forge."]})}),"\n",(0,i.jsx)(n.admonition,{type:"tip",children:(0,i.jsx)(n.p,{children:'If the installer asks to "Add Java to PATH" and or "Set JAVA_HOME" please accept / check the corresponding boxes.'})}),"\n",(0,i.jsx)(n.h2,{id:"downloading-the-demo-project",children:"Downloading the Demo Project"}),"\n",(0,i.jsx)(n.p,{children:"For the purposes of this Guide we will start with an empty version of the Demo Project."}),"\n",(0,i.jsx)(n.admonition,{type:"info",children:(0,i.jsxs)(n.p,{children:["The demo project is on 1.19.2. However, the generated book should work on both 1.18.2 and 1.192. If not please create a github issue at ",(0,i.jsx)(n.a,{href:"https://github.com/klikli-dev/modonomicon",children:"https://github.com/klikli-dev/modonomicon"}),"."]})}),"\n",(0,i.jsx)(n.h3,{id:"via-git-mod-developers",children:"via Git (Mod Developers)"}),"\n",(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsx)(n.li,{children:"Open terminal"}),"\n",(0,i.jsx)(n.li,{children:"Go to a folder of your choice that you want to work in"}),"\n",(0,i.jsxs)(n.li,{children:["Run ",(0,i.jsx)(n.code,{children:"git clone git@github.com:klikli-dev/modonomicon-demo-book.git"})]}),"\n",(0,i.jsxs)(n.li,{children:["Run ",(0,i.jsx)(n.code,{children:"git branch guide/step1"})]}),"\n"]}),"\n",(0,i.jsx)(n.admonition,{type:"tip",children:(0,i.jsx)(n.p,{children:"If you know what you are doing you can also skip ahead by skipping step 4, as step 3 will give you the final version of the Demo Project, allowing you to directly edit the provided files."})}),"\n",(0,i.jsx)(n.h3,{id:"via-zip-file-modpack-creators",children:"via ZIP File (Modpack Creators)"}),"\n",(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsxs)(n.li,{children:["Go to ",(0,i.jsx)(n.a,{href:"https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step1",children:"https://github.com/klikli-dev/modonomicon-demo-book/tree/guide/step1"})]}),"\n",(0,i.jsx)(n.li,{children:'Click the green "Code" button'}),"\n",(0,i.jsxs)(n.li,{children:['Click "Download Zip"',(0,i.jsx)(n.br,{}),"\n",(0,i.jsx)(n.img,{alt:"Download Zip",src:t(548).Z+"",width:"400",height:"349"})]}),"\n",(0,i.jsx)(n.li,{children:"Extract the downloaded zip file to a folder of your choice."}),"\n"]}),"\n",(0,i.jsx)(n.admonition,{type:"tip",children:(0,i.jsxs)(n.p,{children:['If you know what you are doing you can also skip ahead and download the "main" branch from ',(0,i.jsx)(n.a,{href:"https://github.com/klikli-dev/modonomicon-demo-book/tree/main",children:"https://github.com/klikli-dev/modonomicon-demo-book/tree/main"})," and directly edit the provided files."]})}),"\n",(0,i.jsx)(n.h2,{id:"first-test-run",children:"First Test Run"}),"\n",(0,i.jsx)(n.p,{children:"The next steps are required to set up a minecraft development environment allowing you to run the datagen as well as minecraft locally for testing the generated files."}),"\n",(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsxs)(n.li,{children:["Open Terminal in the folder you downloaded and extracted the Demo Project to","\n",(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsxs)(n.li,{children:["If you see the files ",(0,i.jsx)(n.code,{children:"gradlew"})," and ",(0,i.jsx)(n.code,{children:"gradlew.bat"})," you are in the right folder"]}),"\n"]}),"\n"]}),"\n"]}),"\n",(0,i.jsxs)(n.admonition,{type:"tip",children:[(0,i.jsx)(n.p,{children:"Not sure how to open the terminal in the folder?"}),(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsx)(n.li,{children:'Most Operating Systems will allow you to right click or shift + right click in the folder and select "Open in Terminal" or similar.'}),"\n",(0,i.jsxs)(n.li,{children:["Otherwise use the command ",(0,i.jsx)(n.code,{children:"cd"})," like so: ",(0,i.jsx)(n.code,{children:'cd "<path to folder>"'}),"\n",(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsxs)(n.li,{children:["Note: On windows you might first have to switch to the correct drive. If e.g. terminal shows ",(0,i.jsx)(n.code,{children:"C:\\\\"})," but your path starts with ",(0,i.jsx)(n.code,{children:"D:\\\\"})," you can switch to the correct drive by running simply ",(0,i.jsx)(n.code,{children:"D:"})," (no ",(0,i.jsx)(n.code,{children:"cd"})," or any other command/prefix required)"]}),"\n"]}),"\n"]}),"\n"]})]}),"\n",(0,i.jsx)(n.h3,{id:"ide-setup",children:"IDE Setup"}),"\n",(0,i.jsx)(n.admonition,{type:"info",children:(0,i.jsx)(n.p,{children:"If you are not using an IDE you can skip this step."})}),"\n",(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsx)(n.li,{children:"Open Terminal in the folder you downloaded and extracted the Demo Project to"}),"\n",(0,i.jsxs)(n.li,{children:["Generate run configurations:","\n",(0,i.jsxs)(n.ul,{children:["\n",(0,i.jsxs)(n.li,{children:["For IntelliJ Users: Run ",(0,i.jsx)(n.code,{children:"./gradlew genIntellijRuns"})]}),"\n",(0,i.jsxs)(n.li,{children:["For Eclipse Users: Run ",(0,i.jsx)(n.code,{children:"./gradlew genEclipseRuns"})]}),"\n",(0,i.jsxs)(n.li,{children:["For VSCode Users: Run ",(0,i.jsx)(n.code,{children:"./gradlew genVSCodeRuns"})]}),"\n"]}),"\n"]}),"\n"]}),"\n",(0,i.jsx)(n.p,{children:"In future steps you can use the provided run configurations to run the datagen and minecraft, instead of the commands this guide will use."}),"\n",(0,i.jsxs)(n.admonition,{type:"tip",children:[(0,i.jsx)(n.mdxAdmonitionTitle,{}),(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.strong,{children:"Windows Users:"})," If you are getting a message along the lines of ",(0,i.jsx)(n.code,{children:"command . not found"})," try running ",(0,i.jsx)(n.code,{children:"gradlew.bat <...>"})," instead of ",(0,i.jsx)(n.code,{children:"./gradlew <...>"})]})]}),"\n",(0,i.jsx)(n.h3,{id:"running-minecraft",children:"Running Minecraft"}),"\n",(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsx)(n.li,{children:"Open Terminal in the folder you downloaded the Demo Project to."}),"\n",(0,i.jsxs)(n.li,{children:["Run ",(0,i.jsx)(n.code,{children:"./gradlew runClient"}),"."]}),"\n",(0,i.jsx)(n.li,{children:"After a few seconds (possibly minutes) Minecraft should open and show the main menu."}),"\n",(0,i.jsx)(n.li,{children:"Success!"}),"\n"]}),"\n",(0,i.jsxs)(n.admonition,{type:"tip",children:[(0,i.jsx)(n.mdxAdmonitionTitle,{}),(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.strong,{children:"Windows Users:"})," If you are getting a message along the lines of ",(0,i.jsx)(n.code,{children:"command . not found"})," try running ",(0,i.jsx)(n.code,{children:"gradlew.bat <...>"})," instead of ",(0,i.jsx)(n.code,{children:"./gradlew <...>"})]})]}),"\n",(0,i.jsx)(n.h2,{id:"important-improvements--convenience",children:"Important: Improvements & Convenience"}),"\n",(0,i.jsx)(n.p,{children:"Sometimes the Demo Project does not use the cutting edge of available Modonomicon datagen features.\nA good reference for Modonomicon Datagen features are:"}),"\n",(0,i.jsxs)(n.ul,{children:["\n",(0,i.jsxs)(n.li,{children:["the ",(0,i.jsx)(n.a,{href:"https://github.com/klikli-dev/modonomicon/tree/version/1.20.1/src/main/java/com/klikli_dev/modonomicon/datagen/book",children:"Demo Book"})," - especially the CategoryProviders are interesting. Play around with them - they have support for macros, formatting helpers, shortcuts for creating links, ..."]}),"\n",(0,i.jsxs)(n.li,{children:[(0,i.jsx)(n.a,{href:"https://github.com/klikli-dev/theurgy",children:"Theurgy"})," and sometimes it's ",(0,i.jsx)(n.a,{href:"https://github.com/klikli-dev/theurgy/tree/develop",children:"develop Branch"})]}),"\n",(0,i.jsxs)(n.li,{children:[(0,i.jsx)(n.a,{href:"https://github.com/klikli-dev/occultism",children:"Occultism"})," usually has outdated Modonomicon Datagen features, but comes with huge modonomicon book, so a lot of examples."]}),"\n"]})]})}function h(e={}){const{wrapper:n}={...(0,o.a)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(a,{...e})}):a(e)}},548:function(e,n,t){n.Z=t.p+"assets/images/step1-download-zip-fd203ef8b5032d261e76cbffa4034c20.png"},1151:function(e,n,t){t.d(n,{Z:function(){return d},a:function(){return r}});var i=t(7294);const o={},s=i.createContext(o);function r(e){const n=i.useContext(s);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function d(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(o):e.components||o:r(e.components),i.createElement(s.Provider,{value:n},e.children)}}}]);