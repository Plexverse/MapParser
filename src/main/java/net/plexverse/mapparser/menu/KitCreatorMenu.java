package net.plexverse.mapparser.menu;

/***
public class KitCreatorMenu extends Menu {
    private String kitName;
    private GameType gameType;

    private static final ObjectMapper OBJECT_MAPPER;
    private static final InputAsker KIT_NAME_ASKER = new InputAsker("<light_purple>Please type a name for the kit");

    public KitCreatorMenu(Player player) {
        super(player, Menus.KIT_CREATOR);
        this.replacer.replaceWithSupplier("%kit_name%", true, () -> this.kitName == null ? "N/A" : this.kitName)
            .replaceWithSupplier("%game%", true, () -> this.gameType == null ? "N/A" : this.gameType.getDisplayName());

        this.onClick();
    }

    private void onClick() {
        this.onClick("kit-name", (event) -> {
            this.close();
            KIT_NAME_ASKER.ask(this.player, (response) -> {
                this.kitName = response;
                this.open();
            });
        });

        this.onClick("game-type", (event) -> {
            this.gameType = GameType.getNextGameType(this.gameType);
            this.executeAction(MenuAction.REFRESH, null);
        });

        this.onClick("create", (event) -> {
            this.close();
            if (this.kitName == null) {
                this.player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                this.player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You must specify a name for the kit!"));
                return;
            }

            final boolean hasGameType = this.gameType != null;
            final String dirName = "kits/" + (hasGameType ? this.gameType.name() + "/" : "") + this.kitName.toLowerCase(Locale.ROOT).replace(" ", "_") + ".json";
            final Map<Integer, ItemStack> items = new HashMap<>();
            for (int slot = 0; slot < this.player.getInventory().getSize(); slot++) {
                final ItemStack itemStack = this.player.getInventory().getItem(slot);
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }

                items.put(slot, itemStack);
            }

            final File file = new File(Bukkit.getWorldContainer(), dirName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try {
                OBJECT_MAPPER.writer().writeValue(file, items);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            this.player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>You have created a kit named "
                    + this.kitName
                    + (hasGameType ? " for game " + this.gameType.getDisplayName() : "") + ". "
                    + "It has been output to a file in the main directory "
                    + dirName));
        });
    }

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.registerModule(new MineplexJacksonModule());
    }
}***/