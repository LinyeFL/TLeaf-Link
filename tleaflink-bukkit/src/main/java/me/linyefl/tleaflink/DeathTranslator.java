package me.linyefl.tleaflink;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 死亡消息汉化器（无受害者版）。
 * 输入：去掉受害者名字后的英文死亡描述（如 "was shot by Skeleton"）
 * 输出：Minecraft 官方中文描述（如 "被骷髅射杀了"）
 *
 * 数据来源：Minecraft 26.2 官方语言文件 en_us.json / zh_cn.json
 *  - death.attack.* / death.fell.* 模板对照（英文模板去掉 %1$s 受害者部分）
 *  - entity.minecraft.* 实体官方译名
 *
 * 约定：
 *  - 输入描述若已含中文，原样返回（服务器本身是中文环境时无需翻译）
 *  - 物品名（$2）暂不汉化，保持原样
 *  - 查不到实体译名的名字（玩家名、自定义实体）保持原样
 */
public final class DeathTranslator {

    private DeathTranslator() {
    }

    /** 实体名：英文显示名 -> 官方中文译名 */
    private static final Map<String, String> ENTITY_NAMES = new LinkedHashMap<>();

    /** 翻译规则：匹配时按模板长度从长到短尝试（特殊规则用 priority 提权） */
    private static final List<Rule> RULES = new ArrayList<>();

    static {
        registerEntities();
        registerRules();
        RULES.sort((a, b) -> Integer.compare(b.order, a.order));
    }

    /**
     * 翻译一条去掉受害者名的英文死亡描述。
     * 匹配不到时原样返回，不丢信息。
     */
    public static String translate(String desc) {
        if (desc == null || desc.isEmpty()) {
            return desc;
        }
        String clean = stripColor(desc);
        // 已经含中文说明服务器本身就是中文环境，无需翻译
        if (containsChinese(clean)) {
            return clean;
        }
        for (Rule rule : RULES) {
            Matcher matcher = rule.pattern.matcher(clean);
            if (matcher.matches()) {
                return rule.format(matcher);
            }
        }
        return clean;
    }

    // ==================== 规则与实体注册 ====================

    private static void addRule(String englishRegex, String chineseTemplate) {
        RULES.add(new Rule(englishRegex, chineseTemplate, new int[]{1, 2}, 0));
    }

    /**
     * 带参数重排的规则。
     * captureOrder[0] = 凶手在英文正则中的捕获组号（中文模板里写 $1）
     * captureOrder[1] = 物品在英文正则中的捕获组号（中文模板里写 $2）
     * 仅少数模板需要，如 fireworks.item（英文顺序是物品在前、凶手在后）。
     */
    private static void addRule(String englishRegex, String chineseTemplate, int[] captureOrder) {
        RULES.add(new Rule(englishRegex, chineseTemplate, captureOrder, 0));
    }

    /** priority > 0 的规则强制排前（用于 "using magic"、"a skull from" 这类字面比通用模板短的场景） */
    private static void addRule(String englishRegex, String chineseTemplate, int[] captureOrder, int priority) {
        RULES.add(new Rule(englishRegex, chineseTemplate, captureOrder, priority));
    }

