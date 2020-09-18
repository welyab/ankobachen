/*
 * Copyright (C) 2020 Welyab da Silva Paula
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
@file:Suppress("EXPERIMENTAL_UNSIGNED_LITERALS", "EXPERIMENTAL_API_USAGE")

package com.welyab.ankobachen

class MovementFlags(val flags: ULong) {

    //@formatter:off
    val isCapture        inline get() = flags.and(CAPTURE_MASK)         != 0uL
    val isEnPassant      inline get() = flags.and(EN_PASSANT_MASK)      != 0uL
    val isCastling       inline get() = flags.and(CASTLING_MASK)        != 0uL
    val isPromotion      inline get() = flags.and(PROMOTION_MASK)       != 0uL
    val isCheck          inline get() = flags.and(CHECK_MASK)           != 0uL
    val isDiscoveryCheck inline get() = flags.and(DISCOVERY_CHECK_MASK) != 0uL
    val isDoubleCheck    inline get() = flags.and(DOUBLE_CHECK_MASK)    != 0uL
    val isCheckmate      inline get() = flags.and(CHECKMATE_MASK)       != 0uL
    val isStalemate      inline get() = flags.and(STALEMATE_MASK)       != 0uL
    //@formatter:on

    override fun toString(): String =
        mutableListOf<String>()
            .apply {
                if (isCapture) add("capture")
                if (isEnPassant) add("enPassant")
                if (isCastling) add("castling")
                if (isPromotion) add("promotion")
                if (isCheck) add("check")
                if (isDiscoveryCheck) add("discoveryCheck")
                if (isDoubleCheck) add("doubleCheck")
                if (isCheckmate) add("checkmate")
                if (isStalemate) add("stalemate")
            }
            .joinToString()
            .let { if (it.isEmpty()) "[no flags]" else it }


    companion object {
        //@formatter:off
        const val CAPTURE_MASK         = 0b000000000000000000000001uL
        const val EN_PASSANT_MASK      = 0b000000000000000000000010uL
        const val CASTLING_MASK        = 0b000000000000000000000100uL
        const val PROMOTION_MASK       = 0b000000000000000000001000uL
        const val CHECK_MASK           = 0b000000000000000000010000uL
        const val DISCOVERY_CHECK_MASK = 0b000000000000000000100000uL
        const val DOUBLE_CHECK_MASK    = 0b000000000000000001000000uL
        const val CHECKMATE_MASK       = 0b000000000000000010000000uL
        const val STALEMATE_MASK       = 0b000000000000000100000000uL
        //@formatter:on
    }
}

@ExperimentalUnsignedTypes
class MovementMetadata private constructor(
    val nodesCount: Long,
    val captureCount: Long,
    val enPassantCount: Long,
    val castlingCount: Long,
    val promotionCount: Long,
    val checkCount: Long,
    val discoveryCheckCount: Long,
    val doubleCheckCount: Long,
    val checkmateCount: Long,
    val stalemateCount: Long
) {
    @Suppress("MemberVisibilityCanBePrivate")
    class Builder {

        private var nodesCount: Long = 0L
        private var captureCount: Long = 0L
        private var enPassantCount: Long = 0L
        private var castlingCount: Long = 0L
        private var promotionCount: Long = 0L
        private var checkCount: Long = 0L
        private var discoveryCheckCount: Long = 0L
        private var doubleCheckCount: Long = 0L
        private var checkmateCount: Long = 0L
        private var stalemateCount: Long = 0L

        fun addMetadata(metadata: MovementMetadata): Builder {
            incrementNodesCount(metadata.nodesCount)
            incrementCaptureCount(metadata.captureCount)
            incrementEnPassantCount(metadata.enPassantCount)
            incrementCastlingCount(metadata.castlingCount)
            incrementPromotionCount(metadata.promotionCount)
            incrementCheckCount(metadata.checkCount)
            incrementDiscoveryCheckCount(metadata.discoveryCheckCount)
            incrementDoubleCheckCount(metadata.doubleCheckCount)
            incrementCheckmateCount(metadata.checkmateCount)
            incrementStalemateCount(metadata.stalemateCount)
            return this
        }

        fun addFlags(flags: MovementFlags): Builder {
            incrementNodesCount()
            if (flags.isCapture) incrementCaptureCount()
            if (flags.isEnPassant) incrementEnPassantCount()
            if (flags.isCastling) incrementCastlingCount()
            if (flags.isPromotion) incrementPromotionCount()
            if (flags.isCheck) incrementCheckCount()
            if (flags.isDiscoveryCheck) incrementDiscoveryCheckCount()
            if (flags.isDoubleCheck) incrementDoubleCheckCount()
            if (flags.isCheckmate) incrementCheckmateCount()
            if (flags.isStalemate) incrementStalemateCount()
            return this
        }

        fun incrementNodesCount(value: Long = 1L): Builder {
            nodesCount += value
            return this
        }

        fun incrementCaptureCount(value: Long = 1L): Builder {
            captureCount += value
            return this
        }

        fun incrementEnPassantCount(value: Long = 1L): Builder {
            enPassantCount += value
            return this
        }

        fun incrementCastlingCount(value: Long = 1L): Builder {
            castlingCount += value
            return this
        }

        fun incrementPromotionCount(value: Long = 1L): Builder {
            promotionCount += value
            return this
        }

        fun incrementCheckCount(value: Long = 1L): Builder {
            checkCount += value
            return this
        }

        fun incrementDiscoveryCheckCount(value: Long = 1L): Builder {
            discoveryCheckCount += value
            return this
        }

        fun incrementDoubleCheckCount(value: Long = 1L): Builder {
            doubleCheckCount += value
            return this
        }

        fun incrementCheckmateCount(value: Long = 1L): Builder {
            checkmateCount += value
            return this
        }

        fun incrementStalemateCount(value: Long = 1L): Builder {
            stalemateCount += value
            return this
        }

        fun build(): MovementMetadata {
            return MovementMetadata(
                nodesCount = nodesCount,
                captureCount = captureCount,
                enPassantCount = enPassantCount,
                castlingCount = castlingCount,
                promotionCount = promotionCount,
                checkCount = checkCount,
                discoveryCheckCount = discoveryCheckCount,
                doubleCheckCount = doubleCheckCount,
                checkmateCount = checkmateCount,
                stalemateCount = stalemateCount
            )
        }
    }

    override fun toString(): String =
        mutableListOf<String>()
            .apply {
                add("nodesCount = $nodesCount")
                add("captureCount = $captureCount")
                add("enPassantCount = $enPassantCount")
                add("castlingCount = $castlingCount")
                add("promotionCount = $promotionCount")
                add("checkCount = $checkCount")
                add("discoveryCheckCount = $discoveryCheckCount")
                add("doubleCheckCount = $doubleCheckCount")
                add("checkmateCount = $checkmateCount")
                add("stalemateCount = $stalemateCount")
            }
            .joinToString()

    companion object {
        fun builder() = Builder()
    }
}

private fun Int.toPosition() = Position.from(this)

@Suppress("unused")
class Movement(
    val from: Int,
    val to: Int,
    val toPiece: Piece,
    val flags: MovementFlags
) {

    constructor(
        from: Position,
        to: Position,
        toPiece: Piece,
        flags: MovementFlags
    ) : this(
        from.squareIndex,
        to.squareIndex,
        toPiece,
        flags
    )

    val fromPosition: Position get() = Position.from(from)
    val toPosition: Position get() = Position.from(to)

    override fun toString(): String {
        return "${from.toPosition()} -> ${to.toPosition()} = $toPiece, $flags"
    }
}

class MovementTarget(
    val piece: Piece,
    val to: Int,
    val flags: MovementFlags
) {
    override fun toString(): String {
        return "${Position.from(to)} = $piece, $flags"
    }
}

private fun List<MovementTarget>.summarizeFlags(): MovementMetadata {
    return MovementMetadata.builder().apply {
        this@summarizeFlags.forEach {
            addFlags(it.flags)
        }
    }.build()
}

@Suppress("unused")
class PieceMovement(
    val from: Int,
    val targets: List<MovementTarget>
) {
    val metadata: MovementMetadata = targets.summarizeFlags()
    val targetCount = targets.size

    fun isEmpty(): Boolean = targets.isEmpty()
    fun isNotEmpty(): Boolean = targets.isNotEmpty()

    fun forEachTarget(visitor: (MovementTarget) -> Unit) {
        targets.forEach { visitor.invoke(it) }
    }

    fun asSequenceOfTargets() = targets.asSequence()

    override fun toString(): String = buildString {
        append("${Position.from(from)} -> ")
        if (targets.isEmpty()) {
            append("[no movements]")
        } else {
            targets.joinTo(this)
        }
    }
}

private fun List<PieceMovement>.summarizeMetadata(): MovementMetadata {
    return MovementMetadata.builder().apply {
        this@summarizeMetadata.forEach {
            addMetadata(it.metadata)
        }
    }.build()
}

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Movements(val origins: List<PieceMovement>) : Iterable<Movement> {

    val metadata: MovementMetadata = origins.summarizeMetadata()

    fun isEmpty(): Boolean = metadata.nodesCount == 0L
    fun isNotEmpty(): Boolean = !isEmpty()

    fun getOriginsCount(): Int {
        return origins.size
    }

    fun asSequenceOfPieceMovements(): Sequence<PieceMovement> =
        origins.asSequence()

    fun asSequenceOfMovements(): Sequence<Movement> =
        asSequenceOfPieceMovements().flatMap {
            it.targets.asSequence().map { t ->
                Movement(it.from, t.to, t.piece, t.flags)
            }
        }

    fun forEachPieceMovement(visitor: (PieceMovement) -> Unit) {
        asSequenceOfPieceMovements().forEach { visitor.invoke(it) }
    }

    fun forEachMovement(visitor: (Movement) -> Unit) {
        asSequenceOfMovements().forEach {
            visitor.invoke(it)
        }
    }

    override fun iterator(): Iterator<Movement> {
        return asSequenceOfMovements().iterator()
    }

    operator fun plus(other: Movements): Movements {
        return Movements(
            origins + other.origins
        )
    }

    fun getPieceMovement(index: Int): PieceMovement {
        return origins[index]
    }

    fun getRandomPieceMovement(): PieceMovement {
        return origins.random()
    }

    fun getRandomMovement(): Movement {
        return asSequenceOfMovements()
            .toList()
            .random()
    }

    companion object {
        fun empty() = Movements(emptyList())
    }
}
