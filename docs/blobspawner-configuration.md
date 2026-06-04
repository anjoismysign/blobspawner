# BlobSpawner Configuration

BlobSpawner uses three categories of YAML files to define custom mobs and their spawners:

| Directory | Purpose | Loaded By |
|---|---|---|
| `<plugin-data-folder>/config.yml` | Global plugin settings | `SpawnerConfigurationManager` (direct SnakeYAML) |
| `<plugin-data-folder>/mob/` | Mob type definitions | `BukkitIdentityManager<BlobMobData>` (IdentityGenerator pattern) |
| `<plugin-data-folder>/mob_spawner/` | Spawner definitions (one per mob) | `BukkitIdentityManager<BlobMobSpawnerData>` (IdentityGenerator pattern) |

All three are loaded when the plugin enables (or on `/bloblib reload`). The `mob/` and `mob_spawner/` directories follow the [IdentityGenerator Data Asset pattern](https://github.com/anjoismysign/holoworld): each `.yml` file is deserialized into the generator class, and the file name (without `.yml`) becomes the runtime identifier.

> **Critical relationship:** A `mob_spawner/` file's name (identifier) **must match** the name of a `mob/` file. The spawner looks up the mob definition at runtime via `BlobMobData.getBlobMobData()` using the spawner's own identifier. If no matching mob is found, an exception is thrown.

---

## Directory Structure

```
plugins/BlobSpawner/
├── config.yml                  ← global plugin settings
├── mob/                        ← BlobMobData definitions
│   ├── zombie.yml
│   ├── skeleton.yml
│   └── blaze.yml
└── mob_spawner/                ← BlobMobSpawnerData definitions
    ├── zombie.yml              ← identifier "zombie" → looks up mob/zombie.yml
    ├── skeleton.yml            ← identifier "skeleton" → looks up mob/skeleton.yml
    └── blaze.yml               ← identifier "blaze" → looks up mob/blaze.yml
```

Subdirectories within `mob/` and `mob_spawner/` are supported and scanned recursively.

---

## 1. Global Configuration (`config.yml`)

Loaded directly from `<plugin-data-folder>/config.yml` via SnakeYAML into `SpawnerConfiguration`. This is **not** an IdentityGenerator-based file — it is a single file at the plugin root.

### Schema

```yaml
tinyDebug: false
```

### Field Reference

| Field | Type | Default | Description |
|---|---|---|---|
| `tinyDebug` | `boolean` | `false` | When `true`, enables verbose debug logging throughout the plugin |

---

## 2. Mob Definitions (`mob/*.yml`)

Each file in `<plugin-data-folder>/mob/` defines one mob type. The file is deserialized into `BlobMobData.Info` (which implements `IdentityGenerator<BlobMobData>`). The file name becomes the string identifier passed to `generate(identifier)`.

### Schema

```yaml
type: <EntityType>
chance: <double>
defaultIsFollower: <boolean>
awareDistance: <double>
attackDistance: <double>
defaultEntity:
  attributes:
    <attribute_key>:
      key: <string>
      operation: <AttributeModifier.Operation>
      amount: <double>
      equipmentSlotGroup: <string>
  lootTable: <string>
  model: <string>
legendaryEntity:
  attributes:
    <attribute_key>:
      key: <string>
      operation: <AttributeModifier.Operation>
      amount: <double>
      equipmentSlotGroup: <string>
  lootTable: <string>
  model: <string>
```

### Top-Level Fields

| Field | Type | Required | Description |
|---|---|---|---|
| `type` | `EntityType` (enum) | **Yes** | The Bukkit entity type. Must be a `Mob` subclass (e.g., `ZOMBIE`, `SKELETON`, `BLAZE`). |
| `chance` | `double` | **Yes** | Legendary spawn threshold (0.0–1.0). When a mob spawns, a random `Math.random()` draw is compared against this. If the draw is ≤ `chance`, the mob is **legendary**. |
| `defaultIsFollower` | `boolean` | **Yes** | When `true`, non-legendary mobs of this type gain a `FollowerMobGoal` that makes them follow nearby legendary mobs of the same type. |
| `awareDistance` | `double` | **Yes** | Range (in blocks) for detecting enemies or allies. Controls both `BlobMobGoal` and `FollowerMobGoal` awareness. |
| `attackDistance` | `double` | **Yes** | Range (in blocks) within which legendary mobs attack their target. Non-legendary mobs do not use this value (they only follow). |
| `defaultEntity` | `EntityBean` | **Yes** | Configuration applied to **non-legendary** spawns. |
| `legendaryEntity` | `EntityBean` | **Yes** | Configuration applied to **legendary** spawns. |

### `EntityBean` Fields (used under `defaultEntity` / `legendaryEntity`)

| Field | Type | Required | Description |
|---|---|---|---|
| `attributes` | `Map<String, AttributeModifierBean>` | No (empty map) | Map of attribute registry keys to modifier beans. Each key must be a valid Minecraft attribute key (e.g., `minecraft:generic.max_health`). Applied to the mob on spawn. |
| `lootTable` | `String` | No (empty string) | Identifier of a BlobLib loot table. When the mob dies, loot is generated via `BlobLibLootAPI.generateLoot()`. An empty string disables loot generation. |
| `model` | `String` | No (empty string) | Identifier of a BetterModel model to apply. If non-empty, the mob is set to silent (`setSilent(true)`) and the model renderer is attached via BetterModel API. |

### `AttributeModifierBean` Fields (used within `attributes` map)

| Field | Type | Required | Description |
|---|---|---|---|
| `key` | `String` | **Yes** | The `NamespacedKey` for the `AttributeModifier` (e.g., `"blobspawner:max_health_bonus"`). |
| `operation` | `AttributeModifier.Operation` (enum) | **Yes** | The modifier operation. See enum values below. |
| `amount` | `double` | **Yes** | The modifier value. Meaning depends on `operation`. |
| `equipmentSlotGroup` | `String` | **Yes** | The `EquipmentSlotGroup` for the modifier (e.g., `"any"`, `"hand"`, `"chest"`, `"head"`). Passed through `EquipmentSlotGroup.getByName()`. |

### `AttributeModifier.Operation` Enum Values

| YAML Value | Int | Behaviour |
|---|---|---|
| `ADD_NUMBER` | 0 | Adds `amount` to the base value |
| `ADD_SCALAR` | 1 | Multiplies the base value by `amount` (e.g., `0.5` = +50%) |
| `MULTIPLY_SCALAR_1` | 2 | Multiplies (base + all prior modifiers) by `(1 + amount)` |

### `EquipmentSlotGroup` Common Values

| Value | Applies To |
|---|---|
| `any` | All slots |
| `hand` | Either hand |
| `mainhand` | Main hand |
| `offhand` | Off hand |
| `head` | Head slot |
| `chest` | Chestplate slot |
| `legs` | Leggings slot |
| `feet` | Boots slot |
| `armor` | All armor slots |
| `body` | Body slot |

### How Mob Types Work

Each spawn randomly becomes either **legendary** or a regular mob:

1. `Math.random()` is called. If the result ≤ `chance`, the mob is **legendary**.
2. **Legendary** mobs get `BlobMobGoal` (targets players, then other legendary mobs of same type). They use `attackDistance` and `legendaryEntity` config.
3. **Non-legendary** mobs get `FollowerMobGoal` only if `defaultIsFollower` is `true`. They use `defaultEntity` config.
4. In both cases, attributes are applied via `Mob.getAttribute()`, and `setPersistent(false)` is called.
5. A `BlobMobSpawnEvent` (async: false) is fired for legendary spawns.

---

## 3. Spawner Definitions (`mob_spawner/*.yml`)

Each file in `<plugin-data-folder>/mob_spawner/` defines a spawner that produces mobs of a specific type. The file is deserialized into `BlobMobSpawnerData.Info`. The identifier **must match** a mob definition in `mob/`.

### Schema

```yaml
minDelay: <int>
maxDelay: <int>
maxCount: <int>
blocks:
  - <serialized_location>
  - <serialized_location>
```

### Field Reference

| Field | Type | Required | Description |
|---|---|---|---|
| `minDelay` | `int` | **Yes** | Minimum ticks (1/20 second) between spawn attempts. |
| `maxDelay` | `int` | **Yes** | Maximum ticks between spawn attempts. The actual delay is a random integer in `[minDelay, maxDelay]` chosen each cycle. |
| `maxCount` | `int` | **Yes** | Maximum number of simultaneously alive mobs **per spawner block**. If the current count is at or above this, no new mob spawns. |
| `blocks` | `List<String>` | **Yes** | Serialized `Location` strings for each spawner block. Managed automatically — see below. |

### How `blocks` Is Managed

The `blocks` list is **not** typically hand-written. It is managed automatically when a player right-clicks a **spawner block** with a **BlobMob spawn egg**:

1. The admin creates a spawn egg via `/blobmob egg give <player> <mob_identifier>`.
2. The admin right-clicks a spawner block (`Material.SPAWNER`) with the egg.
3. The block's location is serialized via `SerializationLib.serialize()` and appended to the spawner definition's `blocks` list.
4. The spawner block is set to `Material.AIR` (destroyed).
5. The updated spawner definition is written back to its YAML file via `IdentityManager.add()`.

Moving forward, mobs will spawn at the recorded locations.

### Spawner Behaviour at Runtime

- The spawner task runs every tick (`runTaskTimer(plugin, 0, 1)`).
- Each tick, a delay counter decrements. When it reaches 0 while `currentCount < maxCount`, a mob spawns.
- The delay resets to a random value in `[minDelay, maxDelay]`.
- On `/bloblib reload`, all existing entities are killed and spawners are rebuilt from the YAML files.

---

## Complete Example Files

See the [`docs/examples/`](./examples/) directory for ready-to-use YAML files:

| File | Description |
|---|---|
| [`config.yml`](./examples/config.yml) | Global plugin configuration |
| [`mob_zombie.yml`](./examples/mob_zombie.yml) | A zombie mob definition with health/attack attributes |
| [`mob_skeleton.yml`](./examples/mob_skeleton.yml) | A skeleton mob with speed attribute and model |
| [`mobspawner_zombie.yml`](./examples/mobspawner_zombie.yml) | Spawner for the zombie mob |
| [`mobspawner_skeleton.yml`](./examples/mobspawner_skeleton.yml) | Spawner for the skeleton mob |

---

## Lifecycle

```
Plugin enable
  ├─ config.yml → SpawnerConfiguration (direct SnakeYAML load)
  ├─ mob/*.yml → BlobMobData.Info → generate(identifier) → BlobMobData
  └─ mob_spawner/*.yml → BlobMobSpawnerData.Info → generate(identifier) → BlobMobSpawnerData
       └─ Each spawner uses its identifier to look up BlobMobData
            └─ Spawner tasks begin ticking with random delays

Runtime right-click (spawner block + egg)
  └─ Block location appended to spawner's blocks[] list
       └─ Spawner YAML updated via IdentityManager.add()

/bloblob reload
  └─ All spawner tasks cancelled, all entities killed
       └─ config.yml re-read
       └─ mob/*.yml re-loaded
       └─ mob_spawner/*.yml re-loaded → new spawner tasks start

Plugin disable
  └─ All spawner tasks cancelled
  └─ All tracked entities removed
```

---

## Commands

The plugin registers a `/blobmob` command with subcommands:

| Command | Usage | Description |
|---|---|---|
| `/blobmob egg give <player> <mob>` | `/blobmob egg give Notch zombie` | Gives the player a spawn egg for the specified mob type. The egg's `PersistentDataContainer` stores the mob identifier. |
