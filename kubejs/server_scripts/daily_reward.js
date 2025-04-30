const DataFilePath = "kubejs/data/daily_rewards.json";

const LocalDate = Java.loadClass("java.time.LocalDate");
const LocalDateTime = Java.loadClass("java.time.LocalDateTime");
const DateTimeFormatter = Java.loadClass("java.time.format.DateTimeFormatter");
const ZoneId = Java.loadClass("java.time.ZoneId");

const maxReward = 640;
const rewardPerDay = 64;

PlayerEvents.loggedIn(onPlayerJoin);

ServerEvents.commandRegistry(event => {
  const { commands: Commands, literal } = event;

  event.register(Commands.literal("recompensa").executes(ctx => {
    try {
      let player = ctx.source.player;
      if (!player) return 0;

      executeDailyCommand(player);

      return 1;

    } catch (err) {
      console.log("[/recompensa] Error:", err);
      ctx.source.sendFailure("§cError running daily command: " + err.message);
      return 0;
    }
  }));
});

function onPlayerJoin(event) {
  let player = event.player;

  let playerData = readPlayerData(player.uuid);
  let today = getTodayString();
  let lastClaim = playerData.lastClaim;
  let rewarded = playerData.rewarded;

  let alreadyClaimed = lastClaim === today;

  if (alreadyClaimed) {
    return;
  }

  if (rewarded >= maxReward) {
    return;
  }

  player.tell("§8═══════════════════════════════════");
  player.tell("§6§l            Ajuda de custo diária");
  player.tell("");
  player.tell("§a§lVocê tem uma recompensa diária disponível!");
  player.tell("");
  player.tell(Text.of("Clique aqui para receber sua recompensa.").aqua().clickRunCommand("/recompensa"));
  player.tell("§8═══════════════════════════════════");
}

function executeDailyCommand(player) {
  let playerData = readPlayerData(player.uuid);
  let today = getTodayString();
  let lastClaim = playerData.lastClaim;
  let rewarded = playerData.rewarded;

  let alreadyClaimed = lastClaim === today;

  player.tell("§8═══════════════════════════════════");
  player.tell("§6§l            Ajuda de custo diária");
  player.tell("");
  player.tell("Data de hoje: §b" + today);
  player.tell("Última recompensa: §b" + (lastClaim ?? "Nunca"));
  player.tell("");

  if (rewarded >= maxReward) {
    player.tell("§cVocê já atingiu o limite de recompensa diária.");
  } else if (alreadyClaimed) {
    player.tell("§cVocê já pegou sua recompensa de hoje.");
    player.tell(`Restante: §b R$${maxReward - playerData.rewarded}`);
    let timeLeft = getTimeUntilMidnight();
    player.tell(`§7🕒 Próxima recompensa disponível em: §f${timeLeft}`);
  } else {
    player.give(Item.of("kubejs:real", rewardPerDay));

    playerData.lastClaim = today;
    playerData.rewarded += rewardPerDay;

    player.tell(`§aVocê recebeu R$${rewardPerDay} de recompensa! Restante: §bR$${(maxReward - playerData.rewarded)}`);
  }

  player.tell("§8═══════════════════════════════════");

  writePlayerData(player.uuid, playerData);
}

function readPlayerData(playerId) {
  let rawData = JsonIO.read(DataFilePath);

  return rawData[playerId] || { lastClaim: null, rewarded: 0 };
}

function writePlayerData(playerId, data) {
  let rawData = JsonIO.read(DataFilePath);

  rawData[playerId] = data;

  JsonIO.write(DataFilePath, rawData);
}

function getTodayString() {
  let formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  return LocalDate.now().format(formatter);
}

function getTimeUntilMidnight() {
  let now = LocalDateTime.now();
  let tomorrowMidnight = now.toLocalDate().plusDays(1).atStartOfDay();

  let duration = Duration.between(now, tomorrowMidnight);
  let hours = duration.toHours();
  let minutes = duration.toMinutes() % 60;

  return `${hours}h ${minutes}m`;
}
