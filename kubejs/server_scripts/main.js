const diamond_items = [
    { name: "Warped Stem" },
    { name: "Crimson Stem" },
    { name: "Oak" },
    { name: "Birch Logs" },
    { name: "Jungle" },
    { name: "Dark Oak Logs" },
    { name: "Acacia" },
    { name: "Spruce Logs" },
    { name: "Mangrove" },
    { name: "Cherry Logs" },
    { name: "Tuff & Calcite" },
    { name: "Prismarine" },
    { name: "Packed Mud (+ Mud)" },
    { name: "Iron" },
    { name: "Gold" },
    {
        name: "S-Tier Books",
        includes: [
            "Efficiency", "Respiration", "Silk Touch", "Depth Strider",
            "Soul Speed", "Protection", "Looting", "Mending", "Unbreaking",
            "Sharpness", "Feather Falling", "Fortune", "Swift Sneak",
            "Infinity", "Aqua Affinity"
        ]
    },
    { name: "All Templates" },
    { name: "All the Food" },
    { name: "Flight Rockets" },
    { name: "Sand & Gravel (+Suspicious)" },
    { name: "Honey & Slime" },
    { name: "All Froglights" },
    { name: "Copper" },
    { name: "Quartz" },
    { name: "All the Dyes & Flowers" },
    { name: "All the Potions" },
    { name: "All the Concrete" },
    {
        name: "Redstone Collection #1",
        includes: [
            "Redstone", "Repeater", "Comparator", "Torches",
            "Target Blocks", "Daylight Detectors", "Observers",
            "Redstone Lamps"
        ]
    },
    {
        name: "Redstone Collection #2",
        includes: [
            "Dispensers", "Droppers", "Hoppers", "Pistons",
            "Sticky Pistons", "Crafters", "All Sculk Sensors"
        ]
    },
    {
        name: "All the Coral",
        includes: ["Sea Pickles"]
    },
    { name: "Conduits & Beacons" },
    {
        name: "Tough Stuff",
        includes: [
            "Obsidian", "Crying Obsidian", "Ancient Debris",
            "Enderchests", "Respawn Anchors", "Lodestones"
        ]
    },
    { name: "Pale Oak Wood" },
    { name: "Mekanism tools" },
    { name: "Create machines" },
    { name: "Create andesite alloy" },
    { name: "All wool" }
];

const gold_items = [
    { name: "Honeycomb" },
    { name: "Deepslate" },
    { name: "Moss" },
    { name: "All the Horns" },
    { name: "Nametags" },
    { name: "All Music Discs" },
    { name: "Tinted Glass" },
    { name: "Shroomlights" },
    { name: "Elytra" },
    { name: "Rooted Dirt" },
    { name: "Blaze Rods" },
    { name: "Sponge" },
    { name: "Glow Lichen" },
    { name: "Shulker Boxes" },
    { name: "Glass" },
    { name: "All Kelp" },
    { name: "Candles" },
    { name: "Tridents" },
    { name: "Emerald Blocks" },
    { name: "All Mushroom Blocks" },
    { name: "Pots and Sherds" },
    { name: "All the Leaves" },
    { name: "Explosive Fireworks" },
    { name: "TNT" },
    {
        name: "Mid Tier Books",
        includes: [
            "Loyalty", "Projectile Protection", "Impaling", "Fire Protection",
            "Blast Protection", "Smite", "Sweeping Edge", "Thorns", "Power"
        ]
    },
    { name: "All Item Frames" },
    { name: "Glow Ink Sacs" },
    { name: "Bamboo" },
    { name: "Granite" },
    { name: "Diorite" },
    { name: "Andesite" },
    { name: "Bricks" },
    { name: "Blackstone" },
    { name: "Coarse Dirt" },
    { name: "All Ice" },
    { name: "Sculk & Sculk Veins" },
    { name: "Bone Blocks" },
    { name: "Stamps" },
    { name: "Mace & Heavy Core" },
    { name: "Trial Chambers" },
    { name: "All Resin Blocks" },
    { name: "Terracotta" },
    { name: "Mekanism Cables" },
    { name: "Mekanism machines" },
    { name: "Mekanism generators" },
    { name: "Create tools" }
];

const iron_items = [
    { name: "Glowstone" },
    { name: "Grass Block" },
    { name: "Gilded Blackstone" },
    { name: "All Banners & Patterns" },
    { name: "Coal" },
    { name: "Bottle of Enchanting" },
    { name: "Cobwebs" },
    { name: "Magma" },
    { name: "Stone" },
    { name: "Totem of Undying" },
    { name: "Cobble" },
    { name: "Purpur and Chorus Fruit" },
    { name: "All Amethyst" },
    { name: "All Nether Bricks" },
    { name: "Podzol & Mycelium" },
    { name: "All Basalt" },
    { name: "Soul Sand & Soul Soil" },
    { name: "Cut Grass" },
    { name: "Nylium" },
    { name: "Wart Blocks" },
    { name: "All Nether Plants" },
    {
        name: "F Tier Books",
        includes: [
            "Luck of The Sea", "Curse of Binding", "Flame", "Punch",
            "Channeling", "Quick Charge", "Curse of Vanishing", "Piercing",
            "Lure", "Frost Walker", "Multishot", "Riptide", "Fire Aspect",
            "Bane of Anthropods", "Knockback"
        ]
    },
    { name: "Bucket of Lava" },
    { name: "All the Bucket Mobs" },
    { name: "All the Horse Armour" },
    { name: "Book & Quill" },
    { name: "Spore Blossom" },
    { name: "Clay" },
    { name: "Leads & Bundles" },
    { name: "Dirt" },
    { name: "End Rods" },
    { name: "All Dripstone" },
    { name: "Mossy Cobble" },
    { name: "Bee Nests & Bee Hives" },
    { name: "Vines" },
    { name: "Saddles" },
    { name: "Red Sand Products" },
    { name: "Fern" },
    { name: "Glow Berries" },
    { name: "All Rails" },
    { name: "Hay Bales" },
    { name: "Snow & Powder Snow" },
    { name: "All Campfires" },
    { name: "Big & Small Dripleaf" },
    { name: "Lily Pads" },
    { name: "Regular Bookshelves" },
    { name: "Chiseled Bookshelves" },
    { name: "End Crystal" },
    { name: "String" },
    { name: "Endstone" }
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

            // Check if the item has "includes" and add a tooltip
            if (item.includes) {
                const lore = [
                    "",
                    "§7Includes:"
                ];

                for (let i = 0; i < item.includes.length; i++) {
                    lore.push(`§7- ${item.includes[i]}`);
                }

                permit = permit.withLore(lore);
            }

            player.give(permit);
        });
    };
});