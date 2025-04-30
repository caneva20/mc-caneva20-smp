StartupEvents.registry('item', (event) => {
  event.create("real")
    .displayName("Real")
    .parentModel("kubejs:item/real")
    .rarity("uncommon")
    .tag('c20:money')
    ;
});

ItemEvents.modification(event => {
  event.modify("kubejs:real", item => {
    item.setCustomName(`§a1 Real`);
  });
});