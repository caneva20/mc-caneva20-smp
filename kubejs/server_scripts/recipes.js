// Stick 16
// 2 Logs
ServerEvents.recipes((event) => {
  event.shaped(Item.of("minecraft:stick", 16), ["A", "A"], {
    A: "#minecraft:logs",
  });
});

// Chest
// 8 Logs
ServerEvents.recipes((event) => {
  event.shaped(Item.of("minecraft:chest", 4), ["AAA", "A A", "AAA"], {
    A: "#minecraft:logs",
  });
});

// Tuff
// 100mb Lava + 100mb Water + 1 Gravel + 2 Flint = 1 Tuff
ServerEvents.recipes((event) => {
  event.recipes.create.mixing(
    [Item.of("minecraft:tuff")],
    [
      Fluid.of("minecraft:lava", 100),
      Fluid.of("minecraft:water", 100),
      Item.of("minecraft:gravel"),
      Item.of("minecraft:flint", 2),
    ],
  );
});

// Cobblestone
// 100mb Lava + 100mb Water = 1 Cobblestone
ServerEvents.recipes((event) => {
  event.recipes.create
    .mixing(
      [Item.of("minecraft:cobblestone")],
      [Fluid.of("minecraft:lava", 100), Fluid.of("minecraft:water", 100)],
    )
    .heated();
});
