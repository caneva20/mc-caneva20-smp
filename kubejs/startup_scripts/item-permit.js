StartupEvents.registry('item', (event) => {
    // Add item to the permit list
    event.create('permit_diamond').unstackable().displayName('Diamond Permit').rarity("epic");
    event.create('permit_gold').unstackable().displayName('Gold Permit').rarity("rare");
    event.create('permit_iron').unstackable().displayName('Iron Permit').rarity("uncommon");
});