"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[5326],{3905:function(e,n,o){o.d(n,{Zo:function(){return c},kt:function(){return u}});var t=o(7294);function i(e,n,o){return n in e?Object.defineProperty(e,n,{value:o,enumerable:!0,configurable:!0,writable:!0}):e[n]=o,e}function r(e,n){var o=Object.keys(e);if(Object.getOwnPropertySymbols){var t=Object.getOwnPropertySymbols(e);n&&(t=t.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),o.push.apply(o,t)}return o}function a(e){for(var n=1;n<arguments.length;n++){var o=null!=arguments[n]?arguments[n]:{};n%2?r(Object(o),!0).forEach((function(n){i(e,n,o[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(o)):r(Object(o)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(o,n))}))}return e}function d(e,n){if(null==e)return{};var o,t,i=function(e,n){if(null==e)return{};var o,t,i={},r=Object.keys(e);for(t=0;t<r.length;t++)o=r[t],n.indexOf(o)>=0||(i[o]=e[o]);return i}(e,n);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(t=0;t<r.length;t++)o=r[t],n.indexOf(o)>=0||Object.prototype.propertyIsEnumerable.call(e,o)&&(i[o]=e[o])}return i}var s=t.createContext({}),l=function(e){var n=t.useContext(s),o=n;return e&&(o="function"==typeof e?e(n):a(a({},n),e)),o},c=function(e){var n=l(e.components);return t.createElement(s.Provider,{value:n},e.children)},m={inlineCode:"code",wrapper:function(e){var n=e.children;return t.createElement(t.Fragment,{},n)}},p=t.forwardRef((function(e,n){var o=e.components,i=e.mdxType,r=e.originalType,s=e.parentName,c=d(e,["components","mdxType","originalType","parentName"]),p=l(o),u=i,g=p["".concat(s,".").concat(u)]||p[u]||m[u]||r;return o?t.createElement(g,a(a({ref:n},c),{},{components:o})):t.createElement(g,a({ref:n},c))}));function u(e,n){var o=arguments,i=n&&n.mdxType;if("string"==typeof e||i){var r=o.length,a=new Array(r);a[0]=p;var d={};for(var s in n)hasOwnProperty.call(n,s)&&(d[s]=n[s]);d.originalType=e,d.mdxType="string"==typeof e?e:i,a[1]=d;for(var l=2;l<r;l++)a[l]=o[l];return t.createElement.apply(null,a)}return t.createElement.apply(null,o)}p.displayName="MDXCreateElement"},7262:function(e,n,o){o.r(n),o.d(n,{assets:function(){return s},contentTitle:function(){return a},default:function(){return m},frontMatter:function(){return r},metadata:function(){return d},toc:function(){return l}});var t=o(3117),i=(o(7294),o(3905));const r={sidebar_position:10},a="Prerequisites",d={unversionedId:"getting-started-mod-devs/prerequisites",id:"getting-started-mod-devs/prerequisites",title:"Prerequisites",description:"To use modonomicon you need to set up your build.gradle file (and gradle.properties, if you want to store versions in variables) to include modonomicon as dependency.",source:"@site/docs/getting-started-mod-devs/prerequisites.md",sourceDirName:"getting-started-mod-devs",slug:"/getting-started-mod-devs/prerequisites",permalink:"/modonomicon/docs/getting-started-mod-devs/prerequisites",draft:!1,editUrl:"https://github.com/klikli-dev/modonomicon/tree/documentation/docs/getting-started-mod-devs/prerequisites.md",tags:[],version:"current",sidebarPosition:10,frontMatter:{sidebar_position:10},sidebar:"tutorialSidebar",previous:{title:"Getting Started - Mod Devs",permalink:"/modonomicon/docs/getting-started-mod-devs/"},next:{title:"Book Creation with Datagen",permalink:"/modonomicon/docs/getting-started-mod-devs/book-creation-with-datagen"}},s={},l=[{value:"Repository",id:"repository",level:2},{value:"Dependencies",id:"dependencies",level:2},{value:"Use Case 1: I want to use datagen and convenience features to generate a Modonomicon Book",id:"use-case-1-i-want-to-use-datagen-and-convenience-features-to-generate-a-modonomicon-book",level:3},{value:"Use Case 2: I just want to manually write a Modonomicon Book",id:"use-case-2-i-just-want-to-manually-write-a-modonomicon-book",level:3},{value:"Use Case 3: I want to extend Modonomicon with custom features for my mod",id:"use-case-3-i-want-to-extend-modonomicon-with-custom-features-for-my-mod",level:3}],c={toc:l};function m(e){let{components:n,...o}=e;return(0,i.kt)("wrapper",(0,t.Z)({},c,o,{components:n,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"prerequisites"},"Prerequisites"),(0,i.kt)("p",null,"To use modonomicon you need to set up your ",(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle")," file (and ",(0,i.kt)("inlineCode",{parentName:"p"},"gradle.properties"),", if you want to store versions in variables) to include modonomicon as dependency."),(0,i.kt)("h2",{id:"repository"},"Repository"),(0,i.kt)("p",null,"First, add the Modonomicon Maven repository to your ",(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle")," file in the ",(0,i.kt)("inlineCode",{parentName:"p"},"repositories")," section:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},'repositories {\n    maven {\n        name = "KliKli Dev Repsy Maven"\n        url = "https://repo.repsy.io/mvn/klikli-dev/mods"\n    }\n}\n')),(0,i.kt)("h2",{id:"dependencies"},"Dependencies"),(0,i.kt)("p",null,"Depending on your use case you need a compile time dependency against the Modonomicon API, against the full Modonomicon jar, or just a runtime dependency against the full Modonomicon jar (to load Modonomicon in your Dev env). "),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},"Even compile time dependency against the full Modonomicon jar does not imply that Modonomicon will be a required dependency for your mod at runtime!\nModonomicon only becomes a required runtime dependency if any of your classes that reference Modonomicon Classes are loaded at runtime."),(0,i.kt)("p",{parentName:"admonition"},"This is especially relevant if you want to extend Modonomicon content rendering for your mod: You can reference the full Modonomicon jar at compile time, but keep Modonomicon an optional dependency on Curseforge by ensuring only loads classes that reference Modonomicon classes, if modonomicon is loaded.")),(0,i.kt)("h3",{id:"use-case-1-i-want-to-use-datagen-and-convenience-features-to-generate-a-modonomicon-book"},"Use Case 1: I want to use datagen and convenience features to generate a Modonomicon Book"),(0,i.kt)("p",null,"In this case you need a compileOnly dependency against the Modonomicon API jar to access the Modonomicon Datamodel and datagen helpers.\nAdditionally a runtimeOnly dependency against the full Modonomicon jar is required to load Modonomicon in your dev environment."),(0,i.kt)("p",null,"The ",(0,i.kt)("inlineCode",{parentName:"p"},"dependencies")," section of your ",(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle")," should look like this:"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},'dependencies {\n    compileOnly fg.deobf("com.klikli_dev:modonomicon:${modonomicon_mc_version}-${modonomicon_version}:api")\n    runtimeOnly fg.deobf("com.klikli_dev:modonomicon:${modonomicon_mc_version}-${modonomicon_version}")\n}\n')),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"gradle.properties"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-properties"},"modonomicon_mc_version=1.19.2\nmodonomicon_version=1.0.17\n")),(0,i.kt)("p",null,(0,i.kt)("strong",{parentName:"p"},"Ensure UTF-8 Encoding")),(0,i.kt)("p",null,"To avoid errors with special characters in your generated texts, you should set the encoding to UTF-8 in your ",(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle")," file. This can be done by adding the following lines to the bottom of your ",(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},"compileJava.options.encoding = 'UTF-8'\ntasks.withType(JavaCompile) {\n    options.encoding = 'UTF-8'\n}\n\n")),(0,i.kt)("admonition",{type:"tip"},(0,i.kt)("p",{parentName:"admonition"},"This is recommended even if you do not use Modonomicon datagen or Modonomicon at all. As soon as you datagen ",(0,i.kt)("inlineCode",{parentName:"p"},".lang")," this will save you a lot of headache. ")),(0,i.kt)("h3",{id:"use-case-2-i-just-want-to-manually-write-a-modonomicon-book"},"Use Case 2: I just want to manually write a Modonomicon Book"),(0,i.kt)("p",null,"For convenience it is recommended to use Use Case 1 over Use Case 2, but if you prefer to edit JSON files directly this is the way to go. "),(0,i.kt)("p",null,"In this case you do not need a compile time dependency against Modonomicon at all.\nA runtimeOnly dependency against the full Modonomicon jar is required to load Modonomicon in your dev environment."),(0,i.kt)("p",null,"The ",(0,i.kt)("inlineCode",{parentName:"p"},"dependencies")," section of your ",(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle")," should look like this:"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},'dependencies {\n    runtimeOnly fg.deobf("com.klikli_dev:modonomicon:${modonomicon_mc_version}-${modonomicon_version}")\n}\n')),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"gradle.properties"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-properties"},"modonomicon_mc_version=1.19.2\nmodonomicon_version=1.0.17\n")),(0,i.kt)("h3",{id:"use-case-3-i-want-to-extend-modonomicon-with-custom-features-for-my-mod"},"Use Case 3: I want to extend Modonomicon with custom features for my mod"),(0,i.kt)("p",null,"In this case you need a compile time dependency against the full Modonomicon jar to access all of Modonomicon's classes and features, especially the registries for Loader and Renderer classes for new content."),(0,i.kt)("p",null,"The ",(0,i.kt)("inlineCode",{parentName:"p"},"dependencies")," section of your ",(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle")," should look like this:"),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},'dependencies {\n    implementation fg.deobf("com.klikli_dev:modonomicon:${modonomicon_mc_version}-${modonomicon_version}")\n}\n')),(0,i.kt)("p",null,(0,i.kt)("inlineCode",{parentName:"p"},"gradle.properties"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-properties"},"modonomicon_mc_version=1.19.2\nmodonomicon_version=1.0.17\n")))}m.isMDXComponent=!0}}]);