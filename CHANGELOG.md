# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [release/v1.18.2-1.22.1] - 2022-12-29
### :bug: Bug Fixes
- [`8dfec5f`](https://github.com/klikli-dev/modonomicon/commit/8dfec5f6dd8524886b55dfe9421c61e43ed2ba3e) - switch to concurrent maps where necessary *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.18.2-1.22.0] - 2022-12-27
### :sparkles: New Features
- [`adb6005`](https://github.com/klikli-dev/modonomicon/commit/adb600589b61aacc05275018f4fe6d50b7e3d2ae) - add better error reporting to book condition checks *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`d73222d`](https://github.com/klikli-dev/modonomicon/commit/d73222d98c385d3ae7e8f41b81d2cba64a1aee2b) - handle edge case where capability is updated before books are built *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`37febee`](https://github.com/klikli-dev/modonomicon/commit/37febeeb3f1dcf426bc0317871a074303f69cb96) - reformat *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.18.2-1.21.1] - 2022-12-22
### :sparkles: New Features
- [`6084eee`](https://github.com/klikli-dev/modonomicon/commit/6084eee3a985cd6c7e5c5989594040bb32cb78f2) - add shortcut for cat/entry icons from item and add docs *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`615dd77`](https://github.com/klikli-dev/modonomicon/commit/615dd771423eba13ffae222e165430bf5238fd77) - update mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.18.2-1.21.0] - 2022-12-21
### :boom: BREAKING CHANGES
- due to [`34daaa4`](https://github.com/klikli-dev/modonomicon/commit/34daaa4ccf5dc9aeba476eaf51a33175084cc159) - change folder for modonomicon data. *(commit by [@klikli-dev](https://github.com/klikli-dev))*:

  /data/<modid>/modonomicons is now /data/<modid>/modonomicon/books and /data/<modid>/modonomicon_multiblocks is now /data/<modid>/modonomicon/multiblocks


### :sparkles: New Features
- [`29466d9`](https://github.com/klikli-dev/modonomicon/commit/29466d95004069822920c27bc7df0a13fbbfcd53) - add shortcut for withParent from BookEntryModel *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`e0794db`](https://github.com/klikli-dev/modonomicon/commit/e0794db86ced6a7ac0b6967a127958dd298e3eb6) - add shortcut for withParent from BookEntryModel.Builder *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`34daaa4`](https://github.com/klikli-dev/modonomicon/commit/34daaa4ccf5dc9aeba476eaf51a33175084cc159) - change folder for modonomicon data. *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`4360f84`](https://github.com/klikli-dev/modonomicon/commit/4360f8446b6c4e5707ceb19354bd922b81192635) - refactor source sets *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :bug: Bug Fixes
- [`b751858`](https://github.com/klikli-dev/modonomicon/commit/b751858bee1bf078e07da0c32f760de5603cbf83) - add api sources to sources jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`2df7624`](https://github.com/klikli-dev/modonomicon/commit/2df762446d02854e68cead9c635d037a5a244b9e) - reobfuscate sources jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`e8ec309`](https://github.com/klikli-dev/modonomicon/commit/e8ec309e624a94ddc72cebe86659d4ca7deb56f7) - set correct from type for sources jar *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`9e5b119`](https://github.com/klikli-dev/modonomicon/commit/9e5b1190a6bf1ff107b589c6cfc80b43189c60dc) - add docs to BookEntryModel *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`c9a43db`](https://github.com/klikli-dev/modonomicon/commit/c9a43dbd94737360721db92d46273e02b2b91d81) - add maven artifact id for consistent artifact output *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`c5d58a2`](https://github.com/klikli-dev/modonomicon/commit/c5d58a2d0ebca5011f4fbf0df57e301b94781223) - set mod version *(commit by [@klikli-dev](https://github.com/klikli-dev))*


## [release/v1.18.2-1.19.0] - 2022-12-20
### :bug: Bug Fixes
- [`fce4562`](https://github.com/klikli-dev/modonomicon/commit/fce456241fa766e9a570b535e397adc6b3a877d8) - entry icon hover issues near book border *(commit by [@klikli-dev](https://github.com/klikli-dev))*

### :wrench: Chores
- [`43bb2e0`](https://github.com/klikli-dev/modonomicon/commit/43bb2e0bfd21b64a24ed66c2f07d3c3bc805dffb) - update workflow versions and remove old workflows *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`f1112aa`](https://github.com/klikli-dev/modonomicon/commit/f1112aa9afbc226f9446112a256d29687ad4dd40) - disable changelog for test builds *(commit by [@klikli-dev](https://github.com/klikli-dev))*
- [`539f4d6`](https://github.com/klikli-dev/modonomicon/commit/539f4d62bdc0f3f532d020f949930def91effaed) - update to latest forge and gradle *(commit by [@klikli-dev](https://github.com/klikli-dev))*


[release/v1.18.2-1.19.0]: https://github.com/klikli-dev/modonomicon/compare/beta/v1.18.2-1.16.2...release/v1.18.2-1.19.0
[release/v1.18.2-1.21.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.18.2-1.19.0...release/v1.18.2-1.21.0
[release/v1.18.2-1.21.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.18.2-1.21.0...release/v1.18.2-1.21.1
[release/v1.18.2-1.22.0]: https://github.com/klikli-dev/modonomicon/compare/release/v1.18.2-1.21.1...release/v1.18.2-1.22.0
[release/v1.18.2-1.22.1]: https://github.com/klikli-dev/modonomicon/compare/release/v1.18.2-1.22.0...release/v1.18.2-1.22.1