    private static void registerEntities() {
        ENTITY_NAMES.put("Allay", "悦灵");
        ENTITY_NAMES.put("Area Effect Cloud", "区域效果云");
        ENTITY_NAMES.put("Armor Stand", "盔甲架");
        ENTITY_NAMES.put("Arrow", "箭");
        ENTITY_NAMES.put("Bat", "蝙蝠");
        ENTITY_NAMES.put("Bee", "蜜蜂");
        ENTITY_NAMES.put("Blaze", "烈焰人");
        ENTITY_NAMES.put("Bogged", "沼骸");
        ENTITY_NAMES.put("Breeze", "旋风人");
        ENTITY_NAMES.put("Cave Spider", "洞穴蜘蛛");
        ENTITY_NAMES.put("Copper Golem", "铜傀儡");
        ENTITY_NAMES.put("Creaking", "嘎枝");
        ENTITY_NAMES.put("Creeper", "苦力怕");
        ENTITY_NAMES.put("Dolphin", "海豚");
        ENTITY_NAMES.put("Dragon Fireball", "末影龙火球");
        ENTITY_NAMES.put("Drowned", "溺尸");
        ENTITY_NAMES.put("Elder Guardian", "远古守卫者");
        ENTITY_NAMES.put("End Crystal", "末地水晶");
        ENTITY_NAMES.put("Ender Dragon", "末影龙");
        ENTITY_NAMES.put("Enderman", "末影人");
        ENTITY_NAMES.put("Endermite", "末影螨");
        ENTITY_NAMES.put("Evoker", "唤魔者");
        ENTITY_NAMES.put("Evoker Fangs", "唤魔者尖牙");
        ENTITY_NAMES.put("Fireball", "火球");
        ENTITY_NAMES.put("Firework Rocket", "烟花火箭");
        ENTITY_NAMES.put("Frog", "青蛙");
        ENTITY_NAMES.put("Ghast", "恶魂");
        ENTITY_NAMES.put("Giant", "巨人");
        ENTITY_NAMES.put("Goat", "山羊");
        ENTITY_NAMES.put("Guardian", "守卫者");
        ENTITY_NAMES.put("Happy Ghast", "快乐恶魂");
        ENTITY_NAMES.put("Hoglin", "疣猪兽");
        ENTITY_NAMES.put("Husk", "尸壳");
        ENTITY_NAMES.put("Illusioner", "幻术师");
        ENTITY_NAMES.put("Iron Golem", "铁傀儡");
        ENTITY_NAMES.put("Lightning Bolt", "闪电束");
        ENTITY_NAMES.put("Lingering Potion", "滞留药水");
        ENTITY_NAMES.put("Llama", "羊驼");
        ENTITY_NAMES.put("Llama Spit", "羊驼唾沫");
        ENTITY_NAMES.put("Magma Cube", "岩浆怪");
        ENTITY_NAMES.put("Parched", "焦骸");
        ENTITY_NAMES.put("Phantom", "幻翼");
        ENTITY_NAMES.put("Piglin", "猪灵");
        ENTITY_NAMES.put("Piglin Brute", "猪灵蛮兵");
        ENTITY_NAMES.put("Pillager", "掠夺者");
        ENTITY_NAMES.put("Polar Bear", "北极熊");
        ENTITY_NAMES.put("Potion", "药水");
        ENTITY_NAMES.put("Pufferfish", "河豚");
        ENTITY_NAMES.put("Ravager", "劫掠兽");
        ENTITY_NAMES.put("Shulker", "潜影贝");
        ENTITY_NAMES.put("Shulker Bullet", "潜影弹");
        ENTITY_NAMES.put("Silverfish", "蠹虫");
        ENTITY_NAMES.put("Skeleton", "骷髅");
        ENTITY_NAMES.put("Skeleton Horse", "骷髅马");
        ENTITY_NAMES.put("Slime", "史莱姆");
        ENTITY_NAMES.put("Small Fireball", "小火球");
        ENTITY_NAMES.put("Snow Golem", "雪傀儡");
        ENTITY_NAMES.put("Snowball", "雪球");
        ENTITY_NAMES.put("Spectral Arrow", "光灵箭");
        ENTITY_NAMES.put("Spider", "蜘蛛");
        ENTITY_NAMES.put("Splash Potion", "喷溅药水");
        ENTITY_NAMES.put("Stray", "流浪者");
        ENTITY_NAMES.put("Sulfur Cube", "硫方怪");
        ENTITY_NAMES.put("The Killer Bunny", "杀手兔");
        ENTITY_NAMES.put("TNT", "被激活的TNT");
        ENTITY_NAMES.put("Trader Llama", "行商羊驼");
        ENTITY_NAMES.put("Trident", "三叉戟");
        ENTITY_NAMES.put("Vex", "恼鬼");
        ENTITY_NAMES.put("Villager", "村民");
        ENTITY_NAMES.put("Vindicator", "卫道士");
        ENTITY_NAMES.put("Wandering Trader", "流浪商人");
        ENTITY_NAMES.put("Warden", "监守者");
        ENTITY_NAMES.put("Wind Charge", "风弹");
        ENTITY_NAMES.put("Witch", "女巫");
        ENTITY_NAMES.put("Wither", "凋灵");
        ENTITY_NAMES.put("Wither Skeleton", "凋灵骷髅");
        ENTITY_NAMES.put("Wither Skull", "凋灵之首");
        ENTITY_NAMES.put("Wolf", "狼");
        ENTITY_NAMES.put("Zoglin", "僵尸疣猪兽");
        ENTITY_NAMES.put("Zombie", "僵尸");
        ENTITY_NAMES.put("Zombie Horse", "僵尸马");
        ENTITY_NAMES.put("Zombie Villager", "僵尸村民");
        ENTITY_NAMES.put("Zombified Piglin", "僵尸猪灵");
    }

