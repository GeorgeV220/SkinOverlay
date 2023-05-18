# [5.3.0](https://github.com/GeorgeV220/SkinOverlay/compare/v5.2.0...v5.3.0) (2023-05-18)


### Features

* **API:** Make some classes final or annotate them with APIStatus ([41f106c](https://github.com/GeorgeV220/SkinOverlay/commit/41f106ca99bee675d4e5957601c07ee3f8538cca))
* **Locales:** Added Turkish translation [skip ci] ([4bd46ac](https://github.com/GeorgeV220/SkinOverlay/commit/4bd46ac667dc1573bb1fe0235df1e7da8853cbf3)), closes [#57](https://github.com/GeorgeV220/SkinOverlay/issues/57)

# [5.2.0](https://github.com/GeorgeV220/SkinOverlay/compare/v5.1.0...v5.2.0) (2023-05-14)


### Features

* **Locale:** Added proper locale support. ([656c65f](https://github.com/GeorgeV220/SkinOverlay/commit/656c65f1d460284add78d6f9746ab55e8ccc9d05))

# [5.1.0](https://github.com/GeorgeV220/SkinOverlay/compare/v5.0.0...v5.1.0) (2023-05-13)


### Bug Fixes

* **Event:** Ignore the handler if the event is cancelled. ([3eff65b](https://github.com/GeorgeV220/SkinOverlay/commit/3eff65b7593c2f09538eab74c85a90faa0fd4ef8))
* **PlayerListeners:** Change the event priority to HIGHEST ([79d60fe](https://github.com/GeorgeV220/SkinOverlay/commit/79d60fe8650f728077908e200bfa2216570cfc95))
* **PlayerObject:** Removed debug message ([4502a32](https://github.com/GeorgeV220/SkinOverlay/commit/4502a3296fba4740f1f69449f29260161af54c70))
* **SkinHandler_Legacy:** Fixed for Minecraft 1.8.8 ([03c26de](https://github.com/GeorgeV220/SkinOverlay/commit/03c26de3871eab3f27e218a2196487fd774e3cc0))


### Features

* **lang:** Removed lang_en.yml and moved the messages to messages.yml ([3c5107f](https://github.com/GeorgeV220/SkinOverlay/commit/3c5107ff3fdc021b5aa59d591cad7536bbce7187))

# [5.0.0](https://github.com/GeorgeV220/SkinOverlay/compare/v4.1.0...v5.0.0) (2023-05-04)


### Bug Fixes

* **User:** Added serial version ([afe2613](https://github.com/GeorgeV220/SkinOverlay/commit/afe2613e729abed53df0e8f7468265d5b94d5e20))
* **User:** Changed the class package ([0bb98d5](https://github.com/GeorgeV220/SkinOverlay/commit/0bb98d5283208adb33f5517553aa3ca654afae99))


### Features

* Add CompletableFutureManager and getLoadedUsers() ([7c47b19](https://github.com/GeorgeV220/SkinOverlay/commit/7c47b197d6d4d7e61e98890eed02d39a832f6097))
* **Command:** Reintroduce command message ([40a9745](https://github.com/GeorgeV220/SkinOverlay/commit/40a974516bcfffe76558d8114b1ccd732751d432))
* Enhance PlayerObject methods with the UserEvent#getUser() ([58c07b0](https://github.com/GeorgeV220/SkinOverlay/commit/58c07b06dce719d9c072e8cfb88c31369efc9e06))
* **SGameProfile:** Add extra constructor with name, UUID, and skin parameters ([db13424](https://github.com/GeorgeV220/SkinOverlay/commit/db134244e2ebfd4bface1e1922526b8e81e9a476))
* **Skin:** Added Skin class to manage the player skins ([d4ed272](https://github.com/GeorgeV220/SkinOverlay/commit/d4ed2726f5ec532fbb4d2b9658b2ff300e76596b))
* **Skin:** extends Skin to Entity ([7eb5d14](https://github.com/GeorgeV220/SkinOverlay/commit/7eb5d14bf8c13ee15b63d007d2c66195ca6e37bf))
* **SkinHandler:** Full usage of the Skin class ([042e1de](https://github.com/GeorgeV220/SkinOverlay/commit/042e1deef2ce184e80765fd7291c88e76f218210))
* **SkinOptions:** Added static methods to de/serialize SkinOptions ([c4fef62](https://github.com/GeorgeV220/SkinOverlay/commit/c4fef624783ac4dcee762a773c62be3e71f9c209))
* **SkinOverlay:** Added isPluginEnabled ([4bd5663](https://github.com/GeorgeV220/SkinOverlay/commit/4bd566373a038fc53477ca887419eea63b37b776))
* **SkinOverlay:** Implement overlay storage caching to optimize performance and avoid repeated requests. ([6bebf7c](https://github.com/GeorgeV220/SkinOverlay/commit/6bebf7c26afe2b72bb48f7937ef5353f7843f13e))
* **SkinOverlay:** Skins storage ([bf4f39e](https://github.com/GeorgeV220/SkinOverlay/commit/bf4f39e687f7605f02df7f6535574ce763f70db9))
* Skins SQL table and Mongo collection ([266aa50](https://github.com/GeorgeV220/SkinOverlay/commit/266aa5093c1b779a1d234488f33c0f7d251b1770))
* **User:** Added User class ([c59a047](https://github.com/GeorgeV220/SkinOverlay/commit/c59a0470cf50a45e6cfc6ee3dcef4bde0ebd38e1))
* **Utilities:** Generate UUID from String seed ([fec41e1](https://github.com/GeorgeV220/SkinOverlay/commit/fec41e126d4b95380cd69985bbe18b71cf144847))
* **Utilities:** Removed deprecated method ([33e24b8](https://github.com/GeorgeV220/SkinOverlay/commit/33e24b8ad1ded36ed26bd12c971a25a26ec20b14))


### BREAKING CHANGES

* **SkinHandler:** The way internal user saves work has been changed. You should remove the old user data for File, SQL, and Mongo. Additionally, if you are using SQL tables or Mongo collection, delete them. The plugin is going through a lot of changes, and the version 5.x will not be backwards compatible.

# [4.1.0](https://github.com/GeorgeV220/SkinOverlay/compare/v4.0.0...v4.1.0) (2023-04-23)


### Bug Fixes

* **Event:** Override isCancel and cancel methods ([650df6b](https://github.com/GeorgeV220/SkinOverlay/commit/650df6bbfc068895ae1ac92be6754e078ed475b8))
* **Listener:** Register PlayerListeners ([d0a5d25](https://github.com/GeorgeV220/SkinOverlay/commit/d0a5d2570c445497f40ed6ff6fbef7c2724cc764))
* **User:** Save user data only to the proxy if it is running in proxy mode. ([2f5a920](https://github.com/GeorgeV220/SkinOverlay/commit/2f5a92093bf171ea9a6bdc32f2c921694c0fa83f))


### Features

* **Event:** Refactored package structure and event invocation method. ([3861de3](https://github.com/GeorgeV220/SkinOverlay/commit/3861de3a9f9b9dc9ccc69520d2b0e933524aac7e))
* **Events:** Improve management of Player connections and skin parts with new events. ([1768d28](https://github.com/GeorgeV220/SkinOverlay/commit/1768d2879a4090c8fd303eb7fcd784f1717475ff))
* **Exception:** Added EventException ([86b6d63](https://github.com/GeorgeV220/SkinOverlay/commit/86b6d63a950b3a6860a5b90c6a313cfa4393f8df))
* **SkinOverlay:** Added JavaDocs, new methods and changed old ones to public ([3f674a6](https://github.com/GeorgeV220/SkinOverlay/commit/3f674a67216baf601a22eed2661c674dcea784d8))
* **Sponge:** Dropped Sponge support ([8d46a21](https://github.com/GeorgeV220/SkinOverlay/commit/8d46a210fd74af622b6d77aca551791b0abd59fd))

# [4.0.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.18.0...v4.0.0) (2023-04-17)


### Features

* **GlowStone:** Added GlowStone support ([082aa4d](https://github.com/GeorgeV220/SkinOverlay/commit/082aa4d7c467805a9a43e6d5c11d24bd05e5a6b5))
* **ObservableListener:** Changed UserManagerListener to ObservableListener ([845cf67](https://github.com/GeorgeV220/SkinOverlay/commit/845cf673fe295fda95abadba158e4ce9502997a6))


### BREAKING CHANGES

* **ObservableListener:** Bumped major version due to extensive internal API changes

# [3.18.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.17.0...v3.18.0) (2023-04-15)


### Bug Fixes

* Bukkit Async Skin Update ([ac14a77](https://github.com/GeorgeV220/SkinOverlay/commit/ac14a77d7fd14ee661f168b7f6c1ce9c83496a91))
* Fixed ChannelNameTooLongException ([b478004](https://github.com/GeorgeV220/SkinOverlay/commit/b4780044387fb7a96aa311cf2ca49037efdac68f))
* IllegalStateException Asynchronous player tracker update on thread ([92e9b2d](https://github.com/GeorgeV220/SkinOverlay/commit/92e9b2d4b4dabae0ce9d39dc3ab9896fe9635bdc))
* **Listeners:** Fixed all proxy and bukkit listeners ([34ec157](https://github.com/GeorgeV220/SkinOverlay/commit/34ec1571ce1861fa5934dc5254adb581fccd1815))
* **PlayerObject:** Remove player from onlinePlayers and error handling ([a6355da](https://github.com/GeorgeV220/SkinOverlay/commit/a6355da8d0bde7ba7e9b7eb84da74923927fc96c))
* **Proxy:** Fixed Bungee updateSkin getServer() null exception ([0d929ad](https://github.com/GeorgeV220/SkinOverlay/commit/0d929ad63859ea65c5d41fafbb3d898bd56dd83c))
* **Proxy:** Fixed Plugin Messaging ([2541202](https://github.com/GeorgeV220/SkinOverlay/commit/25412026ed142816c4c88212305e1be67c0d61d7))
* **SkinOverlayImpl:** Made print a default ([e4fe0f6](https://github.com/GeorgeV220/SkinOverlay/commit/e4fe0f6f62f9caf3e14db9263a8e89b042f85cf0))
* **SProperty:** Fixed SProperty null pointer exception ([c2329a4](https://github.com/GeorgeV220/SkinOverlay/commit/c2329a4f39ce93802bf007e53910b14cc826705c))
* Updater spamming in the console ([2cf4dd0](https://github.com/GeorgeV220/SkinOverlay/commit/2cf4dd0cb0cd12f622c4c1ed06a35c10e36d0c52))


### Features

* **Events:** Changed how the Events work ([2d32528](https://github.com/GeorgeV220/SkinOverlay/commit/2d32528d98496479c9565e66cb6bf123b2e11149))
* **Paper:** Added Paper listener ([38f4953](https://github.com/GeorgeV220/SkinOverlay/commit/38f4953ad83f76343952e6f6938cdfdababa0e58))
* **SkinHookImpl:** Added SkinHookImpl ([20cc18c](https://github.com/GeorgeV220/SkinOverlay/commit/20cc18c57b5da20027cd6fc317159b1aabdd6de0))
* **SkinOverlayImpl:** Changed onlinePlayers to ObservableObjectMap ([bc43b5b](https://github.com/GeorgeV220/SkinOverlay/commit/bc43b5b7635e05130d670317a74a28687625d849))

# [3.17.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.16.0...v3.17.0) (2023-04-11)


### Bug Fixes

* **Events:** Fixed PlayerObjectUpdateSkinEvent ([da51ce5](https://github.com/GeorgeV220/SkinOverlay/commit/da51ce525158d804f22d1b8b825d99e9aa72d52e))
* **SGameProfile:** Fixed addProperty method ([803fafe](https://github.com/GeorgeV220/SkinOverlay/commit/803fafe0847c7edfb527f559ba988630cbb5f094))


### Features

* **Debug:** Added Debugging listeners ([4d07eb8](https://github.com/GeorgeV220/SkinOverlay/commit/4d07eb8e4e98ca38bb8c6a79d137acd6a4de4105))
* **Events:** Added Events API ([ecaf5d2](https://github.com/GeorgeV220/SkinOverlay/commit/ecaf5d246a954963cc86fb9b29c53f25c34d3ee8))
* **Events:** Added new Events ([7a7ff89](https://github.com/GeorgeV220/SkinOverlay/commit/7a7ff89bdd6afdf25887da6d618453ed4dcc9a7d))
* **Events:** Call the events ([72251f7](https://github.com/GeorgeV220/SkinOverlay/commit/72251f77248531cb8bc14f2c48ab22d9a88b781e))
* **Exception:** Added UserException ([99bbb34](https://github.com/GeorgeV220/SkinOverlay/commit/99bbb343263e01d23f021b8f02b379be102ca816))
* **Utilities:** Added Utilities.getMethodsAnnotatedWith ([6342039](https://github.com/GeorgeV220/SkinOverlay/commit/6342039aeb09947075e533f7e86573cb778ac4b5))

# [3.16.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.15.0...v3.16.0) (2023-04-09)


### Bug Fixes

* **Listeners:** Event priority ([e28d5ef](https://github.com/GeorgeV220/SkinOverlay/commit/e28d5efbb3e3bd70f640600a2428195a6d40055b))


### Features

* **SkinOverlay:** Fixed (de)serialization ([3ed625a](https://github.com/GeorgeV220/SkinOverlay/commit/3ed625a2fc7a1bbe4d3ea39e4a8d02b4ba84b5a9))
* UserManagerListener ([0970fa7](https://github.com/GeorgeV220/SkinOverlay/commit/0970fa72fb6880d2f74f0885ed9e83f3dbcc0650))

# [3.15.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.14.0...v3.15.0) (2023-04-06)


### Bug Fixes

* **PluginMessaging:** Added a check if the source is Server ([5a7698c](https://github.com/GeorgeV220/SkinOverlay/commit/5a7698cea1d6849f94088e0f743b811814282f48))
* **SkinHandler:** BungeeCord property signature ([f691876](https://github.com/GeorgeV220/SkinOverlay/commit/f6918761a69284c611264de941699bfa0ebd4404))
* **SkinHandler:** Fixed SkinHandler_BungeeCord.getGameProfile0 ([9f9c093](https://github.com/GeorgeV220/SkinOverlay/commit/9f9c0931e73a2100b512dfcc429797622ed4f691))
* **SkinHandler:** Fixed SkinHandler.setSkin method ([9201640](https://github.com/GeorgeV220/SkinOverlay/commit/92016402e5b3a299ba4dd12217ab96946ba8cc4b))


### Features

* **SkinHandler:** Removed Callback from updateSkin ([371f16d](https://github.com/GeorgeV220/SkinOverlay/commit/371f16d9417e0e592403826a0ab87c897e7551f2))
* **Type:** change PAPER to BUKKIT ([fd057db](https://github.com/GeorgeV220/SkinOverlay/commit/fd057db8704fde426c9ae7911df626eaff1e89ec))

# [3.14.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.13.0...v3.14.0) (2023-04-02)


### Features

* Added SkinHandler classes for the proxies ([1c30d7f](https://github.com/GeorgeV220/SkinOverlay/commit/1c30d7f0d3b2f54c201789fd1296e01e697c215c))
* **PluginMessaging:** Use Plugin Messaging API ([c2e016b](https://github.com/GeorgeV220/SkinOverlay/commit/c2e016b1383ae78598fdc86a38e19bf9cbd5aa5d))
* **SkinHandler:** Moved set/updateSkin to SkinHandler ([020b545](https://github.com/GeorgeV220/SkinOverlay/commit/020b545220587948857c73cbcf1271adc3afa64d))

# [3.13.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.12.2...v3.13.0) (2023-04-02)


### Bug Fixes

* Fixed PROXY Option ([0aae418](https://github.com/GeorgeV220/SkinOverlay/commit/0aae4184d3c028f28fc61302da048c328863e224))


### Features

* A lot of internal changes ([06d9a67](https://github.com/GeorgeV220/SkinOverlay/commit/06d9a67d9af1a4667f6a29d4f80ad920498fe0d0)), closes [SkinHandler#getGameProfile0](https://github.com/SkinHandler/issues/getGameProfile0)
* **Proxy:** Encrypt Bungee plugin messaging ([3a68882](https://github.com/GeorgeV220/SkinOverlay/commit/3a68882a7c1b01f0e376f9a294becade18ff7c11))

## [3.12.2](https://github.com/GeorgeV220/SkinOverlay/compare/v3.12.1...v3.12.2) (2023-03-18)


### Bug Fixes

* **Adventure:** Fixed adventure ([5f7ac73](https://github.com/GeorgeV220/SkinOverlay/commit/5f7ac73da890a1970e5272084b7b2b811c8573fa))
* **Proxy:** Update skin on server switch ([593756e](https://github.com/GeorgeV220/SkinOverlay/commit/593756e4eff48718ad1e3ed1d20dd048d7f754e2))
* **SkinHandler:** Fixed Legacy SkinHandler ([4881d14](https://github.com/GeorgeV220/SkinOverlay/commit/4881d140a5e49672d6b33859d6d6943c7146ef88))
* **SkinHandler:** Fixed legacy SkinHandler index ([9d1df53](https://github.com/GeorgeV220/SkinOverlay/commit/9d1df533c0df60be4150f1e85c54daa70ffcb9c7))
* SkinsRestorer hook ([1a184a4](https://github.com/GeorgeV220/SkinOverlay/commit/1a184a41f21fc6851fd83d234f216044de9c026f))
* **SkinsRestorer:** More fixes for SkinsRestorer hook ([d1d6505](https://github.com/GeorgeV220/SkinOverlay/commit/d1d6505b1783bada0d2d2d26a4b2941faca3cf62))

## [3.12.1](https://github.com/GeorgeV220/SkinOverlay/compare/v3.12.0...v3.12.1) (2023-03-17)


### Bug Fixes

* 1_19_R3 getGameProfile0 ([608dd87](https://github.com/GeorgeV220/SkinOverlay/commit/608dd8794ffabcd3e03c5927d9d9fdad28844cb3))
* proxy updateSkin ([d716fd1](https://github.com/GeorgeV220/SkinOverlay/commit/d716fd1680ccd1625fd9969f51023f5a4452d606))

# [3.12.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.11.0...v3.12.0) (2023-03-17)


### Bug Fixes

* **SkinOptions:** Improve proxy skin handlers ([b847a9d](https://github.com/GeorgeV220/SkinOverlay/commit/b847a9da70a9da0b8309d3c37883b78466de945b))
* **SkinOptions:** Serializable ([a116c79](https://github.com/GeorgeV220/SkinOverlay/commit/a116c79ea92682f6c729affd6bec41d70a7be3f8))
* URL subcommand ([fb26a09](https://github.com/GeorgeV220/SkinOverlay/commit/fb26a098e6d8c6c9c98d46f16ee70fda89d8db29))


### Features

* 1.19.4 support ([3856a51](https://github.com/GeorgeV220/SkinOverlay/commit/3856a51829c2f60429bac2b578f9c4517c3addce))
* Default database type ([d518263](https://github.com/GeorgeV220/SkinOverlay/commit/d518263d4ff16e1c4a284688c03230c537601ccf))
* **PlayerObject:** change skinName to skinOptions ([13f26fd](https://github.com/GeorgeV220/SkinOverlay/commit/13f26fd321e60ecf6d78cb34d9a028dffca2e76e))
* **SkinHandler:** SkinsRestorer custom skins hook ([5762022](https://github.com/GeorgeV220/SkinOverlay/commit/57620225b0f89d96504e2e675f3fa866d15a86cf))
* **SkinOptions:** changed setSkin constructor String skinName to SkinOptions ([1413354](https://github.com/GeorgeV220/SkinOverlay/commit/1413354a369383f6441f8266249b5926595fce35))
* **SkinOptions:** Changed SkinOverlays to SkinOptions ([5d93797](https://github.com/GeorgeV220/SkinOverlay/commit/5d9379778482f37b60b0b8a473678f619ef16701))
* **SkinOptions:** changed updateSkin vararg String skinName to SkinOptions ([eb98186](https://github.com/GeorgeV220/SkinOverlay/commit/eb98186aa4dcac627a6f28dfed427f86e57b7e12))
* **SkinOverlayCommand:** Added url sub command ([00656ad](https://github.com/GeorgeV220/SkinOverlay/commit/00656adbed47adb1dd90b127ccf2a676a379ed1b))
* Updated onPluginMessageReceived to accept SkinOptions ([136dfb5](https://github.com/GeorgeV220/SkinOverlay/commit/136dfb571b123344269b125c1093a1fef5ea7b51))
* **Utilities:** proper usage of SkinOptions ([b0b20c9](https://github.com/GeorgeV220/SkinOverlay/commit/b0b20c980dc539ab4f7ea7d5ab017cea689106df))
* **Velocity:** Update everything to use the new SkinOptions ([d3c1bc8](https://github.com/GeorgeV220/SkinOverlay/commit/d3c1bc8ee3eff490b44b89ce90203a1b6a0294ea))

# [3.11.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.10.0...v3.11.0) (2023-03-10)


### Bug Fixes

* Check if a username is premium ([6eab834](https://github.com/GeorgeV220/SkinOverlay/commit/6eab83492232334f4398536c240ff84d6b74642d))
* **SkinHandler:** Fixed SkinHandlers on Spigot ([a045ac6](https://github.com/GeorgeV220/SkinOverlay/commit/a045ac66dece1c7bfa1eb441bd40682fa6b512da))


### Features

* **User:** New config option for default skin UUID ([7cbef7d](https://github.com/GeorgeV220/SkinOverlay/commit/7cbef7d28ca77f6642958e5ce1052d30d470afba))
* **User:** Removed Deprecated User class ([7711190](https://github.com/GeorgeV220/SkinOverlay/commit/7711190affe0d26ca1a39c96fb14042c7d09b9e7))

# [3.10.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.9.0...v3.10.0) (2023-03-09)


### Bug Fixes

* SSLHandshakeException ([587c304](https://github.com/GeorgeV220/SkinOverlay/commit/587c3041f3a2c7940e64584d8c7926514b3d76c3))


### Features

* New permission method ([f00b729](https://github.com/GeorgeV220/SkinOverlay/commit/f00b729d89367eee1835c01bcb48340b96f2fab9))
* print method ([ae239ba](https://github.com/GeorgeV220/SkinOverlay/commit/ae239bae258c92f2d320b8c6f0fe2d04206e4f41))
* **Updater:** Send the update message to player ([990bac2](https://github.com/GeorgeV220/SkinOverlay/commit/990bac2e0aad7776d52fbf2f574d6f7b1384af16))

# [3.9.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.8.0...v3.9.0) (2023-02-13)


### Features

* Reworked the entire database ([f10ec9a](https://github.com/GeorgeV220/SkinOverlay/commit/f10ec9aea1acb652ccd62342755533700949a72f))

# [3.8.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.7.3...v3.8.0) (2023-02-11)


### Features

* kyori Audience support ([642dec8](https://github.com/GeorgeV220/SkinOverlay/commit/642dec8576b779343e297da4610e4b28a9d5a7c2))
* SkinOverlays and OptionsUtil changes ([cae60d7](https://github.com/GeorgeV220/SkinOverlay/commit/cae60d7dce1e25408c17e0848b6244ab72d4be3c))

## [3.7.3](https://github.com/GeorgeV220/SkinOverlay/compare/v3.7.2...v3.7.3) (2023-02-07)


### Bug Fixes

* **MessagesUtil:** Fixed msg(CommandIssuer) method ([11751d6](https://github.com/GeorgeV220/SkinOverlay/commit/11751d64515eb19c906e1e422b18f1bc9a091dd5))

## [3.7.2](https://github.com/GeorgeV220/SkinOverlay/compare/v3.7.1...v3.7.2) (2023-02-06)


### Bug Fixes

* config.yml ([6207c04](https://github.com/GeorgeV220/SkinOverlay/commit/6207c048e9de9d23eb6abb0532c4fa330a48e948))

## [3.7.1](https://github.com/GeorgeV220/SkinOverlay/compare/v3.7.0...v3.7.1) (2023-02-05)


### Bug Fixes

* Callback and 1_19_R2 fix ([e15dc62](https://github.com/GeorgeV220/SkinOverlay/commit/e15dc62a076ec3f9eca6d0d397088387fc20a1d3))
* Save only loaded users ([e9bf334](https://github.com/GeorgeV220/SkinOverlay/commit/e9bf334aaa6f2d99d20347f571f77a070a5fd772))

# [3.7.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.6.0...v3.7.0) (2023-01-30)


### Features

* Added bStats Metrics ([93b91b1](https://github.com/GeorgeV220/SkinOverlay/commit/93b91b1c8ba263d979f03d3300834a2d3bb43d0d))
* Added Updater (WIP) ([3edd6c8](https://github.com/GeorgeV220/SkinOverlay/commit/3edd6c81a676462dece5e7d237f58a0be203e8d0))
* Listeners and dependencies. ([9ef1ba4](https://github.com/GeorgeV220/SkinOverlay/commit/9ef1ba40d3418194e051514b710e9882fbc41130))

# [3.6.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.5.0...v3.6.0) (2023-01-22)


### Features

* **Sponge7:** Sponge7 support ([ed0c892](https://github.com/GeorgeV220/SkinOverlay/commit/ed0c892966b1f5cd2dfa988514918466961c9106))

# [3.5.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.4.2...v3.5.0) (2023-01-19)


### Features

* Added User#getPlayer ([0621811](https://github.com/GeorgeV220/SkinOverlay/commit/06218112e85a45f7ec6b610d0148075fcf220832))
* new PlayerObject implementation ([46462f6](https://github.com/GeorgeV220/SkinOverlay/commit/46462f6ca8c475b558e920ea94bd89cae10f0868))
* Removed PlayerObjectWrapper ([3bf806a](https://github.com/GeorgeV220/SkinOverlay/commit/3bf806a3fb6a5cd785f1dd06cb19ef01428549da))
* **SkinOverlay:** getPlayer and isOnline methods ([21f78da](https://github.com/GeorgeV220/SkinOverlay/commit/21f78da56186610c02513ced414d39147b2d98ac))

## [3.4.2](https://github.com/GeorgeV220/SkinOverlay/compare/v3.4.1...v3.4.2) (2023-01-18)


### Bug Fixes

* **Sponge:** Fixed Sponge_SkinHandler for all versions. ([01acad4](https://github.com/GeorgeV220/SkinOverlay/commit/01acad4bd510884c72df57b9b6149d2ccab6e0d9))

## [3.4.1](https://github.com/GeorgeV220/SkinOverlay/compare/v3.4.0...v3.4.1) (2023-01-17)


### Bug Fixes

* **Sponge:** Fixed Sponge_SkinHandler for versions below 1.18 ([87bfd7f](https://github.com/GeorgeV220/SkinOverlay/commit/87bfd7f0fa5819166dcfe9c6b5604c5608b01e09))

# [3.4.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.3.0...v3.4.0) (2023-01-17)


### Bug Fixes

* **Property:** Get correct GameProfile property ([2245006](https://github.com/GeorgeV220/SkinOverlay/commit/224500641b7e26234593b16932ed2b8b7bf2c20b))
* Show if experimental features are enabled ([9257ad5](https://github.com/GeorgeV220/SkinOverlay/commit/9257ad503c4a40d7fa246a683cade37621f38fbe))


### Features

* **SkinHandler:** Sponge SkinHandler ([1aeafb1](https://github.com/GeorgeV220/SkinOverlay/commit/1aeafb1f046248ba6453fd6d80eaeb08a6cacf10))

# [3.3.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.2.0...v3.3.0) (2023-01-16)


### Bug Fixes

* **SkinOverlay:** call setupCommands method ([5afd1cd](https://github.com/GeorgeV220/SkinOverlay/commit/5afd1cd1b081aa3689dc6630ae0b29d7aa0b5082))


### Features

* **PlayerObject:** Added gameProfile method ([b5de2f2](https://github.com/GeorgeV220/SkinOverlay/commit/b5de2f2731e41da51ada1003bbfc1295933ad637))
* **SkinHandler:** Sponge SkinHandler (WIP) ([79606a0](https://github.com/GeorgeV220/SkinOverlay/commit/79606a040b86b9a8c54726240d08e7ebed157d57))

# [3.2.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.1.0...v3.2.0) (2023-01-15)


### Bug Fixes

* **PlayerObjectWrapper:** Fixed wrapper issues with velocity ([d8dfc52](https://github.com/GeorgeV220/SkinOverlay/commit/d8dfc520d5a16509adf06232a6e18fa4ef23f315))
* **Sponge:** Fixed commands on Sponge ([78a72ab](https://github.com/GeorgeV220/SkinOverlay/commit/78a72abcf400b663c5c0d9eeeec3447f4691dc93))
* **Sponge:** Small changes on how to construct the plugin ([c1fbc6e](https://github.com/GeorgeV220/SkinOverlay/commit/c1fbc6ec5b817e45872ce9e0c3f2c2404c3b1fab))


### Features

* **Sponge:** Added Sponge8-9 support ([5c4437f](https://github.com/GeorgeV220/SkinOverlay/commit/5c4437fcbdb24da88cce0f1efd40eb63a2f61cbf))

# [3.1.0](https://github.com/GeorgeV220/SkinOverlay/compare/v3.0.0...v3.1.0) (2023-01-12)


### Bug Fixes

* **SkinOverlayBukkit:** UNKNOWN case for the SkinHandler ([b36d6be](https://github.com/GeorgeV220/SkinOverlay/commit/b36d6be4c0510b301d2ad906f17f1b9a7cbc50dc))


### Features

* **UserData:** Cache UserData database type ([19e9c7a](https://github.com/GeorgeV220/SkinOverlay/commit/19e9c7a23021a19842dee035d9bbb18d5455be19))

# [3.0.0](https://github.com/GeorgeV220/SkinOverlay/compare/v2.1.0...v3.0.0) (2023-01-11)


### Bug Fixes

* Changed SkinHandler fromProperties boolean to Property ([6fc4532](https://github.com/GeorgeV220/SkinOverlay/commit/6fc45320cad197e0200430f06b85782b8d09f1d9))


### Features

* Moved SkinHandler.Request to Utilities ([b8bb9d9](https://github.com/GeorgeV220/SkinOverlay/commit/b8bb9d95a6dc5c3c07e3dfe1198381a5ebca42b2))
* **SkinHandler_Legacy:** Added SkinHandler_Legacy ([31a49c0](https://github.com/GeorgeV220/SkinOverlay/commit/31a49c005a92c4bcd8692d4b0d5db51a82aff9e3))


### BREAKING CHANGES

* **SkinHandler_Legacy:** Added new methods and change of the old ones.

# [2.1.0](https://github.com/GeorgeV220/SkinOverlay/compare/v2.0.0...v2.1.0) (2023-01-07)


### Bug Fixes

* **Command:** Fixed messages ([f72d5d4](https://github.com/GeorgeV220/SkinOverlay/commit/f72d5d4a99c203846cfc59fb859af4e9c5f84366))
* **Velocity:** ACF Locates for velocity ([1fa59a2](https://github.com/GeorgeV220/SkinOverlay/commit/1fa59a2453f29992cbf502a88c09ed733b3f5b24))


### Features

* Added SkinOverlayImpl#getServerImpl ([b143b2e](https://github.com/GeorgeV220/SkinOverlay/commit/b143b2e5724fbe204947922eb8bc4a703843ea5f))
* **Options:** Changed BUNGEE to PROXY ([6a0024a](https://github.com/GeorgeV220/SkinOverlay/commit/6a0024a567dfb814c655a61b9285b526861151cc))

# [2.0.0](https://github.com/GeorgeV220/SkinOverlay/compare/v1.0.2...v2.0.0) (2023-01-06)


### Bug Fixes

* **gradle.yml:** Bump to node v18 ([c2df873](https://github.com/GeorgeV220/SkinOverlay/commit/c2df873951327230afe3f8ab814d12bde76118fa))


### Features

* **MessagesUtil:** Send messages correctly ([7a9a3ae](https://github.com/GeorgeV220/SkinOverlay/commit/7a9a3aee6037e9443f76327b6b3a5878db294207))
* **SkinOverlayBungee:** BungeeCord SkinHandler ([029f4c0](https://github.com/GeorgeV220/SkinOverlay/commit/029f4c0cc665347bc07ae78b35a62944c7983de6))
* **SkinOverlayBungee:** Types ([5048f2e](https://github.com/GeorgeV220/SkinOverlay/commit/5048f2ebb3f1a210f832f53e9f132f392664219f))
* **SkinOverlayCommand:** Send messages correctly ([2e86822](https://github.com/GeorgeV220/SkinOverlay/commit/2e8682269d9d20706e20d4f1ed2c7cc6e2f5d0fa))
* **Velocity:** Velocity support!! ([816d005](https://github.com/GeorgeV220/SkinOverlay/commit/816d005d1711ff426e73edf76a392374a3af02f3))


### BREAKING CHANGES

* **Velocity:** A lot of APIs changed

## [1.0.2](https://github.com/GeorgeV220/SkinOverlay/compare/v1.0.1...v1.0.2) (2022-12-29)


### Bug Fixes

* **1_19_R2:** Fix player re-add packet ([de0bc3a](https://github.com/GeorgeV220/SkinOverlay/commit/de0bc3a96a294661a0b3aade8ae5152bfb95f709))
* **ServerConnectedEvent:** Fixed a bug when a player disconnects immediately after login. ([9d0d0ae](https://github.com/GeorgeV220/SkinOverlay/commit/9d0d0aedf3cc2fe8dba3de8d926bc73260325dc6))

## [1.0.1](https://github.com/GeorgeV220/SkinOverlay/compare/v1.0.0...v1.0.1) (2022-12-27)


### Bug Fixes

* Removed unused class ([b8f2d90](https://github.com/GeorgeV220/SkinOverlay/commit/b8f2d904f2c72400447180fd217f3a79eb0950c5))

# 1.0.0 (2022-12-27)


### Bug Fixes

* **Command:** Fixed overlay command arguments ([c07f57e](https://github.com/GeorgeV220/SkinOverlay/commit/c07f57ea2db7e6f9c797a02e74a701f491bf2768))
* Fix update-versions.sh ([1a43484](https://github.com/GeorgeV220/SkinOverlay/commit/1a434849486d89713957d6e2fd4a187e085470ef))
* **Listener:** Register forgotten listener ([28b1472](https://github.com/GeorgeV220/SkinOverlay/commit/28b1472737875d35a055f275d6fec2f10345c8bc))
* **MongoDB:** Fixed the MongoDB implementation ([e1fb90a](https://github.com/GeorgeV220/SkinOverlay/commit/e1fb90a5e76f53b7653d65006647de2b73b18b94))
* **update-versions.sh:** Added update-versions.sh script ([94a2302](https://github.com/GeorgeV220/SkinOverlay/commit/94a2302893e4bb9ec94c2bc54621403594ac9c7d))


### Features

* **BungeeCord:** BungeeCord first code ([b539442](https://github.com/GeorgeV220/SkinOverlay/commit/b539442ef21ea4b91c63f0b6fb007c657a2ef7d7))
* **Completion:** Added command completion ([c3d928e](https://github.com/GeorgeV220/SkinOverlay/commit/c3d928eb3ea6e11eb75db2a9446c4a6c72a3227c))
* **IDatabaseType:** Reset ([e9665fe](https://github.com/GeorgeV220/SkinOverlay/commit/e9665fe55294c89a3d563873bc144b519459132d))
* **SkinHandler:** Update SkinHandler for BungeeCord and unsupported minecraft version ([f414e3e](https://github.com/GeorgeV220/SkinOverlay/commit/f414e3ea36827418fe3ec5a83fc083470757a578))
* **Utilities:** Added createGameProfile method ([d205b09](https://github.com/GeorgeV220/SkinOverlay/commit/d205b09c7406cd0c770cbd40e98fd428304a1634))
