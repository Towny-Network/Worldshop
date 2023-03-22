# Worldshop

Todo:
- Build shop GUIs
- Build core trading functionality
- Implement ability to use MySQL and SQLite
- Prevent items from being duped by being sold twice (sell detection)
- (Maybe) Make nation options like front page nation items 
- Some customizable options (inventory texture, thank you messages, etc).
- Simple database lookup website for staff to use to confirm trades n such
- A discord bot that does the same as above ^^


## Error Codes

> All WorldShop Error Codes are Prefixed with "WS". (i.e. WS0000)

- 0001: While buying an item, the player did not have enough items to proceed w/ purchase even though it was previously checked that they did.
- 0002: Player attemtped to sell an item without it being in their inventory.
- 0003: No trade was found while searching w/ display item.