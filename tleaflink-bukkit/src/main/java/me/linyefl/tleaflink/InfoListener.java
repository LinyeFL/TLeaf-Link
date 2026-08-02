package me.linyefl.tleaflink;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InfoListener implements Listener {

    private final TLeafLink plugin;

    /** 进度命名空间ID -> 官方中文译名 */
    private static final Map<String, String> ADVANCEMENT_NAMES = new HashMap<>();

    static {
        // ===== Minecraft（16）=====
        ADVANCEMENT_NAMES.put("story/root", "Minecraft");
        ADVANCEMENT_NAMES.put("story/mine_stone", "石器时代");
        ADVANCEMENT_NAMES.put("story/upgrade_tools", "获得升级");
        ADVANCEMENT_NAMES.put("story/smelt_iron", "来硬的");
        ADVANCEMENT_NAMES.put("story/iron_tools", "这不是铁镐么？");
        ADVANCEMENT_NAMES.put("story/lava_bucket", "热腾腾的");
        ADVANCEMENT_NAMES.put("story/obtain_armor", "整装上阵");
        ADVANCEMENT_NAMES.put("story/mine_diamond", "钻石！");
        ADVANCEMENT_NAMES.put("story/form_obsidian", "冰桶挑战");
        ADVANCEMENT_NAMES.put("story/deflect_arrow", "不吃这套，谢谢");
        ADVANCEMENT_NAMES.put("story/enchant_item", "附魔师");
        ADVANCEMENT_NAMES.put("story/shiny_gear", "钻石护体");
        ADVANCEMENT_NAMES.put("story/enter_the_nether", "勇往直下");
        ADVANCEMENT_NAMES.put("story/cure_zombie_villager", "僵尸科医生");
        ADVANCEMENT_NAMES.put("story/follow_ender_eye", "隔墙有眼");
        ADVANCEMENT_NAMES.put("story/enter_the_end", "结束了？");

        // ===== 下界（23）=====
        ADVANCEMENT_NAMES.put("nether/root", "下界");
        ADVANCEMENT_NAMES.put("nether/distract_piglin", "金光闪闪");
        ADVANCEMENT_NAMES.put("nether/fast_travel", "曲速泡");
        ADVANCEMENT_NAMES.put("nether/find_bastion", "光辉岁月");
        ADVANCEMENT_NAMES.put("nether/find_fortress", "阴森的要塞");
        ADVANCEMENT_NAMES.put("nether/obtain_ancient_debris", "深藏不露");
        ADVANCEMENT_NAMES.put("nether/obtain_crying_obsidian", "谁在切洋葱？");
        ADVANCEMENT_NAMES.put("nether/return_to_sender", "见鬼去吧");
        ADVANCEMENT_NAMES.put("nether/ride_strider", "画船添足");
        ADVANCEMENT_NAMES.put("nether/loot_bastion", "战猪");
        ADVANCEMENT_NAMES.put("nether/get_wither_skull", "惊悚恐怖骷髅头");
        ADVANCEMENT_NAMES.put("nether/obtain_blaze_rod", "与火共舞");
        ADVANCEMENT_NAMES.put("nether/netherite_armor", "残骸裹身");
        ADVANCEMENT_NAMES.put("nether/charge_respawn_anchor", "锚没有九条命");
        ADVANCEMENT_NAMES.put("nether/uneasy_alliance", "脆弱的同盟");
        ADVANCEMENT_NAMES.put("nether/explore_nether", "热门景点");
        ADVANCEMENT_NAMES.put("nether/ride_strider_in_overworld_lava", "温暖如家");
        ADVANCEMENT_NAMES.put("nether/summon_wither", "凋零山庄");
        ADVANCEMENT_NAMES.put("nether/brew_potion", "本地酿造厂");
        ADVANCEMENT_NAMES.put("nether/create_beacon", "带信标回家");
        ADVANCEMENT_NAMES.put("nether/all_potions", "狂乱的鸡尾酒");
        ADVANCEMENT_NAMES.put("nether/create_full_beacon", "信标工程师");
        ADVANCEMENT_NAMES.put("nether/all_effects", "为什么会变成这样呢？");

        // ===== 末地（9）=====
        ADVANCEMENT_NAMES.put("end/root", "末地");
        ADVANCEMENT_NAMES.put("end/kill_dragon", "解放末地");
        ADVANCEMENT_NAMES.put("end/dragon_breath", "你需要来点薄荷糖");
        ADVANCEMENT_NAMES.put("end/dragon_egg", "下一世代");
        ADVANCEMENT_NAMES.put("end/enter_end_gateway", "远程折跃");
        ADVANCEMENT_NAMES.put("end/respawn_dragon", "结束了…再一次…");
        ADVANCEMENT_NAMES.put("end/find_end_city", "在游戏尽头的城市");
        ADVANCEMENT_NAMES.put("end/elytra", "天空即为极限");
        ADVANCEMENT_NAMES.put("end/levitate", "这上面的风景不错");

        // ===== 冒险（47）=====
        ADVANCEMENT_NAMES.put("adventure/root", "冒险");
        ADVANCEMENT_NAMES.put("adventure/avoid_vibration", "潜行100级");
        ADVANCEMENT_NAMES.put("adventure/crafters_crafting_crafters", "合成器合成合成器");
        ADVANCEMENT_NAMES.put("adventure/fall_from_world_height", "上天入地");
        ADVANCEMENT_NAMES.put("adventure/heart_transplanter", "移心接木");
        ADVANCEMENT_NAMES.put("adventure/honey_block_slide", "胶着状态");
        ADVANCEMENT_NAMES.put("adventure/kill_a_mob", "怪物猎人");
        ADVANCEMENT_NAMES.put("adventure/lightning_rod_with_villager_no_fire", "电涌保护器");
        ADVANCEMENT_NAMES.put("adventure/minecraft_trials_edition", "Minecraft：试炼版");
        ADVANCEMENT_NAMES.put("adventure/ol_betsy", "扣下悬刀");
        ADVANCEMENT_NAMES.put("adventure/read_power_of_chiseled_bookshelf", "知识就是力量");
        ADVANCEMENT_NAMES.put("adventure/brush_armadillo", "这不是鳞甲么？");
        ADVANCEMENT_NAMES.put("adventure/salvage_sherd", "探古寻源");
        ADVANCEMENT_NAMES.put("adventure/sleep_in_bed", "甜蜜的梦");
        ADVANCEMENT_NAMES.put("adventure/spyglass_at_parrot", "那是鸟吗？");
        ADVANCEMENT_NAMES.put("adventure/trade", "成交！");
        ADVANCEMENT_NAMES.put("adventure/trim_with_any_armor_pattern", "旧貌锻新颜");
        ADVANCEMENT_NAMES.put("adventure/voluntary_exile", "自我放逐");
        ADVANCEMENT_NAMES.put("adventure/use_lodestone", "天涯共此石");
        ADVANCEMENT_NAMES.put("adventure/kill_all_mobs", "资深怪物猎人");
        ADVANCEMENT_NAMES.put("adventure/kill_mob_near_sculk_catalyst", "它蔓延了");
        ADVANCEMENT_NAMES.put("adventure/shoot_arrow", "瞄准目标");
        ADVANCEMENT_NAMES.put("adventure/throw_trident", "抖包袱");
        ADVANCEMENT_NAMES.put("adventure/totem_of_undying", "超越生死");
        ADVANCEMENT_NAMES.put("adventure/spear_many_mobs", "生物串串香");
        ADVANCEMENT_NAMES.put("adventure/blowback", "逆风翻盘");
        ADVANCEMENT_NAMES.put("adventure/lighten_up", "铜光散发");
        ADVANCEMENT_NAMES.put("adventure/overoverkill", "天赐良击");
        ADVANCEMENT_NAMES.put("adventure/under_lock_and_key", "珍藏密敛");
        ADVANCEMENT_NAMES.put("adventure/who_needs_rockets", "还要啥火箭啊？");
        ADVANCEMENT_NAMES.put("adventure/arbalistic", "劲弩手");
        ADVANCEMENT_NAMES.put("adventure/two_birds_one_arrow", "一箭双雕");
        ADVANCEMENT_NAMES.put("adventure/whos_the_pillager_now", "现在谁才是掠夺者？");
        ADVANCEMENT_NAMES.put("adventure/craft_decorated_pot_using_only_sherds", "精修细补");
        ADVANCEMENT_NAMES.put("adventure/adventuring_time", "探索的时光");
        ADVANCEMENT_NAMES.put("adventure/play_jukebox_in_meadows", "音乐之声");
        ADVANCEMENT_NAMES.put("adventure/walk_on_powder_snow_with_leather_boots", "轻功雪上飘");
        ADVANCEMENT_NAMES.put("adventure/spyglass_at_ghast", "那是气球吗？");
        ADVANCEMENT_NAMES.put("adventure/summon_iron_golem", "招募援兵");
        ADVANCEMENT_NAMES.put("adventure/trade_at_world_height", "星际商人");
        ADVANCEMENT_NAMES.put("adventure/trim_with_all_exclusive_armor_patterns", "匠心独具");
        ADVANCEMENT_NAMES.put("adventure/hero_of_the_village", "村庄英雄");
        ADVANCEMENT_NAMES.put("adventure/bullseye", "正中靶心");
        ADVANCEMENT_NAMES.put("adventure/sniper_duel", "狙击手的对决");
        ADVANCEMENT_NAMES.put("adventure/very_very_frightening", "魔女审判");
        ADVANCEMENT_NAMES.put("adventure/spyglass_at_dragon", "那是飞机吗？");
        ADVANCEMENT_NAMES.put("adventure/revaulting", "宝经磨炼");

        // ===== 农牧业（31）=====
        ADVANCEMENT_NAMES.put("husbandry/root", "农牧业");
        ADVANCEMENT_NAMES.put("husbandry/allay_deliver_item_to_player", "找到一个好朋友");
        ADVANCEMENT_NAMES.put("husbandry/breed_an_animal", "我从哪儿来？");
        ADVANCEMENT_NAMES.put("husbandry/fishy_business", "腥味十足的生意");
        ADVANCEMENT_NAMES.put("husbandry/make_a_sign_glow", "眼前一亮！");
        ADVANCEMENT_NAMES.put("husbandry/obtain_sniffer_egg", "怪味蛋");
        ADVANCEMENT_NAMES.put("husbandry/place_dried_ghast_in_water", "补水保湿！");
        ADVANCEMENT_NAMES.put("husbandry/plant_seed", "开荒垦地");
        ADVANCEMENT_NAMES.put("husbandry/ride_a_boat_with_a_goat", "羊帆起航！");
        ADVANCEMENT_NAMES.put("husbandry/safely_harvest_honey", "与蜂共舞");
        ADVANCEMENT_NAMES.put("husbandry/silk_touch_nest", "举巢搬迁");
        ADVANCEMENT_NAMES.put("husbandry/tadpole_in_a_bucket", "蝌到桶里来");
        ADVANCEMENT_NAMES.put("husbandry/tame_an_animal", "永恒的伙伴");
        ADVANCEMENT_NAMES.put("husbandry/uh_oh", "坏了");
        ADVANCEMENT_NAMES.put("husbandry/allay_deliver_cake_to_note_block", "生日快乐歌");
        ADVANCEMENT_NAMES.put("husbandry/bred_all_animals", "成双成对");
        ADVANCEMENT_NAMES.put("husbandry/tactical_fishing", "战术性钓鱼");
        ADVANCEMENT_NAMES.put("husbandry/feed_snifflet", "小小嗅探兽");
        ADVANCEMENT_NAMES.put("husbandry/balanced_diet", "均衡饮食");
        ADVANCEMENT_NAMES.put("husbandry/obtain_netherite_hoe", "终极奉献");
        ADVANCEMENT_NAMES.put("husbandry/wax_on", "涂蜡");
        ADVANCEMENT_NAMES.put("husbandry/leash_all_frog_variants", "呱呱队出动");
        ADVANCEMENT_NAMES.put("husbandry/repair_wolf_armor", "完好如初");
        ADVANCEMENT_NAMES.put("husbandry/remove_wolf_armor", "华丽一剪");
        ADVANCEMENT_NAMES.put("husbandry/complete_catalogue", "百猫全书");
        ADVANCEMENT_NAMES.put("husbandry/whole_pack", "群狼聚首");
        ADVANCEMENT_NAMES.put("husbandry/axolotl_in_a_bucket", "最萌捕食者");
        ADVANCEMENT_NAMES.put("husbandry/plant_any_sniffer_seed", "播种往事");
        ADVANCEMENT_NAMES.put("husbandry/wax_off", "脱蜡");
        ADVANCEMENT_NAMES.put("husbandry/froglights", "相映生辉！");
        ADVANCEMENT_NAMES.put("husbandry/kill_axolotl_target", "友谊的治愈力！");
    }

    public InfoListener(TLeafLink plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.isLinked()) {
            return;
        }
        Player player = event.getEntity();
        Component raw = event.deathMessage();
        if (raw == null) {
            return;
        }
        // Paper 26.2 返回的 deathMessage 已渲染成英文文本，转纯文本后按模板翻译成中文
        String full = PlainTextComponentSerializer.plainText().serialize(raw);
        String desc = full.startsWith(player.getName())
                ? full.substring(player.getName().length()).trim()
                : full;
        send("death", player.getName() + "\u0000" + DeathTranslator.translate(desc));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (!plugin.isLinked()) {
            return;
        }
        Player player = event.getPlayer();
        String key = event.getAdvancement().getKey().getKey();
        if (key.startsWith("recipes/")) {
            return;
        }
        String name = ADVANCEMENT_NAMES.getOrDefault(key, key);
        send("advancement", player.getName() + "\u0000" + name);
    }

    private void send(String type, String content) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        try {
            out.writeUTF(type);
            out.writeUTF(content);
        } catch (IOException e) {
            return;
        }
        plugin.getServer().sendPluginMessage(plugin, TLeafLink.CHANNEL_INFO, bos.toByteArray());
    }
}