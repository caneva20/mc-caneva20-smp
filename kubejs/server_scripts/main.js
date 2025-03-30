const diamond_items = [
    { id: 1, tier: "Diamond", name: "Warped Stem" },
    { id: 2, tier: "Diamond", name: "Crimson Stem" },
    { id: 3, tier: "Diamond", name: "Oak Logs" },
    { id: 4, tier: "Diamond", name: "Birch Logs" },
    { id: 5, tier: "Diamond", name: "Jungle Logs" },
    { id: 6, tier: "Diamond", name: "Dark Oak Logs" },
    { id: 7, tier: "Diamond", name: "Acacia Logs" },
    { id: 8, tier: "Diamond", name: "Spruce Logs" },
    { id: 9, tier: "Diamond", name: "Mangrove Logs" },
    { id: 10, tier: "Diamond", name: "Cherry Logs" },
    { id: 11, tier: "Diamond", name: "Tuff & Calcite" },
    { id: 12, tier: "Diamond", name: "Prismarine" },
    { id: 13, tier: "Diamond", name: "Packed Mud (+ Mud)" },
    { id: 14, tier: "Diamond", name: "Iron" },
    { id: 15, tier: "Diamond", name: "Gold" },
    {
        id: 16, tier: "Diamond", name: "S-Tier Books",
        includes: [
            "Efficiency", "Respiration", "Silk Touch", "Depth Strider",
            "Soul Speed", "Protection", "Looting", "Mending", "Unbreaking",
            "Sharpness", "Feather Falling", "Fortune", "Swift Sneak",
            "Infinity", "Aqua Affinity"
        ]
    },
    { id: 17, tier: "Diamond", name: "All Templates" },
    { id: 18, tier: "Diamond", name: "All the Food" },
    { id: 19, tier: "Diamond", name: "Flight Rockets" },
    { id: 20, tier: "Diamond", name: "Sand & Gravel (+Suspicious)" },
    { id: 21, tier: "Diamond", name: "Honey & Slime" },
    { id: 22, tier: "Diamond", name: "All Froglights" },
    { id: 23, tier: "Diamond", name: "Copper" },
    { id: 24, tier: "Diamond", name: "Quartz" },
    { id: 25, tier: "Diamond", name: "All the Dyes & Flowers" },
    { id: 26, tier: "Diamond", name: "All the Potions" },
    { id: 27, tier: "Diamond", name: "All the Concrete" },
    {
        id: 28, tier: "Diamond", name: "Redstone Collection #1",
        includes: [
            "Redstone", "Repeater", "Comparator", "Torches",
            "Target Blocks", "Daylight Detectors", "Observers",
            "Redstone Lamps"
        ]
    },
    {
        id: 29, tier: "Diamond", name: "Redstone Collection #2",
        includes: [
            "Dispensers", "Droppers", "Hoppers", "Pistons",
            "Sticky Pistons", "Crafters", "All Sculk Sensors"
        ]
    },
    {
        id: 30, tier: "Diamond", name: "All the Coral",
        includes: ["Sea Pickles"]
    },
    { id: 31, tier: "Diamond", name: "Conduits & Beacons" },
    {
        id: 32, tier: "Diamond", name: "Tough Stuff",
        includes: [
            "Obsidian", "Crying Obsidian", "Ancient Debris",
            "Enderchests", "Respawn Anchors", "Lodestones"
        ]
    },
    { id: 33, tier: "Diamond", name: "Pale Oak Wood" },
    { id: 34, tier: "Diamond", name: "Mekanism tools" },
    { id: 35, tier: "Diamond", name: "Create machines" },
    { id: 36, tier: "Diamond", name: "Create andesite alloy" },
    { id: 37, tier: "Diamond", name: "All wool" }
];

