package net.minebo.practice;

import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;

import lombok.Setter;
import net.minebo.practice.misc.Cache;
import net.minebo.practice.scoreboard.ScoreboardAdapter;
import net.minebo.practice.aikar.ACFCommandController;
import net.minebo.practice.arena.ArenaHandler;
import net.minebo.practice.kit.KitHandler;
import net.minebo.practice.kit.kittype.KitType;
import net.minebo.practice.kit.kittype.KitTypeJsonAdapter;
import net.minebo.practice.listener.*;
import net.minebo.practice.lobby.LobbyHandler;
import net.minebo.practice.match.MatchHandler;
import net.minebo.practice.match.duel.DuelHandler;
import net.minebo.practice.match.postmatchinv.PostMatchInvHandler;
import net.minebo.practice.match.rematch.RematchHandler;
import net.minebo.practice.party.PartyHandler;
import net.minebo.practice.profile.elo.EloHandler;
import net.minebo.practice.profile.follow.FollowHandler;
import net.minebo.practice.profile.setting.SettingHandler;
import net.minebo.practice.profile.statistics.StatisticsHandler;
import net.minebo.practice.pvpclasses.PvPClassHandler;
import net.minebo.practice.queue.QueueHandler;
import net.minebo.practice.tournament.TournamentHandler;
import net.minebo.practice.util.ChunkSnapshotAdapter;
import net.minebo.practice.util.menu.ButtonListener;
import net.minebo.practice.util.scoreboard.api.AssembleStyle;
import net.minebo.practice.util.scoreboard.api.ScoreboardHandler;
import net.minebo.practice.util.serialization.*;
import net.minebo.practice.util.uuid.UUIDCache;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.eytril.spigot.chunksnapshot.ChunkSnapshot;

@Getter
public final class Practice extends JavaPlugin {

    @Getter
    private static Practice instance;

    @Getter
    private final static Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .registerTypeHierarchyAdapter(KitType.class, new KitTypeJsonAdapter()) // custom KitType serializer
            .registerTypeAdapter(ChunkSnapshot.class, new ChunkSnapshotAdapter())
            .serializeNulls()
            .create();

    public static Gson plainGson = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .serializeNulls()
            .create();

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    // Command Service
    @Setter
    private PaperCommandManager commandController;

    private SettingHandler settingHandler;
    private DuelHandler duelHandler;
    private KitHandler kitHandler;
    private LobbyHandler lobbyHandler;
    private ArenaHandler arenaHandler;
    private MatchHandler matchHandler;
    private PartyHandler partyHandler;
    private QueueHandler queueHandler;
    private RematchHandler rematchHandler;
    private PostMatchInvHandler postMatchInvHandler;
    private FollowHandler followHandler;
    private EloHandler eloHandler;
    private PvPClassHandler pvpClassHandler;
    private TournamentHandler tournamentHandler;

    public ScoreboardHandler scoreboardHandler;

    public UUIDCache uuidCache;

    private final ChatColor dominantColor = ChatColor.RED;
    private final Cache cache = new Cache();

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        this.setupMongo();
    }

    @Override
    public void onEnable() {
        this.uuidCache = new UUIDCache();

        this.registerExpansions();

        kitHandler = new KitHandler();
        eloHandler = new EloHandler();
        duelHandler = new DuelHandler();
        lobbyHandler = new LobbyHandler();
        arenaHandler = new ArenaHandler();
        matchHandler = new MatchHandler();
        partyHandler = new PartyHandler();
        queueHandler = new QueueHandler();
        followHandler = new FollowHandler();
        rematchHandler = new RematchHandler();
        settingHandler = new SettingHandler();
        pvpClassHandler = new PvPClassHandler();
        tournamentHandler = new TournamentHandler();
        postMatchInvHandler = new PostMatchInvHandler();

        this.getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        this.getServer().getPluginManager().registerEvents(new BowHealthListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatToggleListener(), this);
        this.getServer().getPluginManager().registerEvents(new NightModeListener(), this);
        this.getServer().getPluginManager().registerEvents(new PearlCooldownListener(), this);
        this.getServer().getPluginManager().registerEvents(new StatisticsHandler(), this);
        this.getServer().getPluginManager().registerEvents(new ButtonListener(), this);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, cache, 20L, 20L);

        Bukkit.getServer().getWorlds().forEach(world -> {
            world.setGameRuleValue("doDayLightCycle", "false");
            world.setGameRuleValue("doWeatherCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");

            world.getEntities().stream().filter(entity -> entity.getType() != EntityType.PLAYER && entity.getType() != EntityType.ITEM_FRAME).forEach(Entity::remove);
            world.setStorm(false);
            world.setThundering(false);
            world.setTime(0L);
        });

        ACFCommandController.registerAll();
    }

    @Override
    public void onDisable() {
        matchHandler.cleanup();
        arenaHandler.saveSchematics();
        scoreboardHandler.shutdown();
    }

    private void setupMongo() {
        if (this.getConfig().getBoolean("MONGO.URI-MODE")) {
            this.mongoClient = MongoClients.create(this.getConfig().getString("MONGO.URI.CONNECTION_STRING"));
            this.mongoDatabase = mongoClient.getDatabase(this.getConfig().getString("MONGO.URI.DATABASE"));
            return;
        }

        boolean auth = this.getConfig().getBoolean("MONGO.NORMAL.AUTHENTICATION.ENABLED");
        String host = this.getConfig().getString("MONGO.NORMAL.HOST");
        int port = this.getConfig().getInt("MONGO.NORMAL.PORT");

        String uri = "mongodb://" + host + ":" + port;

        if (auth) {
            String username = this.getConfig().getString("MONGO.NORMAL.AUTHENTICATION.USERNAME");
            String password = this.getConfig().getString("MONGO.NORMAL.AUTHENTICATION.PASSWORD");
            uri = "mongodb://" + username + ":" + password + "@" + host + ":" + port;
        }


        this.mongoClient = MongoClients.create(uri);
        this.mongoDatabase = mongoClient.getDatabase(this.getConfig().getString("MONGO.URI.DATABASE"));
    }


    private void registerExpansions() {
        ScoreboardAdapter scoreboardAdapter = new ScoreboardAdapter();

        this.scoreboardHandler = new ScoreboardHandler(this, scoreboardAdapter);
        this.scoreboardHandler.setAssembleStyle(AssembleStyle.KOHI);
        this.scoreboardHandler.setTicks(2L);
    }
}