    private static void registerRules() {
        // ---------- 3 参数：凶手 + 物品 ----------
        addRule("was obliterated by a sonically-charged shriek while trying to escape (.+?) wielding (.+)",
                "在试图逃离持有$2的$1时被一道音波尖啸抹除了");
        addRule("went off with a bang due to a firework fired from (.+?) by (.+)",
                "随着$1用$2发射的烟花发出的巨响消失了", new int[]{2, 1});
        addRule("was burned to a crisp while fighting (.+?) wielding (.+)",
                "在与持有$2的$1战斗时被烤得酥脆");
        addRule("was killed by (.+?) while trying to hurt (.+)",
                "在试图伤害$1时被$2杀死", new int[]{2, 1});
        addRule("was shot by a skull from (.+?) using (.+)", "被$1用$2发射的头颅射杀", new int[]{1, 2}, 1);
        addRule("was doomed to fall by (.+?) using (.+)", "因为$1使用了$2注定要摔死");
        addRule("fell too far and was finished by (.+?) using (.+)", "摔伤得太重并被$1用$2完结了生命");
        addRule("was shot by (.+?) using (.+)", "被$1用$2射杀了");
        addRule("was blown up by (.+?) using (.+)", "被$1用$2炸死了");
        addRule("was fireballed by (.+?) using (.+)", "被$1用$2发射的火球烧死了");
        addRule("was killed by (.+?) using (.+)", "被$1用$2杀死了");
        addRule("was slain by (.+?) using (.+)", "被$1用$2杀死了");
        addRule("was speared by (.+?) using (.+)", "被$1用$2刺穿了");
        addRule("was stung to death by (.+?) using (.+)", "被$1用$2蛰死了");
        addRule("was pummeled by (.+?) using (.+)", "被$1用$2给砸死了");
        addRule("was impaled by (.+?) with (.+)", "被$1用$2刺穿了");
        addRule("was smashed by (.+?) with (.+)", "被$1用$2一锤毙命");

        // ---------- 2 参数：只有凶手 ----------
        addRule("was obliterated by a sonically-charged shriek while trying to escape (.+)",
                "在试图逃离$1时被一道音波尖啸抹除了");
        addRule("was squashed by a falling anvil while fighting (.+)", "在与$1战斗时被下落的铁砧压扁了");
        addRule("was squashed by a falling block while fighting (.+)", "在与$1战斗时被下落的方块压扁了");
        addRule("was skewered by a falling stalactite while fighting (.+)", "在与$1战斗时被下落的钟乳石刺穿了");
        addRule("was poked to death by a sweet berry bush while trying to escape (.+)",
                "在试图逃离$1时被甜浆果丛刺死了");
        addRule("walked into a cactus while trying to escape (.+)", "在试图逃离$1时撞上了仙人掌");
        addRule("died from dehydration while trying to escape (.+)", "在试图逃离$1时因脱水而死");
        addRule("experienced kinetic energy while trying to escape (.+)", "在试图逃离$1时感受到了动能");
        addRule("hit the ground too hard while trying to escape (.+)", "在试图逃离$1时落地过猛");
        addRule("drowned while trying to escape (.+)", "在试图逃离$1时淹死了");
        addRule("was killed by magic while trying to escape (.+)", "在试图逃离$1时被魔法杀死了");
        addRule("tried to swim in lava to escape (.+)", "在逃离$1时试图在熔岩里游泳");
        addRule("went off with a bang while fighting (.+)", "在与$1战斗时随着一声巨响消失了");
        addRule("was struck by lightning while fighting (.+)", "在与$1战斗时被闪电击中");
        addRule("was impaled on a stalagmite while fighting (.+)", "在与$1战斗时被石笋刺穿了");
        addRule("suffocated in a wall while fighting (.+)", "在与$1战斗时在墙里窒息而亡");
        addRule("starved to death while fighting (.+)", "在与$1战斗时饿死了");
        addRule("was killed while fighting (.+)", "在与$1战斗时被杀死了");
        addRule("withered away while fighting (.+)", "在与$1战斗时凋零了");
        addRule("was burned to a crisp while fighting (.+)", "在与$1战斗时被烤得酥脆");
        addRule("walked into fire while fighting (.+)", "在与$1战斗时不慎走入了火中");
        addRule("left the confines of this world while fighting (.+)", "在与$1战斗时脱离了这个世界");
        addRule("was roasted in dragon's breath by (.+)", "被$1的龙息烤熟了");
        addRule("was frozen to death by (.+)", "被$1冻死了");
        addRule("was stung to death by (.+)", "被$1蛰死了");
        addRule("walked into the danger zone due to (.+)", "因$1而步入危险之地");
        addRule("was doomed to fall by (.+)", "因为$1注定要摔死");
        addRule("fell too far and was finished by (.+)", "摔伤得太重并被$1完结了生命");
        addRule("didn't want to live in the same world as (.+)", "与$1不共戴天");
        addRule("was shot by a skull from (.+)", "被$1发射的头颅射杀", new int[]{1, 2}, 1);
        addRule("was killed by (.+?) using magic", "被$1使用的魔法杀死了", new int[]{1, 2}, 1);
        addRule("was killed while trying to hurt (.+)", "在试图伤害$1时被杀");
        addRule("was killed by even more magic", "被不为人知的魔法杀死了");
        addRule("was fireballed by (.+)", "被$1用火球烧死了");
        addRule("was impaled by (.+)", "被$1刺穿了");
        addRule("was slain by (.+)", "被$1杀死了");
        addRule("was speared by (.+)", "被$1刺穿了");
        addRule("was smashed by (.+)", "被$1一锤毙命");
        addRule("was pummeled by (.+)", "被$1给砸死了");
        addRule("was squashed by (.+)", "被$1挤扁了");
        addRule("was shot by (.+)", "被$1射杀了");
        addRule("was blown up by (.+)", "被$1炸死了");
        addRule("was killed by (.+)", "被$1杀死了");
        addRule("died because of (.+)", "死于$1");

        // ---------- 1 参数：只有受害者，纯描述 ----------
        addRule("was obliterated by a sonically-charged shriek", "被一道音波尖啸抹除了");
        addRule("died because not just the floor is lava", "发现了不只有地板是熔岩做的");
        addRule("discovered the floor was lava", "发现了地板是熔岩做的");
        addRule("was poked to death by a sweet berry bush", "被甜浆果丛刺死了");
        addRule("was squashed by a falling anvil", "被下落的铁砧压扁了");
        addRule("was squashed by a falling block", "被下落的方块压扁了");
        addRule("was skewered by a falling stalactite", "被下落的钟乳石刺穿了");
        addRule("experienced kinetic energy", "感受到了动能");
        addRule("was impaled on a stalagmite", "被石笋刺穿了");
        addRule("hit the ground too hard", "落地过猛");
        addRule("tried to swim in lava", "试图在熔岩里游泳");
        addRule("suffocated in a wall", "在墙里窒息而亡");
        addRule("went off with a bang", "随着一声巨响消失了");
        addRule("was struck by lightning", "被闪电击中");
        addRule("was killed by even more magic", "被不为人知的魔法杀死了");
        addRule("was killed by magic", "被魔法杀死了");
        addRule("was roasted in dragon's breath", "被龙息烤熟了");
        addRule("was stung to death", "被蛰死了");
        addRule("was pricked to death", "被戳死了");
        addRule("was squished too much", "因被过度挤压而死");
        addRule("starved to death", "饿死了");
        addRule("died from dehydration", "因脱水而死");
        addRule("froze to death", "被冻死了");
        addRule("burned to death", "被烧死了");
        addRule("fell from a high place", "从高处摔了下来");
        addRule("fell off a ladder", "从梯子上摔了下来");
        addRule("fell while climbing", "在攀爬时摔了下来");
        addRule("fell off scaffolding", "从脚手架上摔了下来");
        addRule("fell off some twisting vines", "从缠怨藤上摔了下来");
        addRule("fell off some vines", "从藤蔓上摔了下来");
        addRule("fell off some weeping vines", "从垂泪藤上摔了下来");
        addRule("fell out of the world", "掉出了这个世界");
        addRule("left the confines of this world", "脱离了这个世界");
        addRule("went up in flames", "浴火焚身");
        addRule("withered away", "凋零了");
        addRule("was doomed to fall", "注定要摔死");
        addRule("was killed", "被杀死了");
        addRule("drowned", "淹死了");
        addRule("blew up", "爆炸了");
        addRule("died", "死了");
    }

