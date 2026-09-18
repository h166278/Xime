# 相对上游的专属改动

- 本仓 HEAD：`e29e6b7`（main）
- 上游 HEAD：`277eebe Merge pull request #886 from kingzcheung/main`
- 分叉点：`2751c9c2adad680100d9ae232d5cecf2651a989a`
- 专属提交：96 个（未计入本归档提交）
- 新增 27 个文件，修改 34 个，删除 0 个

对照仓库：

- 本仓 https://github.com/h166278/Xime
- 上游 https://github.com/ximeiorg/Xime

`新增/` 是专属新文件全文。`修改/` 是相对分叉点的 unified diff（改动处，不是整文件）。工作树未提交改动已打进对应文件。

## 新增

- `app/src/main/java/com/kingzcheung/xime/service/CommitStack.kt`
- `app/src/main/java/com/kingzcheung/xime/service/ImeKeyRouterLogic.kt`
- `app/src/main/java/com/kingzcheung/xime/service/InputBoxComposing.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/EnglishPunctOverlay.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/FeiKeyHint.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/KeyboardLayoutLogic.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/LongPressDefaultIndex.kt`
- `app/src/test/java/com/kingzcheung/xime/service/CommitStackTest.kt`
- `app/src/test/java/com/kingzcheung/xime/service/FullwidthSemicolonTapPlanTest.kt`
- `app/src/test/java/com/kingzcheung/xime/service/IdleDeletePlanTest.kt`
- `app/src/test/java/com/kingzcheung/xime/service/InlineAsciiShiftRoutingTest.kt`
- `app/src/test/java/com/kingzcheung/xime/service/InputBoxComposingPlanTest.kt`
- `app/src/test/java/com/kingzcheung/xime/service/Layout46SymbolTapPlanTest.kt`
- `app/src/test/java/com/kingzcheung/xime/service/RimePunctKeyCodeTest.kt`
- `app/src/test/java/com/kingzcheung/xime/service/RimeUpperKeyCodeTest.kt`
- `app/src/test/java/com/kingzcheung/xime/service/UndoClearedPlanTest.kt`
- `app/src/test/java/com/kingzcheung/xime/service/WarmStartSchemaReselectTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/ArmedSwipeTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/ComposingLetterSwipeUpTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/ComposingShiftSwipeUpBubbleTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/EnglishPunctOverlayTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/FeiKeyHintTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/IdleSymbolTapTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/Landscape46SemicolonRowTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/LongPressDefaultIndexTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/ShiftSwipeZoneTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/SplitSchemaSpaceLabelTest.kt`

## 修改

- `.github/workflows/build.yml`
- `.github/workflows/nightly.yml`
- `.github/workflows/release.yml`
- `app/build-logic/tasks-plugin-dev.gradle.kts`
- `app/build.gradle.kts`
- `app/src/main/assets/default.custom.yaml`
- `app/src/main/assets/xime.yaml`
- `app/src/main/java/com/kingzcheung/xime/keyboard/GestureAction.kt`
- `app/src/main/java/com/kingzcheung/xime/rime/RimeConfigHelper.kt`
- `app/src/main/java/com/kingzcheung/xime/rime/RimeEngine.kt`
- `app/src/main/java/com/kingzcheung/xime/service/CandidateAction.kt`
- `app/src/main/java/com/kingzcheung/xime/service/ImeKeyRouter.kt`
- `app/src/main/java/com/kingzcheung/xime/service/ImeKeyboardCallbacks.kt`
- `app/src/main/java/com/kingzcheung/xime/service/ImeSchemaController.kt`
- `app/src/main/java/com/kingzcheung/xime/service/ImeSessionController.kt`
- `app/src/main/java/com/kingzcheung/xime/service/XimeInputMethodService.kt`
- `app/src/main/java/com/kingzcheung/xime/settings/KeysConfigHelper.kt`
- `app/src/main/java/com/kingzcheung/xime/settings/SettingsPreferences.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/CandidateBar.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/KeyButton.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/KeyboardCallbacks.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/KeyboardLayout.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/KeyboardLayoutScreen.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/KeyboardView.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/MenuBar.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/keyboard/SwipeBubble.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/settings/AboutScreen.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/settings/LayoutDisplaySettingsScreen.kt`
- `app/src/main/java/com/kingzcheung/xime/ui/settings/SettingsComponents.kt`
- `app/src/main/java/com/kingzcheung/xime/viewmodel/KeyboardViewModel.kt`
- `app/src/main/jni/librime_jni/rime_jni.cc`
- `app/src/test/java/com/kingzcheung/xime/rime/DefaultCustomYamlTest.kt`
- `app/src/test/java/com/kingzcheung/xime/settings/KeyboardGestureConfigTest.kt`
- `app/src/test/java/com/kingzcheung/xime/ui/keyboard/KeyboardResponsiveSizingTest.kt`

## 删除

（无）