const gold_items = [
    { id: 1, tier: "Gold", name: "Honeycomb" },
    { id: 2, tier: "Gold", name: "Deepslate" },
    { id: 3, tier: "Gold", name: "Moss" },
    { id: 4, tier: "Gold", name: "All the Horns" },
    { id: 5, tier: "Gold", name: "Nametags" },
    { id: 6, tier: "Gold", name: "All Music Discs" },
    { id: 7, tier: "Gold", name: "Tinted Glass" },
    { id: 8, tier: "Gold", name: "Shroomlights" },
    { id: 9, tier: "Gold", name: "Elytra" },
    { id: 10, tier: "Gold", name: "Rooted Dirt" },
    { id: 11, tier: "Gold", name: "Blaze Rods" },
    { id: 12, tier: "Gold", name: "Sponge" },
    { id: 13, tier: "Gold", name: "Glow Lichen" },
    { id: 14, tier: "Gold", name: "Shulker Boxes" },
    { id: 15, tier: "Gold", name: "Glass" },
    { id: 16, tier: "Gold", name: "All Kelp" },
    { id: 17, tier: "Gold", name: "Candles" },
    { id: 18, tier: "Gold", name: "Tridents" },
    { id: 19, tier: "Gold", name: "Emerald Blocks" },
    { id: 20, tier: "Gold", name: "All Mushroom Blocks" },
    { id: 21, tier: "Gold", name: "Pots and Sherds" },
    { id: 22, tier: "Gold", name: "All the Leaves" },
    { id: 23, tier: "Gold", name: "Explosive Fireworks" },
    { id: 24, tier: "Gold", name: "TNT" },
    {
        id: 25, tier: "Gold", name: "Mid Tier Books",
        includes: [
            "Loyalty", "Projectile Protection", "Impaling", "Fire Protection",
            "Blast Protection", "Smite", "Sweeping Edge", "Thorns", "Power"
        ]
    },
    { id: 26, tier: "Gold", name: "All Item Frames" },
    { id: 27, tier: "Gold", name: "Glow Ink Sacs" },
    { id: 28, tier: "Gold", name: "Bamboo" },
    { id: 29, tier: "Gold", name: "Granite" },
    { id: 30, tier: "Gold", name: "Diorite" },
    { id: 31, tier: "Gold", name: "Andesite" },
    { id: 32, tier: "Gold", name: "Bricks" },
    { id: 33, tier: "Gold", name: "Blackstone" },
    { id: 34, tier: "Gold", name: "Coarse Dirt" },
    { id: 35, tier: "Gold", name: "All Ice" },
    { id: 36, tier: "Gold", name: "Sculk & Sculk Veins" },
    { id: 37, tier: "Gold", name: "Bone Blocks" },
    { id: 38, tier: "Gold", name: "Stamps" },
    { id: 39, tier: "Gold", name: "Mace & Heavy Core" },
    { id: 40, tier: "Gold", name: "Trial Chambers" },
    { id: 41, tier: "Gold", name: "All Resin Blocks" },
    { id: 42, tier: "Gold", name: "Terracotta" },
    { id: 43, tier: "Gold", name: "Mekanism Cables" },
    { id: 44, tier: "Gold", name: "Mekanism machines" },
    { id: 45, tier: "Gold", name: "Mekanism generators" },
    { id: 46, tier: "Gold", name: "Create tools" }
];

