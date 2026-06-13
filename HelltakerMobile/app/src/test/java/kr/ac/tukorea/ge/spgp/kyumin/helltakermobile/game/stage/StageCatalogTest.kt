package kr.ac.tukorea.ge.spgp.kyumin.helltakermobile.game.stage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StageCatalogTest {
    @Test
    fun stageSequenceUsesEightContinuousInternalStages() {
        assertEquals((1..8).toList(), StageCatalog.stageNumbers)
        assertEquals(8, StageCatalog.selectionEntries.last().displayNo)
        assertEquals(8, StageCatalog.selectionEntries.last().stageNo)
    }

    @Test
    fun nextStageFollowsCatalogOrderAndEndsAfterStageEight() {
        for (stageNo in 1 until 8) {
            assertEquals(stageNo + 1, StageCatalog.nextStageNo(stageNo))
        }
        assertNull(StageCatalog.nextStageNo(8))
    }

    @Test
    fun definitionsUseRenamedStageAssetPathsAndUniquePreloadLists() {
        for (stageNo in StageCatalog.stageNumbers) {
            val definition = StageCatalog.get(stageNo)
            assertEquals(
                "stages/stage${stageNo.toString().padStart(2, '0')}.csv",
                definition.assetPath,
            )
            assertTrue(definition.preloadResIds.isNotEmpty())
            assertEquals(
                definition.preloadResIds.distinct().size,
                definition.preloadResIds.size,
            )
        }
    }
}
