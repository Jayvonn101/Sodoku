# REST API Setup

## Dependencies

- [ ] Add Spring Boot parent to `pom.xml`
- [ ] Add `spring-boot-starter-web` dependency
- [ ] Add `spring-boot-maven-plugin` to build plugins

## Project Structure

- [ ] Create `SudokuController.java` in `src/main/java/org/sudoku/`
- [ ] Create `GameSession.java` to hold in-memory game state
- [ ] Create `GameStore.java` to manage active games with a `HashMap`

## Endpoints

- [ ] `POST /api/game/new` — generate a new puzzle
- [ ] `GET /api/game/{id}` — get current game state
- [ ] `POST /api/game/{id}/move` — place a number
- [ ] `DELETE /api/game/{id}/move` — remove a number
- [ ] `GET /api/game/{id}/hint` — get a hint
- [ ] `GET /api/game/{id}/solve` — auto-solve the puzzle

## Testing

- [ ] Test all endpoints with Postman or curl
- [ ] Verify error responses (invalid move, bad game ID, out of bounds)
