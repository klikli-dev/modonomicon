# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [release/v1.19.2-1.34.0] - 2023-08-01
### :sparkles: New Features
- [`65b6f4f`](https://github.com/klikli-dev/modonomicon/commit/65b6f4f1aedfc49e6aee2476eb1d86a62be282fc) - Update KubeJS integration to KubeJS 6.1 *(PR [#136](https://github.com/klikli-dev/modonomicon/pull/136) by [@MaxNeedsSnacks](https://github.com/MaxNeedsSnacks))*
- [`34024bf`](https://github.com/klikli-dev/modonomicon/commit/34024bf4aabf62a6902d9ffa02017342ddfc6137) - set min kubejs version to 6.1 as it has breaking changes *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`98fc47d`](https://github.com/klikli-dev/modonomicon/commit/98fc47de7d0b9c235aa41eb71c28bf8b6be592f2) - switch to cloudsmith maven *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`06908e9`](https://github.com/klikli-dev/modonomicon/commit/06908e952e25a03ca140e131d4f71b6b9761ed44) - make tag matcher log error instead of throwing exception if tag unavailable *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.33.1] - 2023-07-13
### :sparkles: New Features
- [`5704a05`](https://github.com/klikli-dev/modonomicon/commit/5704a0509f0421917dc956f9a11d534859854244) - Include Modrinth uploads *(PR [#132](https://github.com/klikli-dev/modonomicon/pull/132) by [@ColonelGerdauf](https://github.com/ColonelGerdauf))*

### :bug: Bug Fixes
- [`2f9f880`](https://github.com/klikli-dev/modonomicon/commit/2f9f880cbcae71b55e66b4906244f92c22e09da4) - action *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.33.0] - 2023-06-13
### :sparkles: New Features
- [`908a2c2`](https://github.com/klikli-dev/modonomicon/commit/908a2c287c29da38293fcf0454b395718ee250fb) - add command support for links and for entry read *(PR [#125](https://github.com/klikli-dev/modonomicon/pull/125) by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`ea13e68`](https://github.com/klikli-dev/modonomicon/commit/ea13e682ffd42426ef1dd04a29cb11f3356fa76a) - discrepancy in command data model *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`51b7a2c`](https://github.com/klikli-dev/modonomicon/commit/51b7a2cb4c69c7b3fba8f7f18fdc854aa029a1b9) - merge errors *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.30.2] - 2023-05-03
### :bug: Bug Fixes
- [`eab2d2c`](https://github.com/klikli-dev/modonomicon/commit/eab2d2c3f7f6640229c05d12289b1bd3d17e2a7d) - render issues with block items *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`4103ac9`](https://github.com/klikli-dev/modonomicon/commit/4103ac9a0c477caa1ec69ab000108368d3c49b65) - add workaround for multiblock rendering with rubidium present *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.30.1] - 2023-04-01
### :sparkles: New Features
- [`d35fe06`](https://github.com/klikli-dev/modonomicon/commit/d35fe0611f12e3081cc5d0b37f9de92f254a94fd) - Create ru_ru.json *(PR [#113](https://github.com/klikli-dev/modonomicon/pull/113) by [@Heimdallr-1](https://github.com/Heimdallr-1))*

### :bug: Bug Fixes
- [`ffa8954`](https://github.com/klikli-dev/modonomicon/commit/ffa89540dc16f17280d52c6ccac79b4be20653f9) - render issues if ftb chunks or chunk pregenerator are present *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`f536516`](https://github.com/klikli-dev/modonomicon/commit/f536516105a038951b84cb55aaca2db99d27eeba) - update mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`edd3e4c`](https://github.com/klikli-dev/modonomicon/commit/edd3e4c0cac35f63ffe2527d9e3209dd57c1d3ec) - reformat *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.30.0] - 2023-03-23
### :sparkles: New Features
- [`65cce77`](https://github.com/klikli-dev/modonomicon/commit/65cce7726b573183414d175a55131817f1392301) - render improvements *(PR [#111](https://github.com/klikli-dev/modonomicon/pull/111) by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`b83c639`](https://github.com/klikli-dev/modonomicon/commit/b83c639190acf8a2c052767313798347d34653b4) - json property name *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`6fe7f46`](https://github.com/klikli-dev/modonomicon/commit/6fe7f46bf876bfdc2d36e36ba92b648cde4ec7ff) - remove unused left over code from 1.19.3 *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`c299f36`](https://github.com/klikli-dev/modonomicon/commit/c299f3617f5f784904cfb6f501a0272a42fb2732) - cleanup paths in book provider *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.28.0] - 2023-03-14
### :sparkles: New Features
- [`a5b8711`](https://github.com/klikli-dev/modonomicon/commit/a5b8711b59aebe1468d35a2bc12f3756fb1f6fc5) - add multiblock datagen *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.27.0] - 2023-03-10
### :sparkles: New Features
- [`45c4aa2`](https://github.com/klikli-dev/modonomicon/commit/45c4aa2533b52152b6a532f071a8f4036bfb6792) - add withEntryBackground overload that takes a pair *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`0fcb8f7`](https://github.com/klikli-dev/modonomicon/commit/0fcb8f7674c6277949ea32fbd1aade7484318a19) - update mod version to 1.27.0 *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.26.0] - 2023-02-23
### :sparkles: New Features
- [`91e1623`](https://github.com/klikli-dev/modonomicon/commit/91e1623b471ad546257e696811199bac66b763ae) - add functionality to keep track of content unlocked in one update *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`a17d7ef`](https://github.com/klikli-dev/modonomicon/commit/a17d7ef58851a76b0bf2b0cddf58e8e04e838c8a) - add kubejs event integration *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`886c865`](https://github.com/klikli-dev/modonomicon/commit/886c86558790653396d539e40c9956de7f7ad3de) - update mod version for maven publish *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`f080f8b`](https://github.com/klikli-dev/modonomicon/commit/f080f8b3d6d555309a66afc8f246c1619f4d066f) - rename event to fit meaning *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.25.0] - 2023-02-18
### :sparkles: New Features
- [`c3ce0ce`](https://github.com/klikli-dev/modonomicon/commit/c3ce0ce1de83c2b15d4f7b1965ff292906a7fedb) - add offset getters to multiblock *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`58422c3`](https://github.com/klikli-dev/modonomicon/commit/58422c3c38183ce49014be6b8bd4a485633b254a) - outdated references to patchouli *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.24.0] - 2023-02-12
### :sparkles: New Features
- [`f356e3a`](https://github.com/klikli-dev/modonomicon/commit/f356e3ac8841ac93acd417f7794202dcf9d4b225) - add text margin config to book *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.23.2] - 2023-01-01
### :bug: Bug Fixes
- [`1ff961a`](https://github.com/klikli-dev/modonomicon/commit/1ff961ad4ead8f542c25b2085c741b53303eff89) - network replicated type of mod loaded condition was advancement condition *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`3fbb5ab`](https://github.com/klikli-dev/modonomicon/commit/3fbb5abf23735adac19c274458e830859a22f60b) - update mod version for local builds *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.23.1] - 2022-12-30
### :bug: Bug Fixes
- [`ba469a5`](https://github.com/klikli-dev/modonomicon/commit/ba469a559fa82f003e13abca46b96ac5a3deda37) - add missing model for book mod loaded condition *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.23.0] - 2022-12-30
### :sparkles: New Features
- [`88e1246`](https://github.com/klikli-dev/modonomicon/commit/88e12468cb7210c3f1b1236dc4cd1cb66ec9da76) - add mod loaded unlock condition *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`e244769`](https://github.com/klikli-dev/modonomicon/commit/e2447695102adc7ad34ccf761f03ceef33efc179) - update compatibility matrix for curseforge publishing *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.22.1] - 2022-12-29
### :bug: Bug Fixes
- [`7aa98fa`](https://github.com/klikli-dev/modonomicon/commit/7aa98fa0bc8ad20a5ae13c24fca1fee53388305e) - switch to concurrent maps where necessary *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.22.0] - 2022-12-27
### :sparkles: New Features
- [`29adaba`](https://github.com/klikli-dev/modonomicon/commit/29adabaccf98f9d8b70c8f6d33cb5a5f58d2e843) - add better error reporting to book condition checks *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`4c09da2`](https://github.com/klikli-dev/modonomicon/commit/4c09da2030c7e714a3544bb27cb887b6a4d9598d) - handle edge case where capability is updated before books are built *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`0eb3cbb`](https://github.com/klikli-dev/modonomicon/commit/0eb3cbb4d6c95c2e5a192be8bab6b6bf5da9e5b3) - reformat *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.21.1] - 2022-12-22
### :sparkles: New Features
- [`b916d1e`](https://github.com/klikli-dev/modonomicon/commit/b916d1ef5b6c024c6567d43198d02183034ae3b3) - add shortcut for cat/entry icons from item and add docs *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`f4a5dba`](https://github.com/klikli-dev/modonomicon/commit/f4a5dba17631f2cbab9db5b05b2ec17b1428e46d) - update mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.21.0] - 2022-12-21
### :boom: BREAKING CHANGES
- due to [`a64e7c9`](https://github.com/klikli-dev/modonomicon/commit/a64e7c9fe6012727c06b2f9e716f039f8999f32e) - change folder for modonomicon data. *(commit by [@klikli-dev](https://github.com/klikli-dev))*:

  /data/<modid>/modonomicons is now /data/<modid>/modonomicon/books and /data/<modid>/modonomicon_multiblocks is now /data/<modid>/modonomicon/multiblocks


### :sparkles: New Features
- [`a64e7c9`](https://github.com/klikli-dev/modonomicon/commit/a64e7c9fe6012727c06b2f9e716f039f8999f32e) - change folder for modonomicon data. *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`669d3af`](https://github.com/klikli-dev/modonomicon/commit/669d3afb4b3ae4ebbeb186fc6554349f18acc8df) - refactor source sets *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.20.0] - 2022-12-21
### :sparkles: New Features
- [`1475f92`](https://github.com/klikli-dev/modonomicon/commit/1475f923b8a5ad4f3695c0f54a4e340e583e2e31) - add shortcut for withParent from BookEntryModel *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`b5cfcd1`](https://github.com/klikli-dev/modonomicon/commit/b5cfcd14d8a06de6471a81a93fad0c2c2ecd35cc) - add shortcut for withParent from BookEntryModel.Builder *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`c22ea02`](https://github.com/klikli-dev/modonomicon/commit/c22ea0214611d1c9393cd2f1a9f8b3f3aae2c0b9) - add api sources to sources jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`2c523ea`](https://github.com/klikli-dev/modonomicon/commit/2c523eaa79db577e0f2c4ffe270cea5907164efc) - reobfuscate sources jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`55583d1`](https://github.com/klikli-dev/modonomicon/commit/55583d1316292e46a624fbe454f5600c193bd92a) - set correct from type for sources jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`ded8735`](https://github.com/klikli-dev/modonomicon/commit/ded8735dca174a626deb86bd13badbb1f76d09c1) - add docs to BookEntryModel *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`44f7d6d`](https://github.com/klikli-dev/modonomicon/commit/44f7d6dcf6f0214da78d86dea8b4fb2e201b4659) - add maven artifact id for consistent artifact output *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`d7bef0c`](https://github.com/klikli-dev/modonomicon/commit/d7bef0c1ce7aa129803a7d2dbe997d210ded6bb6) - set mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.19.2-1.19.0] - 2022-12-20
### :bug: Bug Fixes
- [`5b7611a`](https://github.com/klikli-dev/modonomicon/commit/5b7611a1445ddad42fb10d1b8cb665f152609c8c) - entry icon hover issues near book border *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`cda4d21`](https://github.com/klikli-dev/modonomicon/commit/cda4d210653a5656e0e52978563d5b3df261988d) - update workflow versions and remove old workflows *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`cbd7d55`](https://github.com/klikli-dev/modonomicon/commit/cbd7d55d4a5d37454bb6a6851287614080837bdb) - update workflow to match renamed branch *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`d99096b`](https://github.com/klikli-dev/modonomicon/commit/d99096b404b8592c7cfd82da9d0819799d539f6e) - update forge and mappings *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`39a6b02`](https://github.com/klikli-dev/modonomicon/commit/39a6b0283e3f188f49d738a4781f9ca35451f805) - disable changelog for test builds *(commit by [@klikli-dev](https://github.com/klikli-dev))*


[release/v1.19.2-1.19.0]: https://github.com/klikli-dev/modonomicon/compare/beta/v1.19.2-1.16.2...release/v1.19.2-1.19.0
[release/v1.19.2-1.20.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.19.0...release/v1.19.2-1.20.0
[release/v1.19.2-1.21.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.20.0...release/v1.19.2-1.21.0
[release/v1.19.2-1.21.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.21.0...release/v1.19.2-1.21.1
[release/v1.19.2-1.22.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.21.1...release/v1.19.2-1.22.0
[release/v1.19.2-1.22.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.22.0...release/v1.19.2-1.22.1
[release/v1.19.2-1.23.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.22.1...release/v1.19.2-1.23.0
[release/v1.19.2-1.23.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.23.0...release/v1.19.2-1.23.1
[release/v1.19.2-1.23.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.23.1...release/v1.19.2-1.23.2
[release/v1.19.2-1.24.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.23.2...release/v1.19.2-1.24.0
[release/v1.19.2-1.25.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.24.0...release/v1.19.2-1.25.0
[release/v1.19.2-1.26.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.25.0...release/v1.19.2-1.26.0
[release/v1.19.2-1.27.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.26.0...release/v1.19.2-1.27.0
[release/v1.19.2-1.28.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.27.0...release/v1.19.2-1.28.0
[release/v1.19.2-1.30.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.28.0...release/v1.19.2-1.30.0
[release/v1.19.2-1.30.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.30.0...release/v1.19.2-1.30.1
[release/v1.19.2-1.30.2]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.30.1...release/v1.19.2-1.30.2
[release/v1.19.2-1.33.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.30.2...release/v1.19.2-1.33.0
[release/v1.19.2-1.33.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.33.0...release/v1.19.2-1.33.1
[release/v1.19.2-1.34.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.19.2-1.33.1...release/v1.19.2-1.34.0