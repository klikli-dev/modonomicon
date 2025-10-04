---
sidebar_position: 50
---

# Leaflets

Leaflets are single-entry books. That means there is no book overview, if you click the book you immediately see text.
As a leaflet consists of an entry, it can still hold multiple pages, and within these pages use all features available to entries/pages in a normal book.

The book will be treated as a book in index mode (that means, no big "node view" background will be shown behind the entry).

## Creating a leaflet

To create a leaflet book, you need a normal book structure including `book.json`, a single category, and within that category an entry.
The book needs to have `leaflet_entry` set to the ResourceLocation of the single entry in the single category. 
Additional content can be added to the book, but will not be displayed. 

The best way to create a leaflet is to use the leaflet datagen. It is demonstrated in (and can be copied from) https://github.com/klikli-dev/modonomicon/tree/version/1.21/common/src/main/java/com/klikli_dev/modonomicon/datagen/book/DemoLeaflet.java