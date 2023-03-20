# RpgDifficulty
RpgDifficulty is a mod which strengthens mobs over time and distance.

### Installation
RpgDifficulty is a mod built for the [Fabric Loader](https://fabricmc.net/). It requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) to be installed separately; all other dependencies are installed with the mod.

### License
RpgDifficulty is licensed under MIT.

### Config
- `increasingDistance`:   distance in blocks to increase strength of mobs by `distanceFactor`. <br>
    example: 300 blocks = 10% stronger, 600 blocks = 20% stronger, can be disabled by turning `increasingDistance` to 0
- `increasingTime`:   time in minutes to increase strength of mobs by `timeFactor`. <br>
    example: 60 minutes = 5% stronger, 120 minutes = 10% stronger, can be disabled by turning `increasingTime` to 0
- `heightDistance`:   distance in blocks from mob y - `startingHeight` / `heightDistance` to increase strength of mobs by `heightFactor`. <br>
    example: 30 blocks above `startingHeight` = 10% stronger, 60 blocks above `startingHeight` = 20% stronger ,can be disabled by `positiveHeightIncreasion` or `negativeHeightIncreasion` for each direction of height
- `maxFactorHealth`:    max factor for health increasion. <br>
    example: 1.0 = no increasion at all, 2.0 = max double health, 3.0 = max tripple health
- `maxFactorDamage`:    max factor for damage increasion. <br>
    example: see `maxFactorHealth`
- `maxFactorProtection`:    max factor for protection increasion. <br>
    example: see `maxFactorHealth`
- `maxFactorSpeed`:    max factor for speed increasion. <br>
    example: see `maxFactorHealth`, only applies onto special zombie
- `allowRandomValues`:  allow random values to be applied on mobs with chance of `randomChance`. <br>
    example: `randomChance` = 10 equals 10% chance for applying random stats
- `randomFactor`:   applies when `allowRandomValues` is true with the chance of `randomChance` <br>
    example:    mobHealth = mobHealth * (1 - `randomFactor` + (random.nextDouble() * `randomFactor` * 2F));
- `extraXP`:    calculate xp drop by strength. <br>
    example:    strength = 2.0 cause of distance 3000 blocks = double xp amount
- `maxXPFactor`:    max xp factor for xp drop increasion. <br>
    example: 2.0 = max double xp drop 
- `startingFactor`: overall starting factor for calculation. <br>
    example: 1.0 = normal calculation, 2.0 = double calculation
- `startingDistance`:   starting distance in blocks to calculate strength from spawn point. <br>
    example: 0 = strength calculation begins from spawn point, 100 = strength calculation begins from 100 blocks around spawn point
- `startingTime`:   staring time in minutes to calculate strength. <br>
    example: 0 = strength calculation begins from minute 0, 60 = strength calculation begins after 60 minutes

### Datapacks

For settings specific to a dimension a datapack is required.
If you don't know how to create a datapack check out [Data Pack Wiki](https://minecraft.fandom.com/wiki/Data_Pack) website and try to create your first one for the vanilla game.\
If you know how to create one, the folder path has to be ```data\rpgdifficulty\difficulty\YOURFILE.json```\
Boss modificators will get used for all mobs which are in the common entity types boss tag. (Called c:bosses)\
If the mod is used on a server, turning the timeFactor to 0 is recommended.

Settings which can get modified are:
- distanceCoordinatesX
- distanceCoordinatesY
- distanceCoordinatesZ
- increasingDistance
- distanceFactor
- increasingTime
- timeFactor
- heightDistance
- heightFactor
- maxFactorHealth
- maxFactorDamage
- maxFactorProtection
- maxFactorSpeed
- startingFactor
- startingDistance
- startingTime
- startingHeight
- positiveHeightIncreasion
- negativeHeightIncreasion

An example can be found below:

```json
{
    "dimension": "minecraft:the_nether",
    "increasingDistance": 300,
    "distanceFactor": 0.1,
    "increasingTime": 60,
    "timeFactor": 0.05,
    "heightDistance": 30,
    "heightFactor": 0.1,
    "maxFactorHealth": 3.0,
    "maxFactorDamage": 3.0,
    "maxFactorProtection": 1.5,
    "maxFactorSpeed": 2.0,
    "startingFactor": 1.0,
    "startingDistance": 0,
    "startingTime": 0,
    "startingHeight": 62,
    "positiveHeightIncreasion": true,
    "negativeHeightIncreasion": true
}
```
