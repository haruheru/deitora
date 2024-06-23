package plugin.sample5;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sample5 extends JavaPlugin implements Listener {

  private BigInteger totalSneakCount = BigInteger.ZERO;

  @Override
  public void onEnable() {
    // イベントリスナーを登録
    Bukkit.getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onPlayerToggleSneak(PlayerToggleSneakEvent e) throws IOException {
    Player player = e.getPlayer();
    World world = player.getWorld();

    // しゃがみ回数をインクリメント
    totalSneakCount = totalSneakCount.add(BigInteger.ONE);

    // 合計しゃがみ回数をコンソールにログ出力
    Bukkit.getLogger().info("合計スニーク回数: " + totalSneakCount);

    // プレイヤーに合計しゃがみ回数を通知
    player.sendMessage("現在の合計スニーク回数: " + totalSneakCount);

    List<Color> colorList = List.of(Color.RED, Color.BLUE, Color.WHITE, Color.BLACK);

    // しゃがみ回数が確率的に素数かどうかをチェック
    if (totalSneakCount.isProbablePrime(10)) {
      // 確率的に素数であれば花火を打ち上げる
      for (Color color : colorList) {
        spawnFirework(world, player.getLocation(), color);

        Path path = Path.of("firework.txt");
        Files.writeString(path, "たーまやー", StandardOpenOption.APPEND);
        player.sendMessage(Files.readString(path));
      }
    }
  }


  private void spawnFirework(World world, Location location, Color color) {
    Firework firework = world.spawn(location, Firework.class);
    FireworkMeta fireworkMeta = firework.getFireworkMeta();
    fireworkMeta.addEffect(FireworkEffect.builder()
        .withColor(color)
        .with(Type.STAR)
        .withFlicker()
        .build());
    fireworkMeta.setPower(3 * 3 / 3 - 1);
    firework.setFireworkMeta(fireworkMeta);

    // オプションでコンソールやプレイヤーに通知する
    Bukkit.getLogger().info("花火が " + location.toString() + " で打ち上げられました！");
    location.getWorld().getPlayers().forEach(p -> p.sendMessage("花火が打ち上げられました！"));
  }

  @EventHandler
  public void onPlayerBedEnter(PlayerBedEnterEvent e) {
    Player player = e.getPlayer();
    ItemStack[] itemStacks = player.getInventory().getContents();
    for (ItemStack item : itemStacks) {
      if (!Objects.isNull(item) && item.getMaxStackSize() == 64 && item.getAmount() < 64) {
        item.setAmount(64);
      }
    }
    player.getInventory().setContents(itemStacks);
  }
}

