---
sidebar_position: 50
---

# Commands

Commands are defined in json files placed in the `/data/<mod_id>/modonomicons/<book_id>/commands/` folder.  
They are intended to give book creators more flexibility. Commands can be triggered either via a [Command Link](../formatting.md#command-link) or when an entry is first read/openend(See [Entry - command_to_run_on_first_read](./entries.md#attributes)).  

Commands are guarded against abuse and by default can only be run once per player per world (even if the player resets the book).  
In the book you need to specify the command's id (ResourceLocation) instead of the actual minecraft command.

## Command IDs

The id for `/data/<mod_id>/modonomicons/my_book/commands/test_command` would be `<mod_id>:test_command`.  
The book id is not part of the command id, because the book id is taken from context (from the command link or the entry). 

For `/data/<mod_id>/modonomicons/my_book/commands/rewards/apple` the id would be `<mod_id>:rewards/apple`. 


## Attributes

### **command** (String, _mandatory_)

The minecraft command to run. Commands will be run with the player as "sender", so take that into account.

### **permission_level** (Integer, _optional_)

Defaults to `0`.    
The permission level to run the command with. This will be used instead of the actual permission level of the player.

:::tip

For most commands (such as /give) you will need at least permission level 2.

:::

:::tip

When testing and the command fails with an obscure error message (such as: incomplete command), despite the same command working fine when running it in chat, try to increase the permission level up to 4.

:::

### **max_usages** (Integer, _optional_)

Defaults to `-1`.   
The maximum amount of times the command can be used. Modonomicon will keep track of this per command json, independent of how the command is triggered. Resetting the modonomicon will not reset the command count. 

:::tip

This ensures that commands cannot be abused to cheat a reward multiple times.

:::

### **failure_message** (DescriptionId, _optional_)

A custom failure message to display, if the command has been used beyond max_usages already.  
I none is provided, a default message will be shown.
Other failures (such as invalid command syntax) are handled by minecraft and have a failure message rom there.   

:::tip

In most cases this message will never show. One of the possible scenarios is with an entry read command, if the book gets reset and the player reads the entry again. The command will not execute again, and instead this message (or the default) will show.

:::

### **success_message** (DescriptionId, _optional_)

A custom success message to display, if the command has executed successfully. 
I none is provided, **no** message will be shown.

:::tip

This can be used to e.g. let the player know why they suddenly have a new item in their inventory.

:::


## Usage Examples

`/data/<mod_id>/modonomicons/<book_id>/commands/test_command.json`:

```json 
{
  "command": "/give @s minecraft:apple 1",
  "max_uses": 1,
  "permission_level": 2,
  "success_message": "modonomicon.command.test_command.success"
}
```

`/lang/*.json`:
```json
{
    "modonomicon.command.test_command.success": "You got an apple, because reading is cool!",
}
```