const iron_items = [
    { id: 1, tier: "Iron", name: "Glowstone" },
    { id: 2, tier: "Iron", name: "Grass Block" },
    { id: 3, tier: "Iron", name: "Gilded Blackstone" },
    { id: 4, tier: "Iron", name: "All Banners & Patterns" },
    { id: 5, tier: "Iron", name: "Coal" },
    { id: 6, tier: "Iron", name: "Bottle of Enchanting" },
    { id: 7, tier: "Iron", name: "Cobwebs" },
    { id: 8, tier: "Iron", name: "Magma" },
    { id: 9, tier: "Iron", name: "Stone" },
    { id: 10, tier: "Iron", name: "Totem of Undying" },
    { id: 11, tier: "Iron", name: "Cobble" },
    { id: 12, tier: "Iron", name: "Purpur and Chorus Fruit" },
    { id: 13, tier: "Iron", name: "All Amethyst" },
    { id: 14, tier: "Iron", name: "All Nether Bricks" },
    { id: 15, tier: "Iron", name: "Podzol & Mycelium" },
    { id: 16, tier: "Iron", name: "All Basalt" },
    { id: 17, tier: "Iron", name: "Soul Sand & Soul Soil" },
    { id: 18, tier: "Iron", name: "Cut Grass" },
    { id: 19, tier: "Iron", name: "Nylium" },
    { id: 20, tier: "Iron", name: "Wart Blocks" },
    { id: 21, tier: "Iron", name: "All Nether Plants" },
    {
        id: 22, tier: "Iron", name: "F Tier Books",
        includes: [
            "Luck of The Sea", "Curse of Binding", "Flame", "Punch",
            "Channeling", "Quick Charge", "Curse of Vanishing", "Piercing",
            "Lure", "Frost Walker", "Multishot", "Riptide", "Fire Aspect",
            "Bane of Anthropods", "Knockback"
        ]
    },
    { id: 23, tier: "Iron", name: "Bucket of Lava" },
    { id: 24, tier: "Iron", name: "All the Bucket Mobs" },
    { id: 25, tier: "Iron", name: "All the Horse Armour" },
    { id: 26, tier: "Iron", name: "Book & Quill" },
    { id: 27, tier: "Iron", name: "Spore Blossom" },
    { id: 28, tier: "Iron", name: "Clay" },
    { id: 29, tier: "Iron", name: "Leads & Bundles" },
    { id: 30, tier: "Iron", name: "Dirt" },
    { id: 31, tier: "Iron", name: "End Rods" },
    { id: 32, tier: "Iron", name: "All Dripstone" },
    { id: 33, tier: "Iron", name: "Mossy Cobble" },
    { id: 34, tier: "Iron", name: "Bee Nests & Bee Hives" },
    { id: 35, tier: "Iron", name: "Vines" },
    { id: 36, tier: "Iron", name: "Saddles" },
    { id: 37, tier: "Iron", name: "Red Sand Products" },
    { id: 38, tier: "Iron", name: "Fern" },
    { id: 39, tier: "Iron", name: "Glow Berries" },
    { id: 40, tier: "Iron", name: "All Rails" },
    { id: 41, tier: "Iron", name: "Hay Bales" },
    { id: 42, tier: "Iron", name: "Snow & Powder Snow" },
    { id: 43, tier: "Iron", name: "All Campfires" },
    { id: 44, tier: "Iron", name: "Big & Small Dripleaf" },
    { id: 45, tier: "Iron", name: "Lily Pads" },
    { id: 46, tier: "Iron", name: "Regular Bookshelves" },
    { id: 47, tier: "Iron", name: "Chiseled Bookshelves" },
    { id: 48, tier: "Iron", name: "End Crystal" },
    { id: 49, tier: "Iron", name: "String" },
    { id: 50, tier: "Iron", name: "Endstone" }
];

ServerEvents.commandRegistry(event => {
    const { commands: Commands, arguments: Arguments } = event;

    event.register(Commands.literal('permit') // The name of the command
        .requires(s => s.hasPermission(2)) // Check if the player has operator privileges
        .then(Commands.argument('tier', Arguments.STRING.create(event))
            .executes(c => {
                const tier = Arguments.STRING.getResult(c, 'tier');

                if (tier === "iron") {
                    createItems("iron", "7", iron_items, c.source.player);
                }

                if (tier === "gold") {
                    createItems("gold", "6", gold_items, c.source.player);
                }

                if (tier === "diamond") {
                    createItems("diamond", "3", diamond_items, c.source.player);
                }

                return 1;
            })
        )
    )

    let createItems = (tier, color, items, player) => {
        items.forEach(item => {
            let itemName = `§${color}Permit: ${item.name}`;
            let permit = Item.of(`kubejs:permit_${tier}`).withCustomName(itemName);

            const lore = [];

            if (item.includes) {
                lore.push("");
                lore.push("§7Includes:");

                for (let i = 0; i < item.includes.length; i++) {
                    lore.push(`§7- ${item.includes[i]}`);
                }
            }

            lore.push(`§fID: ${item.tier}:${item.id}`);

            permit = permit.withLore(lore);

            player.give(permit);
        });
    };
});