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
- [ ] UCI compatible GUI

## Get started

Creating a board object with 

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