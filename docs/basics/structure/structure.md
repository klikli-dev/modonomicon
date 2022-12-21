---
sidebar_position: 10
---

# Book Structure

Each Modonomicon Book consists of Categories, Entries and Pages. Entries are grouped into Categories, which are grouped into the Book itself. Pages are part of the Entries and represent the actual content of the book, they can be used to display text, images, recipes and more.

The file structure looks as follows:

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

<Tabs>
  <TabItem value="hierarchy" label="Hierarchy" default>

- `data/<mod_id>/modonomicon/books/<book_id>/`
  - `book.json`
  - `categories/`
    - `<category_id1>.json`
    - `<category_id2>.json`
    - ...
  - `entries/`
    - `<category_id1>`
      - `<entry_id1>.json`
      - `<entry_id2>.json`
      - ...
    - `<category_id2>`
      - `<entry_id3>.json`
      - `<entry_id4>.json`
      - ...


  </TabItem>

  <TabItem value="screenshot" label="Example Screenshot">

A book with the book id `demo` and the mod id `modonomicon` would look like this:

![File Structure](/img/docs/basics/structure/files.png)

  </TabItem>
</Tabs>

## Book.json


The `book.json` file contains the general settings for the book. It is located in the root of the book folder.   
See [Book.json](./book) for details.

## Categories

![Categories](/img/docs/basics/structure/categories.png)

Categories are a type of "Quest/Advancement"-style 2D view. A book consists of one or more categories, and each category can contain multiple entries that can be linked to each other.   
See [Categories](./categories) for details.

## Entries

![Entries](/img/docs/basics/structure/entries.png)

Each entry is part of one category and consists of multiple pages, and may be linked to other entries.   
See [Entries](./entries) for details.

## Pages

![Pages](/img/docs/basics/structure/pages.png)

Pages are placed inside entries and represent the actual content of the book. They can be used to display text, images, recipes and more.   
See [Page Types](../page-types) for details.