    // ==================== 内部实现 ====================

    private static final class Rule {
        final Pattern pattern;
        /** captureOrder[0] = 凶手捕获组号，captureOrder[1] = 物品捕获组号（中文模板 $1 $2） */
        final int[] captureOrder;
        final String chineseTemplate;
        /** 排序权重：英文模板越长越先尝试，priority 额外提权 */
        final int order;

        Rule(String englishRegex, String chineseTemplate, int[] captureOrder, int priority) {
            this.chineseTemplate = chineseTemplate;
            this.captureOrder = captureOrder;
            this.order = englishRegex.length() + priority * 1000;
            this.pattern = Pattern.compile("^" + englishRegex + "$");
        }

        String format(Matcher matcher) {
            String killer = groupOf(matcher, captureOrder[0]);
            String item = groupOf(matcher, captureOrder[1]);
            return chineseTemplate
                    .replace("$1", translateEntity(killer))
                    .replace("$2", item);
        }
    }

    private static String groupOf(Matcher matcher, int group) {
        if (group <= matcher.groupCount()) {
            String value = matcher.group(group);
            return value == null ? "" : value;
        }
        return "";
    }

    private static String translateEntity(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        String zh = ENTITY_NAMES.get(name);
        return zh != null ? zh : name;
    }

    private static final Pattern COLOR_PATTERN = Pattern.compile("§.");

    private static String stripColor(String text) {
        return COLOR_PATTERN.matcher(text).replaceAll("");
    }

    private static boolean containsChinese(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '\u4E00' && c <= '\u9FFF') {
                return true;
            }
        }
        return false;
    }
}