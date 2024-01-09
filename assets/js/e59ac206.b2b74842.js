"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[6568],{3305:function(e,n,o){o.r(n),o.d(n,{assets:function(){return c},contentTitle:function(){return r},default:function(){return m},frontMatter:function(){return d},metadata:function(){return s},toc:function(){return l}});var i=o(5893),t=o(1151);const d={sidebar_position:30},r="Maven Dependencies",s={id:"getting-started/maven-dependencies",title:"Maven Dependencies",description:"To use modonomicon you need to set up your build.gradle file (and gradle.properties, if you want to store versions in variables) to include modonomicon as dependency.",source:"@site/docs/getting-started/maven-dependencies.md",sourceDirName:"getting-started",slug:"/getting-started/maven-dependencies",permalink:"/modonomicon/docs/getting-started/maven-dependencies",draft:!1,unlisted:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/getting-started/maven-dependencies.md",tags:[],version:"current",sidebarPosition:30,frontMatter:{sidebar_position:30},sidebar:"tutorialSidebar",previous:{title:"Step 4: Adding texts",permalink:"/modonomicon/docs/getting-started/step-by-step-with-datagen/step4"},next:{title:"Book Crafting Recipes",permalink:"/modonomicon/docs/basics/crafting"}},c={},l=[{value:"Repository",id:"repository",level:2},{value:"Dependencies",id:"dependencies",level:2},{value:"Forge",id:"forge",level:3},{value:"Fabric",id:"fabric",level:3},{value:"Common",id:"common",level:3}];function a(e){const n={a:"a",admonition:"admonition",code:"code",h1:"h1",h2:"h2",h3:"h3",mdxAdmonitionTitle:"mdxAdmonitionTitle",p:"p",pre:"pre",strong:"strong",...(0,t.a)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.h1,{id:"maven-dependencies",children:"Maven Dependencies"}),"\n",(0,i.jsxs)(n.p,{children:["To use modonomicon you need to set up your ",(0,i.jsx)(n.code,{children:"build.gradle"})," file (and ",(0,i.jsx)(n.code,{children:"gradle.properties"}),", if you want to store versions in variables) to include modonomicon as dependency."]}),"\n",(0,i.jsxs)(n.admonition,{type:"tip",children:[(0,i.jsx)(n.mdxAdmonitionTitle,{}),(0,i.jsxs)(n.p,{children:["If you followed the ",(0,i.jsx)(n.strong,{children:(0,i.jsx)(n.a,{href:"./step-by-step-with-datagen/",children:"Step by Step with Datagen"})})," guide you already have this set up. This section is only if you want to add modonomicon to an existing project."]})]}),"\n",(0,i.jsx)(n.h2,{id:"repository",children:"Repository"}),"\n",(0,i.jsxs)(n.p,{children:["First, add the Modonomicon Maven repository to your ",(0,i.jsx)(n.code,{children:"build.gradle"})," file in the ",(0,i.jsx)(n.code,{children:"repositories"})," section:"]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-groovy",children:'repositories {\n    maven {\n        url "https://dl.cloudsmith.io/public/klikli-dev/mods/maven/"\n        content {\n            includeGroup "com.klikli_dev"\n        }\n    }\n}\n'})}),"\n",(0,i.jsx)(n.h2,{id:"dependencies",children:"Dependencies"}),"\n",(0,i.jsx)(n.p,{children:"Modonomicon only provides a single jar file, which contains both the API and the implementation."}),"\n",(0,i.jsxs)(n.admonition,{type:"tip",children:[(0,i.jsx)(n.p,{children:"Even compile time dependency against the full Modonomicon jar does not imply that Modonomicon will be a required dependency for your mod at runtime!\nModonomicon only becomes a required runtime dependency if any of your classes that reference Modonomicon Classes are loaded at runtime."}),(0,i.jsx)(n.p,{children:"This is especially relevant if you want to extend Modonomicon content rendering for your mod: You can reference the full Modonomicon jar at compile time, but keep Modonomicon an optional dependency on Curseforge by ensuring only loads classes that reference Modonomicon classes, if modonomicon is loaded."})]}),"\n",(0,i.jsxs)(n.p,{children:["The below use cases use variables for the modonomicon version, set up your ",(0,i.jsx)(n.code,{children:"gradle.properties"})," file accordingly:"]}),"\n",(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.code,{children:"gradle.properties"}),":"]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-properties",children:"# choose appropriate latest version from: https://cloudsmith.io/~klikli-dev/repos/mods/groups/\nminecraft_version=1.20.1\nmodonomicon_version=1.38.5\n"})}),"\n",(0,i.jsxs)(n.p,{children:["The ",(0,i.jsx)(n.code,{children:"dependencies"})," section of your ",(0,i.jsx)(n.code,{children:"build.gradle"})," should look like this:"]}),"\n",(0,i.jsx)(n.h3,{id:"forge",children:"Forge"}),"\n",(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.code,{children:"build.gradle"}),":"]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-groovy",children:'dependencies {\n    ... //other dependencies\n    compileOnly fg.deobf("com.klikli_dev:modonomicon-${minecraft_version}-common:${modonomicon_version}")\n    implementation fg.deobf("com.klikli_dev:modonomicon-${minecraft_version}-forge:${modonomicon_version}") \n}\n'})}),"\n",(0,i.jsx)(n.admonition,{type:"info",children:(0,i.jsxs)(n.p,{children:["You may need to add ",(0,i.jsx)(n.code,{children:"{transitive=false}"})," at the end of the ",(0,i.jsx)(n.code,{children:"implementation ..."})," line."]})}),"\n",(0,i.jsx)(n.h3,{id:"fabric",children:"Fabric"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-groovy",children:'dependencies {\n    ... //other dependencies\n    modCompileOnly "com.klikli_dev:modonomicon-${minecraft_version}-common:${modonomicon_version}"\n    modImplementation "com.klikli_dev:modonomicon-${minecraft_version}-fabric:${modonomicon_version}"\n}\n'})}),"\n",(0,i.jsx)(n.h3,{id:"common",children:"Common"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-groovy",children:'dependencies {\n    ... //other dependencies\n    compileOnly "com.klikli_dev:modonomicon-${minecraft_version}-common:${modonomicon_version}"\n}\n'})})]})}function m(e={}){const{wrapper:n}={...(0,t.a)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(a,{...e})}):a(e)}},1151:function(e,n,o){o.d(n,{Z:function(){return s},a:function(){return r}});var i=o(7294);const t={},d=i.createContext(t);function r(e){const n=i.useContext(d);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function s(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(t):e.components||t:r(e.components),i.createElement(d.Provider,{value:n},e.children)}}}]);