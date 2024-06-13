# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [release/v1.21-1.73.3] - 2024-06-13
### :sparkles: New Features
- [`49ba482`](https://github.com/klikli-dev/modonomicon/commit/49ba482866eb317a1496d7c66853144dc12c3618) - update neo to 1.21 *(PR [#208](https://github.com/klikli-dev/modonomicon/pull/208) by [@klikli-dev](https://github.com/klikli-dev))*
- [`9202a66`](https://github.com/klikli-dev/modonomicon/commit/9202a668355d909eda4ad26069db1b4d9e2ea965) - update neo to 1.21-rc1 and add fabric support *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`876edd8`](https://github.com/klikli-dev/modonomicon/commit/876edd8df9097b5ba46204250ceb215b3311676e) - add auto-open entries *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`0311384`](https://github.com/klikli-dev/modonomicon/commit/03113845b197b9915e7c3fb2fc1a1324091b8fe0) - release-ready for fabric 1.21 *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`340bccb`](https://github.com/klikli-dev/modonomicon/commit/340bccb0659d5dae14e7aab0364840a850a024db) - default zoom multiplier wrong by a factor 512 *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`679d4d7`](https://github.com/klikli-dev/modonomicon/commit/679d4d78377e7408e97b73a6bcf63f5030c6f887) - store open entry in category properly *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`a24fb60`](https://github.com/klikli-dev/modonomicon/commit/a24fb601887c59790e620f262c8a291d33605261) - redirect entry in history prevents opening category *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`88cca46`](https://github.com/klikli-dev/modonomicon/commit/88cca463f815442f4351ffa096266cd2c2da4713) - Clicking a category opens up an entry *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`0513f08`](https://github.com/klikli-dev/modonomicon/commit/0513f084d8dda187610303978d316a4afbf5eec5) - run datagen *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`b500ca0`](https://github.com/klikli-dev/modonomicon/commit/b500ca04afd8c29e5341e345e1c269ec3e27efec) - increase min compat level for mixin to match MC 1.21 *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.73.3] - 2024-06-10
### :bug: Bug Fixes
- [`5765bc5`](https://github.com/klikli-dev/modonomicon/commit/5765bc5967724f4c942d18e2fb6da7b96e3083bb) - creative tab crash related to search tab *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.73.2] - 2024-06-10
### :bug: Bug Fixes
- [`323a0ff`](https://github.com/klikli-dev/modonomicon/commit/323a0ff86800f8273d0d8e968ae5ab4e68b06ed2) - add custom anti-duplication handling for creative tabs *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`5166752`](https://github.com/klikli-dev/modonomicon/commit/51667527d76f598195d47aef95eebbb3453775c3) - remove fulfilled todo *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.73.1] - 2024-06-08
### :bug: Bug Fixes
- [`54a626b`](https://github.com/klikli-dev/modonomicon/commit/54a626bca7c0ab32034fb199279b9ec78fee2bca) - book closes entirely in some cases instead of closing the open entry *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`06166b2`](https://github.com/klikli-dev/modonomicon/commit/06166b25bf56d5ef02fc35661aeeb3e46fa7bac0) - update neo version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.73.0] - 2024-06-07
### :sparkles: New Features
- [`09572c7`](https://github.com/klikli-dev/modonomicon/commit/09572c7ed1ace995a7832c0458ff87be0c940ce0) - enable forge support *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`aeb84aa`](https://github.com/klikli-dev/modonomicon/commit/aeb84aaa24ad8dedbc7abd60c84478144cf05f40) - forge experiments *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`4f156b2`](https://github.com/klikli-dev/modonomicon/commit/4f156b2781a54365e3b8f85ed2cc3214df7fec76) - setup github action for forge *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.72.2] - 2024-06-05
### :wrench: Chores
- [`f12b39f`](https://github.com/klikli-dev/modonomicon/commit/f12b39f539dd484ea9ef18d63eeb1b31b7ac8b77) - remove unused test data *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`3d2689b`](https://github.com/klikli-dev/modonomicon/commit/3d2689b6196b6329f256082bb0a7fa659b482ce4) - remove test content from game-ready jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.72.1] - 2024-05-29
### :bug: Bug Fixes
- [`b5c0225`](https://github.com/klikli-dev/modonomicon/commit/b5c0225a2e9e0d4d4e90a52327d280a602ad9bbb) - creative mode tab double registration *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`0e8bfeb`](https://github.com/klikli-dev/modonomicon/commit/0e8bfeb30be2c4c34f6a0381249a98b91de3565b) - small forge prep *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.72.0] - 2024-05-18
### :sparkles: New Features
- [`5bf0291`](https://github.com/klikli-dev/modonomicon/commit/5bf02914e0755eb25a241e01d424dcd804cb9993) - Use FastUtil instead of regular Java collections *(PR [#206](https://github.com/klikli-dev/modonomicon/pull/206) by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`ccfc2ad`](https://github.com/klikli-dev/modonomicon/commit/ccfc2ad1eec954e0a3f59a55437c8b80b6307923) - more forge 1.20.6 preparations *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`b69e669`](https://github.com/klikli-dev/modonomicon/commit/b69e66924b04ce910a4da699a29617dab0bcff58) - more forge prep *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.71.1] - 2024-05-04
### :sparkles: New Features
- [`695adda`](https://github.com/klikli-dev/modonomicon/commit/695adda40c7628a1646c6306ab87a18b5f374b56) - Catch errors on entry rendering sooner ([#205](https://github.com/klikli-dev/modonomicon/pull/205)) - thanks @DaFuqs *(commit by [@DaFuqs](https://github.com/DaFuqs))*

### :bug: Bug Fixes
- [`a049c8f`](https://github.com/klikli-dev/modonomicon/commit/a049c8f74c1b2aa305143bc585ad4bdf12c67e83) - use of StreamCodec.unit requires singeltons *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.70.1] - 2024-05-03
### :bug: Bug Fixes
- [`4eb2c70`](https://github.com/klikli-dev/modonomicon/commit/4eb2c70617d5712e7b5e4ca1f85fa730e347c6b2) - workaround for use of internal commonmark modules in extensions *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`248f556`](https://github.com/klikli-dev/modonomicon/commit/248f556977594bb1ca313780d151ce48948e5009) - copy remaining internal commonmark classes to modonomicon *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`e1fef95`](https://github.com/klikli-dev/modonomicon/commit/e1fef951de574c6e38e2a24c9c4770ebce410173) - prepare for forge 1.20.6 *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`283d6ad`](https://github.com/klikli-dev/modonomicon/commit/283d6adba262efc48cc84f8f9e453312e2988ff2) - more preparations for forge 1.20.6 *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`f3b21df`](https://github.com/klikli-dev/modonomicon/commit/f3b21dfc16f03d47fbaad2eebb18fa66d178fa2e) - re-establish reuse compliance *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.70.0] - 2024-05-01
### :sparkles: New Features
- [`6a472c3`](https://github.com/klikli-dev/modonomicon/commit/6a472c318aed9c4047db10fbd61a6ce882f0382e) - Dynamic BookEntries *(PR [#202](https://github.com/klikli-dev/modonomicon/pull/202) by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`96e7246`](https://github.com/klikli-dev/modonomicon/commit/96e724695649f0e09af1155e7b3ef1a4302828ce) - prepare recommended replacement for runtimeOnly *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.6-1.69.3] - 2024-04-30
### :sparkles: New Features
- [`37b0a81`](https://github.com/klikli-dev/modonomicon/commit/37b0a81c6895745db38443c41141667c7f506f23) - update to 1.20.6 *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.5-1.69.2] - 2024-04-26
### :sparkles: New Features
- [`cb83152`](https://github.com/klikli-dev/modonomicon/commit/cb83152f762691f8af2656df691c073e0fa1aaa3) - upgrade to 1.20.5 *(PR [#204](https://github.com/klikli-dev/modonomicon/pull/204) by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`fcb5ac1`](https://github.com/klikli-dev/modonomicon/commit/fcb5ac1b5c1d2ee8b007f4701590a8f718098379) - github action *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`339ebad`](https://github.com/klikli-dev/modonomicon/commit/339ebadc3dbc1380ed9d0a7a7db0c8355e0ed8c3) - github action java version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.69.1] - 2024-04-21
### :sparkles: New Features
- [`29e124e`](https://github.com/klikli-dev/modonomicon/commit/29e124ed866d60f9f46bd0c9513069400e9681a4) - forge support for 1.20.4 *(PR [#203](https://github.com/klikli-dev/modonomicon/pull/203) by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.69.0] - 2024-04-16
### :sparkles: New Features
- [`67fd16a`](https://github.com/klikli-dev/modonomicon/commit/67fd16afecacfbf60098bdfae42d8d5404df4ca5) - make modonomicon content registrations threadsafe *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.68.0] - 2024-04-14
### :sparkles: New Features
- [`cb316c2`](https://github.com/klikli-dev/modonomicon/commit/cb316c226fb35b5e434b5c9c178164b805efeb5d) - The Navigationing ([#197](https://github.com/klikli-dev/modonomicon/pull/197)) - thanks @DaFuqs *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.67.1] - 2024-04-12
### :bug: Bug Fixes
- [`2d2f984`](https://github.com/klikli-dev/modonomicon/commit/2d2f984e0ebe8b6b81b72be633f653963b24f007) - crash when clicking redirect entry in search screen *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.67.0] - 2024-04-11
### :sparkles: New Features
- [`1b21d71`](https://github.com/klikli-dev/modonomicon/commit/1b21d713e70273b2530fce86eaa8e7a48e854371) - Added BookCategoryHasEntriesCondition ([#194](https://github.com/klikli-dev/modonomicon/pull/194)) - thanks @DaFuqs *(commit by [@DaFuqs](https://github.com/DaFuqs))*


## [release/v1.20.4-1.66.5] - 2024-03-31
### :bug: Bug Fixes
- [`a69fba0`](https://github.com/klikli-dev/modonomicon/commit/a69fba01cdbd49883f0b1ab07fc5d8b5901ade20) - Added missing lang for condition.mod_loaded ([#193](https://github.com/klikli-dev/modonomicon/pull/193))  - thanks @DaFuqs *(commit by [@DaFuqs](https://github.com/DaFuqs))*


## [release/v1.20.4-1.66.3] - 2024-03-31
### :bug: Bug Fixes
- [`384d68e`](https://github.com/klikli-dev/modonomicon/commit/384d68e0b8e4b5c20ced6f8d9c86ff28b5ceb00d) - small curves reversed rendering *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.66.2] - 2024-03-31
### :bug: Bug Fixes
- [`5c92322`](https://github.com/klikli-dev/modonomicon/commit/5c9232287a20dc659ed5e6b6597dd6f065fa5909) - book link target text fetching *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.66.0] - 2024-03-31
### :sparkles: New Features
- [`546cdbf`](https://github.com/klikli-dev/modonomicon/commit/546cdbf8ec04781f22f4c6e0e42aecec1cdaef79) - better advancement condition tooltip handling *(PR [#192](https://github.com/klikli-dev/modonomicon/pull/192) by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.65.0] - 2024-03-30
### :bug: Bug Fixes
- [`2308b4f`](https://github.com/klikli-dev/modonomicon/commit/2308b4ffce9a6c2c51794b1f19c45c2f2afb1bf3) - fixed typo in recipe error message + Fixed book not loading when an entry has an invalid parent *(PR [#190](https://github.com/klikli-dev/modonomicon/pull/190) by [@DaFuqs](https://github.com/DaFuqs))*


## [release/v1.20.4-1.64.0] - 2024-03-28
### :sparkles: New Features
- [`214ad8d`](https://github.com/klikli-dev/modonomicon/commit/214ad8d0990c6cfc6fab9085221dd55ed958b5d8) - update to latest commonmark *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.63.0] - 2024-03-27
### :bug: Bug Fixes
- [`eb626b0`](https://github.com/klikli-dev/modonomicon/commit/eb626b08be8171a81b704075ff9199c41e514368) - switch to nbt codec instead of json for book state network sync *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.62.0] - 2024-03-04
### :sparkles: New Features
- [`524d535`](https://github.com/klikli-dev/modonomicon/commit/524d53580d3e179f531a8f415a883b0744559bca) - change getPath on BookProvider.java to protected *(PR [#182](https://github.com/klikli-dev/modonomicon/pull/182) by [@GaeaKat](https://github.com/GaeaKat))*


## [release/v1.20.4-1.61.1] - 2024-03-02
### :bug: Bug Fixes
- [`b8ee85b`](https://github.com/klikli-dev/modonomicon/commit/b8ee85b72f804cac753e49bade9065c8986c6a23) - fontFallbackLocales not used on language change *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.61.0] - 2024-03-01
### :sparkles: New Features
- [`189eab2`](https://github.com/klikli-dev/modonomicon/commit/189eab21fc58b1a3ff893d8c8ebaa43a91a49307) - allow conditions to prevent loading of content entirely *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.60.0] - 2024-02-27
### :sparkles: New Features
- [`3f117ba`](https://github.com/klikli-dev/modonomicon/commit/3f117baa18e9b1b2afc77f55c2ed8ed83f8596d6) - Add conditions to individual pages - Port to 1.20.4 ([#176](https://github.com/klikli-dev/modonomicon/pull/176)) - thanks @Electro593 *(commit by [@Electro593](https://github.com/Electro593))*


## [release/v1.20.4-1.59.0] - 2024-02-16
### :bug: Bug Fixes
- [`2a06735`](https://github.com/klikli-dev/modonomicon/commit/2a067357bacaf3e15a5f490520a4af64807fe83e) - convert entry unlocked condition handling into generic multipass handling *(PR [#174](https://github.com/klikli-dev/modonomicon/pull/174) by [@klikli-dev](https://github.com/klikli-dev))*
  - :arrow_lower_right: *fixes issue [#173](undefined) opened by [@Cmdpro](https://github.com/Cmdpro)*


## [release/v1.20.4-1.58.0] - 2024-02-05
### :sparkles: New Features
- [`b09637b`](https://github.com/klikli-dev/modonomicon/commit/b09637b8d280b8bd0869228c6f17a3b985033d08) - make condition children available *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.57.0] - 2024-01-25
### :bug: Bug Fixes
- [`36e047d`](https://github.com/klikli-dev/modonomicon/commit/36e047d552040148deebbba69c5cb821228068bc) - CJK character render issues *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`3d03604`](https://github.com/klikli-dev/modonomicon/commit/3d03604d1bc9f4528a4b9c2635079bbe5d55bf34) - update to latest neo *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.56.1] - 2024-01-21
### :bug: Bug Fixes
- [`ce694be`](https://github.com/klikli-dev/modonomicon/commit/ce694bea88cc84aada83e496fd5eeb8a2a491fa2) - add error handling for unexpected booktextholder render issues *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.56.0] - 2024-01-21
### :sparkles: New Features
- [`966d497`](https://github.com/klikli-dev/modonomicon/commit/966d49710d668762be7e355bffb21d0ebae9e74a) - refactor builder pattern for remaining model classes *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.55.1] - 2024-01-20
### :sparkles: New Features
- [`0668799`](https://github.com/klikli-dev/modonomicon/commit/0668799c0190bdf6692c45c324cc4c6e3891c548) - enable jei integration *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.55.0] - 2024-01-19
### :sparkles: New Features
- [`c282c9a`](https://github.com/klikli-dev/modonomicon/commit/c282c9a55cfa21ef83582ec9b2cc69a61f40e66b) - add a configurable zoom factor to category backgrounds *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.54.5] - 2024-01-19
### :sparkles: New Features
- [`c9c83d4`](https://github.com/klikli-dev/modonomicon/commit/c9c83d4e9c69ab0d9ac7c3e74f5390726647c7e3) - update past neo breaking changes *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`ccb2410`](https://github.com/klikli-dev/modonomicon/commit/ccb2410e54af66e5564cc43db5104af980033232) - missing builder methods for frame textures on the book model *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`6e2fb80`](https://github.com/klikli-dev/modonomicon/commit/6e2fb80381f95d208af20b3f9544135b7347ef77) - update publish action *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`140465f`](https://github.com/klikli-dev/modonomicon/commit/140465f8950670197b7884fcf2e04eeaed7f70a3) - update classnames to reflect they are associated with Neo, not Forge *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.54.4] - 2024-01-14
### :wrench: Chores
- [`7dd4d96`](https://github.com/klikli-dev/modonomicon/commit/7dd4d96ab905348167a31d001e624c331ad8a7e3) - change item link default color *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.54.3] - 2024-01-14
### :bug: Bug Fixes
- [`e6b2265`](https://github.com/klikli-dev/modonomicon/commit/e6b2265573d535759424eea0dab3ad0c43428c7d) - line_reversed for parents *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`131554b`](https://github.com/klikli-dev/modonomicon/commit/131554b39e1893f61ab454125377245cf8e890c9) - remove modloader from dependencies, is handled via loader config *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.54.2] - 2024-01-13
### :bug: Bug Fixes
- [`8d8f890`](https://github.com/klikli-dev/modonomicon/commit/8d8f89000a865a1530373188b5ccba5f059df3b2) - crash due to missing instanceof check *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.54.1] - 2024-01-13
### :bug: Bug Fixes
- [`3d3f520`](https://github.com/klikli-dev/modonomicon/commit/3d3f52019c2fb3cfead10b2bc0ea7869323524a7) - in SP multiblocks and books get cleared on simulated network sync *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.54.0] - 2024-01-13
### :sparkles: New Features
- [`e0e92d0`](https://github.com/klikli-dev/modonomicon/commit/e0e92d0f74d9f1568fcb435f3e8287e74a5a41a9) - add backspace as hotkey for "back" *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`1141dce`](https://github.com/klikli-dev/modonomicon/commit/1141dceb2fef5c505d39b221e26827e2a2f5d227) - switching between book entries sometimes creates two content screens on top of each other *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`a73e4cc`](https://github.com/klikli-dev/modonomicon/commit/a73e4cc64a733cd4e359b4efaa896c7a13b3912e) - method signature changed in 1.20.4 *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.52.1] - 2024-01-12
### :bug: Bug Fixes
- [`54a4407`](https://github.com/klikli-dev/modonomicon/commit/54a4407faef0f5e61869ee82ecd0795288d3bc68) - Spotlight page shows tooltip twice if on the right side *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.52.0] - 2024-01-11
### :sparkles: New Features
- [`96efa61`](https://github.com/klikli-dev/modonomicon/commit/96efa61c880c27279190ff7c7a6358f9e242c2f1) - add showWhenAnyParentUnlocked to entry *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.51.1] - 2024-01-10
### :bug: Bug Fixes
- [`067a5e1`](https://github.com/klikli-dev/modonomicon/commit/067a5e171b403023b2b0dffe79fb6c1da2b00976) - add workaround for client not receiving book save state *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.51.0] - 2024-01-10
### :sparkles: New Features
- [`853b8e1`](https://github.com/klikli-dev/modonomicon/commit/853b8e1964e4a13454fb4c80d8a422da61ccecfb) - re-establish reuse compliance *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`513a6da`](https://github.com/klikli-dev/modonomicon/commit/513a6dafaca6b2d120b919798defce3deab3e715) - add category hint to links to locked entries *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.50.0] - 2024-01-09
### :sparkles: New Features
- [`fd38e97`](https://github.com/klikli-dev/modonomicon/commit/fd38e97f42820fa84d3de472bd228e14cf7d2aa8) - better default font and allow changing font per book *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`6659794`](https://github.com/klikli-dev/modonomicon/commit/66597948f1131982b97029a3c66eaab547c4ffbf) - set mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.49.1] - 2024-01-09
### :bug: Bug Fixes
- [`8acd6d3`](https://github.com/klikli-dev/modonomicon/commit/8acd6d3e3b2b0da56b8d039aa8ddb20a362fd9a5) - force book update and sync after reload command *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.49.0] - 2024-01-09
### :sparkles: New Features
- [`1bf0591`](https://github.com/klikli-dev/modonomicon/commit/1bf059187d1f657fc029f7b97448c70b3c00e007) - add command to reload resource+ datapacks *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.48.1] - 2024-01-08
### :bug: Bug Fixes
- [`795e471`](https://github.com/klikli-dev/modonomicon/commit/795e4710cb2ceeef69fc41a3ab2a50b325e6376d) - connection arrow sometimes renders in front of entry *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.48.0] - 2024-01-08
### :sparkles: New Features
- [`e7ac009`](https://github.com/klikli-dev/modonomicon/commit/e7ac009123946ae2f86c5f92014db2162ba4face) - add text helper to entry provider *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.47.0] - 2024-01-08
### :sparkles: New Features
- [`0021dce`](https://github.com/klikli-dev/modonomicon/commit/0021dce2f047be86a26d1bae92131675071c48f7) - add entry provider *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.46.0] - 2024-01-01
### :sparkles: New Features
- [`c527e26`](https://github.com/klikli-dev/modonomicon/commit/c527e26bdd4b1cf6fb5fdb3f61e011185ea3812a) - add helper for constructing entry pages more conveniently *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`54e2b8b`](https://github.com/klikli-dev/modonomicon/commit/54e2b8bb800cabe5087d4d3903055fbeb8247ca6) - update to latest fabric *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.45.0] - 2024-01-01
### :boom: BREAKING CHANGES
- due to [`8207870`](https://github.com/klikli-dev/modonomicon/commit/82078708496a96bfcd88779009e860dd29b6c603) - update to the new neo network system *(commit by [@klikli-dev](https://github.com/klikli-dev))*:

  update to the new neo network system


### :sparkles: New Features
- [`8207870`](https://github.com/klikli-dev/modonomicon/commit/82078708496a96bfcd88779009e860dd29b6c603) - update to the new neo network system *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`4d0ccd9`](https://github.com/klikli-dev/modonomicon/commit/4d0ccd97ddb75b5bf0fa199e5d44690b2dba46e7) - enable parchment *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.44.0] - 2023-12-27
### :sparkles: New Features
- [`6ae19a8`](https://github.com/klikli-dev/modonomicon/commit/6ae19a8cbcd4cbb443184941001e88f4d846aae4) - add fluid rendering to multiblocks *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.43.3] - 2023-12-26
### :bug: Bug Fixes
- [`a9eca6f`](https://github.com/klikli-dev/modonomicon/commit/a9eca6f3b67dbd2988966358b40e264282b49ee5) - make book preprender abort if build failed to allow smoother error handling when opening book *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.43.2] - 2023-12-23
### :bug: Bug Fixes
- [`eeb900e`](https://github.com/klikli-dev/modonomicon/commit/eeb900e68809ee04c2870f3387b08b757af0469b) - modonomicons not showing in creative tab until reload *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`4c3c669`](https://github.com/klikli-dev/modonomicon/commit/4c3c66992fa2af2fd29ebba2861b12721062ab0a) - creative tabs for fabric *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`5cd7dc8`](https://github.com/klikli-dev/modonomicon/commit/5cd7dc81eb480a64ff9d5e978f4a7885aba3ec9e) - neo creative tab registry access *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.43.0] - 2023-12-23
### :sparkles: New Features
- [`ef40091`](https://github.com/klikli-dev/modonomicon/commit/ef40091d91139d75d88b7b7c554ec18138e41d14) - allow predicate state matchers to not count towards block total count of multiblock *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.42.1] - 2023-12-22
### :bug: Bug Fixes
- [`0a35282`](https://github.com/klikli-dev/modonomicon/commit/0a3528244c759fe2b00d2ab62e3fcf3b4f4b3562) - ground layer padding extra rows too short *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`e8f5149`](https://github.com/klikli-dev/modonomicon/commit/e8f5149fd79816c317d921d7ae2030f40f7c71c4) - set gradle project name to mc version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.4-1.42.0] - 2023-12-21
### :sparkles: New Features
- [`61fb319`](https://github.com/klikli-dev/modonomicon/commit/61fb319be1e09d3aa9a5b5826172a2698dfe4d02) - upgrade to 1.20.4 *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.2-1.42.0] - 2023-12-19
### :sparkles: New Features
- [`ffad9b2`](https://github.com/klikli-dev/modonomicon/commit/ffad9b263ce510eaba44b594e4807252bab14605) - allow custom texture size for book icons *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`dd7469b`](https://github.com/klikli-dev/modonomicon/commit/dd7469b2bca1ae72533f433036327c95c6b33f89) - duplicate strategy for neo sources jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`d240404`](https://github.com/klikli-dev/modonomicon/commit/d240404e37fafc9baa3cf7b15a7c0977a516ab96) - remove unused build instruction *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`2a96010`](https://github.com/klikli-dev/modonomicon/commit/2a96010473dd34ef1f335c149a03a6f8fddc0f14) - move most resources to common *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.2-1.41.2] - 2023-12-04
### :sparkles: New Features
- [`a98bb85`](https://github.com/klikli-dev/modonomicon/commit/a98bb858191f3d148664ef53bc00f5fe3caaad25) - proper mc classpath setup *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`38bc0fb`](https://github.com/klikli-dev/modonomicon/commit/38bc0fbf5b8aa9cef6667efae0a23a7050b00d65) - tooltip says eye icon is left but it is right *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.2-1.41.1] - 2023-12-01
### :bug: Bug Fixes
- [`486c785`](https://github.com/klikli-dev/modonomicon/commit/486c785fb56309d26a00f4c25dc3534a833e2243) - advancement condition tooltip bug *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`31aa78f`](https://github.com/klikli-dev/modonomicon/commit/31aa78ff1522d080d5edb4843d3a4a1590df80e4) - update workflow *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.40.0] - 2023-10-12
### :sparkles: New Features
- [`fa874b9`](https://github.com/klikli-dev/modonomicon/commit/fa874b9ae044e2a2f2028b1913cdd767ea29e2fd) - add on entry clicked event *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`e4cacf4`](https://github.com/klikli-dev/modonomicon/commit/e4cacf42697c5b9cb4f0dba12404db1e809a7631) - update dependencies *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.39.1] - 2023-10-09
### :bug: Bug Fixes
- [`a546cfa`](https://github.com/klikli-dev/modonomicon/commit/a546cfadfa7b7deffa00b12758066313747b9fab) - advancement title translation in advancement condition *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.39.0] - 2023-08-01
### :sparkles: New Features
- [`70609c9`](https://github.com/klikli-dev/modonomicon/commit/70609c99323b605f79e3dd5ffe45cad96ea05046) - add neoforge to mod platform list *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`fdab288`](https://github.com/klikli-dev/modonomicon/commit/fdab2889cb07f9d34dbb27f5c9958e934ed76c1a) - make tag matcher log error instead of throwing exception if tag unavailable *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.38.6] - 2023-07-24
### :bug: Bug Fixes
- [`37bb030`](https://github.com/klikli-dev/modonomicon/commit/37bb0303e583d021beb83270a6ed92e872233e75) - book state not saved in SP *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`b242bbf`](https://github.com/klikli-dev/modonomicon/commit/b242bbfeb04f55c6a766e3d889adea3031617790) - cleanup book state on world unload *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`7704287`](https://github.com/klikli-dev/modonomicon/commit/7704287a21b334ba06f667bbdc372478621f209e) - cleanup github action *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.38.5] - 2023-07-22
### :sparkles: New Features
- [`ced72fa`](https://github.com/klikli-dev/modonomicon/commit/ced72fa5b4e5d34fe84a13d92bcf07611803107a) - switch to cloudsmith maven *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`e00f662`](https://github.com/klikli-dev/modonomicon/commit/e00f662c73d44268b1a91a0f62e93319eb042687) - simplify reobf *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.38.4] - 2023-07-21
### :bug: Bug Fixes
- [`6e7214a`](https://github.com/klikli-dev/modonomicon/commit/6e7214a716854c93a8dd3d6c6608f0142d7c0022) - **ci/cd**: publish creates non-obfed jarjar *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`d7e4c94`](https://github.com/klikli-dev/modonomicon/commit/d7e4c94b85f6fc3b84eb5a2ba63025b5d42e9191) - **ci/cd**: enable cache cleanup *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.38.3] - 2023-07-21
### :bug: Bug Fixes
- [`6d912af`](https://github.com/klikli-dev/modonomicon/commit/6d912af8aa2a74e19c73ddbf674115bcfb60d65a) - **ci/cd**: artefact upload to github release *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.38.2] - 2023-07-21
### :sparkles: New Features
- [`31411ec`](https://github.com/klikli-dev/modonomicon/commit/31411ec6834e3d34bcc4fa15a19ee4c047349986) - update licensing links for maven *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`4c78b5e`](https://github.com/klikli-dev/modonomicon/commit/4c78b5e29ab02a551e97459e91d6cf86e4ca6f79) - artefact upload to github release *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`90f13f4`](https://github.com/klikli-dev/modonomicon/commit/90f13f43f23a401526fbe2c9fa09d98e82b651bd) - **fabric**: book not showing up in the right creative tab *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.38.1] - 2023-07-20
### :bug: Bug Fixes
- [`356aa0b`](https://github.com/klikli-dev/modonomicon/commit/356aa0b1a2025ffda63a60f391ae2258efd7352f) - fabric version file name *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`364154d`](https://github.com/klikli-dev/modonomicon/commit/364154df0f8c40efc4961e28e3203c10efb4b2e3) - crash on startup *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.38.0] - 2023-07-19
### :sparkles: New Features
- [`0aec62f`](https://github.com/klikli-dev/modonomicon/commit/0aec62f15439d635d12d06b42bc52eb5c1060694) - Multiloader setup (= Forge + Fabric Version available) *(PR [#138](https://github.com/klikli-dev/modonomicon/pull/138) by [@klikli-dev](https://github.com/klikli-dev))*
  - :arrow_lower_right: *addresses issue [#127](undefined) opened by [@klikli-dev](https://github.com/klikli-dev)*


## [release/v1.20.1-1.37.0] - 2023-06-30
### :sparkles: New Features
- [`e668701`](https://github.com/klikli-dev/modonomicon/commit/e668701ed88316776fe365e31a963603cdf2b5aa) - simplify api, improve category provider helpers *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.36.4] - 2023-06-30
### :sparkles: New Features
- [`25bb7d7`](https://github.com/klikli-dev/modonomicon/commit/25bb7d709a5a974f9b4d850da54a58aa910acf34) - add some more helper shortcuts to category provider *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`cb5ebd3`](https://github.com/klikli-dev/modonomicon/commit/cb5ebd33e2cd549fef97e24e62829293831687e1) - increase mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.36.3] - 2023-06-28
### :sparkles: New Features
- [`bea5360`](https://github.com/klikli-dev/modonomicon/commit/bea5360317b65db9bfcf09c48b98fdbec59f23a4) - add category link helper *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`bd17f77`](https://github.com/klikli-dev/modonomicon/commit/bd17f7701e80b56f373a2bd7fbf94e1e1c4c0fbd) - update mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`6dc0282`](https://github.com/klikli-dev/modonomicon/commit/6dc028236c29a38ad5ebc2d36b33f78e4e07a282) - update to 1.20.1 parchment mappings *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.36.2] - 2023-06-25
### :bug: Bug Fixes
- [`87f83a2`](https://github.com/klikli-dev/modonomicon/commit/87f83a20f4934c20b78dff0f6b3939df6fd6ae6c) - make advancement condition take resource location as id *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.36.1] - 2023-06-25
### :bug: Bug Fixes
- [`7123c9c`](https://github.com/klikli-dev/modonomicon/commit/7123c9cee7396128191cd904fd9ef01fe2263cad) - multi-item ingredient not supported, only tag *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.36.0] - 2023-06-16
### :sparkles: New Features
- [`df63b9a`](https://github.com/klikli-dev/modonomicon/commit/df63b9a5a310b7c4d61911d778dafd6507f82b1c) - improve new provider API *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.35.0] - 2023-06-15
### :sparkles: New Features
- [`466a257`](https://github.com/klikli-dev/modonomicon/commit/466a257e16a76f8166e956d6653d32f60ed39bf2) - add category provider with helper functionality *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`e3f9645`](https://github.com/klikli-dev/modonomicon/commit/e3f964545f0c8d797e59ed712264d64327b9aa2c) - switch to new publish plugin that supports modrinth *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`8973962`](https://github.com/klikli-dev/modonomicon/commit/89739629e1348ce0e6af8b8b289c0582079d77bd) - fix new publish plugin file selection *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.34.0] - 2023-06-14
### :sparkles: New Features
- [`836e0cf`](https://github.com/klikli-dev/modonomicon/commit/836e0cf09a16717bbe7c113b63ecdb13182d59cf) - add support for in-book translations *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`c465e1d`](https://github.com/klikli-dev/modonomicon/commit/c465e1da42938bf294c8ff060883cb326fadf9e4) - fix cf metadata *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20.1-1.33.1] - 2023-06-14
### :sparkles: New Features
- [`56cedce`](https://github.com/klikli-dev/modonomicon/commit/56cedce502697cab9bd5268e7655068d11e838cf) - update to 1.20.1 *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20-1.33.1] - 2023-06-12
### :bug: Bug Fixes
- [`c1c0ab3`](https://github.com/klikli-dev/modonomicon/commit/c1c0ab3e2c7e6feae12ab1d657a7b9d918fa38ad) - discrepancy in command data model *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20-1.33.0] - 2023-06-12
### :sparkles: New Features
- [`ef94f76`](https://github.com/klikli-dev/modonomicon/commit/ef94f76d50ca7e10a879867af41bcfa5c2750d30) - add command support for links and for entry read *(PR [#125](https://github.com/klikli-dev/modonomicon/pull/125) by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20-1.32.5] - 2023-06-12
### :bug: Bug Fixes
- [`6d58c9a`](https://github.com/klikli-dev/modonomicon/commit/6d58c9a32f728804b56b213c6a4534369ba5b633) - texture (= non item) entry icons rendering behind entry background *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`251d63c`](https://github.com/klikli-dev/modonomicon/commit/251d63ce878b67a5c38f487481673ad6728cf414) - increase mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20-1.32.4] - 2023-06-09
### :bug: Bug Fixes
- [`34c2fee`](https://github.com/klikli-dev/modonomicon/commit/34c2feea349f28056e9eab7223bc8831bda13abd) - allow all patchouli versions *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`dc2df38`](https://github.com/klikli-dev/modonomicon/commit/dc2df387d43aac602125688cc2cfb385ffc50bcf) - re-establish reuse compliance *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20-1.32.3] - 2023-06-09
### :wrench: Chores
- [`86f9c2a`](https://github.com/klikli-dev/modonomicon/commit/86f9c2a35034177ba5f0d8a04960bb9576810101) - enable jei *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.20-1.32.2] - 2023-06-08
### :sparkles: New Features
- [`9aa294d`](https://github.com/klikli-dev/modonomicon/commit/9aa294d30ff00e6b14e5c670e19689b2322ed279) - upgrade to 1.20.0 *(PR [#121](https://github.com/klikli-dev/modonomicon/pull/121) by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`dc68e98`](https://github.com/klikli-dev/modonomicon/commit/dc68e98829dec4d384bdd2b37faa59ed78ce0c9d) - gradle build *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.32.2] - 2023-05-31
### :sparkles: New Features
- [`7c45a56`](https://github.com/klikli-dev/modonomicon/commit/7c45a56ddac8860a531f000cc969f8020ec608cd) - create zh_cn.json *(PR [#120](https://github.com/klikli-dev/modonomicon/pull/120) by [@Zoshsgahdnkc](https://github.com/Zoshsgahdnkc))*


## [release/v1.19.4-1.32.1] - 2023-05-07
### :bug: Bug Fixes
- [`ccd83d8`](https://github.com/klikli-dev/modonomicon/commit/ccd83d8cbf57529a74495cb245422e66f768989e) - itemstacks are not valid set unique identifiers *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.32.0] - 2023-05-07
### :sparkles: New Features
- [`be4d677`](https://github.com/klikli-dev/modonomicon/commit/be4d677cebb05474039a5760cbaeb0ae6ff95d51) - add render filtering for item/fluid stacks in the book *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.31.6] - 2023-05-03
### :sparkles: New Features
- [`abd2776`](https://github.com/klikli-dev/modonomicon/commit/abd277666221a3cf5d082e098804a76a53ea4661) - Update ru_ru.json *(PR [#117](https://github.com/klikli-dev/modonomicon/pull/117) by [@Heimdallr-1](https://github.com/Heimdallr-1))*

### :wrench: Chores
- [`5c7586d`](https://github.com/klikli-dev/modonomicon/commit/5c7586d43b77b6b7a2751c46f311d61d001952be) - update to latest jei *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.31.5] - 2023-04-30
### :bug: Bug Fixes
- [`2bab28f`](https://github.com/klikli-dev/modonomicon/commit/2bab28f6666259eee7bd0c3dd89190bac700bd68) - reset render color after fluid rendering *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.31.4] - 2023-04-30
### :wrench: Chores
- [`ce4a361`](https://github.com/klikli-dev/modonomicon/commit/ce4a361c65476658a54ec15acd82ecb1d432e62c) - improve fluid stack rendering *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.31.3] - 2023-04-30
### :wrench: Chores
- [`10e1040`](https://github.com/klikli-dev/modonomicon/commit/10e1040fadbf576490855d96ff1e41d4d4e1bf3e) - remove number formatting for fluid tooltips *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.31.2] - 2023-04-30
### :bug: Bug Fixes
- [`40c2510`](https://github.com/klikli-dev/modonomicon/commit/40c2510e6e5e7fd3a9817a188739c5315694b428) - tooltip not resetting *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`525fcd7`](https://github.com/klikli-dev/modonomicon/commit/525fcd73496dc16119263886103467c4a65a10d9) - fluid tooltip lang key -> translation missing *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.31.1] - 2023-04-30
### :bug: Bug Fixes
- [`d1a4641`](https://github.com/klikli-dev/modonomicon/commit/d1a46417c7b2bf70e7c4b233cee72e00066f2077) - recursion *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.31.0] - 2023-04-30
### :sparkles: New Features
- [`11f031f`](https://github.com/klikli-dev/modonomicon/commit/11f031f6facda764e947dbf207f86d71100da408) - add fluid stack rendering *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`76389de`](https://github.com/klikli-dev/modonomicon/commit/76389de367b41e67beaefb51de57f60b4b85cfb1) - add missing licenses and statements for REUSE compliance *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.30.3] - 2023-04-26
### :bug: Bug Fixes
- [`cc6a563`](https://github.com/klikli-dev/modonomicon/commit/cc6a563e3c7d1863ebf60e9fc1dc1aececf247e9) - tooltip rendering behind images *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.30.2] - 2023-04-03
### :sparkles: New Features
- [`1cf2618`](https://github.com/klikli-dev/modonomicon/commit/1cf26180051846af046d0c0b3bc55587388e8857) - enable jei integration now that jei is updated *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`adb9000`](https://github.com/klikli-dev/modonomicon/commit/adb9000f8e463e50a38fb925d7f7ea4ca5e47908) - render issues with block items *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.30.1] - 2023-04-01
### :sparkles: New Features
- [`17ebb72`](https://github.com/klikli-dev/modonomicon/commit/17ebb72ce5d75391371e1d05e50d63abe387b8e3) - Create ru_ru.json *(PR [#113](https://github.com/klikli-dev/modonomicon/pull/113) by [@Heimdallr-1](https://github.com/Heimdallr-1))*

### :bug: Bug Fixes
- [`923bdac`](https://github.com/klikli-dev/modonomicon/commit/923bdac637722b01d778566d33dae7e614130373) - render issues if ftb chunks or chunk pregenerator are present *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.4-1.30.0] - 2023-03-24
### :sparkles: New Features
- [`46ea912`](https://github.com/klikli-dev/modonomicon/commit/46ea912a219fbbe9fbedd179518fd20d5df00dda) - update to 1.19.4 *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`9b9cf5d`](https://github.com/klikli-dev/modonomicon/commit/9b9cf5dce7144ff9863bb12aaab6a23d8b80955d) - render improvements *(PR [#111](https://github.com/klikli-dev/modonomicon/pull/111) by [@klikli-dev](https://github.com/klikli-dev))*
- [`dc9907f`](https://github.com/klikli-dev/modonomicon/commit/dc9907f8bb24a733d081430b4ae877996c2706d2) - new smithing recipe rendering *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`816a0f4`](https://github.com/klikli-dev/modonomicon/commit/816a0f4c39f2c01c2a0ebb66b6f0f282ace42001) - scissor rendering *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`7325e6d`](https://github.com/klikli-dev/modonomicon/commit/7325e6d3e76628ce0caaa2fe5744fa43e77d6706) - entry tooltips rendering behind frame *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`3611de2`](https://github.com/klikli-dev/modonomicon/commit/3611de217b0cc5c4b1ec04ddddcf06f09cd6a435) - button tooltips "stuck" after click *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`062e56f`](https://github.com/klikli-dev/modonomicon/commit/062e56f6198b958031c98d325614e0f95b7fe9b4) - update scissor usage to use MC helper *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`5b761c9`](https://github.com/klikli-dev/modonomicon/commit/5b761c96658ae270e85db1d2b47c67d76c5c0a93) - set correct branch for changelogs *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.29.0] - 2023-03-15
### :sparkles: New Features
- [`d25945d`](https://github.com/klikli-dev/modonomicon/commit/d25945d12c1ec735b88fb48767fb04246c1acc97) - update book model to require proper 1.19.3 creative tab *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`be901d5`](https://github.com/klikli-dev/modonomicon/commit/be901d5276b6b312329a2c50bf3b15465e395ee3) - cleanup paths in book provider *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.28.0] - 2023-03-14
### :sparkles: New Features
- [`86d938a`](https://github.com/klikli-dev/modonomicon/commit/86d938a528c06675c86d81f50828344a3613ae70) - add multiblock datagen *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.27.0] - 2023-03-10
### :sparkles: New Features
- [`f84d8a3`](https://github.com/klikli-dev/modonomicon/commit/f84d8a3948e5aa46f048994c338f02541c2177c2) - add withEntryBackground overload that takes a pair *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`2b611e8`](https://github.com/klikli-dev/modonomicon/commit/2b611e8ba92706de24f0b03ff7298316ac9c0fe1) - update mod version to 1.27.0 *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.25.0] - 2023-02-18
### :sparkles: New Features
- [`a754d7c`](https://github.com/klikli-dev/modonomicon/commit/a754d7c7e3eca3e3987a2435b86440663cf00def) - add offset getters to multiblock *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`d627b87`](https://github.com/klikli-dev/modonomicon/commit/d627b8734b39018335c7b567e83bc8b4af9dcf7d) - outdated references to patchouli *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.24.0] - 2023-02-12
### :sparkles: New Features
- [`d798c93`](https://github.com/klikli-dev/modonomicon/commit/d798c93675d3d2fb095aeb87a23850dd342b588a) - add text margin config to book *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`3b52872`](https://github.com/klikli-dev/modonomicon/commit/3b52872f8797525e61c2ddbd4e5b881dc9570414) - use constant mod id *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.23.2] - 2023-01-01
### :bug: Bug Fixes
- [`5c8cad7`](https://github.com/klikli-dev/modonomicon/commit/5c8cad718437707e3308a16c91bf4aeb66c0f775) - network replicated type of mod loaded condition was advancement condition *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.23.1] - 2022-12-30
### :bug: Bug Fixes
- [`963ea27`](https://github.com/klikli-dev/modonomicon/commit/963ea27af7f95617cedf34e1f759e60078c3ffa5) - add missing model for book mod loaded condition *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.23.0] - 2022-12-30
### :sparkles: New Features
- [`7d813a1`](https://github.com/klikli-dev/modonomicon/commit/7d813a16cf7771f66124f0ef697966ac0e78c2bf) - add mod loaded unlock condition *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.22.1] - 2022-12-29
### :bug: Bug Fixes
- [`e96784f`](https://github.com/klikli-dev/modonomicon/commit/e96784fa4433f46eed59714ad73be42d065e1b30) - switch to concurrent maps where necessary *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.22.0] - 2022-12-27
### :sparkles: New Features
- [`2f26c11`](https://github.com/klikli-dev/modonomicon/commit/2f26c11c14c7a97fa48f2533f8bfb5c050694b25) - add better error reporting to book condition checks *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`de4125c`](https://github.com/klikli-dev/modonomicon/commit/de4125ce85cf42f7f19c6e2da39450ac08879861) - handle edge case where capability is updated before books are built *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`e478e03`](https://github.com/klikli-dev/modonomicon/commit/e478e03dc18a92e4f205859f40435a0d7ea7de1d) - reformat *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.21.1] - 2022-12-22
### :sparkles: New Features
- [`c619c12`](https://github.com/klikli-dev/modonomicon/commit/c619c12b6fc110ab9508669825db9dbdec9fc3bd) - add shortcut for cat/entry icons from item and add docs *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`8cb33a3`](https://github.com/klikli-dev/modonomicon/commit/8cb33a35d33539caf3f328aede6f1c707149782d) - update mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.21.0] - 2022-12-21
### :boom: BREAKING CHANGES
- due to [`c0a4abd`](https://github.com/klikli-dev/modonomicon/commit/c0a4abde333d161b2105ab6ff60edc9012aed6cd) - change folder for modonomicon data. *(commit by [@klikli-dev](https://github.com/klikli-dev))*:

  /data/<modid>/modonomicons is now /data/<modid>/modonomicon/books and /data/<modid>/modonomicon_multiblocks is now /data/<modid>/modonomicon/multiblocks


### :sparkles: New Features
- [`c0a4abd`](https://github.com/klikli-dev/modonomicon/commit/c0a4abde333d161b2105ab6ff60edc9012aed6cd) - change folder for modonomicon data. *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`874af30`](https://github.com/klikli-dev/modonomicon/commit/874af309ab30799bc71bfccb5609e013733ca022) - refactor source sets *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.20.0] - 2022-12-21
### :sparkles: New Features
- [`1475f92`](https://github.com/klikli-dev/modonomicon/commit/1475f923b8a5ad4f3695c0f54a4e340e583e2e31) - add shortcut for withParent from BookEntryModel *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`b5cfcd1`](https://github.com/klikli-dev/modonomicon/commit/b5cfcd14d8a06de6471a81a93fad0c2c2ecd35cc) - add shortcut for withParent from BookEntryModel.Builder *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`5b7611a`](https://github.com/klikli-dev/modonomicon/commit/5b7611a1445ddad42fb10d1b8cb665f152609c8c) - entry icon hover issues near book border *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`c22ea02`](https://github.com/klikli-dev/modonomicon/commit/c22ea0214611d1c9393cd2f1a9f8b3f3aae2c0b9) - add api sources to sources jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`2c523ea`](https://github.com/klikli-dev/modonomicon/commit/2c523eaa79db577e0f2c4ffe270cea5907164efc) - reobfuscate sources jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`55583d1`](https://github.com/klikli-dev/modonomicon/commit/55583d1316292e46a624fbe454f5600c193bd92a) - set correct from type for sources jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`cbd7d55`](https://github.com/klikli-dev/modonomicon/commit/cbd7d55d4a5d37454bb6a6851287614080837bdb) - update workflow to match renamed branch *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`d99096b`](https://github.com/klikli-dev/modonomicon/commit/d99096b404b8592c7cfd82da9d0819799d539f6e) - update forge and mappings *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`39a6b02`](https://github.com/klikli-dev/modonomicon/commit/39a6b0283e3f188f49d738a4781f9ca35451f805) - disable changelog for test builds *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`ded8735`](https://github.com/klikli-dev/modonomicon/commit/ded8735dca174a626deb86bd13badbb1f76d09c1) - add docs to BookEntryModel *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`44f7d6d`](https://github.com/klikli-dev/modonomicon/commit/44f7d6dcf6f0214da78d86dea8b4fb2e201b4659) - add maven artifact id for consistent artifact output *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`d7bef0c`](https://github.com/klikli-dev/modonomicon/commit/d7bef0c1ce7aa129803a7d2dbe997d210ded6bb6) - set mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.3-1.19.0] - 2022-12-20
### :bug: Bug Fixes
- [`f436a68`](https://github.com/klikli-dev/modonomicon/commit/f436a68780ef244dea36cf51f4f7fa2836a20355) - entry icon hover issues near book border *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [beta/v1.19.3-1.18.1] - 2022-12-20
### :sparkles: New Features
- [`877b13f`](https://github.com/klikli-dev/modonomicon/commit/877b13f77f3c5f45b36b39df1e87b3c6431869c7) - update to latest forge datagen changes for 44.0.37 *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`82920fe`](https://github.com/klikli-dev/modonomicon/commit/82920fe1a5c4adbd94b72a1f18ebbf852bba8e18) - update to latest 1.19.3 mappings *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`e18e1f8`](https://github.com/klikli-dev/modonomicon/commit/e18e1f8b0e2cb76e7200cdb7d63c8a3b2fba6a11) - disable changelog for test builds *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [beta/v1.19.3-1.18.0] - 2022-12-11
### :sparkles: New Features
- [`de4cea6`](https://github.com/klikli-dev/modonomicon/commit/de4cea65c1e7949199b2683e17150deec965ff36) - update to forge 44.0.6 breaking changes *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`d62fc1f`](https://github.com/klikli-dev/modonomicon/commit/d62fc1f45f394018e73153f0c79cf021d10d2c6e) - fix pack.mcmeta format *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`baea47d`](https://github.com/klikli-dev/modonomicon/commit/baea47d9c701222a89e0dfdb524a8b944dc1f1b3) - set minimum required forge version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [beta/v1.19.3-1.17.0] - 2022-12-10
### :sparkles: New Features
- [`75ca997`](https://github.com/klikli-dev/modonomicon/commit/75ca997f416c29e22c1c32064838815efccae599) - update to 1.19.3 *(PR [#107](https://github.com/klikli-dev/modonomicon/pull/107) by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`1a3e45b`](https://github.com/klikli-dev/modonomicon/commit/1a3e45b7a96fa0a22eb0a568881c895f3ab9952f) - update release workflow to 1.19.3 *(commit by [@klikli-dev](https://github.com/klikli-dev))*


[beta/v1.19.3-1.17.0]: https://github.com/klikli-dev/modonomicon/compare/dummy/v1.19.3-0.0.0...beta/v1.19.3-1.17.0
[beta/v1.19.3-1.18.0]: https://github.com/klikli-dev/modonomicon/compare/beta/v1.19.3-1.17.0...beta/v1.19.3-1.18.0
[beta/v1.19.3-1.18.1]: https://github.com/klikli-dev/modonomicon/compare/beta/v1.19.3-1.18.0...beta/v1.19.3-1.18.1
[release/v1.19.3-1.19.0]: https://github.com/klikli-dev/modonomicon/compare/beta/v1.19.3-1.18.1...release/v1.19.3-1.19.0
[release/v1.19.3-1.20.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.19.0...release/v1.19.3-1.20.0
[release/v1.19.3-1.21.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.20.0...release/v1.19.3-1.21.0
[release/v1.19.3-1.21.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.21.0...release/v1.19.3-1.21.1
[release/v1.19.3-1.22.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.21.1...release/v1.19.3-1.22.0
[release/v1.19.3-1.22.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.22.0...release/v1.19.3-1.22.1
[release/v1.19.3-1.23.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.22.1...release/v1.19.3-1.23.0
[release/v1.19.3-1.23.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.23.0...release/v1.19.3-1.23.1
[release/v1.19.3-1.23.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.23.1...release/v1.19.3-1.23.2
[release/v1.19.3-1.24.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.23.2...release/v1.19.3-1.24.0
[release/v1.19.3-1.25.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.24.0...release/v1.19.3-1.25.0
[release/v1.19.3-1.27.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.25.0...release/v1.19.3-1.27.0
[release/v1.19.3-1.28.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.27.0...release/v1.19.3-1.28.0
[release/v1.19.3-1.29.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.3-1.28.0...release/v1.19.3-1.29.0
[release/v1.19.4-1.30.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-0.0.0...release/v1.19.4-1.30.0
[release/v1.19.4-1.30.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.30.0...release/v1.19.4-1.30.1
[release/v1.19.4-1.30.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.30.1...release/v1.19.4-1.30.2
[release/v1.19.4-1.30.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.30.2...release/v1.19.4-1.30.3
[release/v1.19.4-1.31.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.30.3...release/v1.19.4-1.31.0
[release/v1.19.4-1.31.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.31.0...release/v1.19.4-1.31.1
[release/v1.19.4-1.31.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.31.1...release/v1.19.4-1.31.2
[release/v1.19.4-1.31.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.31.2...release/v1.19.4-1.31.3
[release/v1.19.4-1.31.4]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.31.3...release/v1.19.4-1.31.4
[release/v1.19.4-1.31.5]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.31.4...release/v1.19.4-1.31.5
[release/v1.19.4-1.31.6]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.31.5...release/v1.19.4-1.31.6
[release/v1.19.4-1.32.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.31.6...release/v1.19.4-1.32.0
[release/v1.19.4-1.32.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.32.0...release/v1.19.4-1.32.1
[release/v1.19.4-1.32.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.4-1.32.1...release/v1.19.4-1.32.2
[release/v1.20-1.32.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20-0.0.0...release/v1.20-1.32.2
[release/v1.20-1.32.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20-1.32.2...release/v1.20-1.32.3
[release/v1.20-1.32.4]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20-1.32.3...release/v1.20-1.32.4
[release/v1.20-1.32.5]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20-1.32.4...release/v1.20-1.32.5
[release/v1.20-1.33.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20-1.32.5...release/v1.20-1.33.0
[release/v1.20-1.33.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20-1.33.0...release/v1.20-1.33.1
[release/v1.20.1-1.33.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-0.0.0...release/v1.20.1-1.33.1
[release/v1.20.1-1.34.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.33.1...release/v1.20.1-1.34.0
[release/v1.20.1-1.35.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.34.0...release/v1.20.1-1.35.0
[release/v1.20.1-1.36.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.35.0...release/v1.20.1-1.36.0
[release/v1.20.1-1.36.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.36.0...release/v1.20.1-1.36.1
[release/v1.20.1-1.36.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.36.1...release/v1.20.1-1.36.2
[release/v1.20.1-1.36.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.36.2...release/v1.20.1-1.36.3
[release/v1.20.1-1.36.4]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.36.3...release/v1.20.1-1.36.4
[release/v1.20.1-1.37.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.36.4...release/v1.20.1-1.37.0
[release/v1.20.1-1.38.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.37.0...release/v1.20.1-1.38.0
[release/v1.20.1-1.38.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.38.0...release/v1.20.1-1.38.1
[release/v1.20.1-1.38.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.38.1...release/v1.20.1-1.38.2
[release/v1.20.1-1.38.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.38.2...release/v1.20.1-1.38.3
[release/v1.20.1-1.38.4]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.38.3...release/v1.20.1-1.38.4
[release/v1.20.1-1.38.5]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.38.4...release/v1.20.1-1.38.5
[release/v1.20.1-1.38.6]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.38.5...release/v1.20.1-1.38.6
[release/v1.20.1-1.39.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.38.6...release/v1.20.1-1.39.0
[release/v1.20.1-1.39.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.39.0...release/v1.20.1-1.39.1
[release/v1.20.1-1.40.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.1-1.39.1...release/v1.20.1-1.40.0
[release/v1.20.2-1.41.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.2-1.41.0...release/v1.20.2-1.41.1
[release/v1.20.2-1.41.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.2-1.41.1...release/v1.20.2-1.41.2
[release/v1.20.2-1.42.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.2-1.41.2...release/v1.20.2-1.42.0
[release/v1.20.4-1.42.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4.0.0.0...release/v1.20.4-1.42.0
[release/v1.20.4-1.42.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.42.0...release/v1.20.4-1.42.1
[release/v1.20.4-1.43.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.42.1...release/v1.20.4-1.43.0
[release/v1.20.4-1.43.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.43.0...release/v1.20.4-1.43.2
[release/v1.20.4-1.43.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.43.2...release/v1.20.4-1.43.3
[release/v1.20.4-1.44.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.43.3...release/v1.20.4-1.44.0
[release/v1.20.4-1.45.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.44.0...release/v1.20.4-1.45.0
[release/v1.20.4-1.46.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.45.0...release/v1.20.4-1.46.0
[release/v1.20.4-1.47.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.46.0...release/v1.20.4-1.47.0
[release/v1.20.4-1.48.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.47.0...release/v1.20.4-1.48.0
[release/v1.20.4-1.48.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.48.0...release/v1.20.4-1.48.1
[release/v1.20.4-1.49.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.48.1...release/v1.20.4-1.49.0
[release/v1.20.4-1.49.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.49.0...release/v1.20.4-1.49.1
[release/v1.20.4-1.50.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.49.1...release/v1.20.4-1.50.0
[release/v1.20.4-1.51.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.50.0...release/v1.20.4-1.51.0
[release/v1.20.4-1.51.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.51.0...release/v1.20.4-1.51.1
[release/v1.20.4-1.52.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.51.1...release/v1.20.4-1.52.0
[release/v1.20.4-1.52.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.52.0...release/v1.20.4-1.52.1
[release/v1.20.4-1.54.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.53.0...release/v1.20.4-1.54.0
[release/v1.20.4-1.54.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.54.0...release/v1.20.4-1.54.1
[release/v1.20.4-1.54.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.54.1...release/v1.20.4-1.54.2
[release/v1.20.4-1.54.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.54.2...release/v1.20.4-1.54.3
[release/v1.20.4-1.54.4]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.54.3...release/v1.20.4-1.54.4
[release/v1.20.4-1.54.5]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.54.4...release/v1.20.4-1.54.5
[release/v1.20.4-1.55.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.54.5...release/v1.20.4-1.55.0
[release/v1.20.4-1.55.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.55.0...release/v1.20.4-1.55.1
[release/v1.20.4-1.56.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.55.1...release/v1.20.4-1.56.0
[release/v1.20.4-1.56.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.56.0...release/v1.20.4-1.56.1
[release/v1.20.4-1.57.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.56.1...release/v1.20.4-1.57.0
[release/v1.20.4-1.58.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.57.0...release/v1.20.4-1.58.0
[release/v1.20.4-1.59.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.58.0...release/v1.20.4-1.59.0
[release/v1.20.4-1.60.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.59.0...release/v1.20.4-1.60.0
[release/v1.20.4-1.61.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.60.0...release/v1.20.4-1.61.0
[release/v1.20.4-1.61.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.61.0...release/v1.20.4-1.61.1
[release/v1.20.4-1.62.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.61.1...release/v1.20.4-1.62.0
[release/v1.20.4-1.63.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.62.0...release/v1.20.4-1.63.0
[release/v1.20.4-1.64.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.63.0...release/v1.20.4-1.64.0
[release/v1.20.4-1.65.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.64.0...release/v1.20.4-1.65.0
[release/v1.20.4-1.66.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.65.0...release/v1.20.4-1.66.0
[release/v1.20.4-1.66.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.66.0...release/v1.20.4-1.66.2
[release/v1.20.4-1.66.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.66.2...release/v1.20.4-1.66.3
[release/v1.20.4-1.66.5]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.66.3...release/v1.20.4-1.66.5
[release/v1.20.4-1.67.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.66.5...release/v1.20.4-1.67.0
[release/v1.20.4-1.67.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.67.0...release/v1.20.4-1.67.1
[release/v1.20.4-1.68.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.67.1...release/v1.20.4-1.68.0
[release/v1.20.4-1.69.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.68.0...release/v1.20.4-1.69.0
[release/v1.20.4-1.69.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.4-1.69.0...release/v1.20.4-1.69.1
[release/v1.20.5-1.69.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.5-0.0.0...release/v1.20.5-1.69.2
[release/v1.20.6-1.69.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-0.0.0...release/v1.20.6-1.69.3
[release/v1.20.6-1.70.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-1.69.3...release/v1.20.6-1.70.0
[release/v1.20.6-1.70.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-1.70.0...release/v1.20.6-1.70.1
[release/v1.20.6-1.71.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-1.70.1...release/v1.20.6-1.71.1
[release/v1.20.6-1.72.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-1.71.1...release/v1.20.6-1.72.0
[release/v1.20.6-1.72.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-1.72.0...release/v1.20.6-1.72.1
[release/v1.20.6-1.72.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-1.72.1...release/v1.20.6-1.72.2
[release/v1.20.6-1.73.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-1.72.2...release/v1.20.6-1.73.0
[release/v1.20.6-1.73.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-1.73.0...release/v1.20.6-1.73.1
[release/v1.20.6-1.73.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-1.73.1...release/v1.20.6-1.73.2
[release/v1.20.6-1.73.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.20.6-1.73.2...release/v1.20.6-1.73.3
[release/v1.21-1.73.3]: https://github.com/klikli-dev/modonomicon/compare/release/v1.21-0.0.0...release/v1.21-1.73.3