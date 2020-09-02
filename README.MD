# AN.KO.BA.CH.EN
___

This is a hobbyist chess engine software primarily programmed in Kotlin. **AN.KO.BA.CH.EN** is just an acronym for **Another Kotlin Based Chess Engine**. 

The features are:

- [x] Movement generation
- [ ] FEN string parsing and generation
- [ ] PERFT calculation and movement enumeration
- [ ] Image and text based board export
- [ ] PGN file reader and writer
- [ ] Game engine for standard and Chess960 variants with UCI support
- [ ] GUI compatible wit UCI

## Get started

Creating board

```kotlin
val board = Board()
board.forEachMovement { move ->
    println(move)
}
```

Making movements

```kotlin
val board = Board()
// ruy lopez opening
board.move(E2, E4)
board.move(E7, E5)
board.move(G1, F3)
board.move(B8, C6)
board.move(F1, B5)
println(board)
```
```text
output:
┌───┬───┬───┬───┬───┬───┬───┬───┐
│ r │   │ b │ q │ k │ b │ n │ r │
├───┼───┼───┼───┼───┼───┼───┼───┤
│ p │ p │ p │ p │   │ p │ p │ p │
├───┼───┼───┼───┼───┼───┼───┼───┤
│   │   │ n │   │   │   │   │   │
├───┼───┼───┼───┼───┼───┼───┼───┤
│   │ B │   │   │ p │   │   │   │
├───┼───┼───┼───┼───┼───┼───┼───┤
│   │   │   │   │ P │   │   │   │
├───┼───┼───┼───┼───┼───┼───┼───┤
│   │   │   │   │   │ N │   │   │
├───┼───┼───┼───┼───┼───┼───┼───┤
│ P │ P │ P │ P │   │ P │ P │ P │
├───┼───┼───┼───┼───┼───┼───┼───┤
│ R │ N │ B │ Q │ K │   │   │ R │
└───┴───┴───┴───┴───┴───┴───┴───┘
```

## Movement generation

## PERFT  calculation

```text
FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
Depth: 5
┌───────┬─────────┬──────────┬─────────────┬───────────┬────────────┬────────┬─────────────┬─────────┬────────────┬────────────┐
│ DEPTH │   NODES │ CAPTURES │ EN_PASSANTS │ CASTLINGS │ PROMOTIONS │ CHECKS │ DISCOVERIES │ DOUBLES │ CHECKMATES │ STALEMATES │
├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
│     1 │      20 │        0 │           0 │         0 │          0 │      0 │           0 │       0 │          0 │          0 │
├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
│     2 │     400 │        0 │           0 │         0 │          0 │      0 │           0 │       0 │          0 │          0 │
├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
│     3 │    8902 │       34 │           0 │         0 │          0 │     12 │           0 │       0 │          0 │          0 │
├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
│     4 │  197281 │     1576 │           0 │         0 │          0 │    469 │           0 │       0 │          8 │          0 │
├───────┼─────────┼──────────┼─────────────┼───────────┼────────────┼────────┼─────────────┼─────────┼────────────┼────────────┤
│     5 │ 4865609 │    82719 │         258 │         0 │          0 │  27351 │           6 │       0 │        347 │          0 │
└───────┴─────────┴──────────┴─────────────┴───────────┴────────────┴────────┴─────────────┴─────────┴────────────┴────────────┘
```
