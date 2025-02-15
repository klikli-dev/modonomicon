---
sidebar_position: 20
---

# Step 3: Copy demo content into your datapack


1. In your file explorer go to the folder you extracted and then into `<extracted_folder>\neo\src\generated\resources\data\modonomicon\modonomicon\books\`.

2. Copy the `demo` folder (or if you want to make a leaflet, the `demo_leaflet` folder) and paste it in your datapack's `/modonomicon/books/` folder.

3. In your file explorer go to the folder you extracted and then into `<extracted_folder>\neo\src\generated\resources\data\modonomicon\modonomicon\multiblocks\`.

4. Copy all `.json` files and paste them in your datapack's `/modonomicon/multiblocks/` folder.
   :::info
        If you do not plan on having any multiblocks or multiblock pages, you can skip copying the contents of the `/multiblocks/` folder.
   :::

5. If you **did not** set up multiblocks, go into your datapack folder `<your datapack>/data/<your namespace>/modonomicon/books/demo/entries/features/` and **delete** the `multiblock.json` file.
   :::info
        If you do not delete that file, this multiblock entry will look for the multiblocks in the `/multiblocks/` folder and will throw an error if it does not find them.
   :::   